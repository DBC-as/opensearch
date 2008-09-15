package dbc.opensearch.components.datadock;
import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.*;
import org.apache.log4j.xml.DOMConfigurator;
//import org.apache.commons.configuration.*;
import org.apache.log4j.Logger;
/**
 * 
 */
public class DataDockPool {

    private boolean initialised; /** tells whether the pool is initialised */
    private ExecutorService ThreadExecutor; /** The threadpool */
    //private XMLConfiguration config;
    // private String[][] NSpidArray; for the management of the pids 
    // for storage in Fedora 
    /**
     * Log
     */

    private static final Logger log = Logger.getRootLogger();

    /**
     * Constructor
     * Checks that the number of threads the threadpool is instantiated with
     * is legal 
     * \todo: find suitable exception(s)
     */
    DataDockPool( int numberOfThreads ) throws Exception{
        if ( numberOfThreads <= 0 ){
            /** \todo: find suitable exception */
            throw new Exception( "refusing to construct empty pool" );
        }
        
        ThreadExecutor = Executors.newFixedThreadPool(numberOfThreads);
        initialised = true;
    }
    

    /**
     * creates and joins a DataDock thread to the workerthreads in the pool
     * returns an estimate for the processing time of the data
     * needs the cargocontainer as an argument
     * This method should have a more telling name form the callers
     * point of view
     * \todo: What is the format of the data we receive
     */
    public FutureTask createAndJoinThread( CargoContainer cc )throws Exception{
        if(!initialised){
            /**\todo: find suitable exception*/
            throw new Exception("trying to create a thread without a threadpool");
        }
         // 20: create the thread, actually a Callable, the DataDock, 
        // with the argument data

        //String pid = getnextPid( cc.getSubmitter() );
        // FutureTask future = new FutureTask(new DataDock(cc), pid);
        FutureTask future = new FutureTask(new DataDock(cc));
       
        // 30: join the thread to the pool
        ThreadExecutor.submit(future);
        // 40: return the FutureTask to the caller
        return future;
        // make compiler happy:
    }
    /**
     * get the next pid for a namespace. If there are no pid's available for 
     * the namespace in question, it will retrive 20 more from the fedorabase 
     * and store them in the NSpidArray, that manages the pids for the 
     * namespaces
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