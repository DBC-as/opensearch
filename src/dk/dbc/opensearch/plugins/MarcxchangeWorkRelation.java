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
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.ITargetField;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.log4j.Logger;


/**
 *
 * This plugin handles the matching of objects in the fedora objectrepository
 * with the special workobjects
 * In contrast to other plugins of the RELATION type it uses more than 1 javascript
 * function to handle the business logic
 */
public class MarcxchangeWorkRelation implements IPluggable
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation.class );


    private PluginType pluginType = PluginType.RELATION;

    //private MarcxchangeWorkRelationEnvironment env = null;


    public MarcxchangeWorkRelation( IObjectRepository repository ) throws PluginException
    {

	// Map< String, String > tmpMap = new HashMap< String, String >();
	// env = (MarcxchangeWorkRelationEnvironment)this.createEnvironment( repository, tmpMap );

    }


    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {   

	if ( !( ienv instanceof MarcxchangeWorkRelationEnvironment) )
	{
	    String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "MarcxchangeWorkRelationEnvironment", ienv.getClass().getName() );
	    log.error( errMsg );
	    throw new PluginException( errMsg );
	}

	MarcxchangeWorkRelationEnvironment env = (MarcxchangeWorkRelationEnvironment)ienv;

	long sp_timer = System.currentTimeMillis();
	List< Pair< ITargetField, String > > searchPairs = env.getSearchPairs( cargo );
	sp_timer = System.currentTimeMillis() - sp_timer;
	log.trace( String.format( "searchPairs Timing: time: %s", sp_timer ) );  
        log.debug( String.format( "the searchList: %s", searchPairs.toString() ) );

	long run_timer = System.currentTimeMillis();
	CargoContainer returnCargo = null;
	synchronized (this )
	{
	    returnCargo = env.run( cargo, searchPairs );
	}
        run_timer = System.currentTimeMillis() - run_timer;
	log.trace( String.format( "run Timing: time: %s", run_timer ) );  

	return returnCargo;
    }



    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    @Override
    public IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new MarcxchangeWorkRelationEnvironment( repository, args );
    }

}
