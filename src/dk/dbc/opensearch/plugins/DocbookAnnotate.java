/**
 * \file FaktalinkAnnotate.java
 * \brief The FaktalinkAnnotate class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamNames;

import java.net.InetAddress;
import java.io.IOException;
import java.net.UnknownHostException;
import java.io.InputStream;
import java.util.Scanner;
import java.net.Socket;
import java.net.URL;

import java.net.HttpURLConnection;
import java.lang.StringBuilder;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.StringReader;

import dk.dbc.opensearch.common.types.DataStreamNames;

import org.xml.sax.InputSource;


import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoContainer;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;

import javax.xml.xpath.*;
import org.apache.log4j.Logger;


import javax.xml.namespace.NamespaceContext;
import javax.xml.XMLConstants;

import java.util.Iterator;

import java.lang.Integer;

//================================================
import java.io.IOException;

import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;



/**
 *
 */
public class DocbookAnnotate implements IAnnotate
{
    static Logger log = Logger.getLogger( DocbookAnnotate.class );

    private PluginType pluginType = PluginType.ANNOTATE;

    public DocbookAnnotate()
    {
        log.debug( "Constructor called" );
    }


    public CargoContainer getCargoContainer( CargoContainer cargo ) throws javax.xml.xpath.XPathExpressionException, IOException//, ParserConfigurationException, SAXException,
    {
        // namespace context for docbook
        NamespaceContext nsc = new NamespaceContext(){
                public String getNamespaceURI( String prefix ){
                    String uri = null;
                    if ( prefix.equals( "docbook" ) ){
                        uri = "http://docbook.org/ns/docbook";
                    }
                    return uri;
                }

                public Iterator getPrefixes( String val ) {
                    return null;
                }
                public String getPrefix( String uri ){
                    return null;
                }
            };

        // Retrive docbook xml from CargoContainer
        CargoObject co = cargo.getFirstCargoObject( DataStreamNames.OriginalData );
        byte[] b = co.getBytes();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression  xPathExpression= xpath.compile( "/docbook:article/docbook:title" );

        InputSource docbookSource = new InputSource(new ByteArrayInputStream( b ) );

        // Find title of the docbook document
        String title = xPathExpression.evaluate( docbookSource ).replaceAll( "\\s", "+" );
        String serverChoice = co.getFormat();
        String queryUrl = formURL( title, serverChoice );

        // query the webservice
        String xmlString = httpGet( formURL( title, serverChoice ) );

        System.out.println( String.format( "data: title='%s', serverChose(format)='%s'\nQueryUrl='%s'\nxml retrieved\n%s", title, serverChoice, queryUrl, xmlString ) );

        // number of records... if one or more than one retrieve the first one
        ByteArrayInputStream bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        InputSource annotateSource = new InputSource( bis );

        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        xPathExpression= xpath.compile( "/*/*[2]" );

        int numOfRec = Integer.parseInt( xPathExpression.evaluate( annotateSource ) );

        if( numOfRec == 0 ){ // no hits make another search without serverchoice
            xmlString = httpGet( formURL( title, serverChoice ) );
            System.out.println( String.format( "data: title='%s', serverChose(format)=\"\"\nxml retrieved\n%s", title, xmlString ) );
        }

        // number of records... if one or more than one retrieve the first one
        bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        annotateSource = new InputSource( bis );

        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        xPathExpression= xpath.compile( "/*/*[2]" );

        numOfRec = Integer.parseInt( xPathExpression.evaluate( annotateSource ) );

        if ( numOfRec == 1 ){
            cargo.add( DataStreamNames.DublinCoreData, co.getFormat(), co.getSubmitter(), "da", "text/xml", xmlString.getBytes() );
        }
        return cargo;
    }

    /**
     * Forms the URL to use for annotate query.
     *
     *
     * @param title the title to query.
     * @param serverChoice This correspond to submitter field (eg. faktalink). Can be empty.
     */

    public String formURL( String title, String serverChoice ){

        int maxRecords = 1;

        String baseURL = "http://koncept.dbc.dk/~fvs/webservice.bibliotek.dk/";

        String preTitle = "?version=1.1&operation=searchRetrieve&query=dc.title+%3D+%28%22";
        String postTitle = "%22%29";

        String preServerChoice = "+and+cql.serverChoice+%3D+%28";
        String postServerChoice = "%29";

        String preRecords = "&startRecord=1&maximumRecords=";
        String postRecords = "&recordSchema=dc&stylesheet=default.xsl&recordPacking=string";

        String queryURL;
        if( serverChoice == "" ){
            queryURL = baseURL + preTitle + title + postTitle +
                preRecords + maxRecords + postRecords;
        }
        else{
            queryURL = baseURL + preTitle + title + postTitle +
                preServerChoice + serverChoice + postServerChoice + preRecords + maxRecords + postRecords;
        }
        return queryURL;
    }

    /**
     *  Performs a http call and returns the answer
     *
     *  @param URLstr The URL to use for hhtp call.
     *
     *  @returns String containing the response.
     *
     *  @throws IOException if we got a connection error.
     */
    public String httpGet( String URLstr ) throws IOException
    {
        URL url = new URL( URLstr );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();

        if (conn.getResponseCode() != 200)
            {
                throw new IOException(conn.getResponseMessage());
            }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
        StringBuilder sb = new StringBuilder();
        String line;
        while ( ( line = rd.readLine() ) != null )
            {
                sb.append( line );
            }

        rd.close();

        conn.disconnect();
        return sb.toString();
    }

    public PluginType getTaskName()
    {
        return pluginType;
    }

}
