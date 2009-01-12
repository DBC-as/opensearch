/**
 * \file PTIPoolAdm.java
 * \brief The PTIPoolAdm Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.statistics.Estimate;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.ConfigurationException;

import java.util.Vector;
import java.util.Iterator;

import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.lang.InterruptedException;
import java.util.concurrent.ExecutionException;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import org.compass.core.converter.ConversionException;

import org.compass.core.Compass;
/**
 * \ingroup pti
 * \brief Handles the pti threads
 */
public class PTIPoolAdm {

    Logger log = Logger.getLogger("PTIPoolAdm");

    private Processqueue processqueue;
    private Estimate estimate;
    private PTIPool ptiPool;
    //private FedoraHandler fedoraHandler;

    private Vector activeThreads;
    private Iterator iter;

    private long sleepInMilliSec;

    private Compass compass;

    /**
     * Initializes the PTIPool with the given number of
     * threads. Initializes a Processqueue and starts the mainLoop for
     * the processing of data within the threads.
     *
     * @param ptiPool the pool that spawns and handles pti threads
     * @param processqueue used to fetch new tasks from the processqueue
     * @param estimate used to update the estimate table in the database
     * @param compass used to index, the target and write the indexes 
     *
     * @throws ConfigurationException If the PTIPool could not be correctly initialized
     * @throws ClassNotFoundException If the Processqueue could not load the database driver
     * @throws MalformedURLException Could not obtain compass configuration
     * @throws UnknownHostException Error obtaining fedora configuration
     * @throws ServiceException Something went wrong initializing the fedora client
     * @throws IOException Something went wrong initializing the fedora client
     * @throws SQLException The processqueue could not retrieve information from the database
     */
    public PTIPoolAdm( PTIPool ptiPool, Processqueue processqueue, Estimate estimate, Compass compass )throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException, SQLException{
        log.debug( String.format( "Entering PTIPoolAdm" ) );

        /** /todo: where should sleepInMilliSec be set?? in a configuration file or what*/
        
        sleepInMilliSec= 20000;
        this.ptiPool = ptiPool;
        this.processqueue = processqueue;
        this.estimate = estimate;
        //this.fedoraHandler = fedoraHandler;
        this.compass = compass;

        activeThreads = new Vector();
        
        
       //  log.debug( String.format( "Setting up the PTIPool with %s threads", numberOfThreads ) );
//         PTIpool = new PTIPool( numberOfThreads, fedoraHandler);        
        
        // log.debug( "PTIPoolAdm is set up" );
    }

    /**
     * the mainLoop polls the processqueue and start threads when the queue pops a fedorahandle.   
     * the mainLoop also checks whether already active threads are finished.
     *
     * @throws ClassNotFoundException if the Processqueue could not load the database driver
     * @throws SQLException if the processqueue could not retrieve information from the database
     * @throws RuntimeException if the started thread stop due to an exception
     * @throws ConfigurationException if the PTIPool could not be correctly initialized
     * @throws InterruptedException is a thread exception
     */    
    public void mainLoop()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException, InterruptedException{

        log.debug( "PTIPoolAdm mainloop" );

        log.debug( "Removing entries marked as active from the processqueue" );
        int removed = processqueue.deActivate();
        log.debug( String.format( "marked  %s 'active' threads as ready to process", removed ) );

        // creates initial timestamp. creates it with an offset - so
        // when the loop is entered it polls the processqueue
        // immediatly
        long stamp = System.currentTimeMillis() - ( sleepInMilliSec+ 1 ) ;

        /** /todo: we need a nicer way to do this than a while true loop. */
        
        try{
            
            Thread.currentThread().sleep(2000);
            if( System.currentTimeMillis() > stamp+sleepInMilliSec ){                
                startThreads();
                stamp = System.currentTimeMillis();
            }  
            //System.out.print( String.format( "Number of elements in activeThreads: '%s'",activeThreads.size() ) );
            checkThreads();
            
        }
        catch(InterruptedException ie){
            if( activeThreads.size() == 0 ){
                log.debug( "There are no active threads... Shutting down" );
                
            }
            else{
                
                while( activeThreads.size() > 0 ){
                    log.debug( String.format( "There are %s active threads... finishing before shutdown", activeThreads.size() ) );
                    checkThreads();
                    Thread.currentThread().sleep(2000);
                }
            }
        }
        log.debug( "Finished the mainLoop method" );
    }
    
