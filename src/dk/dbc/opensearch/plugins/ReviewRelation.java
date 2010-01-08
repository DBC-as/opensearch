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
import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.TargetFields;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.javascript.ScriptMethodsForReviewRelation;

import org.apache.commons.configuration.ConfigurationException;
import java.io.FileNotFoundException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Plugin for creating relations between reviews and there target
 */
public class ReviewRelation implements IRelation
{
    private static Logger log = Logger.getLogger( ReviewRelation.class );

    private final Map<String, Invocable> scriptCache = Collections.synchronizedMap( new HashMap<String, Invocable>() );
    private final ScriptEngineManager manager = new ScriptEngineManager();
    private PluginType pluginType = PluginType.RELATION;

    private Vector< String > types;
    private final String marterialevurderinger = "Materialevurdering:?";
    private final String anmeldelse = "Anmeldelse";
    private final String namespace = "review";
    private IObjectRepository objectRepository;
    private ScriptMethodsForReviewRelation scriptClass;


    /**
     * Constructor for the ReviewRelation plugin.
     */
    public ReviewRelation()
    {
        log.trace( "Constructor called" );
    }


    /**
     * The "main" method of this plugin.
     *
     * @param CargoContainer The CargoContainer to add the reviewOf relation to
     * and be the target of a hasReview relation on another object in the objectRepository
     *
     * @returns A CargoContainer containing relations
     *
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );
        if( objectRepository == null )
        {
            String msg = "no repository set";
            log.error( msg );
            throw new PluginException( msg );
        }
        scriptClass = new ScriptMethodsForReviewRelation( objectRepository );
        boolean ok = false;
        ok = addReviewRelation( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add review relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    private boolean addReviewRelation( CargoContainer cargo ) throws PluginException
    {
        //This mehtod should call a script with the cargocontainer as parameter
        //and expose a getPID and a makeRelation method that enables the script to
        //find the PID of the target of the review and create the hasReview
        //and reviewOf relations
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

	Invocable inv = null;
        try
        {
            inv = lookupJavaScript( co.getSubmitter() );
        }
        catch( ConfigurationException ce )
        {
            String error = String.format( "error message: %s", ce.getMessage() );
            log.error( error );
            throw new PluginException( error, ce );
        }
        catch( FileNotFoundException fnfe )
        {
            String error = String.format( "error message: %s", fnfe.getMessage() );
            log.error( error );
            throw new PluginException( error, fnfe );
        }
        catch( ScriptException se )
        {
            String error = String.format( "error message: %s", se.getMessage() );
            log.error( error );
            throw new PluginException( error, se );
        }

	try 
	{
	    inv.invokeFunction( "test" );
	}
	catch( ScriptException se )
	{
	    String errorMsg = new String( "Could not run script" );
	    log.fatal( errorMsg , se );
	    throw new PluginException( errorMsg, se );
	}
	catch( NoSuchMethodException nsme )
	{
	    String errorMsg = new String( "The method, \"test\" could not be found in the script." );
	    log.fatal( errorMsg , nsme );
	    throw new PluginException( errorMsg, nsme );
	}


        return true;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }

    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }

    /**
     * Tries to do a lookup of a cached instance of the script engine
     * based on the submitter. If no cached instances are found, a new
     * one is created from a supplied javascript matching the role of
     * this plugin.
     *
     * @param submitter
     * @return Configured script engine fitting the submitter value of the CargoContainer
     * @throws ConfigurationException
     * @throws FileNotFoundException
     * @throws ScriptException
     * @throws PluginException
     */
    private Invocable lookupJavaScript( String submitter ) throws ConfigurationException, FileNotFoundException, ScriptException, PluginException
    {

        log.trace( String.format( "Entering lookupJavaScript" ) );
        if( submitter == null || submitter.isEmpty() )
        {
            throw new PluginException( new IllegalArgumentException( "submitter in CargoContainer is not set. Aborting." ) );
        }

        //lookup invocable in cache:
        if( this.scriptCache.containsKey( submitter ) )
        {
            log.trace( String.format( "Returning Invocable js for %s", submitter ) );
            return this.scriptCache.get( submitter );
        }
        else // ...or create new invocable + add it to the cache
        {
            ScriptEngine engine = manager.getEngineByName( "JavaScript" );

            engine.put( "log", log );
            engine.put( "scriptClass", scriptClass );

            String path = FileSystemConfig.getScriptPath();
            String jsFileName = path + "review_relation.js";

            log.debug( String.format( "Using javascript at url '%s'", jsFileName ) );
            engine.eval( new java.io.FileReader( jsFileName ) );

            Invocable inv = (Invocable) engine;

            this.scriptCache.put( submitter, inv );

            log.trace( String.format( "Returning Invokable js for %s", submitter ) );
            return inv;
        }
    }
}
