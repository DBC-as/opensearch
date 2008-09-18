package dbc.opensearch.components.pti;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
//import java.util.NoSuchElementException;
//import java.lang.ClassNotFoundException;
import java.util.Vector;
import java.util.Iterator;

import dbc.opensearch.components.tools.*;

import dbc.opensearch.components.tools.tuple.Tuple;
import dbc.opensearch.components.tools.tuple.Pair;

import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;




public class PTIPoolAdm {

    private static final Logger log = Logger.getRootLogger();

    /**
     * Variables to hold configuration parameters
     */

    private static String driver = "";
    private static String url = "";
    private static String userID = "";
    private static String passwd = "";

    private Processqueue processqueue;
    private PTIPool PTIpool;

    private Vector activeThreads;
    private Iterator iter;

    private long sleepInMilleSec;


    public PTIPoolAdm()throws ConfigurationException, RuntimeException, NoSuchElementException, SQLException, ClassNotFoundException{


        log.debug( "PTIPoolAdm Constructor" );

        log.debug( "Obtain config paramaters");

        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            log.fatal( "ConfigurationException: " + cex.getMessage() );
            System.exit(0);
        }

        driver = config.getString( "database.driver" );
        url    = config.getString( "database.url" );
        userID = config.getString( "database.userID" );
        passwd = config.getString( "database.passwd" );

        log.debug( "driver: "+driver );
        log.debug( "url:    "+url );
        log.debug( "userID: "+userID );

        /** todo: where should sleepInMilleSec be set?? in a configuration file or what*/
        sleepInMilleSec= 2;
        //
        try{
            PTIpool = new PTIPool(10);
            processqueue = new Processqueue();
        }
        catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }
        catch(RuntimeException re){
            throw new RuntimeException( re.getMessage() );
        }

        activeThreads = new Vector();

        mainLoop();
    }

    private void mainLoop()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException{
        try{
            startThreads();
        }
        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException( cne.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }
        catch(RuntimeException re){
            throw new RuntimeException( re.getMessage() );
        }
        catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }

        long stamp = System.currentTimeMillis();

        while(true){

            if( System.currentTimeMillis() > stamp+sleepInMilleSec ){
                // poll processqueue again

                try{
                    startThreads();
                }
                catch(ClassNotFoundException cne){
                    throw new ClassNotFoundException( cne.getMessage() );
                }
                catch(SQLException sqe){
                    throw new SQLException( sqe.getMessage() );
                }
                catch(RuntimeException re){
                    throw new RuntimeException( re.getMessage() );
                }
                catch(ConfigurationException ce){
                    throw new ConfigurationException( ce.getMessage() );
                }

                stamp = System.currentTimeMillis();
            }

            try{
                removeThreads();
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
        }

    }

    /**
     * Iterates through the activeThreads vector and remove all entrys
     * where the associated thread is done. The entrys are also
     * committed to the processqueue, which effectivly removes them
     */

    private void removeThreads()throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "PTIPoolAdm.removeThreads() called" );

        Pair<FutureTask, Integer> vectorPair = null;
        int queueID;
        FutureTask future;

        iter = activeThreads.iterator();
        while( iter.hasNext() ){
            vectorPair = ( Pair ) iter.next();
            future = Tuple.get1(vectorPair);
            queueID = Tuple.get2(vectorPair);

            if( future.isDone() ){// this thread is done
                log.debug( "thread is done... associated queueID = "+queueID );
                try{
                    processqueue.commit( queueID );
                }
                catch(NoSuchElementException nse){
                    throw new NoSuchElementException( nse.getMessage() );
                }
                catch(ClassNotFoundException cne){
                    throw new ClassNotFoundException( cne.getMessage() );
                }catch(SQLException sqe){
                    throw new SQLException( sqe.getMessage() );
                }
                activeThreads.remove( vectorPair );
            }
        }


    }

    /** pop processqueue until its empty, and starts threads which
     * fetch and process data from the fedora repository. Puts thread
     * (futureTask, and queueid on activeThreads vector)
     */

    private void startThreads()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException{
        log.debug( "PTIPoolAdm.startThreads() called" );

        boolean fetchedLast = false;
        Pair<String, Integer> queuePair = null;
        Pair<FutureTask, Integer> vectorPair = null;
        String fedoraHandle;
        int queueID;
        FutureTask future;


        while( !fetchedLast ){

            try{
                queuePair = processqueue.pop();
            }
            catch(NoSuchElementException nse){
                log.debug( "processqueue is empty" );
                fetchedLast = true;
            }
            catch(ClassNotFoundException cne){
                throw new ClassNotFoundException( cne.getMessage() );
            }
            catch(SQLException sqe){
                throw new SQLException( sqe.getMessage() );
            }

            if( !fetchedLast ){

                fedoraHandle = Tuple.get1(queuePair);
                queueID = Tuple.get2(queuePair);

                try{
                    log.debug( "starting new thread with fedoraHandle: "+fedoraHandle+" and queueID: "+queueID );
                    future = PTIpool.createAndJoinThread(fedoraHandle );
                }catch(RuntimeException re){
                    throw new RuntimeException( re.getMessage() );
                }catch(ConfigurationException ce){
                    throw new ConfigurationException( ce.getMessage() );
                }

                // add thread to active thread vector
                vectorPair = Tuple.from(future, queueID);
                activeThreads.add( vectorPair );
            }
        }
        log.debug( "End of PTIPool.startThread()" );
    }




}

