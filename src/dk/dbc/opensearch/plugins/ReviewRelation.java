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
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 * Plugin for creating relations between reviews and their target
 */
public class ReviewRelation implements IPluggable
{
    private static Logger log = Logger.getLogger( ReviewRelation.class );

    private PluginType pluginType = PluginType.RELATION;
    private ReviewRelationEnvironment env = null;

    /**
     * Constructor for the ReviewRelation plugin.
     */
    public ReviewRelation( IObjectRepository repository ) throws PluginException
    {
        log.trace( "Constructor called" );

	Map< String, String > tmpMap = new HashMap< String, String>();
	env = (ReviewRelationEnvironment)this.createEnvironment( repository, tmpMap );
	

    }


    /**
     * The "main" method of this plugin.
     *
     * @param cargo The CargoContainer to add the reviewOf relation to
     * and be the target of a hasReview relation on another object in the objectRepository
     *
     * @return A CargoContainer containing relations
     *
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    synchronized public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );
  
        boolean ok = false;
	ok = env.addReviewRelation( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add review relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }



    public PluginType getPluginType()
    {
        return pluginType;
    }

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
    	return new ReviewRelationEnvironment( repository, args );
    }

}
