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
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import javax.xml.namespace.NamespaceContext;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.Namespace;
import org.dom4j.dom.DOMElement;
import org.dom4j.io.SAXReader;
import java.util.List;


/**
 *
 */
public class DocbookMerger implements IProcesser
{
    private static Logger log = Logger.getLogger( DocbookMerger.class );


    private PluginType pluginType = PluginType.PROCESS;
    private  NamespaceContext nsc;


    public DocbookMerger()
    {
        log.debug( "Entered DocbookMerger()" );
        nsc = new OpensearchNamespaceContext();
    }


    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {

        log.debug( String.format( "Entered getCargoContainer, streams in container: %s", cargo.getCargoObjectCount() ) );
        DublinCore dc = null;
        Element annotation = null;
        if( cargo.hasMetadata( DataStreamType.DublinCoreData ) )
        {
            dc = cargo.getDublinCoreMetaData();
//            byte[] dc_data_bytes = dc.getBytes();
//            ByteArrayInputStream dc_is = new ByteArrayInputStream( dc_data_bytes );
//
//            Document dc_doc = null;
//            try{
//                SAXReader reader = new SAXReader();
//                dc_doc = reader.read( dc_is );
//            }catch( DocumentException docex){
//                log.fatal( String.format( "Could not read the dublin core inputstream into a Document: '%s'", docex ) );
//                throw new PluginException( "Could not read the dublin core inputstream into a Document", docex );
//            }
//            annotation = dc_doc.getRootElement();
        }


        CargoObject orig = cargo.getCargoObject( DataStreamType.OriginalData );

        byte[] orig_bytes = orig.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream( orig_bytes );
        Document doc = null;

        try{
            SAXReader reader = new SAXReader();
            doc = reader.read( is );
        }catch( DocumentException docex){
            log.fatal( String.format( "Could not cast the bytearrayinputstream to a inputsource: '%s'", docex ) );
            throw new PluginException( "Could not cast the bytearrayinputstream to a inputsource", docex );
        }

        Element root = doc.getRootElement();

        List<Namespace> ns_list = annotation.additionalNamespaces();
        ns_list.add( annotation.getNamespace() );

        ns_list.add( root.getNamespace() );

        Namespace ns = new Namespace( "ting", "http://www.dbc.dk/ting/");

        Element tingElement = new DOMElement( "container", ns );

        for( Namespace ns_from_list : ns_list ){
            //adds namespaces from the Dublin Core Document and the old original data
            log.debug( String.format( "Appending namespace %s", ns_from_list ) );
            tingElement.add( ns_from_list );
        }

        DocumentFactory factory = new DocumentFactory();

        Document new_document = factory.createDocument();

        new_document.setRootElement( tingElement );

        Element new_root = new_document.getRootElement();

        if( dc != null) {
            log.debug( String.format( "Adding  annotation data to new xml" ) );
            new_root.add( annotation );
        }

        new_root.add( root );

        String new_original_data = new_document.asXML();
        log.debug( String.format( "Original xml: %s", new String( orig.getBytes() ) ) );
        log.debug( String.format( "New xml: %s", new_original_data ) );
        log.debug( String.format( "Adding annotated data to CargoContainer with alias '%s', overwriting original data", orig.getIndexingAlias() ) );
        // orig.updateByteArray( new_original_data.getBytes() );
        log.debug( String.format( "Removing data with id %s", orig.getId() ) );
        if( ! cargo.remove( orig.getId() ) ) {
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
            log.error( String.format( "Could not add to CargoContainer",ioe.getMessage() ) );
            throw new PluginException( String.format( "Could not add to CargoContainer",ioe.getMessage() ) );
        }
        // } catch (IOException ioe) {
        //     log.fatal( "Could not add Annotation data to CargoContainer" );
        //     throw new PluginException( "Could not add Annotation data to CargoContainer", ioe );
        // }

        // changeme
        log.debug( String.format( "New xml data: %s", new String( cargo.getCargoObject( new_id ).getBytes() ) ) );
        return cargo;

    }
    
    public PluginType getPluginType()
    {
        return pluginType;
    }


    
}
