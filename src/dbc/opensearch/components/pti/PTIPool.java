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
import org.apache.commons.configuration.ConfigurationException;


public class PTIPool {

    private ExecutorService ThreadExecutor; /**The threadpool */
    private static volatile Compass theCompass;

    /**
     * log
     */
    private static final Logger log = Logger.getRootLogger();

    public PTIPool( int numberOfThreads ) throws ConfigurationException {

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
        
        log.info( "The PTIPool has been constructed" );
       
    }   

    /**
     * createAndJoinThread takes a handle to the fedora base and and a Compass session. 
     * The it starts a PTI (callable) that extracts the data. the handle points to, 
     * from the fedora base, index it and store it. The return value is the handle, 
     * that the PTIPoolAdm uses for keeping track of which digitalobjects 
     * are in process 
     */

    /** \todo find better Exception to throw*/
   
    public FutureTask createAndJoinThread (String fHandle )throws RuntimeException, ConfigurationException{
        
        CompassSession session = null;
        FutureTask future = null;
        try{
        session = getSession();

        future = new FutureTask( new PTI( session, fHandle ));
        }catch(RuntimeException re){
            throw new RuntimeException( re.getMessage() );
        }catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }
                
        ThreadExecutor.submit(future);
        return future;
    }       


    /**
     * Returns a Compass session, and checks whether its instantiated.
     */
    public CompassSession getSession() throws RuntimeException {
        if( theCompass == null) {
            log.fatal( String.format( "getSession was called on an object that in the meantime went null. Aborting" ) );
            throw new RuntimeException( "getSession was called on an object that in the meantime went null. Aborting" );
        }
        CompassSession s = theCompass.openSession();
        return s;
    }

}
