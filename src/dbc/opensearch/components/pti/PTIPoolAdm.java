package dbc.opensearch.components.pti;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;

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

import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.Processqueue;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;

public class PTIPoolAdm {

    Logger log = Logger.getLogger("PTIPoolAdm");

    private Processqueue processqueue;
    private PTIPool PTIpool;

    private Vector activeThreads;
    private Iterator iter;

    private long sleepInMilliSec;

    /**
     * Initializes the PTIPool with the given number of
     * threads. Initializes a Processqueue and starts the mainLoop for
     * the processing of data within the threads.
     * @param numberOfThreads is the number of threads to initialize the PTIPool with
     * @throws ConfigurationException if the PTIPool could not be correctly initialized
     * @throws ClassNotFoundException if the Processqueue could not load the database driver
     */
    public PTIPoolAdm( int numberOfThreads )throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException{
        log.debug( String.format( "Entering PTIPoolAdm(numberOfThreads=%s)", numberOfThreads ) );

        /** /todo: where should sleepInMilliSec be set?? in a configuration file or what*/
        sleepInMilliSec= 20000;
        
        log.debug( String.format( "Setting up the PTIPool with %s threads", numberOfThreads ) );
        PTIpool = new PTIPool( numberOfThreads );
        
        processqueue = new Processqueue();
        activeThreads = new Vector();
        
        log.debug( String.format( "PTIPoolAdm is set up" ) );
    }

    /**
     * the mainLoop polls the processqueue and start threads when the queue pops a fedorahandle.   
     * the mainLoop also checks whether already active threads are finished.
     * @throws ClassNotFoundException if the Processqueue could not load the database driver
     * @throws SQLException if the processqueue could not retrieve information from the database
     */
    
    public void mainLoop()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException, InterruptedException, Exception {
        log.debug( "PTIPoolAdm mainloop" );

        // creates initial timestamp. creates it with an offset - so
        // when the loop is entered it polls the processqueue
        // immediatly
        long stamp = System.currentTimeMillis() - ( sleepInMilliSec+ 1 ) ;

        /** /todo: we need a nicer way to do this than a while true loop. */
        while(true){

            if( System.currentTimeMillis() > stamp+sleepInMilliSec ){
                log.info( "Poll processqueue" );
                
                startThreads();
                stamp = System.currentTimeMillis();
            
            }

            try{
                checkThreads();
            }
            catch(ClassNotFoundException cne){
                throw new ClassNotFoundException( cne.getMessage() );
            }
            catch(SQLException sqe){
                throw new SQLException( sqe.getMessage() );
            }
            catch(NoSuchElementException nse){
                throw new NoSuchElementException( nse.getMessage() );
            }
            catch(InterruptedException ie){
                throw new InterruptedException( ie.getMessage() );
            }
            catch(Exception e){// Catching ExecutionException
                throw new Exception( e.getMessage() );
            }

        }
    }

    /**
     * Iterates through the activeThreads vector and remove all entries
     * where the associated thread is done. The entries are also
     * committed to the processqueue, which effectivly removes them
     */
    private void checkThreads()throws ClassNotFoundException, SQLException, NoSuchElementException, InterruptedException, RuntimeException {
        //        log.debug( "PTIPoolAdm.checkThreads() called" );

        

        Pair<FutureTask, Integer> vectorPair = null;
        int queueID;
        FutureTask future;
        Vector removeableThreads = new Vector();

        iter = activeThreads.iterator();

        while( iter.hasNext() ){

            vectorPair = ( Pair ) iter.next();
            future = Tuple.get1(vectorPair);
            queueID = Tuple.get2(vectorPair);
            // log.debug( "checking future belonging to queueID = "+queueID );
            if( future.isDone() ){// this thread is done
                try{
                    Long processtime = (Long) future.get();
                }
                catch(ExecutionException ee){
                     Throwable cause = ee.getCause();
                     log.debug( String.format( "Catched thread error associated with queueid = %s", queueID ) );                     
                         throw new RuntimeException( cause );
                }
                try{
                    processqueue.commit( queueID );
                // }catch(NoSuchElementException nse){
                //     throw new NoSuchElementException( nse.getMessage() );
                // }
                // catch(ClassNotFoundException cne){
                //     throw new ClassNotFoundException( cne.getMessage() );
                // }catch(SQLException sqe){
                //     throw new SQLException( sqe.getMessage() );
                }catch(NullPointerException npe){
                    log.debug( String.format( "vectorPair was possibly null? vectorPair = %s", vectorPair.toString() ) );
                }

                removeableThreads.add( vectorPair );
                log.info( String.format( "job done, Committed to queue, queueID = %s", queueID ) );
            }
        }
        iter = removeableThreads.iterator();
        while( iter.hasNext() ){
            activeThreads.remove( iter.next() );
        }        

    }

    /** pop processqueue until its empty, and starts threads which
     * fetch and process data from the fedora repository. Puts thread
     * (futureTask, and queueid on activeThreads vector)
     */
    private void startThreads()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException{
        log.debug( "PTIPoolAdm.startThreads() called" );

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
            catch(NoSuchElementException nse){
                log.debug( "processqueue is empty" );
                fetchedLast = true;
            }
            // catch(ClassNotFoundException cne){
            //     throw new ClassNotFoundException( cne.getMessage() );
            // }
            // catch(SQLException sqe){
            //     throw new SQLException( sqe.getMessage() );
            // }

            if( !fetchedLast ){

                fedoraHandle = Tuple.get1(queueTriple);
                queueID = Tuple.get2(queueTriple);
                itemID = Tuple.get3(queueTriple);

                // try{
                    log.debug( String.format( "starting new thread with fedoraHandle: %s, queueID: %s and itemID: %s ", fedoraHandle, queueID, itemID ) );
                    future = PTIpool.createAndJoinThread( fedoraHandle, itemID );
                // }catch(RuntimeException re){
                //     throw new RuntimeException( re.getMessage() );
                // }catch(ConfigurationException ce){
                //     throw new ConfigurationException( ce.getMessage() );
                // }

                // add thread to active thread vector
                vectorPair = Tuple.from(future, queueID);
                activeThreads.add( vectorPair );
            }
        }
        log.debug( "End of PTIPool.startThread()" );
    }
}
