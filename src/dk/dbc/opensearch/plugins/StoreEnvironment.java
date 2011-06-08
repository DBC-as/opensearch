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

        this.entryPointFunc = args.get( StoreEnvironment.entryFuncStr );
        this.javascript = args.get( StoreEnvironment.javascriptStr );
        if( javascript != null && javascript.length() > 0 )
        {
            this.validateArguments( args, objectList );
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
        boolean hasObject = false;
        // Let javascript decide if post should be deleted or stored
        boolean deleteRecord = false;
        if (jsWrapper != null)
        {
            log.debug( "Calling javascript to determine if post should be deleted");
            deleteRecord = ( (Boolean) jsWrapper.run( entryPointFunc, submitter, format, language, XML, pidStr ) ).booleanValue();
            log.debug( String.format( "js to determin if it is a deleterecord returned: '%s'", deleteRecord ) );
        }
        else
        {
            log.debug(String.format("Javascript not defined for [%s:%s], skipping", format, submitter));
        }
       
        //determining whether we have the object or not
        try
        {
            hasObject = objectRepository.hasObject( pid );
            log.debug( String.format( "hasObject( %s ) returned %b",pidStr, hasObject ) );
        }
        catch( ObjectRepositoryException ore )
        {
            String error = String.format( "exception caught when calling hasObject for object: '%s'", pidStr  );
            log.error( error, ore );
            throw new PluginException( error, ore );
        }

        String logm = String.format( "Datadock: %s inserted with pid %s", format, pidStr );
        /**
         * if we have the object in the repository we modify it according to whether it is a delete record or an update
         */
        if( hasObject )
        {
            //allways remove inbound relations on an existing object
            try
            {
                objectRepository.removeInboundRelations( pidStr );
            }
            catch( ObjectRepositoryException ore )
            {
                String error = String.format( "exception caught when trying to remove inbound relations to object: '%s'", pidStr  );
                log.error( error, ore );
                throw new PluginException( error, ore );
            }
            
            if( !deleteRecord )
            {
                //an update to an existing record
                //if not a delete record we purge the object in the repos and store the incoming
                log.trace( String.format( "will try to purge pid %s", pidStr ) );
                try
                {
                    objectRepository.purgeObject( pidStr, "purge before store hack" );
                    objectRepository.storeObject( cargo, logm, "auto" );
                }
                catch( ObjectRepositoryException ore )
                {
                    String error = String.format( "exception caught when trying to purge and store object: '%s'", pidStr  );
                    log.error( error, ore );
                    throw new PluginException( error, ore );
                }
            }
            else
            {
                //if its a delete record we mark the post in the repository as deleted
                // remove the outbound relations
                //and set the isDeleteRecord flag on the CargoContainer
                log.info( String.format( "Object will be deleted: pid [%s]", pidStr ) );
                logm = String.format( "Datadock: %s deleted with pid %s", format, pidStr );
                try
                {                    
                    objectRepository.deleteObject( pidStr, format, submitter, logm );
                    objectRepository.removeOutboundRelations( pidStr );
                    cargo.setIsDeleteRecord( deleteRecord );
                }
                catch( ObjectRepositoryException ore )
                {
                    String error = String.format( "Failed to mark deleted CargoContainer with id %s, submitter %s and format %s", pidStr, submitter, format );
                    log.error( error, ore );
                    throw new PluginException( error, ore );
                }
            }
        }
        else
        {
            if( deleteRecord)
            {
                //a delete record for a record that is not in the repository
                //\Todo: throw exception
                log.error( String.format( "no object in the repository for object: '':", pidStr ) );
            }
            else
            {
                //a record that has been received the for first time
                try
                {
                    objectRepository.storeObject( cargo, logm, "auto" ); 
                }
                catch( ObjectRepositoryException ore )
                {
                    String error = String.format( "exception caught when trying to store object: '%s'", pidStr  );
                    log.error( error, ore );
                    throw new PluginException( error, ore );
                }

            }
        }
        /**
        String logm = String.format( "Datadock: %s inserted with pid %s", format, pidStr );
        try
        {
            if ( pid != null )
            {
                boolean hasObject = objectRepository.hasObject( pid );
                log.debug( String.format( "hasObject( %s ) returned %b",pidStr, hasObject ) );
                if ( hasObject )
                {
                    log.trace( String.format( "will try to delete pid %s", pidStr ) );
                    objectRepository.purgeObject( pidStr, "purge before store hack" );
                    //removing relations pointing to the object
                    objectRepository.removeInboundRelations( pidStr );
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
        if( deleteRecord )
        {
            log.info( String.format( "Object will be deleted: pid [%s]", pidStr ) );
            logm = String.format( "Datadock: %s deleted with pid %s", format, pidStr );
            try
            {
                objectRepository.deleteObject( pidStr, format, submitter, logm );
                
                // mark the cargo as delete record so the workflow can be
                // halted.
                cargo.setIsDeleteRecord( deleteRecord );
            }
            catch( ObjectRepositoryException ex )
            {
                String error = String.format( "Failed to mark deleted CargoContainer with id %s, submitter %s and format %s", pidStr, submitter, format );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
        }    */    
        return cargo;
    }


    /**
     * This function will validate the following argumentnames: "javascript" and "entryfunction".
     * All other argumentnames will be silently ignored.
     * Currently the "entryfunction" is not tested for validity.
     *
     * @param Map< String, String > the argumentmap containing argumentnames as keys and arguments as values
     * @param List< Pair< String, Object > > A list of objects used to initialize the RhinoWrapper.
     *
     * @throws PluginException if an argumentname is not found in the argumentmap or if one of the arguments cannot be used to instantiate the pluginenvironment.
     */
    private void validateArguments( Map< String, String > args, List< Pair< String, Object > > objectList ) throws PluginException
    {
        log.info( "Validating Arguments - Begin" );

        // Validating existence of mandatory entrys:
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( StoreEnvironment.javascriptStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", StoreEnvironment.javascriptStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( StoreEnvironment.entryFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", StoreEnvironment.entryFuncStr ) );
        }

        // Validating that javascript can be used in the SimpleRhinoWrapper:
        SimpleRhinoWrapper tmpWrapper = PluginEnvironmentUtils.initializeWrapper( args.get( StoreEnvironment.javascriptStr ), objectList );

        // Validating function entries:
        if( !tmpWrapper.validateJavascriptFunction( args.get( StoreEnvironment.entryFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( StoreEnvironment.entryFuncStr ) ) );
        }

        log.info( "Validating Arguments - End" );
    }
    
}