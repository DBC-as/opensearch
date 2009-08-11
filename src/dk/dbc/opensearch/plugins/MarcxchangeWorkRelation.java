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
public class MarcxchangeWorkRelation implements IRelation
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation.class );


    private PluginType pluginType = PluginType.WORKRELATION;
    private NamespaceContext nsc;
    private Vector< String > types;
    private final String namespace = "work";


    /**
     * Constructor for the DocbookAnnotate plugin.
     */
    public MarcxchangeWorkRelation()
    {
        log.debug( "MarcxchangeWorkRelation constructor called" );
        nsc = new OpensearchNamespaceContext();
        
        types = new Vector< String >();
        types.add( "Anmeldelse" );
        types.add( "Artikel" );
        types.add( "Avis" );
        types.add( "Avisartikel" );
        types.add( "Tidsskrift" );
        types.add( "Tidsskriftsartikel" );
    }


    /**
     * The "main" method of this plugin. Request a relation from
     * a webservice. If a relation is available it is added to the
     * cargocontainer in a new stream typed RelsExtData
     *
     * @param CargoContainer The CargoContainer to add relations to
     *
     * @returns A CargoContainer containing relations
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo, String submitter, String format ) throws PluginException//, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
    	log.debug( "DWR -> getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "MarcxchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "MarcxchangeWorkRelation getCargoContainer throws NullPointerException" ) );
        }
        else 
        {
            log.debug( "MarcxchangeWorkRelation getCargoContainer cargo is not null" );
        }
        
        String dcTitle = cargo.getDCTitle().toLowerCase();
        String dcType = cargo.getDCType().toLowerCase();
        String dcCreator = cargo.getDCCreator().toLowerCase();
        String dcSource = cargo.getDCSource().toLowerCase();
        String dcIdentifier = cargo.getDCIdentifier();
        log.debug( String.format( "relation with values: dcIdentifier (pid): '%s'; dcTitle: '%s'; dcType: '%s'; dcCreator: '%s'; dcSource: '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
        
        boolean ok = false;
        log.debug( String.format( "MWR dcType: '%s'", dcType ) );
        if ( ! types.contains( dcType ) )
        {        	
        	log.debug( String.format( "MWR entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType, dcTitle ) );
        	if ( dcSource.equals( new String( "Harry Potter and the Order of the Phoenix" ).toLowerCase() ) )
            {
                log.debug( String.format( "ORDER OF THE PHOENIX: pid '%s'; title '%s'; type '%s'; creator '%s' source '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ));
            }
            
        	// 1. match SOURCE: dcTitle on TARGET: dcTitle        	
        	if ( ! dcTitle.equals( "" ) )
        	{
                log.debug( String.format( "MWR trying match on title and title", "" ) );
        		ok = addRelationship( dcIdentifier, "title", dcTitle );
        		log.debug( String.format("relationship added on title '%s' and title, pid: '%s'", dcTitle, dcIdentifier ) );
        	}
        	
            if ( ! dcSource.equals( "" ) && ! ok ) // 2. match SOURCE: dcSource on TARGET: dcTitle
        	{
                log.debug( String.format( "MWR trying match on source and title", "" ) );
        		ok = addRelationship( dcIdentifier, "title", dcSource );
        		log.debug( String.format( "relationship added on title with dcSource '%s' and pid: '%s'", dcSource, dcIdentifier ) );
        	}
        	
            if ( ! dcSource.equals( "" ) && ! ok ) // 3. match SOURCE: dcSource on TARGET: dcSource
        	{
                log.debug( String.format( "MWR trying match on source and source", "" ) );
        		ok = addRelationship( dcIdentifier, "source", dcSource );
        		log.debug( String.format( "relationship added on source with dcSource '%s' and pid: '%s'", dcSource, dcIdentifier ) );
        	}
            
            if ( ! dcTitle.equals( "" ) && ! ok ) // 4. match SOURCE: dcTitle on TARGET: dcSource
        	{
                log.debug( String.format( "MWR trying match on title and source", "" ) );
        		ok = addRelationship( dcIdentifier, "source", dcTitle );
        		log.debug( String.format( "relationship added on source with dcTitle '%s' and pid: '%s'", dcTitle, dcIdentifier ) );
        	}

            if ( ! ok )
        	{
        		log.warn( String.format( "dcVariable was", "" ) );
        	}
        }
        else // dcType is in ('Anmeldelse', 'Artikel', 'Avis', 'Avisartikel', 'Tidsskrift', 'Tidsskriftsartikel') 
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
