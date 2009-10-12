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


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import fedora.server.types.gen.RelationshipTuple;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;
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
    private FedoraObjectRelations fedor;
    private final FedoraHandle fedoraHandle;
    private IObjectRepository objectRepository;


    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation_1() throws PluginException
    {
        log.debug( "MarcxchangeWorkRelation constructor called" );
    
        types = new Vector<String>();
        types.add( "Anmeldelse" );
        types.add( "Artikel" );
        types.add( "Avis" );
        types.add( "Avisartikel" );
        types.add( "Tidsskrift" );
        types.add( "Tidsskriftsartikel" );
       try
        {
            this.fedoraHandle = new FedoraHandle();
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to get connection to fedora base" );
            log.error( error );
            throw new PluginException( error, ex );
        }

        try
        {
            fedor = new FedoraObjectRelations( objectRepository );
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to obtain connection to fedora repository" );
            log.error( error );
            throw new PluginException( error, ex );
        }
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
            ok = addWorkRelationForMaterial( cargo );
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to add work relation for %s: %s", cargo.getIdentifier(), ex.getMessage() );
            log.error( error );
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


    private boolean addWorkRelationForMaterial( CargoContainer cargo ) throws PluginException, ObjectRepositoryException, ConfigurationException, MalformedURLException, IOException, ServiceException
    {
        DublinCore dc = cargo.getDublinCoreMetaData();
        if( dc == null )
        {
            String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", cargo.getIdentifier() );
            log.error( error );
            throw new PluginException( error );
        }

        String dcTitle = dc.getDCValue( DublinCoreElement.ELEMENT_TITLE );
        String dcType = dc.getDCValue( DublinCoreElement.ELEMENT_TYPE );
        String dcCreator = dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR );
        String dcSource = dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE );
        String operator = "has";

        List<String> fedoraPids = new ArrayList<String>();
        List<String> searchFields = new ArrayList<String>( 1 );
        if( !types.contains( dcType ) )

        //String title = FedoraConstants.TITLE.getValue();
        //String source = FedoraConstants.SOURCE.getValue();
        //String creator = FedoraConstants.CREATOR.getValue();

        if( ! types.contains( dcType ) )
        {
            log.debug( String.format( "finding work relations for dcType %s", dcType ) );
            if ( ! dcSource.equals( "" ) )
            {
                log.debug( String.format( "1 WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle ) );
                //<<<<<<< .working
                //workRelation = fedor.getSubjectRelation( "source", dcSource, relation );

                searchFields.add( "source" );
                fedoraPids = objectRepository.getIdentifiers( dcSource, searchFields, 10000 );

                //if ( workRelation == null && ! dcTitle.equals( "" ) )
                if ( fedoraPids.size() == 0 && ! dcTitle.equals( "" ) )
// =======
                
//                 fedoraPids = fa.findQualifiedObjectPids( source, operator, dcSource );
//                 if ( fedoraPids.size() == 0 && ! dcTitle.equals( "" ) )
//>>>>>>> .merge-right.r6436
                {
                    //<<<<<<< .working
                    //workRelation = fedor.getSubjectRelation( "source", dcTitle, relation );
                    searchFields.clear();
                    searchFields.add( "title" );
                    fedoraPids = objectRepository.getIdentifiers( dcTitle, searchFields, 10000 );
                    
// =======
//                     fedoraPids = fa.findQualifiedObjectPids( source, operator, dcTitle );
//>>>>>>> .merge-right.r6436
                }
            }

            if ( ( fedoraPids == null || fedoraPids.size() == 0 ) && ! dcTitle.equals( "" ) )
            {
                log.debug( String.format( "2 WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle ) );
                if ( ! dcSource.equals( "" ) )
                {
                    //<<<<<<< .working
                    searchFields.clear();
                    searchFields.add( "title" );

                    fedoraPids = objectRepository.getIdentifiers( dcSource, searchFields, 10000 );
                    //fedoraPids = fa.findObjectPids( "title", operator, dcSource );
// =======
//                     fedoraPids = fa.findQualifiedObjectPids( title, operator, dcSource );
// >>>>>>> .merge-right.r6436
                }
                else
                {
                    //<<<<<<< .working
                    searchFields.clear();
                    searchFields.add( "title" );

                    fedoraPids = objectRepository.getIdentifiers( dcTitle, searchFields, 10000 );
// =======
//                     fedoraPids = fa.findQualifiedObjectPids( title, operator, dcTitle );
// >>>>>>> .merge-right.r6436
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
                //<<<<<<< .working
                searchFields.clear();
                searchFields.add( "title" );
                searchFields.add( "creator" );

                fedoraPids = objectRepository.getIdentifiers( dcTitle, searchFields, 10000 );

// =======
//                 fedoraPids = fa.findQualifiedObjectPids( title, creator, operator, dcTitle, dcCreator );
// >>>>>>> .merge-right.r6436
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

        String[] nextWorkPid = null;
        RelationshipTuple[] rels = null;

        String workPid;
        if ( fedoraPids == null || fedoraPids.size() == 0 )
        {
            //workRelation = PIDManager.getInstance().getNextPID( "work" );
            nextWorkPid = fedoraHandle.getNextPID( 1, "work" );
            //nextWorkPid = PIDManager.getInstance().getNextPID( "work" );
            log.debug( String.format( "nextWorkPid found: %s", nextWorkPid[0] ) );
            CreateWorkObject( nextWorkPid[0] );
        }
        else // fedoraPids.size() > 0
        {
            String pid = cargo.getIdentifier();
            log.debug( String.format( "CC pid: %s; fedoraPids.length: %s", pid, fedoraPids.size() ) );
            for( String foundpid : fedoraPids )
            {

                log.debug( String.format( "checking fedoraPid for equality: %s with pid: %s", foundpid, pid ) );
                if ( ! foundpid.equals( pid ) )
                {
                    log.debug( String.format( "New PID found: %s (curr pid is: %s)", foundpid, pid ) );
                    String predicate = "info:fedora/fedora-system:def/relations-external#isMemberOf";
                    rels = fedor.getRelationships( foundpid, predicate );
                    log.debug( String.format( "Relationships as tuple: %s, length %s", rels.toString(), rels.length ) );
                    break;
                }

            }

            if ( rels != null )
            {
                RelationshipTuple rel = rels[0];
                nextWorkPid[0] = rel.getObject();
                log.debug( String.format( "Relationship found: %s (from rel: %s)", nextWorkPid[0], rel ) );
            }
            else
            {
                nextWorkPid = fedoraHandle.getNextPID( 1, "work" );
                CreateWorkObject( nextWorkPid[0] );
                
            }
        }

        //log.debug( String.format( "Trying to add %s to the collection %s", cargo.getDCIdentifier(), workRelation ) );
        log.debug( String.format( "Trying to add %s to the collection %s", cargo.getIdentifier(), nextWorkPid[0] ) );

        // and add this workrelation pid as the workrelationpid of the
        fedor.addPidToCollection( cargo.getIdentifier(), nextWorkPid[0] );
        // try 
        // {  
            this.fedoraHandle.addRelationship( nextWorkPid[0], "info:fedora/fedora-system:def/relations-external#Contains", cargo.getIdentifier(), true, null );
          
            //            FedoraHandle.getInstance().getAPIM().addRelationship( nextWorkPid, "info:fedora/fedora-system:def/relations-external#Contains", cargo.getIdentifier(), true, null );
        // }
        // catch( OpenSearchRepositoryException e)
        // {
            //log.error( String.format("Problem in addPidToCollection %s -> %s", nextWorkPid, cargo.getIdentifier()), e);
            //        }
        return true;
    }


    private void CreateWorkObject( String nextWorkPid ) throws IOException
    {      
        // todo: Clean up work object xml and language. 
        CargoContainer cargo = new CargoContainer( nextWorkPid );  
        String fakexml="<fisk></fisk>";
        cargo.add( DataStreamType.OriginalData, "format", "internal", "da", "text/xml", IndexingAlias.None , fakexml.getBytes());

        try 
        { 
            this.objectRepository.storeObject( cargo, "internal" );
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

    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
    }
}
