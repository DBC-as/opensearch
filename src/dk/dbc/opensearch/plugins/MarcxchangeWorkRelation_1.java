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


import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IFedoraAdministration.FedoraConstants;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import fedora.server.types.gen.RelationshipTuple;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class MarcxchangeWorkRelation_1 implements IRelation
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation_1.class );


    private PluginType pluginType = PluginType.RELATION;
    private Vector<String> types;
    private FedoraAdministration fa;
    private FedoraObjectRelations fedor;


    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation_1()
    {
        log.debug( "MarcxchangeWorkRelation constructor called" );
    
        types = new Vector<String>();
        types.add( "Anmeldelse" );
        types.add( "Artikel" );
        types.add( "Avis" );
        types.add( "Avisartikel" );
        types.add( "Tidsskrift" );
        types.add( "Tidsskriftsartikel" );

        fa = new FedoraAdministration();
        fedor = new FedoraObjectRelations();
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
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException, RemoteException, ConfigurationException, ServiceException, IOException//, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        log.trace( "getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "MarcxchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "MarcxchangeWorkRelation getCargoContainer throws NullPointerException" ) );
        }

        boolean ok = false;
        ok = addWorkRelationForMaterial( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add work relation on pid %s", cargo.getDCIdentifier() ) );
        }

        return cargo;
    }


    private boolean addWorkRelationForMaterial( CargoContainer cargo ) throws RemoteException, ConfigurationException, ServiceException, IOException
    {
        String dcTitle = cargo.getDCTitle();
        String dcType = cargo.getDCType();
        String dcCreator = cargo.getDCCreator();
        String dcSource = cargo.getDCSource();
        String operator = "has";

        String title = FedoraConstants.TITLE.getValue();
        String source = FedoraConstants.SOURCE.getValue();
        String creator = FedoraConstants.CREATOR.getValue();


        ArrayList< String > fedoraPids = null;
        if( ! types.contains( dcType ) )
        {
            log.debug( String.format( "finding work relations for dcType %s", dcType ) );
            if ( ! dcSource.equals( "" ) )
            {
                log.debug( String.format( "1 WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle ) );
                
                fedoraPids = fa.findQualifiedObjectPids( source, operator, dcSource );
                if ( fedoraPids.size() == 0 && ! dcTitle.equals( "" ) )
                {
                    fedoraPids = fa.findQualifiedObjectPids( source, operator, dcTitle );
                }
            }

            if ( ( fedoraPids == null || fedoraPids.size() == 0 ) && ! dcTitle.equals( "" ) )
            {
                log.debug( String.format( "2 WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle ) );
                if ( ! dcSource.equals( "" ) )
                {
                    fedoraPids = fa.findQualifiedObjectPids( title, operator, dcSource );
                }
                else
                {
                    fedoraPids = fa.findQualifiedObjectPids( title, operator, dcTitle );
                }
            }

            if ( fedoraPids == null || fedoraPids.size() == 0 )
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcSource ) );
            }
        }
        else
        {
            if ( ! ( dcTitle.equals( "" ) || dcCreator.equals( "" ) ) )
            {
                log.debug( String.format( "WR with dcTitle '%s' and dcCreator '%s'", dcTitle, dcCreator ) );
                fedoraPids = fa.findQualifiedObjectPids( title, creator, operator, dcTitle, dcCreator );
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcCreator ) );
            }
        }

        if ( fedoraPids != null && fedoraPids.size() > 0 )
        {
            log.debug( String.format( "Pid with matching title, source, or creator = %s", fedoraPids.get( 0 ) ) );
        }

        String nextWorkPid = null;
        RelationshipTuple[] rels = null;
        int fedoraPidsSize = fedoraPids.size();
        if ( fedoraPids == null || fedoraPidsSize == 0 )
        {
            //workRelation = PIDManager.getInstance().getNextPID( "work" );
            nextWorkPid = PIDManager.getInstance().getNextPID( "work" );
            log.debug( String.format( "nextWorkPid found: %s", nextWorkPid ) );
            CreateWorkObject( nextWorkPid );
        }
        else // fedoraPids.size() > 0
        {
            String pid = cargo.getDCIdentifier();
            log.debug( String.format( "CC pid: %s; fedoraPids.length: %s", pid, fedoraPidsSize ) );
            for ( int i = 0; i < fedoraPidsSize; i++ )
            {
                String fedoraPid = fedoraPids.get( i );
                log.debug( String.format( "checking fedoraPid for equality: %s with pid: %s", fedoraPid, pid ) );
                if ( ! fedoraPid.equals( pid ) )
                {
                    log.debug( String.format( "New PID found: %s (curr pid is: %s)", fedoraPid, pid ) );
                    String predicate = "info:fedora/fedora-system:def/relations-external#isMemberOf";
                    rels = fedor.getRelationships( fedoraPid, predicate );
                    log.debug( String.format( "Relationships as tuple: %s, length %s", rels.toString(), rels.length ) );
                    break;
                }
            }

            if ( rels != null )
            {
                RelationshipTuple rel = rels[0];
                nextWorkPid = rel.getObject();
                log.debug( String.format( "Relationship found: %s (from rel: %s)", nextWorkPid, rel ) );
            }
            else
            {
                nextWorkPid = PIDManager.getInstance().getNextPID( "work" );
                CreateWorkObject( nextWorkPid );
                
            }
        }

        //log.debug( String.format( "Trying to add %s to the collection %s", cargo.getDCIdentifier(), workRelation ) );
        log.debug( String.format( "Trying to add %s to the collection %s", cargo.getDCIdentifier(), nextWorkPid ) );

        // and add this workrelation pid as the workrelationpid of the        
        
        fedor.addPidToCollection( cargo.getDCIdentifier(), nextWorkPid );
        try 
        {            
            FedoraHandle.getInstance().getAPIM().addRelationship( nextWorkPid, "info:fedora/fedora-system:def/relations-external#Contains", cargo.getDCIdentifier(), true, null );
        }
        catch( Exception e)
        {
            log.error( String.format("Problem in addPidToCollection %s -> %s",nextWorkPid, cargo.getDCIdentifier()), e);
        }
        return true;
    }


    private void CreateWorkObject(String nextWorkPid) throws IOException
    {      
        // todo: Clean up work object xml and language. 
        CargoContainer cargo = new CargoContainer();       
        String fakexml="<fisk></fisk>";
        cargo.add( DataStreamType.OriginalData, "format", "internal", "da", "text/xml", IndexingAlias.None , fakexml.getBytes());
        cargo.setDCIdentifier( nextWorkPid );
        try 
        { 
            
            fa.storeCargoContainer(cargo, "internal");
            log.debug(String.format("ja7: added work object %s", nextWorkPid));
        } 
        catch(Exception e) 
        {
            log.error("ja7:error in fs.storeCargocontiner for new work item", e);
        }   
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }
}
