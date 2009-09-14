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

import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import dk.dbc.opensearch.common.xml.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;

import java.util.logging.Level;
import javax.xml.namespace.NamespaceContext;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import org.apache.log4j.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
//import org.w3c.dom.DocumentException;
//import org.w3c.dom.DocumentFactory;
import org.w3c.dom.Element;
//import org.w3c.dom.Namespace;
import javax.xml.namespace.NamespaceContext;
//import org.w3c.dom.DOMElement;
import javax.xml.parsers.SAXParser;
//import org.w3c.io.SAXReader;
import java.util.List;
import java.util.Map;
import javax.xml.stream.events.Namespace;
import org.xml.sax.SAXException;


/**
 *
 */
public class DocbookMerger implements IProcesser
{

    private static Logger log = Logger.getLogger( DocbookMerger.class );
    private PluginType pluginType = PluginType.PROCESS;
    private NamespaceContext nsc;

    public DocbookMerger()
    {
        log.trace( "Entered DocbookMerger()" );
        nsc = new OpensearchNamespaceContext();
    }


    /**
     * 
     * @param cargo
     * @return
     * @throws PluginException
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.trace( String.format( "Entered getCargoContainer, streams in container: %s", cargo.getCargoObjectCount() ) );
        CargoObject dc = null;
        Element annotation = null;

        if( cargo.hasCargo( DataStreamType.DublinCoreData ) )
        {
            dc = cargo.getCargoObject( DataStreamType.DublinCoreData );

            Document dc_doc = documentFromCargoObject( dc );
            annotation = dc_doc.getDocumentElement();
        }

        CargoObject orig = cargo.getCargoObject( DataStreamType.OriginalData );

        Document doc = documentFromCargoObject( orig );
        List<Namespace> nsMap = null;
        try
        {
            nsMap = XMLUtils.getNamespaces( new ByteArrayInputStream( orig.getBytes() ) );
        }
        catch( XMLStreamException xmlex )
        {
            String msg = String.format( "Could not retrieve namespaces from original xml", xmlex.getMessage() );
            log.error(  msg );
            throw new PluginException( msg, xmlex );
        }

        Element root = doc.getDocumentElement();

        List<Namespace> ns_list = annotation.additionalNamespaces();
        ns_list.add( annotation.getNamespace() );

        ns_list.add( root.getNamespace() );

        Namespace ns = new Namespace( "ting", "http://www.dbc.dk/ting/" );

        Element tingElement = new DOMElement( "container", ns );

        for( Namespace ns_from_list : ns_list )
        {
            //adds namespaces from the Dublin Core Document and the old original data
            log.debug( String.format( "Appending namespace %s", ns_from_list ) );
            tingElement.add( ns_from_list );
        }

        DocumentFactory factory = new DocumentFactory();

        Document new_document = factory.createDocument();

        new_document.setRootElement( tingElement );

        Element new_root = new_document.getRootElement();

        if( dc != null )
        {
            log.debug( String.format( "Adding  annotation data to new xml" ) );
            new_root.add( annotation );
        }

        new_root.add( root );

        String new_original_data = new_document.asXML();
        log.trace( String.format( "Original xml: %s", new String( orig.getBytes() ) ) );
        log.trace( String.format( "New xml: %s", new_original_data ) );
        log.debug( String.format( "Adding annotated data to CargoContainer with alias '%s', overwriting original data", orig.getIndexingAlias() ) );
        // orig.updateByteArray( new_original_data.getBytes() );
        log.debug( String.format( "Removing data with id %s", orig.getId() ) );

        if( !cargo.remove( orig.getId() ) )
        {
            log.warn( String.format( "Could not remove data with id %s", orig.getId() ) );
        }

        long new_id = 0;

        try
        {
            new_id = cargo.add( orig.getDataStreamType(),
                    orig.getFormat(),
                    orig.getSubmitter(),
                    orig.getLang(),
                    orig.getMimeType(),
                    orig.getIndexingAlias(),
                    new_original_data.getBytes() );
        }
        catch( IOException ioe )
        {
            log.error( String.format( "Could not add to CargoContainer", ioe.getMessage() ) );
            throw new PluginException( String.format( "Could not add to CargoContainer", ioe.getMessage() ) );
        }
        // } catch (IOException ioe) {
        //     log.fatal( "Could not add Annotation data to CargoContainer" );
        //     throw new PluginException( "Could not add Annotation data to CargoContainer", ioe );
        // }

        // changeme
        log.debug( String.format( "New xml data: %s", new String( cargo.getCargoObject( new_id ).getBytes() ) ) );
        return cargo;

    }


    private Document documentFromCargoObject( CargoObject co ) throws PluginException
    {
        byte[] bytes = co.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream( bytes );
        Document doc = null;
        try
        {
            doc = XMLUtils.documentFromInputStream( is );
        }
            catch( ParserConfigurationException pcex )
            {
                log.fatal( String.format( "Could not read the original data inputstream into a Document: '%s'", pcex.getMessage() ) );
                throw new PluginException( "Could not read the original data inputstream into a Document", pcex );
            }
            catch( SAXException saex )
            {
                log.fatal( String.format( "Could not read the original data inputstream into a Document: '%s'", saex .getMessage()) );
                throw new PluginException( "Could not read the original data inputstream into a Document", saex );
            }
            catch( IOException ioex )
            {
                log.fatal( String.format( "Could not read the original data inputstream into a Document: '%s'", ioex.getMessage() ) );
                throw new PluginException( "Could not read the original data inputstream into a Document", ioex );
            }

        return doc;
    }

    public PluginType getPluginType()
    {
        return pluginType;
    }


}
