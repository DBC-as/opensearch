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


import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;

import org.apache.log4j.Logger;


/**
 * Class for storing CargoContainers
 */
public class Store implements IRepositoryStore
{
    private static Logger log = Logger.getLogger( Store.class );


    private PluginType pluginType = PluginType.STORE;
    private IObjectRepository objectRepository = null;

    

    synchronized public CargoContainer storeCargoContainer( CargoContainer cargo ) throws PluginException
    {
    	log.trace( "Entering storeCargoContainer( CargoContainer )" );

        if( null == this.objectRepository )
        {
            throw new IllegalStateException( "IObjectRepository has not been set" );
        }

        String logm = String.format( "%s inserted with pid %s", cargo.getCargoObject( DataStreamType.OriginalData ).getFormat(), cargo.getIdentifier() );
        try
        {
            if ( cargo.getIdentifier() != null )
            {
                String new_pid = cargo.getIdentifierAsString();
                                
                boolean hasObject = objectRepository.hasObject( cargo.getIdentifier() );
                log.debug( String.format( "hasObject( %s ) returned %b",new_pid, hasObject ) );
                if ( hasObject )
                {
                    log.trace( String.format( "will try to delete pid %s", new_pid ) );
                    objectRepository.deleteObject( new_pid, "delte before store hack" );
                }
            }
            
            objectRepository.storeObject( cargo, logm, "auto" );
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to store CargoContainer with id %s, submitter %s and format %s", cargo.getIdentifierAsString(), cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter(), cargo.getCargoObject( DataStreamType.OriginalData ).getFormat() );
            log.error( error, ex);
            throw new PluginException( error, ex );
        }
        
        return cargo;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }


    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }
}