/**
 * \file DataDockPool.java
 * \brief The DataDockPool class
 * \package datadock
 */
package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.util.concurrent.Executors;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

/**
 * \ingroup datadock
 * \brief The pool controls the datadock threads
 */
public class DataDockPool {

    //private boolean initialised; /** tells whether the pool is initialised */
    private ExecutorService threadExecutor; /** The threadpool */
    private Estimate estimate;
    private Processqueue processqueue;
    private FedoraHandler fedoraHandler;
    Logger log = Logger.getLogger("DataDockPool");

    /**
     * Constructor
     * Checks that the number of threads the threadpool is instantiated with
     * is legal and initializes a FixedThreadPool
     * \see java.util.concurrent.FixedThreadPool
     *
     * @param numberOfThreads Number of concurrent threads in the thread pool.
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     *
     * @throws IllegalArgumentException if the threadpool is tried initialized with no threads
     */
   public DataDockPool( int numberOfThreads, Estimate estimate, Processqueue processqueue, FedoraHandler fedoraHandler ) throws IllegalArgumentException{
        
        this.estimate = estimate;
        this.processqueue = processqueue;
        this.fedoraHandler = fedoraHandler;
        
        if ( numberOfThreads <= 0 ){
            throw new IllegalArgumentException( "refusing to construct empty pool" );
        }        
        threadExecutor = Executors.newFixedThreadPool(numberOfThreads);
    }
    

    /**
     * creates and joins a DataDock thread to the workerthreads in the pool
     * returns an estimate for the processing time of the data
     * needs the cargocontainer as an argument
     * This method should have a more telling name form the callers
     * point of view
     *
     * @param cc The cargo to be processed
     *
     * @returns the FutureTask with the (future) return value of the DataDock calls
     *
     * @throws IllegalArgumentException if the threadpool is not initialized
     * @throws ConfigurationException if the DataDock could not be initialized
     * @throws ClassNotFoundException if the database could not be initialised
     */
    public FutureTask createAndJoinThread( CargoContainer cc )throws IllegalArgumentException, ConfigurationException, ClassNotFoundException{
        log.info( String.format( "Creating the FutureTask with a DataDock" ) );
        FutureTask future = new FutureTask(new DataDock(cc, estimate, processqueue, fedoraHandler ));
        
        log.debug( "submit the thread to the pool" );
        threadExecutor.submit(future);
        log.debug( "return the FutureTask to the caller" );
        return future;
    }
}