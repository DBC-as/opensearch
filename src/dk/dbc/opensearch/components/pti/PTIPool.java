/**
 * \file PTIPool.java
 * \brief The PTIPool class
 * \package pti;
 */
package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;


/**
 * \ingroup PTI
 *
 * \brief The PTIPool manages the pti threads and provides methods
 * to add and check running jobs
 */
public class PTIPool
{
    static Logger log = Logger.getLogger("PTIPool");
    private Vector< Pair< FutureTask< PTIThread >, Integer > > jobs;
    private final ThreadPoolExecutor threadpool;
    private Estimate estimate;
    private Processqueue processqueue;
    //    private FedoraHandler fedoraHandler;
    private Compass compass;
    private int shutDownPollTime;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;

    XMLConfiguration config = null;
    
    /**
     * Constructs the the PTIPool instance
     *
     * @param threadpool The threadpool to submit jobs to
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     */
    public PTIPool( ThreadPoolExecutor threadpool, Estimate estimate, Compass compass, HashMap< Pair< String, String>, ArrayList< String > > jobMap ) throws ConfigurationException
     {
         log.debug( "Constructor( threadpool, estimate, compass ) called" );

         this.threadpool = threadpool;
         this.estimate = estimate;
         this.jobMap = jobMap;
         this.compass = compass;

         jobs = new Vector< Pair< FutureTask< PTIThread >, Integer > >();
         
         //should use the config class
         
        URL cfgURL = getClass().getResource("/config.xml");
        config = new XMLConfiguration( cfgURL );
        shutDownPollTime = config.getInt( "pti.shutdown-poll-time" );
        
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
    
        FutureTask future = getTask( fedoraHandle );
        threadpool.submit( future );
        Pair pair = new Pair< FutureTask< PTIThread >, Integer >( future, queueID );
        jobs.add( pair );
    }
    
    
    public  FutureTask getTask( String fedoraHandle ) throws ConfigurationException , ClassNotFoundException, ServiceException, MalformedURLException, IOException
    {
        log.debug( "GetTask called" );        
        CompassSession session = null;
        log.debug( "Getting CompassSession" );
        session = compass.openSession();
        return new FutureTask( new PTIThread( fedoraHandle, session, estimate, jobMap ) );
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
    public Vector<CompletedTask> checkJobs() throws InterruptedException 
    {
        log.debug( "checkJobs() called" );
    
        Vector<CompletedTask> finishedJobs = new Vector<CompletedTask>();
        for( Pair<FutureTask<PTIThread>, Integer> jobpair : jobs )        
        {
            FutureTask job = jobpair.getFirst();
            Integer queueID = jobpair.getSecond();
            if( job.isDone() )
            {
                Long l = null;
                //log.fatal( "Catched exception from job" );
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
 // throw re; //shouldnt throw just because thread throw
                }
                log.debug( String.format( "adding (queueID='%s') to finished jobs", queueID ) );
                Pair pair = new Pair< Long, Integer >( l, queueID );
                finishedJobs.add( new CompletedTask( job, pair ) );
            }
        }

        for( CompletedTask finishedJob : finishedJobs )
        {
             log.debug( "Removing Job" );            
             
             Pair< Long, Integer > finishedpair = (Pair) finishedJob.getResult();
             log.debug( String.format( "Removing Job queueID='%s'", finishedpair.getSecond() ) );
             
             Vector< Pair< FutureTask< PTIThread >, Integer > > removeableJobs = new Vector< Pair< FutureTask< PTIThread >, Integer > >();
             for( Pair< FutureTask< PTIThread >, Integer > job : jobs )
             {
                Integer queueID = job.getSecond();
                if( queueID.equals( finishedpair.getSecond() ) )
                    removeableJobs.add( job );
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
            for( Pair<FutureTask<PTIThread>, Integer> jobpair : jobs )
            {
                FutureTask job = jobpair.getFirst(); 
                if( ! job.isDone() )
                    activeJobs = true;
            }
        }
    }
}