// package dbc.opensearch.components.pti;

// import dbc.opensearch.components.tools.tuple.Tuple;
// import dbc.opensearch.components.tools.tuple.Pair;
// import dbc.opensearch.components.tools.Processqueue;

// import java.util.concurrent.Executors;
// import java.util.concurrent.ExecutorService;
// import java.util.concurrent.*;
// import org.apache.log4j.xml.DOMConfigurator;
// import org.apache.log4j.Logger;
// import org.compass.core.Compass;
// import org.compass.core.CompassSession;
// import org.compass.core.config.CompassConfiguration;
// import org.compass.core.config.CompassConfigurationFactory;
// import org.compass.core.CompassException;
// import java.net.URL;
// import java.io.File;
// import java.io.IOException;
// import java.sql.SQLException;
// import java.util.NoSuchElementException;
// //import org.compass.core.CompassSession
// //import org.compass.core.CompassSession
// import org.apache.commons.configuration.ConfigurationException;
// import java.util.Vector;
// import java.util.Iterator;

// public class PTIPool {

//     Vector activeThreads = new Vector();
//     Iterator iter;
//     private boolean initialised;
//     private ExecutorService ThreadExecutor; /**The threadpool */
//     private static volatile Compass theCompass;
//     private Processqueue queue;
//     /**
//      * log
//      */
//     private static final Logger log = Logger.getRootLogger();

//     public PTIPool( int numberOfThreads ) throws ConfigurationException, RuntimeException, NoSuchElementException, SQLException, ClassNotFoundException{
//         // Securing nuberOfThreads > 0
//         if ( numberOfThreads <= 0 ){
//             /** \todo Find suitable exception */
//             throw new ConfigurationException( "Refusing to construct empty PTIPool" );
//         }
//         //Starting the threadPool
//         ThreadExecutor = Executors.newFixedThreadPool( numberOfThreads );

//         //Setting up the Compass
//         if( theCompass == null ){
//             CompassConfiguration conf = new CompassConfiguration();

//             URL cfg = getClass().getResource("/compass.cfg.xml");
//             URL cpm = getClass().getResource("/xml.cpm.xml");
//             log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
//             log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );

//             File cpmFile = new File( cpm.getFile() );

//             conf.configure( cfg );
//             conf.addFile( cpmFile );

//             theCompass = conf.buildCompass();
//         }

//         log.info( "Constructing the processqueue" );
//         try{
//             queue = new Processqueue();
//         }catch(ConfigurationException ce){
//             throw new ConfigurationException( ce.getMessage() );
//         }

//         initialised = true;
//         log.info( "The PTIPool has been constructed" );
//         log.info( "The PTIPool starts to pol the processqueue" );
//         try{
//             pol();
//         }catch(ClassNotFoundException cnfe){
//             throw new ClassNotFoundException( cnfe.getMessage() );
//         }catch( SQLException sqle ){
//             throw new SQLException( sqle.getMessage() );
//         }catch( NoSuchElementException nsee ){
//             throw new NoSuchElementException( nsee.getMessage() );
//         }catch(RuntimeException re){
//             throw new RuntimeException( re.getMessage() );
//         }catch(ConfigurationException ce){
//             throw new ConfigurationException( ce.getMessage() );
//         }
//     }

