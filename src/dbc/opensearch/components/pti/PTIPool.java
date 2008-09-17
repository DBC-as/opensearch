package dbc.opensearch.components.pti;

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
//import org.compass.core.CompassSession
//import org.compass.core.CompassSession

public class PTIPool {

    private boolean initialised;
    private ExecutorService ThreadExecutor; /**The threadpool */
    private static volatile Compass theCompass;
    /**
     * log
     */
    private static final Logger log = Logger.getRootLogger();

    public PTIPool( int numberOfThreads ){
        // Securing nuberOfThreads > 0
        if ( numberOfThreads <= 0 ){
            /** \todo Find suitable exception */
            throw new Exception( "Refusing to construct empty PTIPool" );
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
        
        initialised = true;
        log.info( "The PTIPool has been constructed" );
    }   
    public FutureTask createAndjoinThread (){
        if( !initialised){
            throw new Exception("Trying to start a PTIThread without constructing the PTIPool");
        }
        CompassSession session = getSession();
        FutureTask future = new FutureTask( new PTI( session ));

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

}
