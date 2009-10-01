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
import dk.dbc.opensearch.common.fedora.IFedoraAdministration;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;
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


/**
 * \ingroup PTI
 *
 * \brief The PTIPool manages the pti threads and provides methods
 * to add and check running jobs
 */
public class PTIPool
{
    static Logger log = Logger.getLogger( PTIPool.class );
    
    
    private Vector< InputPair< FutureTask< Long >, Integer > > jobs;
    private final ThreadPoolExecutor threadpool;
    private IEstimate estimate;
    private IProcessqueue processqueue;
    private IFedoraAdministration fedoraAdministration;
    private Compass compass;
    private int shutDownPollTime;

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
		public void rejectedExecution(Runnable r, ThreadPoolExecutor executor)  
		{
			if( executor.isShutdown())  
			{
					throw new RejectedExecutionException();
			}
			try 
			{
				executor.getQueue().put(r);
			} 
			catch (InterruptedException e) 
			{		
				e.printStackTrace();
				throw new RejectedExecutionException();
			};
		}    	
    }
    
    /**
     * Constructs the the PTIPool instance
     *
     * @param threadpool The threadpool to submit jobs to
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     */
    public PTIPool( ThreadPoolExecutor threadpool, IEstimate estimate, Compass compass, IFedoraAdministration fedoraAdministration ) throws ConfigurationException
    {
         log.debug( "Constructor( threadpool, estimate, compass ) called" );

         this.fedoraAdministration = fedoraAdministration;
         this.threadpool = threadpool;
         this.estimate = estimate;
         this.compass = compass;
         jobs = new Vector< InputPair< FutureTask< Long >, Integer > >();         
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
    	log.debug( String.format( "submit( fedoraHandle='%s', queueID='%s' )", fedoraHandle, queueID ) );
    
        FutureTask<Long> future = getTask( fedoraHandle );

        threadpool.submit( future );
        InputPair< FutureTask< Long >, Integer > pair = new InputPair< FutureTask< Long >, Integer >( future, queueID );
        jobs.add( pair );
    }
    
    
    public FutureTask<Long> getTask( String fedoraHandle ) throws ConfigurationException , ClassNotFoundException, ServiceException, MalformedURLException, IOException
    {
        log.debug( "GetTask called" );        
        CompassSession session = null;
        log.debug( "Getting CompassSession" );
        session = compass.openSession();

        return new FutureTask<Long>( new PTIThread( fedoraHandle, session, estimate, fedoraAdministration ) );
    }

    
    /**
     * Checks the jobs submitted for execution, and return the number
     * of active jobs.
     *
     * if a Job throws an exception it is written to the log and the
     * PTI continues.
     *
     * @throws InterruptedException if the job.get() call is interrupted (by kill or otherwise).
     */
    public Vector<CompletedTask<InputPair<Long, Integer> >> checkJobs() throws InterruptedException 
    {
        log.debug( "checkJobs() called" );
    
        Vector<CompletedTask<InputPair<Long, Integer>>> finishedJobs = new Vector<CompletedTask<InputPair<Long, Integer>>>();
        for( InputPair<FutureTask<Long>, Integer> jobpair : jobs )        
        {
            FutureTask job = jobpair.getFirst();
            Integer queueID = jobpair.getSecond();
            if( job.isDone() )
            {
                Long l = null;
                try
                {
                    log.debug( "Checking job" );
                    
                    l = (Long) job.get();
                }
                catch( ExecutionException ee )
                {                    
                    log.error( "Exception caught from job" );    
                    // getting exception from thread
                    Throwable cause = ee.getCause();
                    RuntimeException re = new RuntimeException( cause );
                    log.error( String.format( "Exception Caught: '%s'" , re.getMessage() ) );
                    StackTraceElement[] expStack =  re.getStackTrace();
                    for( int i = 0; i < expStack.length; i++ )
                    {
                        log.error( String.format( "Trace element %s : %s", i, expStack[i].toString() ) );
                    }                   
                }

                log.debug( String.format( "adding (queueID='%s') to finished jobs", queueID ) );
                InputPair< Long, Integer > pair = new InputPair< Long, Integer >( l, queueID );
                finishedJobs.add( new CompletedTask<InputPair< Long, Integer >>( job, pair ) );
            }
        }

        for( CompletedTask<InputPair<Long, Integer >> finishedJob : finishedJobs )
        {
             log.debug( "Removing Job" );            
             
             InputPair<Long, Integer> finishedpair =  finishedJob.getResult();
             log.debug( String.format( "Removing Job queueID='%s'", finishedpair.getSecond() ) );
             
             Vector<InputPair<FutureTask<Long>, Integer>> removeableJobs = new Vector<InputPair<FutureTask<Long>, Integer>>();
             for( InputPair<FutureTask<Long>, Integer> job : jobs )
             {
                Integer queueID = job.getSecond();
                if( queueID.equals( finishedpair.getSecond() ) )
                {
                    removeableJobs.add( job );
                }
             }
             
             jobs.removeAll( removeableJobs );
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
        log.debug( "shutdown() called" );    
        boolean activeJobs = true;
        while( activeJobs )
        {
            activeJobs = false;
            for( InputPair<FutureTask<Long>, Integer> jobpair : jobs )
            {
                FutureTask job = jobpair.getFirst(); 
                if( ! job.isDone() )
                {
                    activeJobs = true;
                }
            }
        }
    }
}