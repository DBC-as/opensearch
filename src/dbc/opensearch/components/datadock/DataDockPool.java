package dbc.opensearch.components.datadock;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

public class DataDockPool {

    private boolean initialised; /** tells whether the pool is initialised */
    private ExecutorService threadExecutor; /** The threadpool */

    // private String[][] NSpidArray; for the management of the pids 
    // for storage in Fedora 

    Logger log = Logger.getLogger("DataDockPool");

    /**
     * Constructor
     * Checks that the number of threads the threadpool is instantiated with
     * is legal and initializes a FixedThreadPool
     * \see java.util.concurrent.FixedThreadPool
     * @throws IllegalArgumentException if the threadpool is tried initialized with no threads
     */
    DataDockPool( int numberOfThreads ) throws IllegalArgumentException{
        if ( numberOfThreads <= 0 ){
            throw new IllegalArgumentException( "refusing to construct empty pool" );
        }        
        threadExecutor = Executors.newFixedThreadPool(numberOfThreads);
        /** \todo: is this boolean useful? */
        initialised = true;
    }
    

    /**
     * creates and joins a DataDock thread to the workerthreads in the pool
     * returns an estimate for the processing time of the data
     * needs the cargocontainer as an argument
     * This method should have a more telling name form the callers
     * point of view
     * @returns the FutureTask with the (future) return value of the DataDock calls
     * @throws IllegalArgumentException if the threadpool is not initialized
     * @throws ConfigurationException if the DataDock could not be initialized
     */
    public FutureTask createAndJoinThread( CargoContainer cc )throws IllegalArgumentException, ConfigurationException, ClassNotFoundException{
        /** \todo: this boolean can only and always be true */
        if(!initialised){
            throw new IllegalArgumentException("trying to create a thread without a threadpool");
        }

        //String pid = getnextPid( cc.getSubmitter() );
        // FutureTask future = new FutureTask(new DataDock(cc), pid);
        log.info( String.format( "Creating the FutureTask with a DataDock" ) );
        FutureTask future = new FutureTask(new DataDock(cc));
        
        log.debug( String.format( "join the thread to the pool" ) );
        threadExecutor.submit(future);
        log.debug( String.format( "return the FutureTask to the caller" ) );
        return future;
    }
    /**
     * get the next pid for a namespace. If there are no pid's available for 
     * the namespace in question, it will retrive 20 more from the fedorabase 
     * and store them in the NSpidArray, that manages the pids for the 
     * namespaces
     * @returns the next pid from the fedorabase as a String
     */
    private String getNextPid (String nameSpace){
        String returnPid = "";
        // 10: if pid available for namespace, get one into returnPid
        // 15: delete pid from that namespace 
        // 20: if no pid available for namespace get 21 from the fedorabase
        // write 20 of them into the NSpidArray, write the 21th. into returnPid
        // 30: return the pid
        return returnPid;
    }

}