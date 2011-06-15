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
import dk.dbc.opensearch.metadata.DBCBIB;
//import dk.dbc.opensearch.metadata.DublinCore;
import dk.dbc.opensearch.metadata.IPredicate;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.IObjectIdentifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class PurgeRelationsEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( PurgeRelationsEnvironment.class );

    private IObjectRepository objectRepository;

    private final IPredicate isMemberOfWork = DBCBIB.IS_MEMBER_OF_WORK;
    private final IPredicate hasManifestation = DBCBIB.HAS_MANIFESTATION;
    private final IPredicate reviewOf = DBCBIB.IS_REVIEW_OF;
    private final IPredicate hasReview = DBCBIB.HAS_REVIEW;

    public PurgeRelationsEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        this.objectRepository = repository;
    }


    public boolean purgeAnmeldelsesRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        // DublinCore dc = cargo.getDublinCoreMetaData();

        // if ( dc == null )
        // {
        //     String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
        //     log.error( error );
        //     throw new PluginException( error );
        // }

        IObjectIdentifier identifier = cargo.getIdentifier();

        if ( objectRepository.hasObject( identifier ) )
        {
            log.debug( String.format( "Getting object relations for '%s' with relation '%s'", identifier, hasReview.getPredicateString() ) );
            String subject = identifier.getIdentifier();
            List< Pair< IPredicate, String > > relations = objectRepository.getObjectRelations( subject, hasReview.getPredicateString() );
            
            if ( relations != null )
            {
                for ( Pair pair : relations )
                {
                    String anmeldelse = pair.getSecond().toString();
                    log.debug( String.format( "Getting object relations for '%s' with relation '%s'", anmeldelse, reviewOf.getPredicateString() ) );
                    List< Pair< IPredicate, String > > anmeldelsesRelations = objectRepository.getObjectRelations( anmeldelse, reviewOf.getPredicateString() );
                    if ( anmeldelsesRelations != null && anmeldelsesRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", anmeldelse, reviewOf.getPredicateString(), subject ) );
                        IObjectIdentifier anmeldelsesIdentifier = new dk.dbc.opensearch.fedora.PID( anmeldelse );
                        objectRepository.removeObjectRelation( anmeldelsesIdentifier, reviewOf, subject );
                    }
                }
            }
        }

        return true;
    }



    public boolean purgeWorkRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        // DublinCore dc = cargo.getDublinCoreMetaData();

        // if ( dc == null )
        // {
        //     String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
        //     log.error( error );
        //     throw new PluginException( error );
        // }

        IObjectIdentifier identifier = cargo.getIdentifier();
        
        if ( objectRepository.hasObject( identifier ) )
        {
            String subject = identifier.getIdentifier();
            log.debug( String.format( "Getting work relations for post '%s' with relation '%s'", subject, isMemberOfWork.getPredicateString() ) );
            List< Pair< IPredicate, String > > relations = objectRepository.getObjectRelations( subject, isMemberOfWork.getPredicateString() );
            
            if ( relations != null )
            {
                for ( Pair pair : relations )
                {
                    String work = pair.getSecond().toString();
                    log.debug( String.format( "Getting relations for work '%s' with relation '%s'", work, hasManifestation.getPredicateString() ) );
                    List< Pair< IPredicate, String > > workRelations = objectRepository.getObjectRelations( work, hasManifestation.getPredicateString() );
            
                    if ( workRelations != null && workRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", work, hasManifestation.getPredicateString(), subject ) );
                        IObjectIdentifier workIdentifier = new dk.dbc.opensearch.fedora.PID( work );
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

}