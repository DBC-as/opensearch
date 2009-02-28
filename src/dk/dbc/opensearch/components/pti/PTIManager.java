/**
 * \file PTIManager.java
 * \brief The PTIManager class
 * \package pti;
 */

package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.db.Processqueue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import dk.dbc.opensearch.common.types.Pair;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.XMLConfiguration;
import java.net.URL;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;

/**
 * \brief the PTIManager manages the startup, running and
 * closedown of the associated threadpool
 */
public class PTIManager
{
    static Logger log = Logger.getLogger("PTIManager");

    private PTIPool pool= null;
    private Processqueue processqueue = null;

    private int rejectedSleepTime;

    XMLConfiguration config = null;

    /**
     * Constructs the the PTIManager instance.
     */
    public PTIManager( PTIPool pool, Processqueue processqueue ) throws ClassNotFoundException, SQLException, ConfigurationException
    {
        log.debug( "Constructor( pool ) called" );

        this.processqueue = processqueue;
        this.pool = pool;

        

        URL cfgURL = getClass().getResource("/config.xml");        
        config = new XMLConfiguration( cfgURL );
        rejectedSleepTime = config.getInt( "pti.rejected-sleep-time" );
    
        log.debug( "Removing entries marked as active from the processqueue" );
        int removed = processqueue.deActivate();
        log.debug( String.format( "marked  %s 'active' threads as ready to process", removed ) );
    }

    /**
     * Updates the ptimanager.  First of it checks for new jobs and
     * executes them if it found any.Afterwards it checks already
     * submitted jobs, and if they have returned with a value (job
     * comenced succesfully), the corresponding element in the
     * processqueue are commited.
     */
    public void update() throws SQLException, ConfigurationException, ClassNotFoundException, InterruptedException, ServiceException, MalformedURLException, IOException
    {
        log.debug( "update() called" );

        // checking for new jobs
        Vector<Pair<String, Integer>> newJobs = processqueue.popAll();
        log.debug( String.format( "Found '%s' new jobs", newJobs.size() ) );

        // Starting new Jobs
        for( Pair<String, Integer> job : newJobs ){

            boolean submitted = false;
            while( ! submitted ){
                try{
                    pool.submit( job.getFirst(), job.getSecond() );
                    submitted = true;
                    log.debug( String.format( "submitted job: fedorahandle='%s' and queueID='%s'",job.getFirst(), job.getSecond() ) );
                }
                catch( RejectedExecutionException re ){
                    log.debug( String.format( "job: fedorahandle='%s' and queueID='%s' rejected - trying again",job.getFirst(), job.getSecond() ) );
                    Thread.currentThread().sleep( rejectedSleepTime );
                }
            }
        }

        // Checking jobs and commiting jobs
        Vector<CompletedTask> finishedJobs = pool.checkJobs();
        
        for ( CompletedTask task : finishedJobs){
            
            Pair< Long, Integer > pair = (Pair) task.getResult();
            Long result = pair.getFirst();
            int queueID = pair.getSecond();
            if ( result != null ){// sucess
                log.debug( String.format( "Commiting queueID: '%s', result: '%s' to processqueue", queueID, result ) );
                processqueue.commit( queueID );
            }
            else{ /** the job returned null - rollback ? */ 
                log.debug( String.format( "job with queueID: '%s', result: '%s' Not successfull... rolling back", queueID, result ) );
                processqueue.rollback( queueID );
            }    
        }
    }

    /**
     * Shuts down the ptiManager. Shuts down the pool and waits until
     * all jobs are finished.
     */
    
    public void shutdown() throws InterruptedException
    {
        log.debug( "Shutting down the pool" );
        // waiting for threads to finish before returning
        pool.shutdown();
        try{
            update();
        }
        catch( Exception e){
            log.debug( String.format( "Caught exception in PTIManager:\n'%s'\n'%s'", e.getMessage(), e.getStackTrace() ) );
        }
        log.debug( "The pool is down" );
    }
}