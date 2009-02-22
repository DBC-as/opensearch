/**
 * \file PTIMain.java
 * \brief The PTIMain class
 * \package pti;
 */

package dk.dbc.opensearch.components.pti;

//import dk.dbc.opensearch.common.fedora.FedoraClientFactory;
//import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.statistics.Estimate;

import dk.dbc.opensearch.common.compass.CompassFactory;

import org.compass.core.Compass;
//import fedora.client.FedoraClient;

import java.io.File;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import java.net.URL;

import dk.dbc.opensearch.common.db.Processqueue;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
/**
 * The Main method of the PTI. It secures all necessary
 * resources for the program, starts the PTIManager and then
 * closes stdin and stdout thus closing connection to the console.
 *
 * It also adds a shutdown hook to the JVM so orderly shutdown is
 * accompleshed when the process is killed.
 */
public class PTIMain
{
    static Logger log = Logger.getLogger("PTIMain");
    static protected boolean shutdownRequested = false;
    static PTIPool ptiPool = null;
    static PTIManager ptiManager = null;

    static XMLConfiguration config = null;
    static int queueSize;
    static int corePoolSize;
    static int maxPoolSize;
    static long keepAliveTime;
    static int pollTime;
    static URL cfgURL;

    public PTIMain()  throws ConfigurationException{

        cfgURL = getClass().getResource("/config.xml");

        config = new XMLConfiguration( cfgURL );

        pollTime = config.getInt( "pti.main-poll-time" );
        queueSize = config.getInt( "pti.queuesize" );
        corePoolSize = config.getInt( "pti.corepoolsize" );
        maxPoolSize = config.getInt( "pti.maxpoolsize" );
        keepAliveTime = config.getInt( "pti.keepalivetime" );
    }


    /**
     * The shutdown hook. This method is called when the program catch
     * the kill signal.
     */
    static public void shutdown()
    {
        shutdownRequested = true;

        try{
            log.info("Shutting down.");
            ptiManager.shutdown();
        }
        catch(InterruptedException e){
            log.error("Interrupted while waiting on main daemon thread to complete.");
        }

        log.info("Exiting.");
    }


    /**
     * Getter method for shutdown signal.
     */
    static public boolean isShutdownRequested()
    {
        return shutdownRequested;
    }


    /**
     * Daemonizes the program, ie. disconnects from the console and
     * creates a pidfile.
     */
    static public void daemonize()
    {
        FileHandler.getFile( System.getProperty("daemon.pidfile") ).deleteOnExit();
        System.out.close();
        System.err.close();
    }


    /**
     * Adds the shutdownhook.
     */
    static protected void addDaemonShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread() { public void run() { shutdown(); }});
    }

    /**
     * The PTIs main method.
     * Starts the PTI and starts the PTIManager.
     */
    static public void main(String[] args)
    {
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

        try{
            PTIMain ptimain = new PTIMain();
            log.removeAppender( "RootConsoleAppender" );
            log.addAppender(startupAppender);

            /** -------------------- setup and start the PTImanager -------------------- **/
            log.info("Starting the PTI");

            log.debug( "initializing resources" );

            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();

            //FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();
            //FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            //FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );

            CompassFactory compassFactory = new CompassFactory();
            Compass compass = compassFactory.getCompass();

            log.debug( "Starting PTIPool" );
            // PTIpool
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( queueSize );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );
            PTIPool ptiPool = new PTIPool( threadpool, estimate, //fedoraHandler, 
                                           compass );

            ptiManager = new PTIManager( ptiPool, processqueue );

            /** --------------- setup and startup of the PTImanager done ---------------- **/
            log.debug( "Daemonizing" );

            daemonize();
            addDaemonShutdownHook();

        }catch (Throwable e){
            System.out.println("Startup failed." + e);
            log.fatal("Startup failed.",e);
        }
        finally
            {
                log.removeAppender(startupAppender);
            }

        while(!isShutdownRequested())
            {// Mainloop
                try
                    {
                        ptiManager.update();
                        Thread.currentThread().sleep( pollTime );
                    }
                catch(InterruptedException ie)
                    {
                        log.error("InterruptedException caught in mainloop: ");
                        log.error("  "+ie.getMessage() );
                    }
                catch(RuntimeException re)
                    {
                        log.error("RuntimeException caught in mainloop: " + re);
                        log.error("\n" + re.getCause().getMessage() );
                        log.error("\n" + re.getCause().getStackTrace() );
                        throw re;
                    }
                catch(Exception e)
                    {
                        log.error("Exception caught in mainloop: " + e);
                        log.error("  " + e.getMessage() );
                    }
            }
    }
}
