/**
 * \file PTIThread.java
 * \brief The PTIThread Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;

/**
 *
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s,
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */


import dk.dbc.opensearch.common.fedora.FedoraCommunication;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
//import dk.dbc.opensearch.common.types.Pair;

import fedora.server.types.gen.MIMETypedStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dk.dbc.opensearch.common.fedora.FedoraCommunication;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;

/**
 * \ingroup pti
 * \brief the PTIThread class is responsible for getting a dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values in the statistics db will be updated
 */
public class PTIThread implements Callable< Long >
{
    Logger log = Logger.getLogger( PTIThread.class );


    private CompassSession session;
    private String fedoraPid;
    private IEstimate estimate;
    private ArrayList< String > list;
    private HashMap< InputPair< String, String >, ArrayList< String > > jobMap;
    private IFedoraCommunication fedoraCommunication;


    /**
     * \brief Constructs the PTI instance with the given parameters
     *
     * @param fedoraPid the handle identifying the data object
     * @param session the compass session this pti should communicate with
     * @param estimate used to update the estimate table in the database
     * @param jobMap information about the tasks that should be solved by the pluginframework
     */

    public PTIThread( String fedoraPid, CompassSession session, IEstimate estimate, HashMap< InputPair< String, String >, ArrayList< String > > jobMap, IFedoraCommunication fedoraCommunication ) throws ConfigurationException, IOException, MalformedURLException, ServiceException
    {
        super();

        log.debug( String.format( "constructor(session, fedoraPid=%s )", fedoraPid ) );

        this.jobMap = jobMap;
        this.estimate = estimate;
        this.session = session;
        this.fedoraPid = fedoraPid;
        this.fedoraCommunication = fedoraCommunication;
        log.debug( "constructor done" );
    }


    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     *
     * @return the processtime
     *
     * @throws CompassException something went wrong with the compasssession
     * @throws IOException something went wrong initializing the fedora client
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws PluginResolverException when the PluginResolver encounters problem that are not neccesarily showstoppers
     * @throws InstantiationException when the PluginResolver cant instantiate a plugin
     * @throws ParserConfigurationException when the PluginResolver has problems parsing files
     * @throws IllegalAccessException when the PluginiResolver cant access a plugin that should be loaded
     * */
    public Long call() throws ClassNotFoundException, CompassException, ConfigurationException, IllegalAccessException, InstantiationException, InterruptedException, IOException, MarshalException, ParserConfigurationException, PluginException, PluginResolverException, SAXException, ServiceException, SQLException, ValidationException
    {
        log.debug( String.format( "Entering with handle: '%s'", fedoraPid ) );
        CargoContainer cc = null;
        CargoObject co = null;
        String submitter =  null;
        String format = null;
        


        log.debug( String.format( "Trying to get stream from fedora with pid %s and name %s", fedoraPid, DataStreamType.AdminData.getName() ) );
       
        try{
            cc = fedoraCommunication.retrieveContainer( fedoraPid );
        }catch( Exception e ){
            log.fatal( String.format( "Caught exception with cause: %s, message: %s", e.getCause(), e.getMessage() ) );
            throw new PluginException( "Could not retrieve adminstream elements, aborting", e );
        }
        
        co = cc.getCargoObject( DataStreamType.OriginalData );
        submitter =  co.getSubmitter();
        format = co.getFormat();

        long result = 0l;
        // Get the job from the jobMap
        list = jobMap.get( new InputPair< String, String >( submitter, format ) );
        if ( list == null )
        {
            log.fatal( String.format( "no jobs for submitter: %s format: %s", submitter, format ) );
            throw new NullPointerException( String.format( "no jobs for submitter: %s format: %s", submitter, format ) );
        }

        //50: validate that there exists plugins for all the tasks
        PluginResolver pluginResolver = new PluginResolver();
        for ( int i = 0; i < list.size(); i++ )
        {
            log.debug( String.format( " plugin to be found: %s", list.get( i ) ) );
        }

        Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
        //60: execute the plugins
        if ( ! missingPlugins.isEmpty() )
        {
            Iterator< String > iter = missingPlugins.iterator();
            while ( iter.hasNext() )
            {
                log.debug( String.format( "no plugin for task: %s", ( String )iter.next() ) );
            }


            log.debug( " kill thread" );
        }
        else
        {
            log.debug( "Entering switch" );

            for ( String task : list )
            {
                IPluggable plugin = ( IPluggable )pluginResolver.getPlugin( submitter, format, task );
                switch ( plugin.getTaskName() )
                {
                case PROCESS:
                    log.debug( "calling processerplugin" );
                    IProcesser processPlugin = ( IProcesser )plugin;
                    cc = processPlugin.getCargoContainer( cc );
                    break;
                case INDEX:
                    log.debug( "calling indexerplugin" );
                    IIndexer indexPlugin = ( IIndexer )plugin;
                    result = indexPlugin.getProcessTime( cc, session, fedoraPid );
                    //update statistics database
                    break;
                }
            }
        }

        log.debug( result );

        return result;
    }
}

