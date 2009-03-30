/**
 * \file DocbookMerger.java
 * \brief The DocbookMerger class
 * \package plugins;
 */
package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.IProcesser;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CPMAlias;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
// import javax.xml.parsers.DocumentBuilder;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.ParserConfigurationException;
// import javax.xml.rpc.ServiceException;
// import javax.xml.xpath.*;
import org.dom4j.dom.DOMElement;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.io.SAXReader;
import org.dom4j.DocumentFactory;
import org.dom4j.Element;
import org.dom4j.DocumentException;
import org.dom4j.Namespace;
import org.xml.sax.SAXException;


/**
 *
 */
public class DocbookMerger implements IProcesser
{
    Logger log = Logger.getLogger( DocbookMerger.class );
    private PluginType pluginType = PluginType.ANNOTATE;
    private  NamespaceContext nsc;


    public DocbookMerger()
    {
        log.debug( "Entered DocbookMerger()" );
        nsc = new OpensearchNamespaceContext();
    }

    public PluginType getTaskName()
    {
        return PluginType.PROCESS;
    }

    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {

        log.debug( "Entered getCargoContainer( CargoContainer cargo )" );

        CargoObject dc = cargo.getFirstCargoObject( DataStreamType.DublinCoreData );

        Element annotation = null;

        CargoObject orig = cargo.getFirstCargoObject( DataStreamType.OriginalData );

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

        Namespace ns = new Namespace( "ting", "http://www.dbc.dk/ting/");

        Element tingElement = new DOMElement( "container", ns );
        
        DocumentFactory factory = new DocumentFactory();

        Document new_document = factory.createDocument();

        new_document.setRootElement( tingElement );

        Element new_root = new_document.getRootElement();

        if( dc != null) {
            log.debug( String.format( "CargoContainer has no annotation data" ) );
            new_root.add( annotation );
        }

        new_root.add( root );

        String new_original_data = new_document.asXML();

        log.debug( "Adding annotated data to CargoContainer, overwriting original data" );
        orig.updateByteArray( new_original_data.getBytes() );
        // } catch (IOException ioe) {
        //     log.fatal( "Could not add Annotation data to CargoContainer" );
        //     throw new PluginException( "Could not add Annotation data to CargoContainer", ioe );
        // }

        // changeme
        log.debug( String.format( "New xml data: %s", new String( orig.getBytes() ) ) );
        return cargo;

    }
}
