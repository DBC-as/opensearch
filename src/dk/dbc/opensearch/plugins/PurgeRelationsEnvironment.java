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
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.metadata.DBCBIB;
import dk.dbc.opensearch.metadata.IPredicate;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.IObjectIdentifier;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class PurgeRelationsEnvironment implements IPluginEnvironment
{

    private static Logger log = LoggerFactory.getLogger( PurgeRelationsEnvironment.class );

    private final FcrepoReader reader;
    private final FcrepoModifier modifier;

    private final IPredicate isMemberOfWork = DBCBIB.IS_MEMBER_OF_WORK;
    private final IPredicate hasManifestation = DBCBIB.HAS_MANIFESTATION;
    private final IPredicate reviewOf = DBCBIB.IS_REVIEW_OF;
    private final IPredicate hasReview = DBCBIB.HAS_REVIEW;

    public PurgeRelationsEnvironment( FcrepoReader reader, FcrepoModifier modifier, Map<String, String> args ) throws PluginException
    {
        this.reader = reader;
        this.modifier = modifier;
    }


    public boolean purgeAnmeldelsesRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        String pid = cargo.getIdentifierAsString();

        if ( reader.hasObject( pid ) )
        {
            log.debug( String.format( "Getting object relations for '%s' with relation '%s'", pid, hasReview.getPredicateString() ) );
            List< Pair< IPredicate, String > > relations = modifier.getObjectRelations( pid, hasReview.getPredicateString() );
            
            if ( relations != null )
            {
                for ( Pair pair : relations )
                {
                    String anmeldelse = pair.getSecond().toString();
                    log.debug( String.format( "Getting object relations for '%s' with relation '%s'", anmeldelse, reviewOf.getPredicateString() ) );
                    List< Pair< IPredicate, String > > anmeldelsesRelations = modifier.getObjectRelations( anmeldelse, reviewOf.getPredicateString() );
                    if ( anmeldelsesRelations != null && anmeldelsesRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", anmeldelse, reviewOf.getPredicateString(), pid ) );
                        IObjectIdentifier anmeldelsesIdentifier = new dk.dbc.opensearch.fedora.PID( anmeldelse );
                        modifier.removeObjectRelation( anmeldelsesIdentifier, reviewOf, pid );
                    }
                }
            }
        }

        return true;
    }



    public boolean purgeWorkRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        String subject = cargo.getIdentifierAsString();
        
        if ( reader.hasObject( subject ) )
        {
            log.debug( String.format( "Getting work relations for post '%s' with relation '%s'", subject, isMemberOfWork.getPredicateString() ) );
            List< Pair< IPredicate, String > > relations = modifier.getObjectRelations( subject, isMemberOfWork.getPredicateString() );
            
            if ( relations != null )
            {
                for ( Pair pair : relations )
                {
                    String work = pair.getSecond().toString();
                    log.debug( String.format( "Getting relations for work '%s' with relation '%s'", work, hasManifestation.getPredicateString() ) );
                    List< Pair< IPredicate, String > > workRelations = modifier.getObjectRelations( work, hasManifestation.getPredicateString() );
            
                    if ( workRelations != null && workRelations.size() > 0 )
                    {
                        log.debug( String.format( "Purging object relations for work '%s' with relation '%s' to '%s'", work, hasManifestation.getPredicateString(), subject ) );
                        IObjectIdentifier workIdentifier = new dk.dbc.opensearch.fedora.PID( work );
                        modifier.removeObjectRelation( workIdentifier, hasManifestation, subject );
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
