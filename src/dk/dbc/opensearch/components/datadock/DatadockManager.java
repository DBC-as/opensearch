/**
 * \file DatadockManager.java
 * \brief The DatadockManager class
 * \package datadock;
 */

package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.components.harvest.IHarvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \brief the DataDockManager manages the startup, running and
 * closedown of the associated harvester and threadpool
 */
public class DatadockManager
{
    static Logger log = Logger.getLogger("DatadockManager");

    private DatadockPool pool= null;
    private IHarvester harvester = null;
    private int rejectedSleepTime;

    
    /**
     * Constructs the the DatadockManager instance.
     */
    public DatadockManager( DatadockPool pool, IHarvester harvester )
    {
        log.debug( "Constructor( pool, harvester ) called" );

        this.pool = pool;
        this.harvester = harvester;
        harvester.start();
        
        rejectedSleepTime = 3000; // configurationfile        
    }

    
    public void update() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException
    {
        log.debug( "update() called" );
        
        // checking for new jobs and executing them
        Vector< DatadockJob > jobs = harvester.getJobs();
        for( DatadockJob job : jobs )
        {
            boolean submitted = false;
            
            while( ! submitted )
            {
                 try
                 {
                     pool.submit( job );
                     submitted = true;
             
                     log.debug( String.format( "submitted job: '%s'",job.getPath().getRawPath() ) );
                 }
                 catch( RejectedExecutionException re )
                 {
                     log.debug( String.format( "job: '%s' rejected, trying again",job.getPath().getRawPath() ) );
                     Thread.currentThread().sleep( rejectedSleepTime );
                 }
            }
        }
        
        //checking jobs
        Vector<CompletedTask> finishedJobs = pool.checkJobs();
    }
    

    public void shutdown() throws InterruptedException
    {
        log.debug( "Shutting down the pool" );
        pool.shutdown();        
        log.debug( "The pool is down" );        
        
        log.debug( "Stopping harvester" );        
        harvester.shutdown();
        log.debug( "The harvester is stopped" );
    }
}