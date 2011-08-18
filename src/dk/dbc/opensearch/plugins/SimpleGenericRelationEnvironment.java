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

import dk.dbc.commons.javascript.E4XXMLHeaderStripper;
import dk.dbc.commons.types.Pair;
import dk.dbc.jslib.Environment;
import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.FcrepoReader;
import dk.dbc.opensearch.javascript.JSFedoraPIDSearch;
import dk.dbc.opensearch.javascript.JSRelationFunctions;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginEnvironmentUtils;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoObject;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.DataStreamType;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class SimpleGenericRelationEnvironment implements IPluginEnvironment
{

    private static Logger log = LoggerFactory.getLogger( SimpleGenericRelationEnvironment.class );

    private Environment jsEnvironment;

    private final String entryPointFunc;

    // For validation:
    private static final String javascriptStr = "javascript";
    private static final String entryFuncStr  = "entryfunction";


    /**
     */
    public SimpleGenericRelationEnvironment( FcrepoReader reader, FcrepoModifier modifier, Map<String, String> args, String scriptPath ) throws PluginException
    {
        // Setting the objectlist.
        JSFedoraPIDSearch fedoraPIDSearch = new JSFedoraPIDSearch( reader );
        JSRelationFunctions relationFunctions = new JSRelationFunctions( reader, modifier );

        List<Pair<String, Object>> objectList = new ArrayList<Pair<String, Object>>();
        objectList.add( new Pair<String, Object>( "Log", log ) );
        objectList.add( new Pair<String, Object>( "scriptClass", relationFunctions ) );
        objectList.add( new Pair<String, Object>( "FedoraPIDSearch", fedoraPIDSearch ) );

        this.validateArguments( args, objectList, scriptPath );

        this.entryPointFunc = args.get( SimpleGenericRelationEnvironment.entryFuncStr );

        //this.jsWrapper = PluginEnvironmentUtils.initializeWrapper( args.get( SimpleGenericRelationEnvironment.javascriptStr ), objectList, scriptPath );
        this.jsEnvironment = PluginEnvironmentUtils.initializeJavaScriptEnvironment( args.get( SimpleGenericRelationEnvironment.javascriptStr ), objectList, scriptPath );
    }


    /**
     */
    public void addRelation( CargoContainer cargo )
    {
        //This mehtod should call a script with the cargocontainer as parameter
        //and expose a getPID and a makeRelation method that enables the script to
        //find the PID of the target of the review and create the hasReview
        //and reviewOf relations
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        String submitter = co.getSubmitter();
        String format = co.getFormat();
        String language = co.getLang();
        String XML = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>

        String pid = cargo.getIdentifierAsString(); // get the pid of the cargocontainer
        //jsWrapper.run( entryPointFunc, submitter, format, language, XML, pid );
        jsEnvironment.callMethod( entryPointFunc, new Object[] { submitter, format, language, XML, pid } );

    }


    /**
     * This function will validate the following argumentnames: "javascript" and "entryfunction".
     * All other argumentnames will be silently ignored.
     * Currently the "entryfunction" is not tested for validity.
     * 
     * @param Map< String, String > the argumentmap containing argumentnames as keys and arguments as values
     * @param List< Pair< String, Object > > A list of objects used to initialize the JavaScript environment.
     *
     * @throws PluginException if an argumentname is not found in the argumentmap or if one of the arguments cannot be used to instantiate the pluginenvironment.
     */
    private void validateArguments( Map< String, String > args, 
				    List< Pair< String, Object > > objectList, 
				    String scriptPath ) throws PluginException
    {
        log.info( "Validating Arguments - Begin" );

        // Validating existence of mandatory entrys:
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( SimpleGenericRelationEnvironment.javascriptStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", SimpleGenericRelationEnvironment.javascriptStr ) );
        }
        if( !PluginEnvironmentUtils.validateMandatoryArgumentName( SimpleGenericRelationEnvironment.entryFuncStr, args ) )
        {
            throw new PluginException( String.format( "Could not find argument: %s", SimpleGenericRelationEnvironment.entryFuncStr ) );
        }

        Environment tmpJsEnv = PluginEnvironmentUtils.initializeJavaScriptEnvironment( args.get( SimpleGenericRelationEnvironment.javascriptStr ), objectList, scriptPath );

        // Validating JavaScript function entries.
        if( !PluginEnvironmentUtils.validateJavaScriptFunction( tmpJsEnv, args.get( SimpleGenericRelationEnvironment.entryFuncStr ) ) )
        {
            throw new PluginException( String.format( "Could not use %s as function in javascript", args.get( SimpleGenericRelationEnvironment.entryFuncStr ) ) );
        }

        log.info( "Validating Arguments - End" );
    }
}