//     private void mainLoop(){

//     }

//     /** \todo find better Exception to throw*/
//     /** \todo find out whether we need a return value at all */
//     public FutureTask createAndJoinThread (String fHandle )throws RuntimeException, ConfigurationException{
//         if( !initialised){
//             throw new ConfigurationException("Trying to start a PTIThread without constructing the PTIPool");
//         }
//         CompassSession session = null;
//         FutureTask future = null;
//         try{
//         session = getSession();
//         future = new FutureTask( new PTI( session, fHandle ));
//         }catch(RuntimeException re){
//             throw new RuntimeException( re.getMessage() );
//         }catch(ConfigurationException ce){
//             throw new ConfigurationException( ce.getMessage() );
//         }


//         ThreadExecutor.submit(future);
//         return future;
//     }


//     /**
//      * Returns a Compass session, and check whether its instantiated.
//      */
//     public CompassSession getSession() throws RuntimeException {
//         if( theCompass == null) {
//             log.fatal( String.format( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" ) );
//             throw new RuntimeException( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" );
//         }
//         CompassSession s = theCompass.openSession();
//         return s;
//     }
//     public void pol() throws RuntimeException, ConfigurationException, ClassNotFoundException, SQLException, NoSuchElementException {
//         Pair<String, Integer> handlePair;
//         String fHandle;
//         int queueID;
//         FutureTask future;// dont quite know if we need any return value
//         boolean hat = false; // please kill me
//         boolean ged = false; // please kill me


//         while(hat){
//             //does something shm defines ;-)
//             if(ged){
//                 try{
//                 handlePair = queue.pop();
//                 }catch(ClassNotFoundException cnfe){
//                     throw new ClassNotFoundException( cnfe.getMessage() );
//                 }catch( SQLException sqle ){
//                     throw new SQLException( sqle.getMessage() );
//                 }catch( NoSuchElementException nsee ){
//                     throw new NoSuchElementException( nsee.getMessage() );
//                 }
//                 fHandle = Tuple.get1(handlePair);
//                 queueID = Tuple.get2(handlePair);
//                 try{
//                     future = createAndJoinThread( fHandle );
//                 }catch(RuntimeException re){
//                     throw new RuntimeException( re.getMessage() );
//                 }catch(ConfigurationException ce){
//                     throw new ConfigurationException( ce.getMessage() );
//                 }
//             }
//         }
//     }

//     private void removeThread(){
//         iter = new activeThreads.iterator();


//     }

//     /** pop processqueue until its empty, and starts threads which
//      * fetch and process data from the fedora repository. Puts thread
//      * (futureTask, and queueid on activeThreads vector)
//      */

//     private void startThreads()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException{

//         boolean fetchedLast = false;
//         Pair<String, Integer> queuePair = null;
//         Pair<FutureTask, Integer> vectorPair = null;
//         String fedoraHandle;
//         int queueID;
//         FutureTask future;


//         while( !fetchedLast ){

//             try{
//                 queuePair = queue.pop();
//             }
//             catch(NoSuchElementException nse){
//                 // processqueue is empty
//                 fetchedLast = true;
//             }
//             catch(ClassNotFoundException cne){
//                 throw new ClassNotFoundException( cne.getMessage() );
//             }
//             catch(SQLException sqe){
//                 throw new SQLException( sqe.getMessage() );
//             }

//             fedoraHandle = Tuple.get1(queuePair);
//             queueID = Tuple.get2(queuePair);

//             try{
//                 future = createAndJoinThread(fedoraHandle );
//             }catch(RuntimeException re){
//                 throw new RuntimeException( re.getMessage() );
//             }catch(ConfigurationException ce){
//                 throw new ConfigurationException( ce.getMessage() );
//             }

//             // add thread to active thread vector
//             vectorPair = Tuple.from(future, queueID);
//             activeThreads.add( vectorPair );
//         }
//     }

// }
