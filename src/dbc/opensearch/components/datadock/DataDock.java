package dbc.opensearch.components.datadock;

import dbc.opensearch.components.pti.FedoraHandler;

import dbc.opensearch.components.tools.*;

import dbc.opensearch.components.tools.Processqueue;


import java.sql.Connection;

import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


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
   
    /**
     * Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     * Estimate class
     */
    Estimate estimate;

    /**
     * DataDock is initialized with a CargoContainer and a configuration file for the
     * database access. The same config file is used for all the needed connections
     */
    //public DataDock( CargoContainer cargo, String pid ){

    public DataDock( CargoContainer cargo ) throws ConfigurationException{

        log.debug("DataDock Constructor");
        cc = cargo;
        queue = new Processqueue();
        
        estimate = new Estimate();

    }

    public Float call() throws SQLException, NoSuchElementException, ConfigurationException, RemoteException, XMLStreamException, IOException, ClassNotFoundException, Exception{
        log.info( String.format( "Entering DataDock.call" ) );
        Float processEstimate = 0f;

        try{
            log.info( "\n calling DataDock.estimate \n" );
            // 10: Estimate
            processEstimate = estimate.getEstimate(cc.getMimeType(), cc.getStreamLength());
            // 20: Store data in Fedora
            // 30: queue FedoraHandle
            log.info( String.format( "Estimate = %s ", processEstimate ) );
            queueFedoraHandle(fedoraStoreData());
            log.info( String.format( "data queued" ) );
        }

        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException(cne.getMessage());
        }
        catch(ConfigurationException ce) {
            throw new ConfigurationException( ce.getMessage() );
        }
        catch(SQLException sqe) {
            throw new SQLException( sqe.getMessage() );
        }
        catch(NoSuchElementException nee) {
            throw new NoSuchElementException( nee.getMessage() );
        }
        catch(RemoteException re) {
            throw new RemoteException( re.getMessage() );
        }
        catch(XMLStreamException xe) {
            throw new XMLStreamException( xe.getMessage() );
        }
        catch(IOException  ioe) {
            throw new IOException( ioe.getMessage() );
        }
        catch( Exception e ) {
            throw new Exception( e.getMessage() );
        }


        // 40: Return estimate
        log.info("\n about to return estimate in DataDock.call \n");
        return processEstimate;
    }

    /**
     *
     */
    public String fedoraStoreData() throws ConfigurationException, RemoteException, XMLStreamException, IOException, Exception {
        log.info( "Entering DataDock.fedoraStoreData" );
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
            try{
                fh = new FedoraHandler();
            }
            catch (ConfigurationException cex){
                throw new ConfigurationException( cex.getMessage() );
            }

        }else{
            log.info( String.format( "A FedoraHandler is already initialized, using it" ) );
        }

        // 20: submit data
        try{
            // The next 2 lines of code waits for the getNextPid method from fh
            // String submitter = cargo.getSubmitter();
            //try{
            // String usePid = submitter + ":" + fh.getNextPid(submitter);
            // }catch (RemoteException re){
            // }catch(XMLStreamException xmle){
            // }catch(IOException ioe){
            //}
            fedoraHandle = fh.submitDatastream( cc, usePid, itemId, label );
        }catch( RemoteException re ){
            throw new RemoteException(re.getMessage());
        }catch( XMLStreamException xmle ){
            throw new XMLStreamException(xmle.getMessage());
        }catch( IOException ioe ){
            throw new IOException(ioe.getMessage());
        }
        catch( fedora.server.errors.ServerException se ){
            throw new Exception( se.getMessage() ) ;
        }
        //        }catch( Exception e ){
        // throw new Exception( e.getMessage() );

        // 30: deposit objectInfo as dissaminator to data
        // 40: deposit metadata as dissamminator to data
        // 50: convert handle to data from fedora to String
        // 60: return fedoraHandle
        log.info( String.format( "Ingest succeded, returning pid=%s",fedoraHandle ) );
        log.info( "Exiting DataDock.fedoraStoreData" );
        return fedoraHandle;
    }

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public void queueFedoraHandle( String fedoraHandle ) throws ClassNotFoundException, ConfigurationException, SQLException {
        /**
         * the push queues a fedoraHandle on the processQueue
         * to take the parameteres from the config file
         */
        log.info( "Entering DataDock.queueFedoraHandle" );
        // 10: call push
        try{
            queue.push( fedoraHandle );

        }
        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException(cne.getMessage());
        }
        catch(SQLException sqe){
            throw new SQLException(sqe.getMessage());
        }
        log.info( "Exiting.queueFedoraHandle" );
    }
}