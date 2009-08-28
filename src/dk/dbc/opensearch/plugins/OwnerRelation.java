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
 * \brief Adding owner relation information to fedora repository objects
 */
package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.FedoraRelsExt;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.namespace.QName;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Plugin for adding owner relation to cargoContainers
 */
public class OwnerRelation implements IRelation
{

    private static Logger log = Logger.getLogger( OwnerRelation.class );
    private FedoraRelsExt rel;
    private PluginType pluginType = PluginType.RELATION;
    // Relations
    /** \todo: this is a subject for configuration/javascripting */
    private final String info = "info:fedora/%s";
    private final String free = String.format( info, "free" );
    private final String materialevurderinger = String.format( info, "materialevurderinger" );
    private final String forfatterweb = String.format( info, "forfatterweb" );
    private final String faktalink = String.format( info, "faktalink" );
    private final String artikler = String.format( info, "artikler" );
    private final String aakb_catalog = String.format( info, "aakb_catalog" );
    private final String aakb_ebrary = String.format( info, "aakb_ebrary" );
    private final String aakb_ebsco = String.format( info, "aakb_ebsco" );
    private final String kkb_catalog = String.format( info, "kkb_catalog" );
    private final String louisiana = String.format( info, "louisiana" );
    private final String nota = String.format( info, "nota" );

    /**
     * Constructor for the OwnerRelation plugin.
     */
    public OwnerRelation()
    {

        log.trace( "OwnerRelation constructor called" );
    }


    /**
     * Entry point of the plugin
     * @param CargoContainer The {@link CargoContainer} to construct the owner relations from
     * @returns a {@link CargoContainer} containing a RELS-EXT stream reflecting the owner relations
     * @throws PluginException, which wraps all exceptions thrown from
     * within this plugin, please refer to {@link PluginException} for
     * more information on how to retrieve information on the
     * originating exception.
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( "getCargoContainer() called" );

        if( cargo == null )
        {
            log.error( "No cargo in CargoContainer, aborting the construction of owner relations" );
            throw new PluginException( new NullPointerException( "No cargo in CargoContainer, aborting the construction of owner relations" ) );
        }

        String submitter = null;
        String format = null;

        if( cargo.hasCargo( DataStreamType.OriginalData ) )
        {
            CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
            submitter = co.getSubmitter();
            format = co.getFormat();
        }
        else
        {
            log.error( "CargoContainer has no OriginalData to contruct relations from, aborting" );
            throw new PluginException( new IllegalStateException( "CargoContainer has no OriginalData to contruct relations from, aborting" ) );
        }

        String pid = cargo.getDCIdentifier();

        try
        {
            // RELS-EXT object for carrying the relations information with the CargoContainer
            rel = new FedoraRelsExt( pid );
        }
        catch( ParserConfigurationException pce )
        {
            throw new PluginException( String.format( "Could not create RELS-EXT stream for cargo with pid='%s'", pid ), pce );
        }

        log.debug( String.format( "owner relation with values: submitter: '%s'; format: '%s'", submitter, format ) );
        if( submitter.equals( "dbc" ) )
        {
            log.trace( String.format( "submitter 'dbc' format: %s", format ) );
            if( format.equals( "anmeldelser" ) || format.equals( "anmeld" ) )
            {
                addRelationship( pid, free );
            }
            else if( format.equals( "materialevurderinger" ) || format.equals( "matvurd" ) )
            {
                addRelationship( pid, materialevurderinger );
            }
            else if( format.equals( "forfatterweb" ) || format.equals( "forfatterw" ) )
            {
                addRelationship( pid, forfatterweb );
            }
            else if( format.equals( "faktalink" ) )
            {
                addRelationship( pid, faktalink );
            }
            else if( format.equals( "dr_forfatteratlas" ) || format.equals( "dr_atlas" ) )
            {
                addRelationship( pid, free );
            }
            else if( format.equals( "dr_bonanza" ) )
            {
                addRelationship( pid, free );
            }
            else if( format.equals( "louisiana" ) )
            {
                addRelationship( pid, louisiana );
            }
            else if( format.equals( "artikler" ) )
            {
                addRelationship( pid, artikler );
            }
            else if( format.equals( "dsd" ) )
            {
                addRelationship( pid, free );
            }
            else
            {
                log.error( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
                throw new PluginException( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
            }
        }
        else if( submitter.equals( "kkb" ) || submitter.equals( "710100" ) )
        {
            log.debug( String.format( "submitter 'kkb' or '710100' format: %s", format ) );
            if( format.equals( "danmarcxchange" ) || format.equals( "katalog" ) )
            {
                addRelationship( pid, kkb_catalog );
            }
            else
            {
                log.error( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
                throw new PluginException( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
            }
        }
        else if( submitter.equals( "aakb" ) || submitter.equals( "775100" ) )
        {
            log.debug( String.format( "submitter 'aakb' or '775100' format: %s", format ) );
            if( format.equals( "danmarcxchange" ) || format.equals( "katalog" ) )
            {
                addRelationship( pid, aakb_catalog );
            }
            else if( format.equals( "ebrary" ) )
            {
                addRelationship( pid, aakb_ebrary );
            }
            else if( format.equals( "ebsco" ) )
            {
                addRelationship( pid, aakb_ebsco );
            }
            else
            {
                log.error( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
                throw new PluginException( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
            }
        }
        else if( submitter.equals( "nota" ) || submitter.equals( "874310" ) )
        {
            addRelationship( pid, nota );
        }
        else
        {
            log.error( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
            throw new PluginException( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
        }

        log.trace( "getCargoContainer() returning" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            rel.serialize( baos );
        }
        catch( TransformerConfigurationException tcex )
        {
            log.error( String.format( "Could not construct byte[] from rels ext document on pid '%s'", pid ) );
            throw new PluginException( String.format( "Could not construct byte[] from rels ext document on pid '%s'", pid ), tcex );
        }
        catch( TransformerException tex )
        {
            log.error( String.format( "Could not construct byte[] from rels ext document on pid '%s'", pid ) );
            throw new PluginException( String.format( "Could not construct byte[] from rels ext document on pid '%s'", pid ), tex );
        }
        try
        {
            cargo.add( DataStreamType.RelsExt, "rels-ext", "dbc", "en", "application/rdf+xml", IndexingAlias.None, baos.toByteArray() );
        }
        catch( IOException ex )
        {
            log.error( String.format( "Could not add RELS-EXT stream to CargoContainer (pid '%s')", pid ) );
            throw new PluginException( String.format( "Could not add RELS-EXT stream to CargoContainer (pid '%s')", pid ) );
        }
        return cargo;
    }


    private boolean addRelationship( String pid, String namespace ) throws PluginException
    {
        FedoraAdministration fa = new FedoraAdministration();
        boolean ok = false;
        try
        {
            log.debug( String.format( "OR addRelationship with pid: '%s'; namespace: '%s'", pid, namespace ) );
            ok = fa.addIsMbrOfCollRelationship( pid, namespace );
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

        log.trace( String.format( "Added relation: '%s' (pid: '%s')", ok, pid ) );
        return ok;
    }


    private boolean addRelationToRelsExt( String pid, String namespace )// throws PluginException
    {

        FedoraNamespaceContext fns = new FedoraNamespaceContext();
        QName pred = new QName( fns.getNamespaceURI( "rel" ),
                "isMemberOfCollection",
                "fedora" );

        QName obj = new QName( "", namespace, "" );
        boolean added = false;

        added = rel.addRelationship( pred, obj );

        return added;
    }


    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }


}
