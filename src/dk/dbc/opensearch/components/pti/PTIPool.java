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
 * \brief manages threads used for execution of ptijobs
 */
package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;

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
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;


/**
 * \ingroup PTI
 *
 * \brief The PTIPool manages the pti threads and provides methods
 * to add and check running jobs
 */
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

    /**
     * \brief private class that handles RejectedExecutions.
     *
     * This class Handles RejectedExecutions by implementing
     * RejectedExecutionHandler, which is thrown if the
     * threadpoolqueue is full.  . When one is encountered The Handler
     * waits until it can put the element on queue, and only throws an
     * exception if the queue is shutdown
     */
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

    /**
     * Constructs the the PTIPool instance
     *
     * @param threadpool The threadpool to submit jobs to
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     */
    public PTIPool( ThreadPoolExecutor threadpool, Compass compass, IObjectRepository objectRepository, PluginResolver pluginResolver ) throws ConfigurationException
    {
        log.debug( "Constructor( threadpool, compass ) called" );

        this.objectRepository = objectRepository;
        this.threadpool = threadpool;
        this.compass = compass;
        this.pluginResolver = pluginResolver;
        jobs = new HashMap<Integer, FutureTask<Boolean>>();

        //jobs = new Vector< InputPair< FutureTask< Boolean >, Integer > >();
        shutDownPollTime = PtiConfig.getShutdownPollTime();

        threadpool.setRejectedExecutionHandler( new BlockingRejectedExecutionHandler() );
    }


    /**
     * submits a job to the threadpool for execution by a PTIThread.
     *
     * @param fedoraHandle the handle to fedora repository
     *
     * @throws RejectedExecutionException Thrown if the threadpools jobqueue is full.
     */
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


    /**
     * Checks the jobs submitted for execution, and returns a set containing
     * identifiers for the jobs that are not running anymore
     *
     * if a Job throws an exception it is written to the log and the
     * datadock continues.
     *
     * @throws InterruptedException if the job.get() call is interrupted (by kill or otherwise).
     */
    public Map<Integer, Boolean> checkJobs() throws InterruptedException
    {
        log.trace( "Entering checkJobs" );

        Map<Integer, Boolean> finishedJobs = new HashMap<Integer, Boolean>();
        for( Integer id : jobs.keySet() )
        {
            FutureTask<Boolean> job = jobs.get( id );
            if( job.isDone() )
            {
                Boolean success = null;

                try
                {
                    log.debug( "checking job" );
                    success = job.get();
                }
                catch( ExecutionException ee )
                {
                    // getting exception from thread
                    Throwable cause = ee.getCause();
                    log.error( String.format( "Exception Caught from thread: '%s' Message: '%s'", cause.getClass(), cause.getMessage() ), cause );
                }

                log.debug( String.format( "adding (queueID='%s') to finished jobs", id ) );

                log.debug( String.format( "Removing Job queueID='%s'", id ) );
                jobs.remove( id );
                finishedJobs.put( id, success );
            }
        }

        return finishedJobs;
    }


    /**
     * Shuts down the PTIPool. it waits for all current jobs to
     * finish before exiting.
     *
     * @throws InterruptedException if the checkJobs or sleep call is interrupted (by kill or otherwise).
     */
    public void shutdown() throws InterruptedException
    {
        log.info( "shutdown() called" );
        boolean activeJobs = true;
        while( activeJobs )
        {
            activeJobs = false;
            for( Entry<Integer, FutureTask<Boolean>> jobpair : jobs.entrySet() )
            {
                FutureTask<Boolean> job = jobpair.getValue();
                if( !job.isDone() )
                {
                    activeJobs = true;
                }
            }
        }
    }


}
