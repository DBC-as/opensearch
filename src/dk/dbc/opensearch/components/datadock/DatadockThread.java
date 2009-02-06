/**
 * \file DataDock.java
 * \brief The DataDock class
 * \package datadock
 */

package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import javax.xml.stream.XMLStreamException;
import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;


/**
 * \ingroup datadock
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
public class DatadockThread implements Callable<Float>
{
    private CargoContainer cc;
    private Processqueue queue;
    private static volatile FedoraHandler fh;
   
    private Logger log = Logger.getLogger("DataDockThread");

    Estimate estimate;

    
    /**
     * DataDock is initialized with a CargoContainer containing the
     * data to be 'docked' into to system
     *
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.DBConnection
     *
     * @param cargo The cargo to be processed
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     *
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     */
    public DatadockThread( DatadockJob datadockJob, Estimate estimate, Processqueue processqueue, FedoraHandler fedoraHandler ) throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException 
    {
        log.debug( String.format( "Entering DataDock Constructor" ) );
        CargoContainer cargo = null;

        String mimetype = "TEXT_XML";
        String lang = "da";
        
        File f = new File( datadockJob.getPath().getRawPath() );
        cargo = createCargoContainerFromFile( f, mimetype, lang, datadockJob.getSubmitter(), datadockJob.getFormat() );
        
        //InputStream data = new FileInputStream( f );
        //cargo = new CargoContainer( data, mimetype, lang, datadockJob.getSubmitter(), datadockJob.getFormat() );

    	log.debug( String.format( "Entering DataDock Constructor" ) );
        cc = cargo;
        queue = processqueue;
        fh = fedoraHandler;
        
        this.estimate = estimate;
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
     * \see dk.dbc.opensearch.tools.Estimation
     *
     * @returns an estimate on the completion-time of indexing and fedora submission
     *
     * @throws SQLException if the estimate could not be retrieved from the database
     * @throws NoSuchElementException if the mimetype is unknown to the estimate method. \see dk.dbc.opensearch.tools.Estimation.getEstimate(String, long)
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws RemoteException if the datastream could not be ingested into the fedora base
     * @throws XMLStreamException if the foxml could not be constructed in the FedoraHandler \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     */
    public Float call() throws SQLException, NoSuchElementException, ConfigurationException, RemoteException, XMLStreamException, IOException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException, ValidationException , MarshalException, ParseException 
    {
        log.debug( String.format( "Entering call" ) );
        Float processEstimate = 0f;
        String fedoraHandle = null;
        
        log.debug( String.format( "Getting estimation for a combination of mimetype '%s' and data length '%s'", cc.getMimeType(), cc.getStreamLength() ) ); 
      
        processEstimate = estimate.getEstimate( cc.getMimeType(), cc.getStreamLength() );
      
        fedoraHandle = fedoraStoreData();

        log.debug( String.format( "Queueing handle %s with itemId %s", fedoraHandle, cc.getFormat() ) );
        queue.push( fedoraHandle, cc.getFormat() );

        log.debug( String.format( "data queued" ) );

        log.info( String.format( "Data queued. Returning estimate = %s", processEstimate ) );
        processEstimate = doProcessing();
        return processEstimate;
    }

    
    /**
     * doProcessing is the main method for the DataDock. Call operates
     * on the DataDock object, and all data critical for its success
     * is given at DataDock initialization. This method is used with
     * java.util.concurrent.FutureTask, which upon finalisation
     * (completion, exception or termination) will return an
     * estimation of how long time it will take to bzw. index and save
     * in fedora the data given with the CargoContainer.
     * \see dk.dbc.opensearch.tools.Estimation
     *
     * @returns an estimate on the completion-time of indexing and fedora submission
     *
     * @throws SQLException if the estimate could not be retrieved from the database
     * @throws NoSuchElementException if the mimetype is unknown to the estimate method.
     * \see dk.dbc.opensearch.tools.Estimation.getEstimate(String, long)
     * @throws ConfigurationException if the FedoraHandler could not be initialized.
     * \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws RemoteException if the datastream could not be ingested into the fedora base
     * @throws XMLStreamException if the foxml could not be constructed in the FedoraHandler
     * \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws ClassNotFoundException if the database could not be initialised 
     * in the Estimation class \see dk.dbc.opensearch.tools.Estimate 
     * @throws NullPointerException 
     * @throws IllegalStateException 
     * @throws ValidationException 
     * @throws MarshalException 
     */
    private Float doProcessing() throws SQLException, NoSuchElementException, ConfigurationException, RemoteException, XMLStreamException, IOException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException, MarshalException, ValidationException, IllegalStateException, NullPointerException, ParseException
    {
        log.debug( "Entering doProcessing" );
        Float processEstimate = 0f;
        String fedoraHandle = null;

        log.debug( String.format( "Getting estimation for a combination of mimetype '%s' and data length '%s'", cc.getMimeType(), cc.getStreamLength() ) );        
        processEstimate = estimate.getEstimate( cc.getMimeType(), cc.getStreamLength() );
        fedoraHandle = fedoraStoreData();

        log.debug( String.format( "Queueing handle %s with itemId %s", fedoraHandle, cc.getFormat() ) );
        queue.push( fedoraHandle, cc.getFormat() );

        log.debug( String.format( "data queued" ) );

        log.info( String.format( "Data queued ") );
        return processEstimate;
    }
   
    /**
     * fedoraStoreData is an internal method for storing data given
     * with the initialization of the DataDock into a fedora base.
     *
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws RemoteException if the datastream could not be ingested into the fedora base
     * @throws XMLStreamException if the foxml could not be constructed in the FedoraHandler \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws NullPointerException 
     * @throws IllegalStateException 
     * @throws ValidationException 
     * @throws MarshalException 
     */
    private String fedoraStoreData() throws ConfigurationException, RemoteException, XMLStreamException, IOException, MalformedURLException, ParseException, UnknownHostException, ServiceException, MarshalException, ValidationException, IllegalStateException, NullPointerException
    {
        log.debug( "Entering DataDock.fedoraStoreData" );
        String fedoraHandle = "";

        /** todo: give real applicable value to label. value should be given by cargo container*/
        String label = "test";
        
        // submit data 
        fedoraHandle = fh.submitDatastream( cc, label );
        log.info( String.format( "Ingest succeded, returning pid=%s",fedoraHandle ) );
        log.debug( "Exiting DataDock.fedoraStoreData" );
        
        return fedoraHandle;
    }
    
    
    /**
     * Creating CargoContainer from file.
     * 
     */
    private CargoContainer createCargoContainerFromFile( File file, String mimetype, String lang, String submitter, String format ) throws IOException
    {
    	InputStream data = new FileInputStream( file );
    	int contentLength = (int)file.length();
    	    	
    	CargoObjectInfo coi = new CargoObjectInfo( CargoMimeType.valueOf( mimetype ), lang, submitter, format, contentLength );
    	Pair< CargoObjectInfo, InputStream > pair = new Pair< CargoObjectInfo, InputStream >(coi, data);
    	
    	ArrayList< Pair< CargoObjectInfo, InputStream > > al = new ArrayList< Pair< CargoObjectInfo, InputStream > >();
    	al.add( pair);
    	CargoContainer cc = new CargoContainer( al );
    	
    	return cc;
    }
}