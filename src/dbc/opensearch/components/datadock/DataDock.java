package dbc.opensearch.components.datadock;

import dbc.opensearch.tools.FedoraHandler;

import dbc.opensearch.tools.Estimate;

import dbc.opensearch.tools.Processqueue;


import java.sql.Connection;

import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import fedora.server.errors.ServerException;

import java.util.concurrent.Callable;

import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;

/**
 * \brief The public interface for the OpenSearch DataDockService
 * DataDock, together with DataDockPool, is the primary accesspoint
 * for the delivery of material to be saved in the Fedora repository
 * and processed by lucene. The DataDock interface allows clients to
 * submit data that represents a textual material to be stored in a
 * Fedora repository and indexed by Lucene. When submitted, the data
 * is validated against a dictionary of possible handlers using the
 * supplied metadata and object-information. All methods throw
 * exceptions on errors.
 *
 * \todo a schema for errors returned should be defined
 *
 * DataDock is the central class in the datadock component. This class
 * offers the service of receiving data, metadata and objectinfo for
 * later processing with lucene. The primary responsibility of the
 * DataDock is to validate incoming data and construct data-carrying
 * objects for use within OpenSearch. Furthermore, DataDock starts the
 * process of fedora storing, data processing, indexing and
 * search-capabilities
 */

public class DataDock implements Callable<Float>{
    private CargoContainer cc;
    private Processqueue queue;
    // String pid;
    private XMLConfiguration config;
    private static volatile FedoraHandler fh;
   
    private Logger log = Logger.getLogger("DataDock");

    Estimate estimate;

    /**
     * DataDock is initialized with a CargoContainer containing the
     * data to be 'docked' into to system
     */
    public DataDock( CargoContainer cargo ) throws ConfigurationException, ClassNotFoundException {
        log.debug( String.format( "Entering DataDock Constructor" ) );
        cc = cargo;
        queue = new Processqueue();
        
        estimate = new Estimate();
        log.debug( String.format( "DataDock Construction finished" ) );
    }

    /**
     * call is the thread entry method on the DataDock. Call operates
     * on the DataDock object, and all data critical for its success
     * is given at DataDock initialization. This method is used with
     * java.util.concurrent.FutureTask, which upon finalisation
     * (completion, exception or termination) will return an
     * estimation of how long time it will take to bzw. index and save
     * in fedora the data given with the CargoContainer.
     * \see dbc.opensearch.tools.Estimation
     * @returns an estimate on the completion-time of indexing and fedora submission
     * @throws SQLException if the estimate could not be retrieved from the database
     * @throws NoSuchElementException if the mimetype is unknown to the estimate method. \see dbc.opensearch.tools.Estimation.getEstimate(String, long)
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dbc.opensearch.tools.FeodraHandler
     * @throws RemoteException if the datastream could not be ingested into the fedora base
     * @throws XMLStreamException if the foxml could not be constructed in the FedoraHandler \see dbc.opensearch.tools.FedoraHandler
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dbc.opensearch.tools.Estimate
     */
    public Float call() throws SQLException, NoSuchElementException, ConfigurationException, RemoteException, XMLStreamException, IOException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException {
        log.debug( String.format( "Entering call" ) );
        Float processEstimate = 0f;

        // try{
        log.debug( String.format( "Getting estimation for a combination of mimetype '%s' and data length '%s'", cc.getMimeType(), cc.getStreamLength() ) );
        
        processEstimate = estimate.getEstimate( cc.getMimeType(), cc.getStreamLength() );
        
        
        queueFedoraHandle( fedoraStoreData(), cc.getSubmitter() );
        log.info( String.format( "data queued" ) );
        // }

        // catch(ClassNotFoundException cne){
        //     throw new ClassNotFoundException(cne.getMessage());
        // }
        // catch(ConfigurationException ce) {
        //     throw new ConfigurationException( ce.getMessage() );
        // }
        // catch(SQLException sqe) {
        //     throw new SQLException( sqe.getMessage() );
        // }
        // catch(NoSuchElementException nee) {
        //     throw new NoSuchElementException( nee.getMessage() );
        // }
        // catch(RemoteException re) {
        //     throw new RemoteException( re.getMessage() );
        // }
        // catch(XMLStreamException xe) {
        //     throw new XMLStreamException( xe.getMessage() );
        // }
        // catch(IOException  ioe) {
        //     throw new IOException( ioe.getMessage() );
        // }
        // catch( Exception e ) {
        //     throw new Exception( e.getMessage() );
        // }

        log.info( String.format( "Returning estimate = %s", processEstimate ) );
        return processEstimate;
    }

