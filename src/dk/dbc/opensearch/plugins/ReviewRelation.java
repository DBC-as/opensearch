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


import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import dk.dbc.opensearch.common.types.TargetFields;
import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Plugin for creating relations between reviews and there target
 */
public class ReviewRelation implements IRelation
{
    private static Logger log = Logger.getLogger( ReviewRelation.class );


    private PluginType pluginType = PluginType.RELATION;

    private Vector< String > types;
    private final String marterialevurderinger = "Materialevurdering:?";
    private final String anmeldelse = "Anmeldelse";
    private final String namespace = "review";
    private IObjectRepository objectRepository;


    /**
     * Constructor for the ReviewRelation plugin.
     */
    public ReviewRelation()
    {
        log.trace( "Constructor called" );
        //nsc = new OpensearchNamespaceContext();

        types = new Vector< String >();
        types.add( "Bog" );
        types.add( "DVD" );
        types.add( "CD" );
        types.add( "Wii-spil" );
        types.add( "Playstation-spil" );
        types.add( "Playstation2-spil" );
        types.add( "Playstation3-spil" );
        types.add( "DVD-Rom" );
        types.add( "Gameboy" );
        types.add( "XBOX-spil" );
        types.add( "Tegneserie" );
        types.add( "Billedbog" );
    }


    /**
     * The "main" method of this plugin.
     *
     * @param CargoContainer The CargoContainer to add relations to
     *
     * @returns A CargoContainer containing relations
     *
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException//, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        log.trace( "getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "ReviewRelation getCargoContainer cargo is null" );
            throw new PluginException( "CargoContainer contains no data, aborting" );
        }

        boolean ok = false;
        ok = addReviewRelation( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add review relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    private boolean addReviewRelation( CargoContainer cargo ) throws PluginException
    {
        boolean ok = false;
        DublinCore dc = cargo.getDublinCoreMetaData();
        String dcTitle = dc.getDCValue( DublinCoreElement.ELEMENT_TITLE );
        String dcType = dc.getDCValue( DublinCoreElement.ELEMENT_TYPE );
        String dcCreator = dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR );
        String dcSource = dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE );
        String identifier = cargo.getIdentifierAsString();

        log.debug( String.format( "relation with values: dcIdentifier (pid): '%s'; dcTitle: '%s'; dcType: '%s'; dcCreator: '%s'; dcSource: '%s'", identifier, dcTitle, dcType, dcCreator, dcSource ) );

        if ( dcType.equals( marterialevurderinger )|| types.contains( dcType ) )
        {
            //log.trace( String.format( "entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType, dcTitle ) );

            //findtarget

            // match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
            if ( ! ( dcTitle.equals( "" ) && dcCreator.equals( "" ) ) )
            {
                try
                {
                    TargetFields targetTitle = FedoraObjectFields.TITLE;
                    TargetFields targetCreator = FedoraObjectFields.CREATOR;
                    ok = addRelationship( identifier, targetTitle, dcTitle, targetCreator, dcCreator );
                }
                catch( ObjectRepositoryException ex )
                {
                    String error = String.format( "Failed to add Relationship on %s with %s and %s", identifier, dcTitle, dcCreator );
                    log.error( error );
                    throw new PluginException( error, ex );
                }

                log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle, dcCreator, identifier ) );
            }
            else
            {
                log.warn( String.format( "dcSource '%s' is empty", dcSource ) );
            }
        }
        //  else if ( types.contains( dcType ) )
        //         {
        //              // match SOURCE: dcTitle and dcCreator on TARGET dcTitle and dcCreator
        //              if ( ! ( dcTitle.equals( "" ) && dcCreator.equals( "" ) ) )
        //              {
        //                 try
        //                 {
        //                     TargetFields targetTitle = FedoraObjectFields.TITLE;
        //                     TargetFields targetCreator = FedoraObjectFields.CREATOR;
        //                     ok = addRelationship( identifier, targetTitle, dcTitle, targetCreator, dcCreator );
        //                 }
        //                 catch( ObjectRepositoryException ex )
        //                 {
        //                     String error = String.format( "Failed to add Relationship on %s with %s and %s", identifier, dcTitle, dcCreator );
        //                     log.error( error );
        //                     throw new PluginException( error, ex );
        //                 }

        //                      log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle, dcCreator, identifier ) );
        //              }
        //              else
        //              {
        //                      log.warn( String.format( "dcSource '%s' is empty", dcSource ) );
        //              }
        //         }
        else if ( dcType.equals( anmeldelse ) )
        {
            // match SOURCE: dcRelation on TARGET: identifier
            if ( ! dcTitle.equals( "" ) )
            {
                //find target
                try
                {
                    TargetFields targetSource = FedoraObjectFields.SOURCE;
                    ok = addRelationship( identifier, targetSource, dcTitle );
                }
                catch ( ObjectRepositoryException ex )
                {
                    String error = String.format( "Failed to add Relationship on %s with source -> %s", identifier, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                log.debug( String.format( "relationship added on source with dcTitle '%s' and pid: '%s'", dcTitle, identifier ) );
            }
            else
            {
                log.warn( String.format( "dcCreator '%s' is empty", dcCreator ) );
            }
        }

        log.debug( String.format( "MWR (pid: '%s') found dcVariables: '%s', '%s', '%s', and '%s'", identifier, dcTitle, dcType, dcCreator, dcSource ) );
        log.trace( "Adding relationship succeeded: " + ok );

        return ok;
    }


    private boolean addRelationship( String dcIdentifier, TargetFields property_1, String dcVariable_1, TargetFields property_2, String dcVariable_2 ) throws ObjectRepositoryException
    {
        FedoraObjectRelations fedor = new FedoraObjectRelations( objectRepository );

        boolean ok = fedor.addIsMbrOfCollRelationship( dcIdentifier, property_1, dcVariable_1, property_2, dcVariable_2, namespace );

        return ok;
    }


    private boolean addRelationship( String dcIdentifier, TargetFields property, String dcVariable ) throws ObjectRepositoryException
    {
        FedoraObjectRelations fedor = new FedoraObjectRelations( objectRepository );

        boolean  ok = fedor.addIsMbrOfCollRelationship( dcIdentifier, property, dcVariable, namespace );

        return ok;
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
