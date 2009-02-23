/**
 * \file DataDock.java
 * \brief The DataDock class
 * \package datadock
 */

package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.db.Processqueue;

import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Set;
import java.util.Vector;
import java.util.concurrent.Callable;
import java.util.ArrayList;
import javax.xml.stream.XMLStreamException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;


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
	private Logger log = Logger.getLogger( DatadockThread.class );
	
    private CargoContainer cc;
    private Processqueue queue;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;

    private Estimate estimate;
    private DatadockJob datadockJob;
    private String submitter;
    private String format;
    private ArrayList< String > list;
    
    
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
     * @throws ParserConfigurationException 
     * @throws PluginResolverException 
     * @throws NullPointerException 
     * @throws SAXException 
     */
    public DatadockThread( DatadockJob datadockJob, Estimate estimate, Processqueue processqueue, HashMap< Pair< String, String >, ArrayList< String > > jobMap) throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException 
    {
    	log.debug( String.format( "Entering DatadockThread Constructor" ) );
    	
        this.jobMap = jobMap;
        this.datadockJob = datadockJob;
        
        // Each pair identifies a plugin by p1:submitter and p2:format    	
        submitter = datadockJob.getSubmitter();
        format = datadockJob.getFormat();        
        list = this.jobMap.get( new Pair< String, String >( submitter, format ) );
        
        queue = processqueue;
        
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
    public Float call() throws Exception  
    {
    	try
    	{
	    	// Must be implemented due to class implementing Callable< Float > interface.
	    	// Method is to be extended when we connect to 'Posthuset'
	    	// Validate plugins
			PluginResolver pluginResolver = new PluginResolver();
			Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
			
			if( ! missingPlugins.isEmpty() )
			{		
				System.out.println( " kill thread" );
				// kill thread/throw meaningful exception/log message
			}
			else
			{
				CargoContainer cc = null;
			
				for( String task : list)
	    		{	
					IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
					switch ( plugin.getTaskName() )
					{	
						case HARVEST:
							IHarvestable harvestPlugin = (IHarvestable)plugin; 
							cc = harvestPlugin.getCargoContainer( datadockJob );
							break;
						case ANNOTATE:
							IAnnotate annotatePlugin = (IAnnotate)plugin;
							cc = annotatePlugin.getCargoContainer( cc );
							break;
						case STORE:
							IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
							repositoryStore.storeCargoContainer( cc, this.datadockJob );
					}
	    		}
			}
    	}
    	catch ( Exception ex )
    	{
    		log.error( ex.getStackTrace() );
    		throw ex;
    	}
    	
        return null;
    }
}