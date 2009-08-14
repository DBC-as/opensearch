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
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.Vector;

import java.util.logging.Level;
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
    private PluginType pluginType = PluginType.RELATION;
    private NamespaceContext nsc;
    private Vector<String> types;
    private final String namespace = "work";
    private FedoraAdministration fa;

    /**
     * Constructor for the MarcxchangeWorlkRelation plugin.
     */
    public MarcxchangeWorkRelation()
    {
        log.debug( "MarcxchangeWorkRelation constructor called" );
        nsc = new OpensearchNamespaceContext();

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

        if( cargo == null )
        {
            log.error( "MarcxchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "MarcxchangeWorkRelation getCargoContainer throws NullPointerException" ) );
        }
//        else
//        {
//            log.debug( "MarcxchangeWorkRelation getCargoContainer cargo is not null" );
//        }
//
//        String dcTitle = cargo.getDCTitle().toLowerCase();
//        String dcType = cargo.getDCType().toLowerCase();
//        String dcCreator = cargo.getDCCreator().toLowerCase();
//        String dcSource = cargo.getDCSource().toLowerCase();
//        String dcIdentifier = cargo.getDCIdentifier();
//        log.debug( String.format( "relation with values: dcIdentifier (pid): '%s'; dcTitle: '%s'; dcType: '%s'; dcCreator: '%s'; dcSource: '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
//
//        boolean ok = false;
//        log.debug( String.format( "MWR dcType: '%s'", dcType ) );
//        if( !types.contains( dcType ) )
//        {
//            log.debug( String.format( "MWR entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType, dcTitle ) );
//            if( dcSource.equals( new String( "Harry Potter and the Order of the Phoenix" ).toLowerCase() ) )
//            {
//                log.debug( String.format( "ORDER OF THE PHOENIX: pid '%s'; title '%s'; type '%s'; creator '%s' source '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
//            }
//
//            // 1. match SOURCE: dcTitle on TARGET: dcTitle
//            if( !dcTitle.equals( "" ) )
//            {
//                log.debug( String.format( "MWR trying match on title and title", "" ) );
//                ok = addRelationship( dcIdentifier, "title", dcTitle );
//                log.debug( String.format( "relationship added on title '%s' and title, pid: '%s'", dcTitle, dcIdentifier ) );
//            }
//
//            if( !dcSource.equals( "" ) && !ok ) // 2. match SOURCE: dcSource on TARGET: dcTitle
//            {
//                log.debug( String.format( "MWR trying match on source and title", "" ) );
//                ok = addRelationship( dcIdentifier, "title", dcSource );
//                log.debug( String.format( "relationship added on title with dcSource '%s' and pid: '%s'", dcSource, dcIdentifier ) );
//            }
//
//            if( !dcSource.equals( "" ) && !ok ) // 3. match SOURCE: dcSource on TARGET: dcSource
//            {
//                log.debug( String.format( "MWR trying match on source and source", "" ) );
//                ok = addRelationship( dcIdentifier, "source", dcSource );
//                log.debug( String.format( "relationship added on source with dcSource '%s' and pid: '%s'", dcSource, dcIdentifier ) );
//            }
//
//            if( !dcTitle.equals( "" ) && !ok ) // 4. match SOURCE: dcTitle on TARGET: dcSource
//            {
//                log.debug( String.format( "MWR trying match on title and source", "" ) );
//                ok = addRelationship( dcIdentifier, "source", dcTitle );
//                log.debug( String.format( "relationship added on source with dcTitle '%s' and pid: '%s'", dcTitle, dcIdentifier ) );
//            }
//
//            if( !ok )
//            {
//                log.warn( String.format( "dcVariable was", "" ) );
//            }
//        }
//        else // dcType is in ('Anmeldelse', 'Artikel', 'Avis', 'Avisartikel', 'Tidsskrift', 'Tidsskriftsartikel')
//        {
//            // match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
//            if( !(dcTitle.equals( "" ) && dcCreator.equals( "" )) )
//            {
//                ok = addRelationship( dcIdentifier, "title", dcTitle, "creator", dcCreator );
//                log.debug( String.format( "relationship added on title and creator with dcTitle '%s' and dcCreator '%s' and pid: '%s'", dcTitle, dcCreator, dcIdentifier ) );
//            }
//            else
//            {
//                log.warn( String.format( "dcSource '%s' is empty", dcSource ) );
//            }
//        }
//
//        log.debug( String.format( "MWR (pid: '%s') found dcVariables: '%s', '%s', '%s', and '%s'", dcIdentifier, dcTitle, dcType, dcCreator, dcSource ) );
//        log.debug( "Adding relationship succeeded: " + ok );
        boolean ok = false;
        ok = addWorkRelationForMaterial( cargo );

        if( !ok )
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

        List<String> pids = new ArrayList<String>();
        if( !types.contains( dcType ) )
        {
            //pids = fa.findMatchingFieldPids( "title", dcTitle );
            List<String> titlePids = fa.findMatchingFieldPids( "title", dcTitle );
            List<String> sTitlePids = fa.findMatchingFieldPids( "source", dcTitle );
            List<String> sourcePids = fa.findMatchingFieldPids( "source", dcSource );
            List<String> tSourcePids = fa.findMatchingFieldPids( "title", dcSource );
//            String[] sourcePids = fa.findObjectPids( "source", "has", dcSource );

            if( !titlePids.isEmpty() )
            {
                pids.addAll( titlePids );
            }
            else if( !sTitlePids.isEmpty() )
            {
                pids.addAll( sTitlePids );
            }
            else if( !sourcePids.isEmpty() )
            {
                pids.addAll( sourcePids );
            }
            else if( !tSourcePids.isEmpty() )
            {
                pids.addAll( tSourcePids );
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcSource ) );
            }
        }
        else
        {
            List<String> titlePids = fa.findMatchingFieldPids( "title", dcTitle );
            List<String> creatorPids = fa.findMatchingFieldPids( "creator", dcCreator );
            titlePids.retainAll( creatorPids );

            if( !titlePids.isEmpty() )
            {
                pids.addAll( titlePids );
            }
            else
            {
                log.debug( String.format( "No matching posts found for '%s' or '%s'", dcTitle, dcCreator ) );
            }
        }
        log.debug( String.format( "pids = %s", pids.toString() ) );

        //find work-pid for all pids in pids
        HashMap<String, String> workPids = new HashMap<String, String>();

        for( String pid : pids )
        {
            List<String> workRelationPid = fedor.getSubjectRelationships( pid, "isMemberOf" );
            log.debug( String.format( "pid %s, workRelationPid = %s", pid, workRelationPid.toString() ) );
            if( workRelationPid.isEmpty() )
            {
                log.warn( String.format( "no workrelation was found for pid %s", pid ) );
            }
            else if( workRelationPid.size() > 1 )
            {
                //there should be only one work-pid per pid, ie. the work-relation 
                // should be identical for all found pids, as they match on the
                // workrelation criteria
                log.warn( String.format( "the pid %s has more than one work it relates to (works: %s)", pid, workRelationPid.toString() ) );
                /** \todo: should more be done about this?*/
            }

            if( workRelationPid.size() >= 1 )
            {
                // the client has been duly warned, now we'll just select the first
                // (and hopefully only) of the returned workrelation pids
                workPids.put( pid, workRelationPid.get( 0 ) );
            }
        }

        // lets check that there was only one workpid for all the returned
        // materials:
        String theone = null;
        ArrayList<String> howmany = new ArrayList<String>( workPids.values() );
        log.debug( String.format( "workpids list = %s", howmany.toString() ) );
        if( howmany.size() > 1 )
        {
            log.warn( String.format( "The CargoContainer %s matched more than one workrelation ( = %s)", cargo.getDCTitle(), howmany.toString() ) );
        }
        else if( howmany.isEmpty() )
        {
            // this is a new workrelation, lets get a pid
            theone = PIDManager.getInstance().getNextPID( "work" );
        }

        if( howmany.size() >= 1)
        {
            //again, the client has been duly warned, we simply take the first, and
            //hopefully only value:
            theone = howmany.get( 0 );
            log.debug( String.format( "Adding workrelation %s to pid %s", theone, cargo.getDCIdentifier() ) );
        }

        log.debug( String.format( "Trying to add %s to the collection %s", cargo.getDCIdentifier(), theone ) );
        // and add this workrelation pid as the workrelationpid of the
        // cargocontainer material:
        return fedor.addPidToCollection( cargo.getDCIdentifier(), theone );
    }


