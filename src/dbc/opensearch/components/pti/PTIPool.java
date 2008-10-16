package dbc.opensearch.components.pti;

import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.FedoraClientFactory;
import dbc.opensearch.tools.Estimate;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.CompassException;

import fedora.client.FedoraClient;

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

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;


/**
 * \brief PTIPool is a threadpool managing PTI threads. It has a
 * createAndJoin method for stating threads, and a private method
 * providing a compassSession
 */
public class PTIPool {

    private ExecutorService threadExecutor; /**The threadpool */
    private static Compass theCompass;
    //private static FedoraHandler theFedoraHandler;
    //private FedoraClientFactory fedoraClientFactory;
    private FedoraHandler fedoraHandler;
    Logger log = Logger.getLogger("PTIPool");

    /**
     * \brief Constructor initializes the threadpool 
     * @param numberOfThreads The number of threads in the pool
     * @throws ConfigurationException if the pool is instanciated with less than 1 thread 
     * @throws MalformedURLException Could not obtain compass configuration
     * @throws UnknownHostException error obtaining fedora configuration
     * @throws ServiceException ServiceException something went wrong initializing the fedora client
     * @throws IOException something went wrong initializing the fedora client
     */
    public PTIPool( int numberOfThreads, FedoraHandler fedoraHandler ) throws ConfigurationException, MalformedURLException, UnknownHostException, ServiceException, IOException {
        log.debug( String.format( "Entering PTIPool(NumberOfThreads=%s)", numberOfThreads ) );
        
        this.fedoraHandler = fedoraHandler;

        if ( numberOfThreads <= 0 ){
            /** \todo Find suitable exception */
            log.fatal( String.format( "Number of threads specified was 0 or less." ) );
            throw new ConfigurationException( "Refusing to construct empty PTIPool" );
        }
        
        log.debug( String.format( "Starting the threadPool" ) );
        threadExecutor = Executors.newFixedThreadPool( numberOfThreads );

        log.debug( "Setting up the Compass object" );
        if( theCompass == null ){
            log.debug( "No session found. Setting up a new one. getting parameters." );
            
            CompassConfiguration conf = new CompassConfiguration();
            URL cfg = getClass().getResource("/compass.cfg.xml");
            URL cpm = getClass().getResource("/xml.cpm.xml");
            log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
            log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );
            File cpmFile = new File( cpm.getFile() );

            conf.configure( cfg );
            conf.addFile( cpmFile );
            theCompass = conf.buildCompass();
            
            log.debug( "Compass build" );
        }        
        log.info( "The PTIPool has been constructed" );
    }

    /**
     * createAndJoinThread takes a handle to the fedora base and and a Compass session.
     * The it starts a PTI (callable) that extracts the data. the handle points to,
     * from the fedora base, index it and store it. The return value is the handle,
     * that the PTIPoolAdm uses for keeping track of which digitalobjects
     * are in process
     * @throws ConfigurationException error reading the PTI configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public FutureTask createAndJoinThread (String fHandle, String itemID, Estimate estimate )throws ConfigurationException, ClassNotFoundException{
        log.debug( String.format( "entering createAndJoinThreads( fhandle=%s, itemID=%s )", fHandle, itemID ) );
        
        CompassSession session = null;
        FutureTask future = null;
        
        log.debug( "Getting CompassSession" );

        session = getSession();
        
        log.debug( "Constructing FutureTask on PTI" );
        future = new FutureTask( new PTI( session, fHandle, itemID, fedoraHandler, estimate ));
        
        log.debug( String.format( "Submitting the FutureTask to the threads" ) );
        threadExecutor.submit(future);
        
        log.debug( "FutureTask submitted" );
        return future;
    }

    /**
     * Returns a Compass session, and checks whether its instantiated.
     * @throws RuntimeException called getSession on non-existing compass
     */
    public CompassSession getSession() throws RuntimeException {
        log.debug( "Entering getSession" );
        if( theCompass == null) {
            log.fatal( "getSession was called on an object that in the meantime went null. Aborting" );
            throw new RuntimeException( "getSession was called on an object that in the meantime went null. Aborting" );
        }
        CompassSession s = theCompass.openSession();
        log.debug( String.format( "returning compass session %s", s.toString() ) );
        return s;
    }

}
