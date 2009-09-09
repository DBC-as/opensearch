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
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.List;
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
    private FedoraAdministration fa;


    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation()
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

        FedoraObjectRelations fedor = new FedoraObjectRelations();

        String relation = "isMemberOf";
        String workRelation = null;
        if( ! types.contains( dcType ) )
        {
            log.debug( String.format( "finding work relations for dcType %s", dcType ) );
            String sSourceWorkRelation = null;
            String sTitleWorkRelation = null;
            String tTitleWorkRelation = null;
            String tSourceWorkRelation = null;

            log.debug( String.format( "WR with dcSource '%s' and dcTitle '%s'", dcSource, dcTitle) );
            if ( ! dcSource.equals( "" ) )
            {
                sSourceWorkRelation = fedor.getSubjectRelations( "source", dcSource, relation );
                if ( sSourceWorkRelation == null && ! dcTitle.equals( "" ) )
                {
                    sTitleWorkRelation = fedor.getSubjectRelations( "source", dcTitle, relation );
                }
            }

            if ( sSourceWorkRelation == null && sTitleWorkRelation == null && ! dcTitle.equals( "" ) )
            {
                if ( ! dcSource.equals( "" ) )
                {
                    tSourceWorkRelation = fedor.getSubjectRelations( "title", dcSource, relation );
                }
                else
                {
                    tTitleWorkRelation = fedor.getSubjectRelations( "title", dcTitle, relation );
                }
            }
            
            if( sSourceWorkRelation != null )
            {
                workRelation = sSourceWorkRelation;
            }
            else if( sTitleWorkRelation != null )
            {
                workRelation = sTitleWorkRelation;
            }
            else if( tSourceWorkRelation != null )
            {
                workRelation = tSourceWorkRelation;
            }
            else if( tTitleWorkRelation != null )
            {
                workRelation = tTitleWorkRelation;
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcSource ) );
            }
        }
        else
        {

            //String titlePid = null;
            if ( ! ( dcTitle.equals( "" ) || dcCreator.equals( "" ) ) )
            {
                //titlePid = fedor.getSubjectRelations( "title", dcTitle, "creator", dcCreator, relation );
                workRelation = fedor.getSubjectRelations( "title", dcTitle, "creator", dcCreator, relation );
            }
            /*if( titlePid != null )
            {
                workRelation = titlePid;
            }*/
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcCreator ) );
            }
        }
        log.debug( String.format( "workRelation = %s", workRelation) );

        if ( workRelation == null )
        {
            // this is a new workrelation, lets get a pid
            workRelation = PIDManager.getInstance().getNextPID( "work" );
        }

        log.debug( String.format( "Trying to add %s to the collection %s", cargo.getDCIdentifier(), workRelation ) );

        // and add this workrelation pid as the workrelationpid of the
        return fedor.addPidToCollection( cargo.getDCIdentifier(), workRelation );
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }
}
