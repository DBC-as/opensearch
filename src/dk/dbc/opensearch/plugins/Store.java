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


package dk.dbc.opensearch.plugins;


import dk.dbc.commons.types.StringLock;
import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.FcrepoReader;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoContainer;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


/**
 * Class for storing CargoContainers
 */
public class Store implements IPluggable
{
    private static Logger log = LoggerFactory.getLogger( Store.class );

    StoreEnvironment env = null;

    StringLock strlock = null;

    public Store() throws PluginException
    {
        strlock = new StringLock();
    }

    /*
     * This funtion is synchronized in the case the rare event happens that two records with identical ID's,
     * eg. two updates of the record, are stored by two different threads simultaniously.
     * See bug#10534.
     */
    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {
        if ( !( ienv instanceof StoreEnvironment ) )
        {
            String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "StoreEnvironment", ienv.getClass().getName() );
            log.error( errMsg );
            throw new PluginException( errMsg );
        }

        StoreEnvironment env = ( StoreEnvironment ) ienv;

        log.trace( "Entering storeCargoContainer( CargoContainer )" );

        String pid = cargo.getIdentifierAsString(); // saving pid in case cargo should be modified during call!
        strlock.lock( pid );
        try
        {
            CargoContainer returnCC = env.run( cargo );
            return returnCC;
        }
        finally
        {
            strlock.unlock( pid );
        }

    }

    @Override
    public IPluginEnvironment createEnvironment( FcrepoReader reader, FcrepoModifier modifier, Map< String, String > args, String scriptPath ) throws PluginException
    {
    	return new StoreEnvironment( reader, modifier, args, scriptPath );
    }

}
