/**
 * \file FaktalinkAnnotate.java
 * \brief The FaktalinkAnnotate class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
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
import javax.xml.xpath.*;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;


/**
 *
 */
public class DocbookAnnotate implements IAnnotate
{
    static Logger log = Logger.getLogger( DocbookAnnotate.class );

    private PluginType pluginType = PluginType.ANNOTATE;

    public DocbookAnnotate()
    {
        log.debug( "Constructor() called" );
    }

    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException
    {
        log.debug( "getCargoContainer() called" );

        // our namespace context for evaluating xpath expressions
        NamespaceContext nsc = new OpensearchNamespaceContext();


        log.debug( "Retrive docbook xml from CargoContainer" );
        CargoObject co = cargo.getFirstCargoObject( DataStreamType.OriginalData );
        byte[] b = co.getBytes();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression;
        try {
            xPathExpression = xpath.compile( "/docbook:article/docbook:title" );
        } catch (XPathExpressionException e) {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'",  "/docbook:article/docbook:title" ), e );
        }

        InputSource docbookSource = new InputSource(new ByteArrayInputStream( b ) );

        // Find title of the docbook document
        String title;
        try {
            title = xPathExpression.evaluate( docbookSource ).replaceAll( "\\s", "+" );
        } catch (XPathExpressionException xpe) {
            throw new PluginException( "Could not evaluate xpath expression to find title", xpe );

        }
        String serverChoice = co.getFormat();
        String queryUrl = formURL( title, serverChoice );

        
        log.debug( "querying the webservice" );
        String xmlString = null;
        String wsURL = null;
        try {
            wsURL = formURL( title, serverChoice );
            xmlString = httpGet( wsURL );
        } catch (IOException ioe) {
            throw new PluginException( String.format( "could not get result from webservice = %s", wsURL ), ioe);
        }
        log.debug( String.format( "data: title='%s', serverChoise(format)='%s', QueryUrl='%s', xml retrieved='%s'", title, serverChoice, queryUrl, xmlString ) );

        ByteArrayInputStream bis;
        try {
            bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        } catch (UnsupportedEncodingException uee) {
            throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        }
        InputSource annotateSource = new InputSource( bis );

        
        // Get number of records... if one or more than one record retrieve the first one
                
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        try {
            xPathExpression= xpath.compile( "/*/*[2]" );
        } catch (XPathExpressionException e) {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'",  "/docbook:article/docbook:title" ), e );
        }

        log.debug( "Got answer from the webservice" );
        int numOfRec = 0;
        try {
            numOfRec = Integer.parseInt( xPathExpression.evaluate( annotateSource ) );
        } catch (NumberFormatException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        } catch (XPathExpressionException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }

        if( numOfRec == 0 ){ // no hits make another search without serverchoice
            String xmlStr = null;
            String ws_URL = null;
            try {
                ws_URL = formURL( title, serverChoice );
                xmlStr = httpGet( ws_URL );
            } catch (IOException ioe) {
                log.fatal( String.format( "Caugth IOException: Could not get result from webservice = %s.", wsURL ) );
                throw new PluginException( String.format( "could not get result from webservice = %s", wsURL ), ioe);
            }
            log.debug( String.format( "data: title='%s', serverChose(format)=\"\"\nxml retrieved\n%s", title, xmlString ) );
        }

        // number of records... if one or more than one retrieve the first one
        try
            {
                bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
            } catch (UnsupportedEncodingException uee) {
            throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        }

        annotateSource = new InputSource( bis );

        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        try
            {
                xPathExpression= xpath.compile( "/*/*[2]" );
            } catch (XPathExpressionException e) {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'",  "/docbook:article/docbook:title" ), e );
        }

        String xpath_evaluation = null;
        try
            {
                xpath_evaluation = xPathExpression.evaluate( annotateSource );
                numOfRec = Integer.parseInt( xpath_evaluation );
            } catch (NumberFormatException nfe) {
            log.fatal( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int", xpath_evaluation ) );
            throw new PluginException( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int",
                                                      xpath_evaluation ), nfe );
        } catch (XPathExpressionException xpe) {
            throw new PluginException( "Could not evaluate xpath expression to find number of returned records", xpe );
        }

        if ( numOfRec == 1 ){
            try {
                cargo.add( DataStreamType.DublinCoreData, co.getFormat(), co.getSubmitter(), "da", "text/xml", xmlString.getBytes() );
            } catch (IOException ioe) {
                log.fatal( "Could not add DC data to CargoContainer" );
                throw new PluginException( "Could not add DC data to CargoContainer", ioe );
            }
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

    private String formURL( String title, String serverChoice ){

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
    private String httpGet( String URLstr ) throws IOException
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