    /**
     * Iterates through the activeThreads vector and remove all entries
     * where the associated thread is done. The entries are also
     * committed to the processqueue, which effectivly removes them
     *
     * @throws ClassNotFoundException if the Processqueue could not load the database driver
     * @throws SQLException if the processqueue could not retrieve information from the database
     * @throws NoSuchElementException if the processqueue does not contain an element matching the queueid
     * @throws InterruptedException is a thread exception
     * @throws RuntimeException if the started thread stop due to an exception
     */
    private void checkThreads()throws ClassNotFoundException, SQLException, NoSuchElementException, InterruptedException, RuntimeException {
        log.debug( "Entering PTIPoolAdm.checkThreads()" );
        
        Pair<FutureTask, Integer> vectorPair = null;
        int queueID;
        FutureTask future;
        Vector removeableThreads = new Vector();
        
        while( activeThreads.size() > 0 ){
        iter = activeThreads.iterator();
            while( iter.hasNext() ){
                
                vectorPair = ( Pair ) iter.next();
                future = Tuple.get1(vectorPair);
                queueID = Tuple.get2(vectorPair);
                
                if( future.isDone() ){// this thread is done or stopped with and exception
                    try{
                        Long processtime = (Long) future.get();
                    }
                    catch(ExecutionException ee){
                        // getting exception from thread
                        Throwable cause = ee.getCause();
                        
                        RuntimeException re = new RuntimeException(cause);
                        
                        if( cause.getClass() == ConversionException.class ) {
                            
                            log.debug( String.format( "Element to be removed with queueID: '%s'",queueID ) );  
                            log.error("An element on the processqueue does not match its promised format and can therefore not be indexed. The Validation of the elements being pushed to the queue is flawed! It was removed from the processqueue and execution of other elements continue");
                        }else{
                            log.fatal( String.format( " %s caused under execution with message %s associated with queueid '%s' in the processqueue", cause.getClass(), cause.getMessage(), queueID ) );
                            throw re;
                        }
                    }
                    
                    processqueue.commit( queueID );
                    removeableThreads.add( vectorPair );
                    log.info( String.format( "Job done, committed to queue, queueID = %s", queueID ) );
                }
            }
            
            // remove threads that have returned, from activeThreads
            iter = removeableThreads.iterator();
            while( iter.hasNext() ){
                activeThreads.remove( iter.next() );
            }       
        } 
    }
    
    /** pop processqueue until its empty, and starts threads which
     * fetch and process data from the fedora repository. Puts thread
     * (futureTask, and queueid on activeThreads vector)
     * committed to the processqueue, which effectivly removes them
     *
     * @throws ClassNotFoundException if the Processqueue could not load the database driver
     * @throws SQLException if the processqueue could not retrieve information from the database
     * @throws RuntimeException if the started thread stop due to an exception
     * @throws ConfigurationException if the PTIPool could not be correctly initialized
     */
    private void startThreads()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException{
        log.debug( "Entering PTIPoolAdm.startThreads()" );

        boolean fetchedLast = false;
        Triple<String, Integer, String> queueTriple = null;
        Pair<FutureTask, Integer> vectorPair = null;
        String fedoraHandle;
        String itemID;
        int queueID;
        FutureTask future;

        while( !fetchedLast ){

            try{
                queueTriple = processqueue.pop();
            }
            catch( NoSuchElementException nse ){
                log.debug( "processqueue is empty" );
                fetchedLast = true;
            }

            if( !fetchedLast ){
                log.debug( "Fetched element from processqueue" );
                fedoraHandle = Tuple.get1(queueTriple);
                queueID = Tuple.get2(queueTriple);
                itemID = Tuple.get3(queueTriple);
              
                future = ptiPool.createAndJoinThread( fedoraHandle, itemID, estimate, compass );
              
                // add thread to active thread vector
                vectorPair = Tuple.from(future, queueID);
                activeThreads.add( vectorPair );
                log.info( String.format( "started new thread with fedoraHandle: %s, queueID: %s and itemID: %s ",             
                                         fedoraHandle, queueID, itemID ) );
            }
        }
    }
}
