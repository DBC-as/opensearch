package dbc.opensearch.components.pti;

import dbc.opensearch.components.tools.tuple.Tuple;
import dbc.opensearch.components.tools.tuple.Pair;
import dbc.opensearch.components.tools.Processqueue;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.CompassException;
import java.net.URL;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
//import org.compass.core.CompassSession
//import org.compass.core.CompassSession
import org.apache.commons.configuration.ConfigurationException;


public class PTIPool {

    private boolean initialised;
    private ExecutorService ThreadExecutor; /**The threadpool */
    private static volatile Compass theCompass;
    private Processqueue queue;
    /**
     * log
     */
    private static final Logger log = Logger.getRootLogger();

    public PTIPool( int numberOfThreads ) throws ConfigurationException/* , RuntimeException, NoSuchElementException, SQLException, ClassNotFoundException*/{
        // Securing nuberOfThreads > 0
        if ( numberOfThreads <= 0 ){
            /** \todo Find suitable exception */
            throw new ConfigurationException( "Refusing to construct empty PTIPool" );
        }
        //Starting the threadPool
        ThreadExecutor = Executors.newFixedThreadPool( numberOfThreads );
        
        //Setting up the Compass
        if( theCompass == null ){
            CompassConfiguration conf = new CompassConfiguration();
            
            URL cfg = getClass().getResource("/compass.cfg.xml"); 
            URL cpm = getClass().getResource("/xml.cpm.xml"); 
            log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
            log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );
            
            File cpmFile = new File( cpm.getFile() );
            
            conf.configure( cfg );
            conf.addFile( cpmFile );
            
            theCompass = conf.buildCompass();   
        }
        
        log.info( "Constructing the processqueue" );
        try{
            queue = new Processqueue();
        }catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }
        
        initialised = true;
        log.info( "The PTIPool has been constructed" );
        /**obsolete
        log.info( "The PTIPool starts to pol the processqueue" );
        try{
            pol();
        }catch(ClassNotFoundException cnfe){
            throw new ClassNotFoundException( cnfe.getMessage() );
        }catch( SQLException sqle ){
            throw new SQLException( sqle.getMessage() );
        }catch( NoSuchElementException nsee ){
            throw new NoSuchElementException( nsee.getMessage() );
        }catch(RuntimeException re){
            throw new RuntimeException( re.getMessage() );
        }catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }

        */
    }   
    /** \todo find better Exception to throw*/
    /** \todo find out whether we need a return value at all */
    public FutureTask createAndJoinThread (String fHandle, int queueID)throws RuntimeException, ConfigurationException{
        if( !initialised){
            throw new ConfigurationException("Trying to start a PTIThread without constructing the PTIPool");
        }
        CompassSession session = null;
        FutureTask future = null;
        try{
        session = getSession();
        future = new FutureTask( new PTI( session, fHandle, queueID  ));
        }catch(RuntimeException re){
            throw new RuntimeException( re.getMessage() );
        }catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }
        
        
        ThreadExecutor.submit(future);
        return future;
    }       


    /**
     * Returns a Compass session, and check whether its instantiated.
     */
    public CompassSession getSession() throws RuntimeException {
        if( theCompass == null) {
            log.fatal( String.format( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" ) );
            throw new RuntimeException( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" );
        }
        CompassSession s = theCompass.openSession();
        return s;
    }

    /** obsolete
    public void pol() throws RuntimeException, ConfigurationException, ClassNotFoundException, SQLException, NoSuchElementException {
        Pair<String, Integer> handlePair;
        String fHandle;
        int queueID;
        FutureTask future;// dont quite know if we need any return value
        boolean hat = false; // please kill me
        boolean ged = false; // please kill me


        while(hat){
            //does something shm defines ;-)
            if(ged){
                try{
                handlePair = queue.pop();
                }catch(ClassNotFoundException cnfe){
                    throw new ClassNotFoundException( cnfe.getMessage() );
                }catch( SQLException sqle ){
                    throw new SQLException( sqle.getMessage() );
                }catch( NoSuchElementException nsee ){
                    throw new NoSuchElementException( nsee.getMessage() );
                }
                fHandle = Tuple.get1(handlePair);
                queueID = Tuple.get2(handlePair);
                try{
                    future = createAndJoinThread(fHandle, queueID);
                }catch(RuntimeException re){
                    throw new RuntimeException( re.getMessage() );
                }catch(ConfigurationException ce){
                    throw new ConfigurationException( ce.getMessage() );
                }
            }
        }
    }
    */
}
