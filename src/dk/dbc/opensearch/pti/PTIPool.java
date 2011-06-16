/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/**
 * \file PTIPool.java
 * \brief Brief description
 */


package dk.dbc.opensearch.pti;


import dk.dbc.opensearch.config.PtiConfig;
import dk.dbc.opensearch.db.IProcessqueue;
import dk.dbc.opensearch.pluginframework.PluginResolver;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.RejectedExecutionHandler;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import dk.dbc.opensearch.fedora.IObjectRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import java.util.concurrent.TimeUnit;

public class PTIPool
{

    static Logger log = Logger.getLogger( PTIPool.class );
    private Map<Integer, FutureTask<Boolean>> jobs;
    private final ThreadPoolExecutor threadpool;
    private IProcessqueue processqueue;
    private IObjectRepository objectRepository;
    private Compass compass;
    private int shutDownPollTime;
    private PluginResolver pluginResolver;

    private class BlockingRejectedExecutionHandler implements RejectedExecutionHandler
    {
        @Override
        public void rejectedExecution( Runnable r, ThreadPoolExecutor executor )
        {
            if( executor.isShutdown() )
            {
                throw new RejectedExecutionException();
            }

            try
            {
                executor.getQueue().put( r );
            }
            catch( InterruptedException e )
            {
                String error = String.format( "Caught interruption from the executor: %s", e.getMessage() );
                log.error( error, e );
                throw new RejectedExecutionException( error, e );
            }
        }
    }


    public PTIPool( ThreadPoolExecutor threadpool, Compass compass, IObjectRepository objectRepository, PluginResolver pluginResolver ) throws ConfigurationException
    {
        this.objectRepository = objectRepository;
        this.threadpool = threadpool;
        this.compass = compass;
        this.pluginResolver = pluginResolver;
        jobs = new HashMap<Integer, FutureTask<Boolean>>();

        shutDownPollTime = PtiConfig.getShutdownPollTime();

        threadpool.setRejectedExecutionHandler( new BlockingRejectedExecutionHandler() );
    }


    public void submit( String fedoraHandle, Integer queueID ) throws RejectedExecutionException, ConfigurationException, ClassNotFoundException, ServiceException, MalformedURLException, IOException
    {
        log.debug( String.format( "Submitting job '%s'", fedoraHandle ) );

        CompassSession session = compass.openSession();

        FutureTask<Boolean> future = new FutureTask<Boolean>( new PTIThread( fedoraHandle, session, objectRepository, pluginResolver ) );

        if( future == null )
        {
            log.error( "PTIPool submit 'future' is null" );
            throw new IllegalStateException( "PTIPool submit 'future' is null" );
        }

        threadpool.submit( future );

        jobs.put( queueID, future );
    }

    public Map<Integer, Boolean> checkJobs() throws InterruptedException
    { 
        log.trace( "Entering checkJobs" );

        Map<Integer, Boolean> finishedJobs = new HashMap<Integer, Boolean>();
        
        for( Integer id : jobs.keySet() )
        {
            //log.trace( String.format( "Checking job '%s'", id ) );

            FutureTask<Boolean> job = jobs.get( id );

            if( job.isCancelled() )
            {
                //log.debug( String.format( "Job with queueID '%s' cancelled", id ) );  
                finishedJobs.put( id, false );
            }

            else if( job.isDone() )
            {
                Boolean success = null;

                try
                {
                    success = job.get();
                }
                catch( ExecutionException ee )
                {
                    // getting exception from thread
                    Throwable cause = ee.getCause();
                    log.error( String.format( "Exception Caught from thread: '%s' Message: '%s'", cause.getClass(), cause.getMessage() ), cause );
                }
                
                finishedJobs.put( id, success );
                
            }
        }
        
        for( Integer finishedJobID : finishedJobs.keySet() ){
            jobs.remove( finishedJobID );
        }
        
        return finishedJobs;
    }

    public void shutdown() throws InterruptedException
    {

        log.debug( "Shutting down PTIPool" );
       
        threadpool.shutdown();

        log.debug( String.format( "Cancelling queued jobs. jobs in queue = '%s'", jobs.size() ) );
    
        // for( Integer id : jobs.keySet() )
        // {
        //     FutureTask<Boolean> job = jobs.get( id );
        //     boolean canceled = job.cancel( false );
        //     //log.trace( String.format( "Trying to cancel job with queueID '%s'. success = '%s'", id, canceled ) );
        // }
       
        log.debug( "Awaiting threadpool termination" );
        log.debug( String.format( "is compass down? ='%s'", compass.isClosed() ) );
        threadpool.awaitTermination( 10l, TimeUnit.SECONDS ); // configurable ?
        log.debug( "Threadpool down" );

        log.debug( String.format( "Shutdown compass... is it already down ? = '%s'", compass.isClosed() ) );
        compass.close();
        log.debug( "Compass is down" );

        log.debug( "PTIPool down" );
    }
}