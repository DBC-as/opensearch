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
 * \file XMLUtils.java
 * \brief utility methods for XML handling
 */


package dk.dbc.opensearch.common.xml;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLEventFactory;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.events.Namespace;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;



/**
 * Provides static methods for common xml operations.
 */
public class XMLUtils
{

    static Logger log = Logger.getLogger( XMLUtils.class );


    /**
     * creates and returns a Document from is.
     *
     * @param is the inputsource to build the Document from
     *
     * @return the root element of the created document
     *
     * @throws ParserConfigurationException Could not parse xmlFile
     * @throws SAXException Could not parse xmlFile
     * @throws IOException could not read xmlFile
     */
    public static Document getDocument( InputSource is ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        return docBuilder.parse( is );
    }

    public static Document getDocument( byte[] data ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        Document parse = docBuilder.parse( new ByteArrayInputStream( data ) );

        return parse;
    }
    
    /**
     * creates and returns a Document from is.
     *
     * @param is the inputsource to build the Document from
     * @param er The entity resolver to use
     * @return The document
     * @throws ParserConfigurationException Could not parse xmlFile
     * @throws SAXException Could not parse xmlFile
     * @throws IOException could not read xmlFile
     */
    public static Document getDocument( InputSource is, EntityResolver er ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        docBuilder.setEntityResolver( er );
        return docBuilder.parse( is );
    }

    /**
     * creates a Nodelist consisting of all the Nodes in xmlFile
     * matching elementName.
     *
     * @param xmlFile the file to retrive nodes from
     * @param elementName name of the nodes to retrieve
     *
     * @return a Nodelist consisting of all matching Nodes
     *
     * @throws ParserConfigurationException Could not parse xmlFile
     * @throws SAXException Could not parse xmlFile
     * @throws IOException could not read xmlFile
     */
    public static NodeList getNodeList( File xmlFile, String elementName ) throws ParserConfigurationException, SAXException, IOException
    {
        DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document jobDocument = docBuilder.parse( xmlFile );
        Element xmlRoot = jobDocument.getDocumentElement();

        return xmlRoot.getElementsByTagName( elementName );
    }


    /**
     * creates a Nodelist consisting of all the Nodes in a documemt,
     * build with the provided entityresolver, that matches elementName
     *
     * @param xmlFile the file to retrive nodes from
     * @param elementName name of the nodes to retrieve
     * @param er The entityResolver to use when build document
     *
     * @return a Nodelist consisting of all matching Nodes
     *
     * @throws ParserConfigurationException Could not parse xmlFile
     * @throws SAXException Could not parse xmlFile
     * @throws IOException could not read xmlFile
     */
    public static NodeList getNodeList( String xmlFile, String elementName, EntityResolver er ) throws ParserConfigurationException, SAXException, IOException
    {
        log.trace( String.format( "Getting nodelist using xml file '%s' and tag name '%s'", xmlFile, elementName ) );

        DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        docBuilder.setEntityResolver( er );
        Document jobDocument = docBuilder.parse( xmlFile );
        Element xmlRoot = jobDocument.getDocumentElement();

        log.trace( "getNodeList done" );
        return xmlRoot.getElementsByTagName( elementName );
    }


    /**
     * Returns a NodeList of Elements matching {@code elementName}
     *
     * @param data is the byte array to retrieve the Element(s) from
     *
     * @return a NodeList of matching Elements
     *
     * @throws ParserConfigurationException Could not parse data
     * @throws SAXException Could not parse data
     * @throws IOException could not read data
     */
    public static NodeList getNodeList( byte[] data, String elementName ) throws ParserConfigurationException, SAXException, IOException
    {
        ByteArrayInputStream bis = new ByteArrayInputStream( data );
        Document document = getDocument( new InputSource( bis ) );

        return document.getElementsByTagName( elementName );
    }


    /**
     * builds a byte array from the xml fragment pointet to by root.
     *
     * @param root the Element to read into byte array
     *
     * @return a byte array consisting if the xml fragment pointet to by root
     *
     * @throws TransformerException
     * @throws UnsupportedEncodingException
     */
    public static byte[] getByteArray( Element root ) throws TransformerException, UnsupportedEncodingException
    {
        Source source = new DOMSource( (Node) root );
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult( stringWriter );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        //transformer.setOutputProperty( javax.xml.transform.OutputKeys.OMIT_XML_DECLARATION, "yes" );
        transformer.transform( source, result );

        String streamString = stringWriter.getBuffer().toString();
        log.debug( String.format( "Constructed stream for the CargoContainer = %s", streamString ) );
        byte[] byteArray = streamString.getBytes( "UTF-8" );

        return byteArray;
    }


