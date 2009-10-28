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
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.PID;
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
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for adding owner relation to cargoContainers
 */
public class OwnerRelation implements IRelation
{

    private static Logger log = Logger.getLogger( OwnerRelation.class );
    private PluginType pluginType = PluginType.RELATION;
    private IObjectRepository objectRepository = null;
    private final Map<String, Invocable> scriptCache = Collections.synchronizedMap( new HashMap<String, Invocable>() );
    private final ScriptEngineManager manager = new ScriptEngineManager();

    /**
     * Constructor for the OwnerRelation plugin.
     * @throws PluginException 
     */
    public OwnerRelation() throws PluginException
    {
        log.debug( "ja7o: plugin created" );
    }


    /**
     * lookup a cached Instance of the script engine.  

     * @param submitter 
     * @return Configured script engine.. 
     * @throws ConfigurationException
     * @throws FileNotFoundException
     * @throws ScriptException
     * @throws PluginException
     */
    Invocable lookupJavaScript( String submitter ) throws ConfigurationException, FileNotFoundException, ScriptException, PluginException
    {
        if( submitter == null )
        {
            throw new PluginException( "Internal Error OwnerRealation:lookupJavaScript called with empty submitter" );
        }
        log.debug( String.format( "ja7o: lookup %s", submitter ) );

        if( scriptCache.containsKey( submitter ) )
        {
            log.debug( String.format( "ja7o: lookup %s - hit", submitter ) );

            return scriptCache.get( submitter );
        }
        else
        {
            log.debug( String.format( "ja7o: lookup %s - mis", submitter ) );

            ScriptEngine engine = manager.getEngineByName( "JavaScript" );


            engine.put( "log", log );

            engine.put( "objectRepository", objectRepository );

            String path = FileSystemConfig.getScriptPath();
            String jsFileName = path + "owner_relation.js";

            log.debug( String.format( "ja7O: url = %s", jsFileName ) );
            engine.eval( new java.io.FileReader( jsFileName ) );

            Invocable inv = (Invocable) engine;

            scriptCache.put( submitter, inv );
            return scriptCache.get( submitter );
        }
    }


    /**
     * Entry point of the plugin
     * @param CargoContainer The {@link CargoContainer} to construct the owner relations from
     * @returns a {@link CargoContainer} containing a RELS-EXT stream reflecting the owner relations
     * @throws PluginException, which wraps all exceptions thrown from
     * within this plugin, please refer to {@link PluginException} for
     * more information on how to retrieve information on the
     * originating exception.
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );

        try
        {
            setOwnerRelations( cargo );

            //this.fedoraHandle.addRelationship( pid, "info:fedora/fedora-system:def/relations-external#isMemberOfCollection", owner, false, null );            
        }
        catch( ConfigurationException e )
        {
            log.error( "setOwnerRelation: Eception e", e );
            throw new PluginException( "Error setting OwnerRelation ", e );
        }
        catch( IOException e )
        {
            log.error( "setOwnerRelation: Eception e", e );
            throw new PluginException( "Error setting OwnerRelation ", e );

        }
        catch( Exception e )
        {
            log.error( "setOwnerRelation: Eception e", e );
            throw new PluginException( "Error setting OwnerRelation ", e );
        }

        return cargo;
    }


    public void setOwnerRelations( CargoContainer cargo ) throws ConfigurationException, FileNotFoundException, Exception
    {
        //! 
        String submitter = null;
        String format = null;
        if( cargo.hasCargo( DataStreamType.OriginalData ) )
        {
            CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
            submitter = co.getSubmitter();
            format = co.getFormat();
        }
        else
        {
            log.error( "CargoContainer has no OriginalData to contruct relations from, aborting" );
            throw new PluginException( new IllegalStateException( "CargoContainer has no OriginalData to contruct relations from, aborting" ) );
        }

        PID pid = new PID( cargo.getIdentifier() );
        Invocable inv = lookupJavaScript( submitter );
        inv.invokeFunction( "doit", pid, submitter, format );
    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


    public void setObjectRepository( IObjectRepository newObjectRepository )
    {
        objectRepository = newObjectRepository;
    }
}
