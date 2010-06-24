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
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.SimplePair;
import dk.dbc.opensearch.common.types.Pair;

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


    private static final String javascript_str = "javascript";
    private static final String entryFunc_str  = "entryfunction";
    private final String javascript;
    private final String entryPointFunc;

    /**
     */
    public SimpleGenericRelationEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        this.objectRepository = repository;

	this.validateArguments( args );
	this.javascript = args.get( javascript_str );
	this.entryPointFunc  = args.get( entryFunc_str );

	this.jsWrapper = this.initializeWrapper( this.javascript );

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
     */
    private SimpleRhinoWrapper initializeWrapper( String jsFileName ) throws PluginException
    {
        JSFedoraPIDSearch fedoraPIDSearch = new JSFedoraPIDSearch( objectRepository );
	ScriptMethodsForReviewRelation scriptClass = new ScriptMethodsForReviewRelation( objectRepository );

	List< Pair< String, Object > > objectList = new ArrayList< Pair< String, Object > >();
	objectList.add( new SimplePair< String, Object >( "Log", log ) );
	objectList.add( new SimplePair< String, Object >( "scriptClass", scriptClass ) );
	objectList.add( new SimplePair< String, Object >( "FedoraPIDSearch", fedoraPIDSearch ) );

	SimpleRhinoWrapper wrapper = null;
        try 
	{
	    wrapper = new SimpleRhinoWrapper( FileSystemConfig.getScriptPath() + jsFileName, objectList );
	}
	catch( FileNotFoundException fnfe )
	{
	    String errorMsg = String.format( "Could not find the file: %s", jsFileName );
	    log.error( errorMsg, fnfe );
	    throw new PluginException( errorMsg, fnfe );
	}
	catch( ConfigurationException ce )
	{
	    String errorMsg = String.format( "A ConfigurationExcpetion was cought while trying to construct the path+filename for javascriptfile: %s", jsFileName );
	    log.fatal( errorMsg, ce );
	    throw new PluginException( errorMsg, ce );
	}

	log.trace( "Checking wrapper (inner)" );
	if (wrapper == null) {
	    log.trace("Wrapper is null");
	} else {
	    log.trace("Wrapper is initialized");
	}

	return wrapper;

    }

    /**
     */
    private void validateArguments( Map< String, String > args ) throws PluginException
    {
	log.info("Validating Arguments - Begin");

	// Validating Entry: "javascript".
	String jsName = null;
	if ( args.containsKey( javascript_str ) ) 
	{
	    jsName = args.get( javascript_str ); 
	}
	else
	{
	    // This is Fatal! We cannot create the environment.
	    String errMsg = String.format( "Could not find mandatory argument: \"%s\"", javascript_str );
	    log.fatal( errMsg );
	    throw new PluginException( errMsg );
	}
	SimpleRhinoWrapper tmpWrapper =  initializeWrapper( jsName );
	
	// Validating Entry: "entryfunction":
	String entry = null;
	if ( args.containsKey( entryFunc_str ) ) 
	{
	    entry = args.get( entryFunc_str ); 
	}
	else
	{
	    // This is Fatal! We cannot create the environment.
	    String errMsg = String.format( "Could not find mandatory argument: \"%s\"", entryFunc_str );
	    log.fatal( errMsg );
	    throw new PluginException( errMsg );
	}

	log.info("Validating Arguments - End");
    }

}