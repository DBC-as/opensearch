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


import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Plugin for adding owner relation to cargoContainers
 */
public class OwnerRelation implements IPluggable
{
    private static Logger log = Logger.getLogger( OwnerRelation.class );

    private PluginType pluginType = PluginType.RELATION;

    private OwnerRelationEnvironment env = null;

    /**
     * Constructor for the OwnerRelation plugin.
     * @param repository is the {@link IObjectRepository} to access if needed by the plugin
     * @throws PluginException
     */
    public OwnerRelation( IObjectRepository repository ) throws PluginException
    {
	log.trace( "Entering OwnerRelation" );

	Map< String, String > tmpMap = new HashMap< String, String >();
	env = (OwnerRelationEnvironment)this.createEnvironment( repository, tmpMap );

        log.trace( "OwnerRelation plugin constructed" );

    }


    /**
     * Entry point of the plugin
     * @param cargo The {@link CargoContainer} to construct the owner relations from
     * @param argsMap, the remaining arguments for the method
     * @return a {@link CargoContainer} containing a RELS-EXT stream reflecting the owner relations
     * @throws PluginException, which wraps all exceptions thrown from
     * within this plugin, please refer to {@link PluginException} for
     * more information on how to retrieve information on the
     * originating exception.
     */
    @Override
    public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap ) throws PluginException
    { 
        log.trace( "getCargoContainer() called" );

        // cargo = setOwnerRelations( cargo );
	cargo = env.setOwnerRelations( cargo );

        return cargo;
    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }
    
    /**
     * Method to validate that the plugin has the right arguments
     */
    private boolean validateArgs( Map<String, String> argsMap )
    {
        if( argsMap.get( "script" ) == null ||  argsMap.get( "script" ).equals( "" ) )
        {
            return false;
        }
        return true;
    }


    public static IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new OwnerRelationEnvironment( repository, args );
    }


}