//    private boolean addRelationship( String dcIdentifier, String property_1, String dcVariable_1, String property_2, String dcVariable_2 ) throws PluginException
//    {
//        FedoraAdministration fa = new FedoraAdministration();
//        boolean ok = false;
//        try
//        {
//            ok = fa.addIsMbrOfCollRelationship( dcIdentifier, property_1, dcVariable_1, property_2, dcVariable_2, namespace );
//        }
//        catch( RemoteException re )
//        {
//            throw new PluginException( "RemoteException thrown from FedoraAdministration.addIsMbrOfCollRelationship", re );
//        }
//        catch( ConfigurationException ce )
//        {
//            throw new PluginException( "ConfigurationException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ce );
//        }
//        catch( MalformedURLException mue )
//        {
//            throw new PluginException( "MalformedURLException thrown from FedoraAdministration.addIsMbrOfCollRelationship", mue );
//        }
//        catch( NullPointerException npe )
//        {
//            throw new PluginException( "NullPointerException thrown from FedoraAdministration.addIsMbrOfCollRelationship", npe );
//        }
//        catch( ServiceException se )
//        {
//            throw new PluginException( "ServiceException thrown from FedoraAdministration.addIsMbrOfCollRelationship", se );
//        }
//        catch( IOException ioe )
//        {
//            throw new PluginException( "IOException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ioe );
//        }
//
//        return ok;
//    }
    private boolean addRelationship( String dcIdentifier, String property, String dcVariable ) throws PluginException
    {
        //FedoraAdministration fa = new FedoraAdministration();
        boolean ok = false;
        try
        {
            ok = fa.addIsMbrOfCollRelationship( dcIdentifier, property, dcVariable, namespace );
        }
        catch( RemoteException re )
        {
            throw new PluginException( "RemoteException thrown from FedoraAdministration.addIsMbrOfCollRelationship", re );
        }
        catch( ConfigurationException ce )
        {
            throw new PluginException( "ConfigurationException thrown from FedoraAdministration.addIsMbrOfCollRelationship", ce );
        }
        catch( MalformedURLException mue )
        {
            throw new PluginException( "MalformedURLException thrown from FedoraAdministration.addIsMbrOfCollRelationship", mue );
        }
        catch( NullPointerException npe )
        {
            throw new PluginException( "NullPointerException thrown from FedoraAdministration.addIsMbrOfCollRelationship", npe );
        }
        catch( ServiceException se )
        {
            throw new PluginException( "ServiceException thrown from FedoraAdministration.addIsMbrOfCollRelationship", se );
        }
        catch( IOException ioe )
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
