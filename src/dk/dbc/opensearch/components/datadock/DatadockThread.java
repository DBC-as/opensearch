/*   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.IWorkRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

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
public class DatadockThread implements Callable< Float >
{
    private Logger log = Logger.getLogger( DatadockThread.class );


    private CargoContainer cargo;
    private IProcessqueue queue;
    private IEstimate estimate;
    private DatadockJob datadockJob;
    private String submitter;
    private String format;
    private ArrayList< String > list;
    private String result;
    

    /**
     *\todo: Wheet out in the Exceptions
     *
     * DataDock is initialized with a DatadockJob containing information
     * about the data to be 'docked' into to system
     *
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.DBConnection
     *
     * @param datadockJob the information about the data to be docked
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param jobMap information about the tasks that should be solved by the pluginframework
     *
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws NullPointerException
     * @throws SAXException
     */
    public DatadockThread( DatadockJob datadockJob, IEstimate estimate, IProcessqueue processqueue ) throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        log.debug( String.format( "Entering DatadockThread Constructor" ) );

        this.datadockJob = datadockJob;

        // Each pair identifies a plugin by p1:submitter and p2:format
        submitter = datadockJob.getSubmitter();
        format = datadockJob.getFormat();

        log.debug( String.format("submitter: %s, format: %s", submitter, format ) );
        log.debug( String.format( "Calling jobMap.get( new Pair< String, String >( %s, %s ) )", submitter, format ) );

        list = DatadockJobsMap.getDatadockPluginsList( submitter, format );
        if( list == null )
        {
            throw new NullPointerException( String.format( "The returned list from the DatadockJobsMap.getDatadockJobsMap( %s, %s ) is null", submitter, format ) );
        }
        log.debug( "constructor PluginList " + list.toString() );
        queue = processqueue;

        this.estimate = estimate;

        log.debug( String.format( "DataDock Construction finished" ) );
    }

    
    /**
     * call() is the thread entry method on the DataDock. Call operates
     * on the DataDock object, and all data critical for its success
     * is given at DataDock initialization. This method is used with
     * java.util.concurrent.FutureTask, which upon finalisation
     * (completion, exception or termination) will return an
     * estimation of how long time it will take to bzw. index and save
     * in fedora the data given with the CargoContainer.
     * \see dk.dbc.opensearch.tools.Estimation
     *
     * @returns an estimate on the completion-time of indexing and fedora submission
     * @throws PluginResolverException if the PluginResolver encountered problems, see dk.dbc.opensearch.common.pluginframework.PluginResolverException and dk.dbc.opensearch.common.pluginframework.PluginResolver
     * @throws FileNotFoundException if the PluginResolver.getPlugin cant find the file its searcing for
     * @throws IOException if the FedoraHandler could not read data from the CargoContainer
     * @throws ParserConfigurationException if the PluginResolver or CargoContainer has troubles with the DocumentBuilder and DocumentBuilderFactory
     * @throws InstantationException if the PluginResolver cant instantiate a plugin
     * @throws IllegalAccessException if the PluginResolver cant access the desired plugin
     * @throws ClassNotFoundException if the PluginResolver cant find the desired plugin class
     * @throws SAXException when the a harvest or an annotate plugin have problems with the data
     * @throws MarshalException
     * @throws ValidationException
     * @throws IllegalStateException
     * @throws ServiceException
     * @throws IOException
     * @throws ParseException
     */
    public Float call() throws PluginResolverException, IOException, FileNotFoundException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException, SAXException, MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, XPathExpressionException, PluginException, SQLException, TransformerException, TransformerConfigurationException, ConfigurationException
    {
        // Must be implemented due to class implementing Callable< Float > interface.
        // Method is to be extended when we connect to 'Posthuset'

        log.debug( "DatadockThread call method called" );

        // Validate plugins
        PluginResolver pluginResolver = new PluginResolver();

        log.debug( String.format( "pluginList classname %s", list.toString() ) );
        for( String classname : list)
        {
            log.debug( "DatadockThread getPlugin 'classname' " + classname );

            IPluggable plugin = pluginResolver.getPlugin( classname );
            log.debug( String.format( "plugin::TaskName = '%s'", plugin.getTaskName() ) );
            
            switch ( plugin.getTaskName() )
            {
                case HARVEST:
                    log.debug( String.format( "case HARVEST pluginType %s", plugin.getTaskName().toString() ) );
                    
                    IHarvestable harvestPlugin = (IHarvestable)plugin;
                    cargo = harvestPlugin.getCargoContainer( datadockJob );
                    
                    checkCargoObjectCount( cargo );
                    
                    break;
                case ANNOTATE:
                    log.debug( String.format( "case ANNOTATE pluginType %s", plugin.getTaskName().toString() ) );
                    
                    IAnnotate annotatePlugin = (IAnnotate)plugin;

                    checkCargoContainerIsNotNull( cargo );
                    
                    cargo = annotatePlugin.getCargoContainer( cargo );
                    
                    break;
                case STORE:
                    IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
                    cargo = repositoryStore.storeCargoContainer( cargo );
                	break;
                case WORKRELATION:
                    log.debug( String.format( "case WORKRELATION pluginType %s", plugin.getTaskName().toString() ) );
                    
                    checkCargoContainerIsNotNull( cargo );
                    
                    IWorkRelation workRelationPlugin = (IWorkRelation)plugin;
                    cargo = workRelationPlugin.getCargoContainer( cargo );
                    
                    break;
                case GETESTIMATE:
                    log.debug( "" );
                    
                    //    
                    
                    break;
                default:
                	log.warn( String.format( "plugin.getTaskName ('%s') did not match HARVEST or ANNOTATE", plugin.getTaskName() ) );
            }
        }

        //obtain mimetype and length from CargoContainer
        String mimeType = null;
        String format = null;
        String submitter = null;
        long length = 0;
        
        for( CargoObject co : cargo.getCargoObjects() )
        {
            if( co.getDataStreamName() == DataStreamType.OriginalData )
            {
                mimeType = co.getMimeType();
                format = co.getFormat();
                submitter = co.getSubmitter();
            }
            
            length += co.getContentLength();
        }

        String pid = FedoraAdministration.storeCargoContainer( cargo, submitter, format );
        
        //push to processqueue job to processqueue and get estimate
        queue.push( pid );
        Float est = estimate.getEstimate( mimeType, length );
        log.debug( String.format( "Got estimate of %s", est ) );
        return est;
    }
    
    
    private void checkCargoContainerIsNotNull( CargoContainer cargo ) throws NullPointerException
    {
    	if ( cargo == null )
        {
        	log.error( "DatadockThread call throws NullPointerException, cc is null" );
           	throw new NullPointerException( "DatadockThread call throws NullPointerException" );
        }
    }
    
    
    private void checkCargoObjectCount( CargoContainer cargo ) throws IllegalStateException
    {
    	if( cargo.getCargoObjectCount() < 1 ) // no data in the cargocontainer, so no reason to continue
        {
            log.error( String.format( "no cargoobjects in the cargocontainer" ) );
            throw new IllegalStateException( String.format( "no cargoobjects in the cargocontainer " ) );
        }   	
    }
}
