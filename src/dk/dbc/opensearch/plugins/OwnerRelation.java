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
 * \brief Adding owner relation information to fedora repository objects
 */


package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.javascript.JavaScriptWrapperException;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.FedoraRelsExt;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for adding owner relation to cargoContainers
 */
public class OwnerRelation implements IRelation
{
    private static Logger log = Logger.getLogger( OwnerRelation.class );

    private PluginType pluginType = PluginType.RELATION;
    private final Map<String, Invocable> scriptCache = Collections.synchronizedMap( new HashMap<String, Invocable>() );

    private SimpleRhinoWrapper jsWrapper = null;

    
    /**
     * Constructor for the OwnerRelation plugin.
     * @throws PluginException
     */
    public OwnerRelation() throws PluginException
    {
	try 
	{
	    jsWrapper = new SimpleRhinoWrapper( "owner_relation.js" );
	}
	catch( JavaScriptWrapperException jswe )
	{
	    String errorMsg = new String("An exception occured when trying to instantiate the SimpleRhinoWrapper");
	    log.fatal( errorMsg, jswe );
	    throw new PluginException( errorMsg, jswe );
	}

        log.trace( "OwnerRelation plugin constructed" );
    }


    /**
     * Entry point of the plugin
     * @param cargo The {@link CargoContainer} to construct the owner relations from
     * @return a {@link CargoContainer} containing a RELS-EXT stream reflecting the owner relations
     * @throws PluginException, which wraps all exceptions thrown from
     * within this plugin, please refer to {@link PluginException} for
     * more information on how to retrieve information on the
     * originating exception.
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );

	cargo = setOwnerRelations( cargo );

        return cargo;
    }

    public CargoContainer setOwnerRelations( CargoContainer cargo ) throws PluginException
    {

        CargoObject co = null;
        if ( ! cargo.hasCargo( DataStreamType.OriginalData ) )
        {
            String error = String.format( "CargoContainer with id '%s' has no OriginalData to contruct relations from, aborting", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( new IllegalStateException( error ) );
        }
        else
        {
            co = cargo.getCargoObject( DataStreamType.OriginalData );
        }

        String submitter = co.getSubmitter();
        String format = co.getFormat();

        if ( null == submitter || submitter.isEmpty() || null == format || format.isEmpty() )
        {
            String error = String.format( "CargoContainer with id '%s' has no information on submitter or format", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( new IllegalStateException( error ) );
        }

        FedoraRelsExt rels = (FedoraRelsExt) cargo.getMetaData( DataStreamType.RelsExtData );
        if ( null == cargo.getIdentifier() )
        {
            log.warn( String.format( "CargoContainer has no identifier, this will be a problem in the RELS-EXT generation/validation" ) );
        }

        if ( null == rels )
        {
	    try 
	    {
		rels = new FedoraRelsExt();
	    }
	    catch ( ParserConfigurationException pce )
	    {
		String errorMsg = new String( "Could not create a new FedoraRelsExt.");
		log.error( errorMsg, pce );
		throw new PluginException( errorMsg, pce );
	    }
        }

        log.debug( String.format( "Trying to add owner relation for rels '%s'; submitter '%s'; format '%s'", rels.toString(), submitter, format ) );

	String entryPointFunc = "addOwnerRelation";

	try 
	{
	    rels = ( FedoraRelsExt ) jsWrapper.run( entryPointFunc,
						    rels,
						    submitter,
						    format );        
	} 
	catch( JavaScriptWrapperException jswe ) 
	{
	    String errorMsg = String.format( "An excpetion occured when trying to run the javascript: %s with entrypoint function: %s", jsWrapper.getJavascriptName(), entryPointFunc );
	    log.fatal( errorMsg, jswe );
	    throw new PluginException( errorMsg, jswe );
	}

        log.debug( String.format( "rels: '%s'", rels.toString() ) );

        cargo.addMetaData( rels );
	
	return cargo;

    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


    public void setObjectRepository( IObjectRepository objectRepository )
    {
        jsWrapper.put( "Log", log ); // SOI prefers Log with capital L!
	jsWrapper.put( "IS_OWNED_BY", DBCBIB.IS_OWNED_BY );
        jsWrapper.put( "IS_AFFILIATED_WITH", DBCBIB.IS_AFFILIATED_WITH );
    }
}
