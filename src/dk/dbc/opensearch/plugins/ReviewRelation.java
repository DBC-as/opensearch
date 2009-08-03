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


import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.Vector;

import javax.xml.namespace.NamespaceContext;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class ReviewRelation implements IRelation
{
    private static Logger log = Logger.getLogger( ReviewRelation.class );


    private PluginType pluginType = PluginType.REVIEWRELATION;
    //private NamespaceContext nsc;
    private Vector< String > types;
    private final String marterialevurderinger = "Materialevurdering:?";
    private final String anmeldelse = "Anmeldelse";
    private final String namespace = "review";


    /**
     * Constructor for the DocbookAnnotate plugin.
     */
    public ReviewRelation()
    {
        log.debug( "ReviewRelation constructor called" );
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
    public CargoContainer getCargoContainer( CargoContainer cargo, String submitter, String format ) throws PluginException//, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
    	log.debug( "RR -> getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "ReviewRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "ReviewRelation getCargoContainer throws NullPointerException" ) );
        }
        else 
        {
            log.debug( "ReviewRelation getCargoContainer cargo is not null" );
        }
        
        String dcTitle = cargo.getDCTitle();
        String dcType = cargo.getDCType();
        String dcCreator = cargo.getDCCreator();
        String dcSource = cargo.getDCSource();
        String dcIdentifier = cargo.getDCIdentifier();
        log.debug( String.format( "relation with values: dcIdentifier (pid): '%s'; dcTitle: '%s'; dcType: '%s'; dcCreator: '%s'; dcSource: '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
        
        boolean ok = false;
        log.debug( String.format( "RR dcType: '%s'", dcType ) );
        if ( dcType.equals( marterialevurderinger ) )
        {        	
        	log.debug( String.format( "MWR entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType, dcTitle ) );
        	
        	// match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
        	if ( ! ( dcTitle.equals( "" ) && dcCreator.equals( "" ) ) )
        	{
        		ok = addRelationship( dcIdentifier, "title", dcTitle, "creator", dcCreator );
        		log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle, dcCreator, dcIdentifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcSource '%s' is empty", dcSource ) ); 
        	}
        }
        else if ( types.contains( dcType ) ) 
        {
        	// match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
        	if ( ! ( dcTitle.equals( "" ) && dcCreator.equals( "" ) ) )
        	{
        		ok = addRelationship( dcIdentifier, "title", dcTitle, "creator", dcCreator );
        		log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle, dcCreator, dcIdentifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcSource '%s' is empty", dcSource ) ); 
        	}
        }
        else if ( dcType.equals( anmeldelse ) )
        {
        	// match SOURCE: dcRelation on TARGET: dcIdentifier
        	if ( ! dcTitle.equals( "" ) )
        	{
        		ok = addRelationship( dcIdentifier, "source", dcTitle );
        		log.debug( String.format( "relationship added on source with dcTitle '%s' and pid: '%s'", dcTitle, dcIdentifier ) );
        	}
        	else
        	{
        		log.warn( String.format( "dcCreator '%s' is empty", dcCreator ) ); 
        	}
        }
        else
        {
        	
        }
        
        log.debug( String.format( "MWR (pid: '%s') found dcVariables: '%s', '%s', '%s', and '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
        log.debug( "Adding relationship succeeded: " + ok );
        
        return cargo;
    }
    
    
    private boolean addRelationship( String dcIdentifier, String property_1, String dcVariable_1, String property_2, String dcVariable_2 ) throws PluginException
    {
    	FedoraAdministration fa = new FedoraAdministration();        
    	boolean ok = false;
		try 
		{
			ok = fa.addIsMbrOfCollRelationship( dcIdentifier, property_1, dcVariable_1, property_2, dcVariable_2, namespace );
		} 
		catch ( RemoteException re ) 
		{		
			throw new PluginException( "RemoteException thrown from FedoraAdministration.addIsMbrOfCollRelationship", re );
		} 
		catch ( ConfigurationException ce ) 
		{	
			throw new PluginException( "ConfigurationException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ce );
		} 
		catch ( MalformedURLException mue ) 
		{
			throw new PluginException( "MalformedURLException thrown from FedoraAdministration.addIsMbrOfCollRelationship", mue );
		} 
		catch ( NullPointerException npe ) 
		{
			throw new PluginException( "NullPointerException thrown from FedoraAdministration.addIsMbrOfCollRelationship", npe );
		} 
		catch ( ServiceException se ) 
		{
			throw new PluginException( "ServiceException thrown from FedoraAdministration.addIsMbrOfCollRelationship", se );
		} 
		catch ( IOException ioe ) 
		{
			throw new PluginException( "IOException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ioe );
		} 
		
		return ok;
    }
    
    
    private boolean addRelationship( String dcIdentifier, String property, String dcVariable ) throws PluginException
    {
    	FedoraAdministration fa = new FedoraAdministration();        
    	boolean ok = false;
		try 
		{
			ok = fa.addIsMbrOfCollRelationship( dcIdentifier, property, dcVariable, namespace );
		} 
		catch ( RemoteException re ) 
		{		
			throw new PluginException( "RemoteException thrown from FedoraAdministration.addIsMbrOfCollRelationship", re );
		} 
		catch ( ConfigurationException ce ) 
		{	
			throw new PluginException( "ConfigurationException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ce );
		} 
		catch ( MalformedURLException mue ) 
		{
			throw new PluginException( "MalformedURLException thrown from FedoraAdministration.addIsMbrOfCollRelationship", mue );
		} 
		catch ( NullPointerException npe ) 
		{
			throw new PluginException( "NullPointerException thrown from FedoraAdministration.addIsMbrOfCollRelationship", npe );
		} 
		catch ( ServiceException se ) 
		{
			throw new PluginException( "ServiceException thrown from FedoraAdministration.addIsMbrOfCollRelationship", se );
		} 
		catch ( IOException ioe ) 
		{
			throw new PluginException( "IOException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ioe );
		}
		
		return ok;
    }

    
    public PluginType getPluginType()
    {
        return pluginType;
    }

}