    /**
     * fedoraStoreData is an internal method for storing data given
     * with the initialization of the DataDock into a fedora base.
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dbc.opensearch.tools.FeodraHandler
     * @throws RemoteException if the datastream could not be ingested into the fedora base
     * @throws XMLStreamException if the foxml could not be constructed in the FedoraHandler \see dbc.opensearch.tools.FedoraHandler
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     */
    private String fedoraStoreData() throws ConfigurationException, RemoteException, XMLStreamException, IOException, MalformedURLException, UnknownHostException, ServiceException{
        log.debug( "Entering DataDock.fedoraStoreData" );
        String fedoraHandle = "";
        /**
         * \todo find out where and how we get the pid
         * its format is "namespace:identifier" fx "faktalink:2"
         * If we use the submitter as namespace we still need a way
         * to be sure the identifier is unique
         */
        String usePid = cc.getSubmitter();
        /**
         * \todo find out where and how we get the itemId
         */
        String itemId = cc.getMimeType().substring( cc.getMimeType().indexOf("/") + 1 );
        
        /** todo: give real applicable value to label. value should be given by cargo container*/
        String label = "test";
        // 10: open connection to fedora base
        if( fh == null ){
            // try{
                fh = new FedoraHandler();
            // }
            // catch (ConfigurationException cex){
            //     throw new ConfigurationException( cex.getMessage() );
            // }

        }else{
            log.info( String.format( "A FedoraHandler is already initialized, using it" ) );
        }

        // 20: submit data
        // try{
            // The next 2 lines of code waits for the getNextPid method from fh
            // String submitter = cargo.getSubmitter();
            //try{
            // String usePid = submitter + ":" + fh.getNextPid(submitter);
            // }catch (RemoteException re){
            // }catch(XMLStreamException xmle){
            // }catch(IOException ioe){
            //}
        fedoraHandle = fh.submitDatastream( cc, usePid, itemId, label );
        // }catch( RemoteException re ){
        //     throw new RemoteException(re.getMessage());
        // }catch( XMLStreamException xmle ){
        //     throw new XMLStreamException(xmle.getMessage());
        // }catch( IOException ioe ){
        //     throw new IOException(ioe.getMessage());
        // }catch( ServerException se ){
        //     throw new ServerException( se.getMessage() ) ;
            //        }
        //        }catch( Exception e ){
        // throw new Exception( e.getMessage() );

        // 30: deposit objectInfo as dissaminator to data
        // 40: deposit metadata as dissamminator to data
        // 50: convert handle to data from fedora to String
        // 60: return fedoraHandle
        log.info( String.format( "Ingest succeded, returning pid=%s",fedoraHandle ) );
        log.debug( "Exiting DataDock.fedoraStoreData" );
        return fedoraHandle;
    }


    public void queueFedoraHandle( String fedoraHandle, String itemID ) throws ClassNotFoundException, ConfigurationException, SQLException {
        /**
         * the push queues a fedoraHandle on the processQueue
         * to take the parameteres from the config file
         */
        log.debug( "Entering DataDock.queueFedoraHandle" );
        // 10: call push
        try{
            queue.push( fedoraHandle, itemID );

        }
        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException(cne.getMessage());
        }
        catch(SQLException sqe){
            throw new SQLException(sqe.getMessage());
        }
        log.debug( "Exiting.queueFedoraHandle" );
    }
}