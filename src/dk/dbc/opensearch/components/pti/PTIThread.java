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


package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.fedora.IFedoraAdministration;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.concurrent.Callable;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;


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
    private IFedoraAdministration fedoraAdministration;

    /**
     * \brief Constructs the PTI instance with the given parameters
     *
     * @param fedoraPid the handle identifying the data object
     * @param session the compass session this pti should communicate with
     * @param estimate used to update the estimate table in the database
     * @param jobMap information about the tasks that should be solved by the pluginframework
     */
    public PTIThread( String fedoraPid, CompassSession session, IEstimate estimate, IFedoraAdministration fedoraAdministration ) throws ConfigurationException, IOException, MalformedURLException, ServiceException
        {
            super();

            log.debug( String.format( "constructor(session, fedoraPid=%s )", fedoraPid ) );

            this.fedoraAdministration = fedoraAdministration;
            this.estimate = estimate;
            this.session = session;
            this.fedoraPid = fedoraPid;

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
            CargoContainer cc2 = null;
            CargoObject co = null;
            String submitter =  null;
            String format = null;

            try
                {
                    log.debug( String.format( "PTIThread -> fedoraPid: ", fedoraPid ) );
                    cc = fedoraAdministration.retrieveCargoContainer( fedoraPid );
                }
            catch( Exception e )
                {
                    log.fatal( String.format( "Caught exception with cause: %s, message: %s", e.getCause(), e.getMessage() ) );
                    throw new PluginException( "Could not retrieve adminstream elements, aborting", e );
                }

            co = cc.getCargoObject( DataStreamType.OriginalData );
            submitter =  co.getSubmitter();
            format = co.getFormat();

            long result = 0l;

            // Get the job from the jobMap
            list = PTIJobsMap.getPtiPluginsList( submitter, format );
            if ( list == null )
                {
                    log.warn( String.format( "no jobs for submitter: %s format: %s", submitter, format ) );
                }
            else{
                PluginResolver pluginResolver = new PluginResolver();

                log.debug( "Entering switch" );
                log.debug( "PluginsList: " + list.toString() );
                for ( String classname : list )
                    {
                        log.debug( "PTIThread running through plugins list" );
                        IPluggable plugin = pluginResolver.getPlugin( classname );
                        log.debug( "PTIThread plugin resolved" );
                        PluginType taskName = plugin.getPluginType();
                        log.debug( "PTIThread taskName: " + taskName );
                        switch ( taskName )
                            {
                            case PROCESS:
                                log.debug( "calling processerplugin" );
                                IProcesser processPlugin = ( IProcesser )plugin;
                                cc = processPlugin.getCargoContainer( cc );
                                log.debug( "PTIThread PROCESS plugin done" );
                                break;
                            case INDEX:
                                log.debug( "calling indexerplugin" );
                                IIndexer indexPlugin = ( IIndexer )plugin;
                                result = indexPlugin.getProcessTime( cc, session, fedoraPid, estimate );
                                log.debug( "PTIThread INDEX plugin done" );
                                //update statistics database
                                break;
                            }
                    }
            }
            log.debug( "PTIThread done with result: " + result );

            return result;
        }
}

