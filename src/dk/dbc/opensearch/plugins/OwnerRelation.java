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

import dk.dbc.opensearch.common.fedora.FedoraRelsExt;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;

import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.ComparablePair;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.namespace.QName;
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

    private final Map<ComparablePair<String,String>, String> ownertable;
    /**
     * Constructor for the OwnerRelation plugin.
     */
    public OwnerRelation()
    {
        ownertable = new HashMap<ComparablePair<String, String>, String>();
        ownertable.put( new ComparablePair<String, String>( "dbc", "anmeldelser" ), String.format( info, "free" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "anmeld" ), String.format( info, "free" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "materialevurderinger" ), String.format( info, "materialevurderinger" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "matvurd" ), String.format( info, "materialevurderinger" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "forfatterweb" ), String.format( info, "forfatterweb" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "forfatterw" ), String.format( info, "forfatterweb" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "faktalink" ), String.format( info, "faktalink" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "dr_forfatteratlas" ), String.format( info, "free" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "dr_atlas" ), String.format( info, "free" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "dr_bonanza" ), String.format( info, "free" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "louisiana" ), String.format( info, "louisiana" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "artikler" ), String.format( info, "artikler" ) );
        ownertable.put( new ComparablePair<String, String>( "dbc", "dsd" ), String.format( info, "free" ) );

        ownertable.put( new ComparablePair<String, String>( "kkb", "danmarcxchange" ), String.format( info, "kkb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "kkb", "katalog" ), String.format( info, "kkb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "710100", "danmarcxchange" ), String.format( info, "kkb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "710100", "katalog" ), String.format( info, "kkb_catalog" ) );

        ownertable.put( new ComparablePair<String, String>( "aakb", "danmarcxchange" ), String.format( info, "aakb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "aakb", "katalog" ), String.format( info, "aakb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "aakb", "ebrary" ), String.format( info, "aakb_ebrary" ) );
        ownertable.put( new ComparablePair<String, String>( "aakb", "ebsco" ), String.format( info, "aakb_ebsco" ) );
        ownertable.put( new ComparablePair<String, String>( "775100", "danmarcxchange" ), String.format( info, "aakb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "775100", "katalog" ), String.format( info, "aakb_catalog" ) );
        ownertable.put( new ComparablePair<String, String>( "775100", "ebrary" ), String.format( info, "aakb_ebrary" ) );
        ownertable.put( new ComparablePair<String, String>( "775100", "ebsco" ), String.format( info, "aakb_ebsco" ) );

        ownertable.put( new ComparablePair<String, String>( "nota", "" ), String.format( info, "" ) );
        ownertable.put( new ComparablePair<String, String>( "874310", "" ), String.format( info, "" ) );
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

        String pid = cargo.getIdentifier();

        try
        {
            // RELS-EXT object for carrying the relations information with the CargoContainer
            rel = new FedoraRelsExt( pid );
        }
        catch( ParserConfigurationException pce )
        {
            throw new PluginException( String.format( "Could not create RELS-EXT stream for cargo with pid='%s'", pid ), pce );
        }

        String owner = ownertable.get( new ComparablePair<String, String>( submitter, format ) );
        if( owner == null )
        {
            log.error( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
            throw new PluginException( String.format( "no processing rule found for format '%s', submitter '%s' (pid '%s')", format, submitter, pid ) );
        }

        //target xml is <isMemberOfCollection rdf:resource="info:fedora/{owner}" xmlns="info:fedora/fedora-system:def/relations-external#"/>
        QName predicate = new QName( FedoraNamespaceContext.FedoraNamespace.FEDORARELSEXT.getURI(),
                "isMemberOfCollection",
                FedoraNamespaceContext.FedoraNamespace.FEDORARELSEXT.getPrefix() );
        QName object = new QName( "",
                owner,
                FedoraNamespaceContext.FedoraNamespace.FEDORA.getURI() );
        
        rel.addRelationship( predicate, object);
       
        cargo.addMetaData( rel );
        return cargo;
    }

    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    public void setObjectRepository( IObjectRepository objectRepository )
    {
        //just do nothing. We need no objectRepository here anymore.
    }
}
