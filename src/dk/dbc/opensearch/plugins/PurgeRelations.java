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


import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.pluginframework.PluginType;
import dk.dbc.opensearch.types.CargoContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook cargoContainers
 */
public class PurgeRelations implements IPluggable
{
    private static Logger log = Logger.getLogger( PurgeRelations.class );


    private PluginType pluginType = PluginType.RELATION;

    //    private IObjectRepository objectRepository;
    //    private PurgeRelationsEnvironment env = null;
    


    /**
     * Constructor for the PurgeRelation plugin.
     */
    public PurgeRelations( IObjectRepository repository ) throws PluginException
    {
        // log.trace( "PurgeRelations constructor called" );

        // Map< String, String> tmpMap = new HashMap< String, String >();
        // env = (PurgeRelationsEnvironment)this.createEnvironment( repository, tmpMap );
    }


    /**
     * The "main" method of this plugin. Request a relation from
     * a webservice. If a relation is available it is added to the
     * cargocontainer in a new stream typed RelsExtData
     *
     * @param cargo The CargoContainer to add relations to
     *
     * @return A CargoContainer containing relations
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {
        log.trace( "runPlugin() called" );

        if( !( ienv instanceof PurgeRelationsEnvironment) )
        {
            String errMsg = String.format( "The given PluginEnvironment is of incorrect type. Expected: %s, got: %s", "PurgeRelationsEnvironment", ienv.getClass().getName() );
            log.error( errMsg );
            throw new PluginException( errMsg );
        }

        PurgeRelationsEnvironment env = (PurgeRelationsEnvironment)ienv;

        if( cargo == null )
        {
            log.error( "cargo is null" );
            throw new PluginException( new NullPointerException( "PurgeRelation runPlugin throws NullPointerException" ) );
        }

        boolean ok = false;
        try
        {
            log.debug( String.format( "purging work relations", "" ) );
            synchronized(this)
            {
                ok = env.purgeWorkRelationForMaterial( cargo );
            }
            log.debug( String.format( "work relations purged: %s", ok ) );

            if ( ok )
            {
                log.debug( String.format( "purging anmeldelses relations", "" ) );
                synchronized(this)
                {
                    ok = env.purgeAnmeldelsesRelationForMaterial( cargo );
                }
                log.debug( String.format( "anmeldelses relations purged: %s", ok ) );
            }
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error , ex);
            throw new PluginException( error, ex );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
            throw new PluginException( error, ex );
        }

        if ( ! ok )
        {
            log.error( String.format( "could not add work relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


    private boolean validateArgs( Map<String, String> argsMap )
    {
        return true;
    }


    @Override
    public IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new PurgeRelationsEnvironment( repository, args );
    }
}
