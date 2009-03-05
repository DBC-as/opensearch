/**
 * \file DatadockPool.java
 * \brief The DatadockPool class
 * \package datadock;
 */

package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * \ingroup datadock
 *
 * \brief The datadockPool manages the datadock threads and provides methods
 * to add and check running jobs
 */
public class DatadockPool
{
    static Logger log = Logger.getLogger("DatadockPool");
    private Vector< FutureTask > jobs;
    private final ThreadPoolExecutor threadpool;
    private Estimate estimate;
    private Processqueue processqueue;
    private int shutDownPollTime;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;
    private PIDManager PIDmanager;

    XMLConfiguration config = null;

    /**
     * Constructs the the datadockPool instance
     *
     * @param threadpool The threadpool to submit jobs to
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     */
    public DatadockPool( ThreadPoolExecutor threadpool, Estimate estimate, Processqueue processqueue, PIDManager PIDmanager, 
                         HashMap< Pair< String, String >, ArrayList< String > > jobMap )throws ConfigurationException
    {
        log.debug( "Constructor( threadpool, estimat, processqueue, fedoraHandler ) called" );

        this.threadpool = threadpool;
        this.estimate = estimate;
        this.processqueue = processqueue;
        this.PIDmanager = PIDmanager;
        this.jobMap = jobMap;

        jobs = new Vector< FutureTask >();

        String cfgFile = FileSystemConfig.getConfigPath() + "config.xml"; 
        config = new XMLConfiguration( cfgFile );
        
        shutDownPollTime = DatadockConfig.getDatadockShutdownPollTime();
    }

    
    /**
     * submits a job to the threadpool for execution by a datadockThread.
     *
     * @param DatadockJob The job to start.
     *
     * @throws RejectedExecutionException Thrown if the threadpools jobqueue is full.
     * @throws ParserConfigurationException 
     * @throws PluginResolverException 
     * @throws NullPointerException 
     * @throws SAXException 
     */
    public void submit( DatadockJob datadockJob ) throws RejectedExecutionException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, ServiceException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        log.debug( String.format( "submit( path='%s', submitter='%s', format='%s' )",
                                  datadockJob.getUri().getRawPath(), datadockJob.getSubmitter(), datadockJob.getFormat() ) );

        // Get fedoraPID for job and adding it to the datadockJob.
        datadockJob.setPID( PIDmanager.getNextPID( datadockJob.getSubmitter() ) );
        
        FutureTask future = getTask( datadockJob );
        threadpool.submit( future );
        jobs.add( future );
    }

    
    public FutureTask getTask( DatadockJob datadockJob )throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        return new FutureTask( new DatadockThread( datadockJob, estimate, processqueue, jobMap ) );
    }


    /**
     * Checks the jobs submitted for execution, and return the number
     * of active jobs.
     *
     * if a Job throws an exception it is written to the log and the
     * datadock continues.
     *
     * @throws InterruptedException if the job.get() call is interrupted (by kill or otherwise).
     */
    public Vector< CompletedTask > checkJobs() throws InterruptedException 
    {
        log.debug( "checkJobs() called" );
    
        Vector< CompletedTask > finishedJobs = new Vector< CompletedTask >();
        for( FutureTask job : jobs )        
        {
            if( job.isDone() )
            {
                Float f = -1f;
                //log.fatal( "Catched exception from job" );
                try
                {
                    log.debug( "Checking job" );
                    
                    f = (Float) job.get();
                }
                catch( ExecutionException ee )
                {                    
                    log.fatal( "Exception caught from job" );
                 
                    // getting exception from thread
                    Throwable cause = ee.getCause();
                    RuntimeException re = new RuntimeException( cause );
                    
                    log.error( String.format( "Exception Caught: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
                    StackTraceElement[] trace = cause.getStackTrace();
                    for( int i = 0; i < trace.length; i++ )
                    	log.error( trace[i].toString() );
                }
                log.debug( "adding to finished jobs" );
                finishedJobs.add( new CompletedTask( job, f ) );
            }
        }
        
        for( CompletedTask finishedJob : finishedJobs )
        {
            log.debug( "Removing Job" );
            jobs.remove( finishedJob.getFuture() );
        }
        
        return finishedJobs;
    }

    
    /**
     * Shuts down the datadockPool. it waits for all current jobs to
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
            for( FutureTask job : jobs )
                if( ! job.isDone() )
                    activeJobs = true;
        }
    }
}
