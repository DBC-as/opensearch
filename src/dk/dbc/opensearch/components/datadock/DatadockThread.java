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


import dk.dbc.opensearch.db.IProcessqueue;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.pluginframework.PluginTask;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.HarvesterUnknownIdentifierException;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;

import dk.dbc.opensearch.common.types.IIdentifier;
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
    private static final Logger log = Logger.getLogger( DatadockThread.class );

    private IIdentifier identifier;
    private IProcessqueue queue;
    private IHarvest harvester;
    private Map<String, List<PluginTask>> flowMap;


    /**
     * DataDock is initialized with a DatadockJob containing information about
     * the data to be 'docked' into to system
     *
     * @param identifier
     *            the information about the data to be docked
     * @param processqueue
     *            the processqueue handler
     */
    public DatadockThread( IIdentifier identifier, IProcessqueue processqueue, IHarvest harvester, Map<String, List<PluginTask>> flowMap )
    {
        log.trace( String.format( "Entering DatadockThread Constructor" ) );

        this.identifier = identifier;
        this.queue = processqueue;
        this.harvester = harvester;
        this.flowMap = flowMap;

        log.trace( String.format( "DatadockThread Construction finished" ) );
    }


    /**
     *
     * @throws HarvesterIOException
     * @throws HarvesterUnknownIdentifierException
     * @throws PluginException
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    @Override
    public Boolean call() throws HarvesterIOException, HarvesterUnknownIdentifierException, PluginException, ClassNotFoundException, SQLException
    {
        // Must be implemented due to class implementing Callable< Boolean > interface.
        // Method is to be extended when we connect to 'Posthuset'
        log.info( String.format( "DatadockThread call method called on identifier: %s", this.identifier.toString() ) );

        CargoContainer cargo;
        try
        {
            cargo = harvester.getCargoContainer( this.identifier );
        }
        catch( HarvesterUnknownIdentifierException huie )
        {
            String error = "Could not get CargoContainer from harvester";
            log.error( error );
            throw new HarvesterUnknownIdentifierException( error, huie );
        }

        //get submitter and format from the first cargoObject in the cargoContainer
        String submitter = cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter();
        String format = cargo.getCargoObject( DataStreamType.OriginalData ).getFormat();
	log.info( String.format( "Found submitter = \"%s\" and format = \"%s\"", submitter, format ) );
        List<PluginTask> pluginTaskList = flowMap.get( submitter + format);

        for( PluginTask pluginTask : pluginTaskList )
        {

            IPluggable plugin = pluginTask.getPlugin();
            String classname = plugin.getClass().getName();

            IPluginEnvironment env = pluginTask.getEnvironment();
            log.trace( String.format("DatadockThread getPlugin classname: '%s'",classname) );   
	    
            long timer = System.currentTimeMillis();
            cargo = plugin.runPlugin( env, cargo );
            timer = System.currentTimeMillis() - timer;
            log.info( String.format( "runPlugin Timing: %s time: %s", classname, timer ) );  
            
            if( null == cargo || cargo.getCargoObjectCount() < 1 )
            {
                String error = String.format( "Plugin '%s' returned an empty CargoContainer", classname );
                log.error( error );
                throw new IllegalStateException( error );
            }

            /** Check to see if it is a delete record **/
            if ( cargo.getIsDeleteRecord() )
            {
                log.info( String.format( "Delete record found with id '%s' leaving plugin loop", cargo.getIdentifierAsString() ) );
                break;
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
