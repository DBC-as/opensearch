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
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.IPredicate;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import java.io.IOException;
import java.net.MalformedURLException;

import java.util.List;
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

    private IObjectRepository objectRepository;
    
    private final IPredicate isMemberOfWork = DBCBIB.IS_MEMBER_OF_WORK;
    private final IPredicate hasManifestation = DBCBIB.HAS_MANIFESTATION;
    private final IPredicate reviewOf = DBCBIB.IS_REVIEW_OF;
    private final IPredicate hasReview = DBCBIB.HAS_REVIEW;


    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public PurgeRelations() throws PluginException
    {
        log.trace( "PurgeRelations constructor called" );
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
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "MarcxchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "MarcxchangeWorkRelation getCargoContainer throws NullPointerException" ) );
        }

        boolean ok = false;
        try
        {
            log.debug( String.format( "purging work relations", "" ) );
            ok = purgeWorkRelationForMaterial( cargo );
            log.debug( String.format( "work relations purged: %s", ok ) );

            if ( ok )
            {
                log.debug( String.format( "purging anmeldelses relations", "" ) );
                ok = purgeAnmeldelsesRelationForMaterial( cargo );
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


    synchronized private boolean purgeAnmeldelsesRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        DublinCore dc = cargo.getDublinCoreMetaData();

        if ( dc == null )
        {
            String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( error );
        }

        ObjectIdentifier identifier = cargo.getIdentifier();

        if ( objectRepository.hasObject( identifier ) )
        {
            log.debug( String.format( "Getting object relations for '%s' with relation '%s'", identifier, hasReview.getPredicateString() ) );
            String subject = identifier.getIdentifier();
            List< InputPair< IPredicate, String > > relations = objectRepository.getObjectRelations( subject, hasReview.getPredicateString() );
            
            if ( relations != null )
            {
                for ( InputPair pair : relations )
                {
                    String anmeldelse = pair.getSecond().toString();
                    log.debug( String.format( "Getting object relations for '%s' with relation '%s'", anmeldelse, reviewOf.getPredicateString() ) );
                    List< InputPair< IPredicate, String > > anmeldelsesRelations = objectRepository.getObjectRelations( anmeldelse, reviewOf.getPredicateString() );
                    if ( anmeldelsesRelations != null && anmeldelsesRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", anmeldelse, reviewOf.getPredicateString(), subject ) );
                        ObjectIdentifier anmeldelsesIdentifier = new dk.dbc.opensearch.common.fedora.PID( anmeldelse );
                        objectRepository.removeObjectRelation( anmeldelsesIdentifier, reviewOf, subject );
                    }
                }
            }
        }

        return true;
    }


    synchronized private boolean purgeWorkRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        DublinCore dc = cargo.getDublinCoreMetaData();

        if ( dc == null )
        {
            String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( error );
        }

        ObjectIdentifier identifier = cargo.getIdentifier();
        
        if ( objectRepository.hasObject( identifier ) )
        {
            String subject = identifier.getIdentifier();
            log.debug( String.format( "Getting work relations for post '%s' with relation '%s'", subject, isMemberOfWork.getPredicateString() ) );
            List< InputPair< IPredicate, String > > relations = objectRepository.getObjectRelations( subject, isMemberOfWork.getPredicateString() );
            
            if ( relations != null )
            {
                for ( InputPair pair : relations )
                {
                    String work = pair.getSecond().toString();
                    log.debug( String.format( "Getting relations for work '%s' with relation '%s'", work, hasManifestation.getPredicateString() ) );
                    List< InputPair< IPredicate, String > > workRelations = objectRepository.getObjectRelations( work, hasManifestation.getPredicateString() );
            
                    if ( workRelations != null && workRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", work, hasManifestation.getPredicateString(), subject ) );
                        ObjectIdentifier workIdentifier = new dk.dbc.opensearch.common.fedora.PID( work );
                        objectRepository.removeObjectRelation( workIdentifier, hasManifestation, subject );
                    }
                    else
                    {
                        log.debug( "no relations to purge" );
                    }
                }
            }
        }
        
        return true;
    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


    @Override
    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }

    @Override
    public void setArgs( Map<String, String> argsMap )
    {}

    @Override
    public boolean validateArgs( Map<String, String> argsMap )
    {
        return true;
    }
}
