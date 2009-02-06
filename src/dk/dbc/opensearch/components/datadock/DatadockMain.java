/**
 * \file DatadockMain.java
 * \brief The DatadockMain class
 * \package datadock;
 */

package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.os.FileHandler;
import org.apache.log4j.Logger;
import org.apache.log4j.FileAppender;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.SimpleLayout;

import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.fedora.FedoraClientFactory;

import dk.dbc.opensearch.components.harvest.Harvester;
import dk.dbc.opensearch.components.harvest.FileHarvest;

import fedora.client.FedoraClient;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.Callable;
import java.io.File;
/**
 * The Main method of the datadock. It secures all necessary
 * resources for the program, starts the datadockManager and then
 * closes stdin and stdout thus closing connection to the console.
 * 
 * It also adds a shutdown hook to the JVM so orderly shutdown is
 * accompleshed when the process is killed.
 */
public class DatadockMain {

    static Logger log = Logger.getLogger("DatadockMain");
    static protected boolean shutdownRequested = false;    
    static DatadockPool datadockPool = null;
    static DatadockManager datadockManager = null;

    private static int pollTime = 1000; //POLL TIME

    /**
     * The shutdown hook. This method is called when the program catch
     * the kill signal.
     */
    static public void shutdown(){
        shutdownRequested = true;

        try{
            log.info("Shutting down.");
            datadockManager.shutdown();
        }catch(InterruptedException e){
            log.error("Interrupted while waiting on main daemon thread to complete.");
        }
        log.info("Exiting.");
    }

    /**
     * Getter method for shutdown signal.
     */
    static public boolean isShutdownRequested(){
        return shutdownRequested;
    }

    /**
     * Daemonizes the program, ie. disconnects from the console and
     * creates a pidfile.
     */
    static public void daemonize(){
        FileHandler.getFile( System.getProperty("daemon.pidfile") ).deleteOnExit();
        System.out.close();
        System.err.close();
    }

    /**
     * Adds the shutdownhook.
     */
    static protected void addDaemonShutdownHook(){
        Runtime.getRuntime().addShutdownHook( new Thread() { public void run() { shutdown(); }});
    }

    /**
     * The datadocks main method.  
     * Starts the datadock and starts the datadockManager.
     */
    static public void main(String[] args){
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());
        try{
            log.removeAppender( "RootConsoleAppender" );
            log.addAppender(startupAppender);


            /** -------------------- setup and start the datadockmanager -------------------- **/
            
            log.info("Starting the datadock");

            // todo: skal lægges i konfigurationsfil
            int queueSize = 10;
            int corePoolSize = 2;
            int maxPoolSize = 5;
            long keepAliveTime = 10;

            log.debug( "initializing resources" );
            // DB access
            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();               
            // Fedora access
            FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();
            FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );      

            log.debug( "Starting datadockPool" );
            // datadockpool
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( queueSize );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );
            datadockPool = new DatadockPool( threadpool, estimate, processqueue, fedoraHandler );

            log.debug( "Starting harvester" );
            // harvester;
            File harvestDirectory = new File( "/home/shm/opensearch/trunk/Harvest-pollTest/" );
            Harvester harvester = new FileHarvest( harvestDirectory ); 
            
            
            log.debug( "Starting the manager" );
            // Starting the manager
            
            datadockManager = new DatadockManager( datadockPool, harvester );

            /** --------------- setup and startup of the datadockmanager done ---------------- **/

            log.debug( "Daemonizing" );
            daemonize();
            addDaemonShutdownHook();

        }catch (Throwable e){
            System.out.println("Startup failed."+e);
            log.fatal("Startup failed.",e);
        }finally{
            log.removeAppender(startupAppender);
        }

        while(!isShutdownRequested()){
            try{
                datadockManager.update();                
                Thread.currentThread().sleep( pollTime );
            }
            catch(InterruptedException ie){
                log.error("InterruptedException caught in mainloop: ");
                log.error("  "+ie.getMessage() );
            }
            catch(RuntimeException re){
                log.error("RuntimeException caught in mainloop: "+ re);
                log.error("  "+re.getCause().getMessage() );
                throw re;
            }
            catch(Exception e){
                log.error("Exception caught in mainloop: "+ e);
                log.error("  "+e.getMessage() );
            }
        }
    }
}
