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

/**
 * \file
 * \brief Thread handling in datadock framework
 */

package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.pluginframework.PluginTask;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.HarvesterUnknownIdentifierException;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;

import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.IJob;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.sql.SQLException;
import java.util.Map;
import java.util.List;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * \ingroup datadock \brief The public interface for the OpenSearch
 * DataDockService DataDock, together with DataDockPool, is the primary
 * accesspoint for the delivery of material to be saved in the Fedora repository
 * and processed by lucene. The DataDock interface allows clients to submit data
 * that represents a textual material to be stored in a Fedora repository and
 * indexed by Lucene. When submitted, the data is validated against a dictionary
 * of possible handlers using the supplied metadata and object-information. All
 * methods throw exceptions on errors.
 *
 * \todo a schema for errors returned should be defined
 *
 * DataDock is the central class in the datadock component. This class offers
 * the service of receiving data, metadata and objectinfo for later processing
 * with lucene. The primary responsibility of the DataDock is to validate
 * incoming data and construct data-carrying objects for use within OpenSearch.
 * Furthermore, DataDock starts the process of fedora storing, data processing,
 * indexing and search-capabilities
 */
public class DatadockThread implements Callable<Boolean>
{
    private Logger                  log = Logger.getLogger( DatadockThread.class );

    private IHarvest                harvester;
    private CargoContainer          cargo;
    private IProcessqueue           queue;
    private IIdentifier             identifier;
    private String                  submitter;
    private String                  format;
    private Map<String, List<PluginTask>> flowMap;
    private PluginResolver pluginResolver;


    /**
     *\todo: Wheet out in the Exceptions
     *
     * DataDock is initialized with a DatadockJob containing information about
     * the data to be 'docked' into to system
     *
     * @param identifier
     *            the information about the data to be docked
     * @param processqueue
     *            the processqueue handler
     * @throws ConfigurationException if no ObjectRepository could be reached
     *
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public DatadockThread( IIdentifier identifier, IProcessqueue processqueue, IHarvest harvester, PluginResolver pluginResolver, Map<String, List<PluginTask>> flowMap ) throws ConfigurationException, IOException, SAXException, ParserConfigurationException
    {
        log.trace( String.format( "Entering DatadockThread Constructor" ) );

        /**
         * \todo: We should get rid of the datadockjob. the info lies in 
         * the cargoContainer, so all that is needed is the identifier
         */

	this.identifier = identifier;
        this.harvester = harvester;
        this.pluginResolver = pluginResolver;
        this.flowMap = flowMap;
        this.queue = processqueue;

        log.trace( String.format( "DatadockThread Construction finished" ) );
    }


    /**
     * call() is the thread entry method on the DataDock. Call operates on the
     * DataDock object, and all data critical for its success is given at
     * DataDock initialization. This method is used with
     * java.util.concurrent.FutureTask, which upon finalisation (completion,
     * exception or termination) will return an estimation of how long time it
     * will take to bzw. index and save in fedora the data given with the
     * CargoContainer. \see dk.dbc.opensearch.tools.Estimation
     *
     * @return true if job is performed
     * @throws InstantationException
     *             if the PluginResolver cant instantiate a plugin
     * @throws IllegalAccessException
     *             if the PluginResolver cant access the desired plugin
     * @throws ClassNotFoundException
     *             if the PluginResolver cant find the desired plugin class
     * @throws IllegalStateException
     * @throws HarvesterIOException
     * @throws HarvesterUnknownIdentifierException
     * @throws PluginException
     * @throws SQLException
     */
    @Override
        public Boolean call() throws ConfigurationException, InstantiationException, IllegalAccessException, IOException, ClassNotFoundException, HarvesterIOException, HarvesterUnknownIdentifierException, ParserConfigurationException, PluginException, SAXException, SQLException, InvocationTargetException
    {
        // Must be implemented due to class implementing Callable< Boolean > interface.
        // Method is to be extended when we connect to 'Posthuset'
        log.trace( "DatadockThread call method called" );

        try
        {
            cargo = harvester.getCargoContainer( identifier );
        }
        catch( HarvesterUnknownIdentifierException huie )
        {
            String error = "Could not get CargoContainer from harvester";
            log.error( error );
            throw new HarvesterUnknownIdentifierException( error, huie );
        }

        //get submitter and format from the first cargoObject in the cargoContainer
        submitter = cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter();
        format = cargo.getCargoObject( DataStreamType.OriginalData ).getFormat();
        List<PluginTask> pluginTaskList = flowMap.get( submitter + format);

        for( PluginTask pluginTask : pluginTaskList )
        {
            
            String classname = pluginTask.getPluginName();
            Map<String, String> argsMap = pluginTask.getArgsMap();
            log.trace( "the argsMap: " + argsMap.toString() ); 
            String script = (String)argsMap.get( "script" );
            log.trace( String.format("DatadockThread getPlugin classname: '%s' script: '%s' ",classname, script ) );   
            IPluggable plugin = pluginResolver.getPlugin( classname );
            
            long timer = System.currentTimeMillis();
            cargo = plugin.runPlugin( cargo, argsMap );
            timer = System.currentTimeMillis() - timer;
            log.trace( String.format( "Timing: %s time: %s", classname, timer ) );  
            
            if( null == cargo || cargo.getCargoObjectCount() < 1 )
            {
                String error = String.format( "Plugin '%s' returned an empty CargoContainer", classname );
                log.error( error );
                throw new IllegalStateException( error );
            }
        }

        String identifierAsString = cargo.getIdentifierAsString();

        //push to processqueue job to processqueue
        queue.push( identifierAsString );

        //inform the harvester that it was a success
        try
        {
            harvester.setStatusSuccess( identifier, identifierAsString );
        }
        catch ( HarvesterInvalidStatusChangeException hisce )
        {
            log.error( hisce.getMessage() , hisce);
            return Boolean.FALSE;
        }

	return Boolean.TRUE;
    }
}
