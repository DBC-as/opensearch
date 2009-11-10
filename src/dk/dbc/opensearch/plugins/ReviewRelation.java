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


import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.IObjectRepository.Property;
import dk.dbc.opensearch.common.fedora.IObjectRepository.Value;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.util.Vector;

import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
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
     * Constructor for the DocbookAnnotate plugin.
     */
    public ReviewRelation()
    {
        log.trace( "ReviewRelation constructor called" );
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
        Value dcTitle = new Value( dc.getDCValue( DublinCoreElement.ELEMENT_TITLE ) );
        Value dcType = new Value( dc.getDCValue( DublinCoreElement.ELEMENT_TYPE ) );
        Value dcCreator = new Value( dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR ) );
        Value dcSource = new Value( dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE ) );
        String identifier = cargo.getIdentifierAsString();
        Property title = new Property( "title" );
        Property creator = new Property( "creator" );
        Property source = new Property( "source" );

        log.debug( String.format( "relation with values: dcIdentifier (pid): '%s'; dcTitle: '%s'; dcType: '%s'; dcCreator: '%s'; dcSource: '%s'", identifier, dcTitle, dcType, dcCreator, dcSource ) );
        
        if ( dcType.getValue().equals( marterialevurderinger ) )
        {        	
        	log.trace( String.format( "entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType.getValue(), dcTitle.getValue() ) );
        	
        	// match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
        	if ( ! ( dcTitle.getValue().equals( "" ) && dcCreator.getValue().equals( "" ) ) )
        	{
                try
                {
                    ok = addRelationship( identifier, title, dcTitle, creator, dcCreator );
                }
                catch( ObjectRepositoryException ex )
                {
                    String error = String.format( "Failed to add Relationship on %s with %s and %s", identifier, dcTitle.getValue(), dcCreator.getValue() );
                    log.error( error );
                    throw new PluginException( error, ex );
                }

        		log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle.getValue(), dcCreator.getValue(), identifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcSource '%s' is empty", dcSource.getValue() ) );
        	}
        }
        else if ( types.contains( dcType.getValue() ) )
        {
        	// match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
        	if ( ! ( dcTitle.getValue().equals( "" ) && dcCreator.getValue().equals( "" ) ) )
        	{
                try
                {
                    ok = addRelationship( identifier, title, dcTitle, creator, dcCreator );
                }
                catch( ObjectRepositoryException ex )
                {
                    String error = String.format( "Failed to add Relationship on %s with %s and %s", identifier, dcTitle.getValue(), dcCreator.getValue() );
                    log.error( error );
                    throw new PluginException( error, ex );
                }

        		log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle.getValue(), dcCreator.getValue(), identifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcSource '%s' is empty", dcSource.getValue() ) );
        	}
        }
        else if ( dcType.getValue().equals( anmeldelse ) )
        {
        	// match SOURCE: dcRelation on TARGET: identifier
        	if ( ! dcTitle.getValue().equals( "" ) )
        	{
                try
                {
                    ok = addRelationship( identifier, source, dcTitle );
                }
                catch( ObjectRepositoryException ex )
                {
                    String error = String.format( "Failed to add Relationship on %s with source -> %s", identifier, dcTitle.getValue() );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
        		log.debug( String.format( "relationship added on source with dcTitle '%s' and pid: '%s'", dcTitle.getValue(), identifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcCreator '%s' is empty", dcCreator.getValue() ) );
        	}
        }
        
        log.debug( String.format( "MWR (pid: '%s') found dcVariables: '%s', '%s', '%s', and '%s'", identifier, dcTitle, dcType, dcCreator, dcSource ) );
        log.trace( "Adding relationship succeeded: " + ok );
        
        return ok;
    }
    
    
    private boolean addRelationship( String dcIdentifier, Property property_1, Value dcVariable_1, Property property_2, Value dcVariable_2 ) throws ObjectRepositoryException
    {
    	FedoraObjectRelations fedor = new FedoraObjectRelations( objectRepository );

		boolean	ok = fedor.addIsMbrOfCollRelationship( dcIdentifier, property_1, dcVariable_1, property_2, dcVariable_2, namespace );
		
		return ok;
    }
    
    
    private boolean addRelationship( String dcIdentifier, Property property, Value dcVariable ) throws ObjectRepositoryException
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