    /**
     * Transforms xml document with a xslt document and returns the
     * result.
     *
     * @param xml The root element of the xml to transform
     * @param xslt The root element of the xslt to do the transformation with
     *
     * @return the result of the transformation
     *
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public static Document transform( Source xml, Source xslt ) throws IOException, ParserConfigurationException, SAXException, TransformerException
    {
        ByteArrayOutputStream os = new ByteArrayOutputStream();
        StreamResult res = new StreamResult( os );

        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer( xslt );

        transformer.transform( xml, res ); // do the transformation

        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

        return documentBuilder.parse( new ByteArrayInputStream( os.toByteArray() ) );
    }


    /**
     * Creates a string representation of xml document
     *
     * @param document The {@link Document} to transform
     *
     * @return a string representation of the document
     *
     * @throws TransformerException
     */
    public static String xmlToString( Document document ) throws TransformerException
    {
        Node rootNode = (Node) document.getDocumentElement();
        Source source = new DOMSource( rootNode );

        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult( stringWriter );
        TransformerFactory factory = TransformerFactory.newInstance();
        Transformer transformer = factory.newTransformer();
        transformer.transform( source, result );

        return stringWriter.getBuffer().toString().replace( "\n", "");
    }


    /**
     * This method tries to construct a {@link Document} from an input String
     *
     * @param assumedXml a String containing xml formatted text
     * @return a {@link Document} DOM representation of the xml String
     * @throws ParserConfigurationException if the preconditions for creating a Document is not met
     * @throws SAXException if the {@code assumedXml} fails to meet the expectations that it can trivially be transformed into a DOM structure
     * @throws IOException if the bytes cannot be read from the String {@code assumedXml}
     */
    public static Document documentFromString( String assumedXml ) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse( new InputSource( new ByteArrayInputStream( assumedXml.getBytes() ) ) );

        return doc;
    }

    
    /**
     * Constructs a {@link Document} instance from an {@link InputStream}
     *
     * @param in the {@link InputStream} to be parsed into a {@link Document}
     *
     * @return a {@link Document} implementation if the {@link InputStream} {@code in} could be parsed
     * @throws ParserConfigurationException if the preconditions for creating a Document is not met
     * @throws SAXException if the {@code in} fails to meet the expectations that it can be transformed into a DOM structure
     * @throws IOException if the bytes cannot be read from the InputStream {@code in}
     */
    public static Document documentFromInputStream( InputStream in ) throws ParserConfigurationException, SAXException, IOException
    {
        Document doc = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        doc = builder.parse( new InputSource( in ) );
        return doc;
    }
       

    /**
     * Returns a {@link List} of all namespaces found in the {@link InputStream}
     * {@code in}, iff {@code in} is parsable as an XML Document. This method is
     * interested in constructing {@link Namespace}s, so global namespaces
     * declared in {@code in} will have a default prefix assigned to it. This is,
     * of course, not the case with the reserved namespace uris, see
     * {@code http://www.w3.org/TR/REC-xml-names/#ns-decl}
     *
     * @param in assumed parsable xml
     * @return a {@link List} of {@link Namespace}s found in {@code in}
     * @throws XMLStreamException if {@code in} is not parsable as an XML Document
     */
    public static List<Namespace> getNamespaces( InputStream in ) throws XMLStreamException
    {
        List< Namespace > namespaces = new ArrayList< Namespace >();
        XMLInputFactory infac = XMLInputFactory.newInstance();
        XMLStreamReader parser = infac.createXMLStreamReader( in );
        XMLEventFactory eventfac = XMLEventFactory.newInstance();
        Set<Integer> eventtypes = new HashSet<Integer>();
        eventtypes.add( new Integer( XMLStreamConstants.START_ELEMENT ) );
        eventtypes.add( new Integer( XMLStreamConstants.NAMESPACE ) );

        int event;
        while( parser.hasNext() )
        {
            event = parser.next();
            if( eventtypes.contains( event ) && parser.getNamespaceCount() > 0 )
            {
                for( int i = 0; i < parser.getNamespaceCount(); i++ )
                {
                    String prefix = parser.getNamespacePrefix( i );
                    if( prefix == null )
                    {
                        //when constructing Namespaces, null prefixes are not allowed, so we'll provide one:
                        prefix = "_"+new Integer( i ).toString();
                    }
                    namespaces.add( eventfac.createNamespace( prefix, parser.getNamespaceURI( i ) ) );
                }
            }
        }

        return namespaces;
    }
}
