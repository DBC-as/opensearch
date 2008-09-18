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
import java.util.concurrent.*;

import dbc.opensearch.components.tools.*;
import dbc.opensearch.components.tools.tuple.Tuple;
import dbc.opensearch.components.tools.tuple.Pair;

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

    public PTIPoolAdm()throws ConfigurationException, RuntimeException, NoSuchElementException, SQLException, ClassNotFoundException, InterruptedException, Exception{

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
        
        try{
        mainLoop();
        }
        catch(InterruptedException ie){
            throw new InterruptedException( ie.getMessage() );
        }
        catch(Exception ee){
            throw new Exception( ee.getMessage() );
        }
        
    }

    private void mainLoop()throws ClassNotFoundException, SQLException, RuntimeException, ConfigurationException, InterruptedException, Exception {
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
            catch(InterruptedException ie){
                throw new InterruptedException( ie.getMessage() );
            }
            catch(Exception ee){// Catching ExecutionException
                throw new Exception( ee.getMessage() );
            }

        }
    }

    /**
     * Iterates through the activeThreads vector and remove all entrys
     * where the associated thread is done. The entrys are also
     * committed to the processqueue, which effectivly removes them
     */

    private void removeThreads()throws ClassNotFoundException, SQLException, NoSuchElementException, InterruptedException, Exception {
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
                log.debug( "Commiting to queue" );
                long processtime = 0l;
                
                try{
                    processtime = (Long) future.get();
                    processqueue.commit( queueID );
                }
                catch(NoSuchElementException nse){
                    throw new NoSuchElementException( nse.getMessage() );
                }
                catch(ClassNotFoundException cne){
                    throw new ClassNotFoundException( cne.getMessage() );
                }catch(SQLException sqe){
                    throw new SQLException( sqe.getMessage() );
                }catch(InterruptedException ie){
                    throw new InterruptedException( ie.getMessage() );
                }
                catch(Exception ee){// Catching ExecutionException
                    throw new Exception( ee.getMessage() );
                }

                log.debug( "Commiting to queue" );
                
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
