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

import dk.dbc.commons.types.Pair;
import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.FcrepoReader;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.ITargetField;

import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 *
 * This plugin handles the matching of objects in the fedora objectrepository
 * with the special workobjects
 * In contrast to other plugins of the RELATION type it uses more than 1 javascript
 * function to handle the business logic
 */
public class MarcxchangeWorkRelation implements IPluggable
{
    private static Logger log = LoggerFactory.getLogger( MarcxchangeWorkRelation.class );


    public MarcxchangeWorkRelation() throws PluginException
    {
    }


    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {   
        if( !(ienv instanceof MarcxchangeWorkRelationEnvironment) )
        {
            String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "MarcxchangeWorkRelationEnvironment", ienv.getClass().getName() );
            log.error( errMsg );
            throw new PluginException( errMsg );
        }

        MarcxchangeWorkRelationEnvironment env = (MarcxchangeWorkRelationEnvironment)ienv;

        long sp_timer = System.currentTimeMillis();
        List< Pair< ITargetField, String > > searchPairs = env.getSearchPairs( cargo );
        sp_timer = System.currentTimeMillis() - sp_timer;
        log.info( String.format( "searchPairs Timing: time: %s", sp_timer ) );
        log.debug( String.format( "the searchList: %s", searchPairs.toString() ) );

        long run_timer = System.currentTimeMillis();
        CargoContainer returnCargo = null;
        synchronized( this )
        {
            returnCargo = env.run( cargo, searchPairs );
        }
        run_timer = System.currentTimeMillis() - run_timer;
        log.info( String.format( "run Timing: time: %s", run_timer ) );

        return returnCargo;
    }


    @Override
    public IPluginEnvironment createEnvironment( FcrepoReader reader, FcrepoModifier modifier, Map< String, String > args, String scriptPath ) throws PluginException
    {
    	return new MarcxchangeWorkRelationEnvironment( reader, modifier, args, scriptPath );
    }

}
