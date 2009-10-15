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
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Vector;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class MarcxchangeWorkRelation implements IRelation
{
    private static Logger log = Logger.getLogger( MarcxchangeWorkRelation.class );


    private PluginType pluginType = PluginType.RELATION;

    private Vector<String> types;
    private IObjectRepository objectRepository;
    private final FedoraHandle fedoraHandle;


    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation() throws PluginException
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
}

    public void setObjectRepository( IObjectRepository objectRepository )
    {
        this.objectRepository = objectRepository;
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
        ok = addWorkRelationForMaterial( cargo );

        if ( ! ok )
        {
            log.error( String.format( "could not add work relation on pid %s", cargo.getIdentifier() ) );
        }

        return cargo;
    }


    private boolean addWorkRelationForMaterial( CargoContainer cargo ) throws PluginException
    {
        DublinCore dc = cargo.getDublinCoreMetaData();
        String identifier = cargo.getIdentifier();

        if( dc == null )
        {
            String error = String.format( "CargoContainer with identifier %s contains no DublinCore data", identifier );
            log.error( error );
            throw new PluginException( error );
        }

        String dcTitle = dc.getDCValue( DublinCoreElement.ELEMENT_TITLE );
        String dcType = dc.getDCValue( DublinCoreElement.ELEMENT_TYPE );
        String dcCreator = dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR );
        String dcSource = dc.getDCValue( DublinCoreElement.ELEMENT_SOURCE );

        log.trace( String.format( "Found dc values: title: %s, type: %s, creator: %s, source: %s for identifier %s", dcTitle, dcType, dcCreator, dcSource, identifier ) );

        FedoraObjectRelations fedor;
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

        String relation = "isMemberOf";
        String workRelation = null;
        if( ! types.contains( dcType ) )
        {
            log.debug( String.format( "finding work relations for dcType %s", dcType ) );
            log.debug( String.format( "WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle) );
            if ( ! "".equals( dcSource.trim() ) )
            {
                try
                {
                    workRelation = fedor.getSubjectRelation( "source", dcSource, relation );
                    if( workRelation == null && !dcTitle.equals( "" ) )
                    {
                        workRelation = fedor.getSubjectRelation( "source", dcTitle, relation );
                    }
                }
                catch( ConfigurationException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error, ex );
                    throw new PluginException( error, ex );
                }
                catch( ServiceException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error, ex );
                    throw new PluginException( error, ex );
                }
                catch( MalformedURLException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error, ex );
                    throw new PluginException( error, ex );
                }
                catch( IOException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error, ex );
                    throw new PluginException( error, ex );
                }
            }
            if ( workRelation == null && ! "".equals( dcTitle.trim() ) )
            {
                try{
                    if( !dcSource.equals( "" ) )
                    {
                        workRelation = fedor.getSubjectRelation( "title", dcSource, relation );
                    }
                    else
                    {
                        workRelation = fedor.getSubjectRelation( "title", dcTitle, relation );
                    }
                }
                catch( ConfigurationException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( ServiceException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( MalformedURLException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( IOException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcSource, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }

            }

            if ( workRelation == null )
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s' on object with id '%s'", dcTitle, dcSource, identifier ) );
            }
        }
        else
        {

            if ( ! ( dcTitle.equals( "" ) || dcCreator.equals( "" ) ) )
            {
                try
                {
                    workRelation = fedor.getSubjectRelation( "title", dcTitle, "creator", dcCreator, relation );
                }
                catch( ConfigurationException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcCreator, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( ServiceException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcCreator, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( MalformedURLException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcCreator, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
                catch( IOException ex )
                {
                    String error = String.format( "Failed to retrieve work relation for rules on %s and %s", dcCreator, dcTitle );
                    log.error( error );
                    throw new PluginException( error, ex );
                }
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s' on object with id: %s", dcTitle, dcCreator, identifier ) );
            }
        }

        if ( workRelation == null )
        {
                String[] newPid = null;
            try
            {
                // this is a new workrelation, lets get a pid
                newPid = fedoraHandle.getNextPID( 1,  "work" );
            }
            catch( ServiceException ex )
            {
                String error = String.format( "Failed to retrieve work relation for new workrelation: %s", ex.getMessage() );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
            catch( ConfigurationException ex )
            {
                String error = String.format( "Failed to retrieve new work relation for object '%s': %s", ex.getMessage() );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
            catch( MalformedURLException ex )
            {
                String error = String.format( "Failed to retrieve new work relation for object '%s': %s", ex.getMessage() );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
            catch( IOException ex )
            {
                String error = String.format( "Failed to retrieve new work relation for object '%s': %s", ex.getMessage() );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }
            catch( IllegalStateException ex )
            {
                String error = String.format( "Failed to retrieve new work relation for object '%s': %s", ex.getMessage() );
                log.error( error, ex );
                throw new PluginException( error, ex );
            }

            if( null == newPid || 0 == newPid.length )
            {
                String error = String.format( "pid is empty for namespace '%s', but no exception was caught.", "work" );
                log.error( error );
                throw new PluginException( new IllegalStateException( error ) );
            }
            // we assume that there was only one pid, per the instructions to getNextPID
            workRelation = newPid[0];
        }
        log.debug( String.format( "workRelation = %s", workRelation) );

        if( identifier == null || "".equals( identifier.trim() ) )
        {
            String error = String.format( "cargo contained no identifier, shouldn't have gotten this far without" );
            log.error( error );
            throw new PluginException( error );
        }
        log.debug( String.format( "Trying to add %s to the collection %s", identifier, workRelation ) );
        boolean addedPidToCollection = false;
        try
        {
            // and add this workrelation pid as the workrelationpid of the 
             addedPidToCollection = fedor.addPidToCollection( identifier, workRelation );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to contruct work relation between %s and %s", identifier, workRelation );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to contruct work relation between %s and %s", identifier, workRelation );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to contruct work relation between %s and %s", identifier, workRelation );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        return addedPidToCollection;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }
}
