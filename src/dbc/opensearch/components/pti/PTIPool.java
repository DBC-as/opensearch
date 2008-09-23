package dbc.opensearch.components.pti;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.FedoraHandler;

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
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;


public class PTIPool {

    private ExecutorService threadExecutor; /**The threadpool */
    private static volatile Compass theCompass;
    private static volatile FedoraHandler theFedoraHandler;
    /**
     * log
     */
    private static final Logger log = Logger.getRootLogger();

    public PTIPool( int numberOfThreads ) throws ConfigurationException {

        log.debug( String.format( "Number of threads = %s", numberOfThreads ) );
        // Securing nuberOfThreads > 0
        if ( numberOfThreads <= 0 ){
            /** \todo Find suitable exception */
            log.fatal( String.format( "Number of threads specified was 0, quite absurd." ) );
            throw new ConfigurationException( "Refusing to construct empty PTIPool" );
        }
        
        log.debug( String.format( "Starting the threadPool" ) );
        threadExecutor = Executors.newFixedThreadPool( numberOfThreads );

        log.debug( String.format( "Setting up the Compass object" ) );
        if( theCompass == null ){
            CompassConfiguration conf = new CompassConfiguration();


            /** \todo: FIXME hardcoded values. Should come from startup config file */
            URL cfg = getClass().getResource("/compass.cfg.xml");
            URL cpm = getClass().getResource("/xml.cpm.xml");
            log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
            log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );

            File cpmFile = new File( cpm.getFile() );

            conf.configure( cfg );
            conf.addFile( cpmFile );

            theCompass = conf.buildCompass();
        }

        log.debug( String.format( "Setting up the FedoraHandler" ) );
        if( theFedoraHandler == null) {
            theFedoraHandler = new FedoraHandler();
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

    public FutureTask createAndJoinThread (String fHandle, String itemID )throws ConfigurationException, ClassNotFoundException{

        CompassSession session = null;
        FutureTask future = null;
        // try{
            log.debug( String.format( "Getting CompassSession" ) );
            session = getSession();
            log.debug( String.format( "Constructing FutureTask on PTI" ) );
            future = new FutureTask( new PTI( session, fHandle, itemID, theFedoraHandler ));
        // }catch(RuntimeException re){
        //     log.fatal( String.format( "RunTimeException occured in createAndJoinThread" ) );
        //     throw new RuntimeException( re.getMessage() );
        // }catch(ConfigurationException ce){
        //     log.fatal( String.format( "ConfigurationException occured in createAndJoinThread" ) );
        //     throw new ConfigurationException( ce.getMessage() );
        // }

        log.debug( String.format( "Submitting the FutureTask to the threads" ) );
        threadExecutor.submit(future);
        log.debug( String.format( "Returning the FutureTask" ) );
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
        log.debug( String.format( "returning compass session %s", s.getSettings().toString() ) );
        return s;
    }

}
