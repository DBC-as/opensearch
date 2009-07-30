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
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class OwnerRelation implements IRelation
{
    private static Logger log = Logger.getLogger( OwnerRelation.class );


    private PluginType pluginType = PluginType.OWNER;
    
    // Relations
    private final String free = String.format( "info:fedora/%s", "free" );
    private final String materialevurderinger = String.format( "info:fedora/%s", "materialevurderinger" );
    private final String forfatterweb = String.format( "info:fedora/%s", "forfatterweb" );
    private final String faktalink = String.format( "info:fedora/%s", "faktalink" );
    private final String aakb_catalog = String.format( "info:fedora/%s", "aakb_catalog" );
    private final String aakb_ebrary = String.format( "info:fedora/%s", "aakb_ebrary" );
    private final String aakb_ebsco = String.format( "info:fedora/%s", "aakb_ebsco" );
    private final String kkb_catalog = String.format( "info:fedora/%s", "kkb_catalog" );
    private final String louisiana = String.format( "info:fedora/%s", "louisiana" );
    private final String nota = String.format( "info:fedora/%s", "nota" );
    

    /**
     * Constructor for the OwnerRelation plugin.
     */
    public OwnerRelation()
    {
        log.debug( "OwnerRelation constructor called" );
    }


    /**
     * The "main" method of this plugin. 
     *
     * @param CargoContainer The CargoContainer to...     *
     * @returns ...     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo, String submitter, String format ) throws PluginException
    {
    	log.debug( "Owner -> getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "OwnerRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "OwnerRelation getCargoContainer throws NullPointerException" ) );
        }
        else 
        {
            log.debug( "OwnerRelation getCargoContainer cargo is not null" );
        }
        
        String pid = cargo.getDCIdentifier();
        boolean ok = false;
        log.debug( String.format( "owner relation with values: submitter: '%s'; format: '%s'", submitter, format ) );        
        if ( submitter == "dbc" )
        {        	
        	if ( format.equals( "anmedelser" ) )
        	{
        		ok = addRelationship( pid, free );
        	}
        	else if ( format.equals( "materialevurderinger" ) )
        	{
        		ok = addRelationship( pid, materialevurderinger );
        	}
        	else if ( format.equals( "forfatterweb" ) )
        	{
        		ok = addRelationship( pid, forfatterweb );
        	}
        	else if ( format.equals( "faktalink" ) )
        	{
        		ok = addRelationship( pid, faktalink );
        	}
        	else if ( format.equals( "dr_forfatteratlas" ) )
        	{
        		ok = addRelationship( pid, free );	
        	}
        	else if ( format.equals( "dr_bonanza" ) )
        	{
        		ok = addRelationship( pid, free );	
        	}
        	else if ( format.equals( "louisiana" ) )
        	{
        		ok = addRelationship( pid, louisiana );
        	}
        	else
        	{
        		throw new PluginException( String.format( "format '%s' from submitter '%s' could not be processed!", format, submitter ) );
        	}
        }
        else if ( submitter.equals( "kkb" ) )
        {
        	if ( format.equals( "danmarcxchange" ) )
        	{
        		ok = addRelationship( pid, kkb_catalog );
        	}
        	else 
        	{
        		throw new PluginException( String.format( "format '%s' from submitter '%s' could not be processed!", format, submitter ) );
        	}
        }
        else if ( submitter.equals( "aakb" ) )
        {
        	if ( format.equals( "danmarcxchange" ) )
        	{
        		ok = addRelationship( pid, aakb_catalog );	
        	} 
        	else if ( format.equals( "ebrary" ) )
        	{
        		ok = addRelationship( pid, aakb_ebrary );	
        	} 
        	else if ( format.equals( "ebsco" ) )
        	{
        		ok = addRelationship( pid, aakb_ebsco );
        	} 
        	else 
        	{
        		throw new PluginException( String.format( "format '%s' from submitter '%s' could not be processed!", format, submitter ) );
        	}
        } 
        else if ( submitter.equals( "nota" ) )
    	{
        	ok = addRelationship( pid, nota );
    	} 
    	else 
    	{
    		throw new PluginException( String.format( "submitter '%s' could not be processed!", submitter ) );
    	}
        
        return cargo;
    }
    
    
    private boolean addRelationship( String pid, String namespace ) throws PluginException
    {
    	FedoraAdministration fa = new FedoraAdministration();        
    	boolean ok = false;
		try 
		{
			ok = fa.addIsMbrOfCollRelationship( pid, namespace );
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
