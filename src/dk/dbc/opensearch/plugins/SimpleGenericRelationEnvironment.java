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

import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.javascript.JSFedoraPIDSearch;
import dk.dbc.opensearch.common.javascript.ScriptMethodsForReviewRelation;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginEnvironmentUtils;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.SimplePair;
import dk.dbc.opensearch.common.types.IPair;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class SimpleGenericRelationEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( SimpleGenericRelationEnvironment.class );

    private SimpleRhinoWrapper jsWrapper = null;
    private IObjectRepository objectRepository;

    private final String entryPointFunc;

    // For validation:
    private static final String javascriptStr = "javascript";
    private static final String entryFuncStr  = "entryfunction";


    /**
     */
    public SimpleGenericRelationEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        this.objectRepository = repository;

	// Setting the objectlist.
        JSFedoraPIDSearch fedoraPIDSearch = new JSFedoraPIDSearch( objectRepository );
	ScriptMethodsForReviewRelation scriptClass = new ScriptMethodsForReviewRelation( objectRepository );

	List< IPair< String, Object > > objectList = new ArrayList< IPair< String, Object > >();
	objectList.add( new SimplePair< String, Object >( "Log", log ) );
	objectList.add( new SimplePair< String, Object >( "scriptClass", scriptClass ) );
	objectList.add( new SimplePair< String, Object >( "FedoraPIDSearch", fedoraPIDSearch ) );

	this.validateArguments( args, objectList );

	this.entryPointFunc  = args.get( this.entryFuncStr );

	this.jsWrapper = PluginEnvironmentUtils.initializeWrapper( args.get( this.javascriptStr ), objectList );

	log.trace( "Checking wrapper (outer)" );
	if (jsWrapper == null) {
	    log.trace("Wrapper is null");
	} else {
	    log.trace("Wrapper is initialized");
	}

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
	String format    = co.getFormat();
       	String language  = co.getLang();
	String XML       = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>

        String pid = cargo.getIdentifierAsString(); // get the pid of the cargocontainer
	jsWrapper.run( entryPointFunc, submitter, format, language, XML, pid );

    }



    /**
     * This function will validate the following argumentnames: "javascript" and "entryfunction".
     * All other argumentnames will be silently ignored.
     * Currently the "entryfunction" is not tested for validity.
     * 
     * @param Map< String, String > the argumentmap containing argumentnames as keys and arguments as values
     * @param List< IPair< String, Object > > A list of objects used to initialize the RhinoWrapper.
     *
     * @throws PluginException if an argumentname is not found in the argumentmap or if one of the arguments cannot be used to instantiate the pluginenvironment.
     */
    private void validateArguments( Map< String, String > args, List< IPair< String, Object > > objectList ) throws PluginException
    {
	log.info("Validating Arguments - Begin");

	// Validating existence of mandatory entrys:
	if ( ! PluginEnvironmentUtils.validateMandatoryArgumentName( this.javascriptStr, args ) )
	    throw new PluginException( String.format( "Could not find argument: %s", this.javascriptStr ) );
	if ( ! PluginEnvironmentUtils.validateMandatoryArgumentName( this.entryFuncStr, args ) )
	    throw new PluginException( String.format( "Could not find argument: %s", this.entryFuncStr ) );

	// Validating that javascript can be used in the SimpleRhinoWrapper:
	SimpleRhinoWrapper tmpWrapper =  PluginEnvironmentUtils.initializeWrapper( args.get( this.javascriptStr ), objectList );

	// Validating function entries:
	if ( ! tmpWrapper.validateJavascriptFunction( args.get(this.entryFuncStr) ) )
	    throw new PluginException( String.format( "Could not use %s as function in javascript", args.get(this.entryFuncStr) ) );

	log.info("Validating Arguments - End");
    }


}