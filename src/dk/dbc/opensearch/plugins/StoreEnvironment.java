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
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginEnvironmentUtils;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IObjectIdentifier;
import dk.dbc.opensearch.common.types.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import org.apache.log4j.Logger;

public class StoreEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( StoreEnvironment.class );

    private final IObjectRepository objectRepository;
    private final SimpleRhinoWrapper jsWrapper;

    // For validation:
    private static final String javascriptStr = "javascript";
    private static final String entryFuncStr  = "entryfunction";

    private final String entryPointFunc;
    private final String javascript;

    public StoreEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        this.objectRepository = repository;

        List<Pair<String, Object>> objectList = new ArrayList<Pair<String, Object>>();
        objectList.add( new Pair<String, Object>( "Log", log ) );

        //this.validateArguments( args, objectList );

        this.entryPointFunc = args.get( StoreEnvironment.entryFuncStr );
        this.javascript = args.get( StoreEnvironment.javascriptStr );
        if( javascript != null && javascript.length() > 0 )
        {
            this.jsWrapper = PluginEnvironmentUtils.initializeWrapper( javascript, objectList );
        }
        else
        {
            // Use old behaviour
            jsWrapper = null;
        }

        log.trace( "Checking wrapper (outer)" );
        if( jsWrapper == null )
        {
            log.trace( "Wrapper is null" );
        }
        else
        {
            log.trace( "Wrapper is initialized" );
        }
    }


    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        String submitter = co.getSubmitter();
        String format = co.getFormat();
        String language = co.getLang();
        String XML = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>
        IObjectIdentifier pid = cargo.getIdentifier(); // get the pid of the cargocontainer
        String pidStr = cargo.getIdentifierAsString();

        // Let javascript decide if post should be deleted or stored
        boolean deletePost = false;
        if (jsWrapper != null)
        {
            log.debug( "Calling javascript to determine if post should be deleted");
            deletePost = ( (Boolean) jsWrapper.run( entryPointFunc, submitter, format, language, XML, pidStr ) ).booleanValue();
        }
        if( !deletePost )
        {
            String logm = String.format( "Datadock: %s inserted with pid %s", format, pid );
            try
            {
                if ( pid != null )
                {
                    boolean hasObject = objectRepository.hasObject( pid );
                    log.debug( String.format( "hasObject( %s ) returned %b",pidStr, hasObject ) );
                    if ( hasObject )
                    {
                        log.trace( String.format( "will try to delete pid %s", pidStr ) );
                        objectRepository.deleteObject( pidStr, "delte before store hack" );
                    }
                }

                objectRepository.storeObject( cargo, logm, "auto" );
            }
            catch( ObjectRepositoryException ex )
            {
                String error = String.format( "Failed to store CargoContainer with id %s, submitter %s and format %s", pidStr, submitter, format );
                log.error( error, ex);
                throw new PluginException( error, ex );
            }
        }
        else
        {
            log.info( "Post will be deleted: pid="+pidStr );
            String logm = String.format( "Datadock: %s marked deleted with pid %s", format, pid );
            try
            {
                objectRepository.markDeleted( pidStr, format, submitter, logm );
            }
            catch( ObjectRepositoryException ex )
            {
                String error = String.format( "Failed to mark deleted CargoContainer with id %s, submitter %s and format %s", pidStr, submitter, format );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
        }
        
        return cargo;
    }

    
}