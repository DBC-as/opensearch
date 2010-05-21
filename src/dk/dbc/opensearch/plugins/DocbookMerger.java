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

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.OpenSearchTransformException;
import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.TransformerException;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentFragment;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 *
 */
public class DocbookMerger implements IPluggable
{

    private static Logger log = Logger.getLogger( DocbookMerger.class );
    private PluginType pluginType = PluginType.PROCESS;
    private NamespaceContext nsc;
    private IObjectRepository objectRepository;

    public DocbookMerger( IObjectRepository repository )
    {
        this.objectRepository = repository;
        log.debug( "Entered DocbookMerger()" );
        nsc = new OpensearchNamespaceContext();
    }


    /**
     * 'main' plugin method.
     *
     * The purpose of this plugin is to merge Originaldata contained in the
     * CargoContainer with DublinCore data, also contained in the CargoContainer.
     * 
     * @param cargo the input CargoContainer to be processed by the plugin
     * @return the modified CargoContainer
     * @throws PluginException with a nested exception explaining the error
     */
    @Override
    public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap ) throws PluginException
    {
        log.trace( String.format( "Entered getCargoContainer, streams in container: %s", cargo.getCargoObjectCount() ) );

        DublinCore dc = null;

        if( cargo.hasMetadata( DataStreamType.DublinCoreData ) )
        {
            dc = cargo.getDublinCoreMetaData();
        }

        CargoObject orig = cargo.getCargoObject( DataStreamType.OriginalData );

        byte[] orig_bytes = orig.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream( orig_bytes );
        Document doc = null;
        try
        {
            doc = XMLUtils.documentFromInputStream( is );
        }
        catch( ParserConfigurationException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }


        Element origRoot = doc.getDocumentElement();

        ByteArrayOutputStream dc_out = new ByteArrayOutputStream();
        try
        {
            dc.serialize( dc_out, null );
        }
        catch( OpenSearchTransformException ex )
        {
            String error = String.format( "Failed to retrieve Dublin Core metadata from id '%s'", cargo.getIdentifierAsString() );
            log.warn( error, ex );
            log.info( "This plugin will now not merge the OriginalData with the DublinCore metadata" );
        }
        ByteArrayInputStream dc_is = new ByteArrayInputStream( dc_out.toByteArray() );

        String new_original_data = null;

        try
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document tingDoc = builder.newDocument();

            log.debug( "Creating top level element for new xml" );
            Element tingElement = tingDoc.createElementNS( "http://www.dbc.dk/ting/", "ting:container" );

            tingDoc.appendChild( tingElement );

            Document dCDoc = XMLUtils.documentFromInputStream( dc_is );
            Element dcRoot = dCDoc.getDocumentElement();

            log.debug( "Adding DublinCore metadata to new xml" );
            tingDoc.adoptNode( dcRoot );

            log.debug( "Adding OriginalData to new xml" );
            tingDoc.adoptNode( origRoot );

            new_original_data = XMLUtils.xmlToString( tingDoc );
        }
        catch( ParserConfigurationException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( TransformerException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }

        log.trace( String.format( "Original xml: %s", new String( orig.getBytes() ) ) );
        log.trace( String.format( "New xml: %s", new_original_data ) );
        log.debug( String.format( "Adding annotated data to CargoContainer with alias '%s', overwriting original data", orig.getIndexingAlias() ) );

        log.debug( String.format( "Removing data with id %s", orig.getId() ) );
        if( !cargo.remove( orig.getId() ) )
        {
            log.warn( String.format( "Could not remove data with id %s", orig.getId() ) );
        }

        try
        {
            cargo.add( orig.getDataStreamType(),
                    orig.getFormat(),
                    orig.getSubmitter(),
                    orig.getLang(),
                    orig.getMimeType(),
                    orig.getIndexingAlias(),
                    new_original_data.getBytes() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "Could not replace original data in CargoContainer", ioe.getMessage() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        log.trace( String.format( "New xml data: %s", new String( cargo.getCargoObject( orig.getDataStreamType() ).getBytes() ) ) );
        return cargo;
    }


    @Override
    public PluginType getPluginType()
    {
        return this.pluginType;
    }
}
