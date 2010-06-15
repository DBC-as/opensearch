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
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
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
import java.net.HttpURLConnection;
import java.net.URL;

import java.util.HashMap;
import java.util.Map;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.stream.XMLStreamException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Plugin for annotating docbook data from {@link CargoContainer}
 */
public class DocbookAnnotate implements IPluggable
{

    static Logger log = Logger.getLogger( DocbookAnnotate.class );
    private PluginType pluginType = PluginType.ANNOTATE;

    DocbookAnnotateEnvironment env = null;

    /**
     * Constructor for the DocbookAnnotate plugin.
     */
    // public DocbookAnnotate( IObjectRepository repository )
    // {
    //     log.trace( "DocbookAnnotate Constructor() called" );
    //     nsc = new OpensearchNamespaceContext();
    // }

    public DocbookAnnotate( IObjectRepository repository ) throws PluginException
    {
        log.trace( "DocbookAnnotate Constructor() called" );
	Map< String, String > tmpMap = new HashMap< String, String >();
        env = (DocbookAnnotateEnvironment)this.createEnvironment( repository, tmpMap );
    }


    /**
     * The "main" method of this plugin. Request annotation data from
     * a webservice. If annotationdata is available it added to the
     * cargocontainer in a new stream typed DublinCoreData
     *
     * @param cargo The CargoContainer to annotate
     *
     * @return An annotated CargoContainer
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer runPlugin( CargoContainer cargo, Map<String,String> argsMap ) throws PluginException
    {
        log.trace( "DocbookAnnotate getCargoContainer() called" );

        if( cargo == null )
        {
            log.error( "DocbookAnnotate getCargoContainer cargo is null" );
            throw new PluginException( new IllegalStateException( "CargoContainer was null. Cannot operate without a CargoContainer instance" ) );
        }

	return env.run( cargo );

        // CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

        // if( co == null )
        // {
        //     String error = "Could not retrieve CargoObject with original data from CargoContainer";
        //     log.error( error );
        //     throw new PluginException( String.format( error ) );
        // }

        // byte[] b = co.getBytes();
        // XPath xpath = XPathFactory.newInstance().newXPath();
        // xpath.setNamespaceContext( nsc );
        // XPathExpression xPathExpression;
        // XPathExpression xPathExpression_numOfRec;

        // try
        // {
        //     xPathExpression = xpath.compile( "/docbook:article/docbook:title" );
        // }
        // catch( XPathExpressionException e )
        // {
        //     throw new PluginException( String.format( "Could not compile xpath expression '%s'", "/docbook:article/docbook:title" ), e );
        // }

        // InputSource docbookSource = new InputSource( new ByteArrayInputStream( b ) );

        // // Find title of the docbook document
        // String title;
        // try
        // {
        //     title = xPathExpression.evaluate( docbookSource ).replaceAll( "\\s", "+" );
        // }
        // catch( XPathExpressionException xpe )
        // {
        //     throw new PluginException( "Could not evaluate xpath expression to find title", xpe );
        // }

        // // isolate format
        // String serverChoice = co.getFormat();

        // // Querying webservice
        // log.debug( String.format( "querying the webservice with title='%s', serverChoice(format)='%s'", title, serverChoice ) );

        // String xmlString = null;
        // String queryURL = null;

        // try
        // {
        //     queryURL = formURL( title, serverChoice );
        //     xmlString = httpGet( queryURL );
        // }
        // catch( IOException ioe )
        // {
        //     throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe );
        // }
        // log.debug( String.format( "data: title='%s', serverChoice(format)='%s', queryURL='%s', xml retrieved='%s'", title, serverChoice, queryURL, xmlString ) );


        // // put retrieved answer into inputsource object
        // log.debug( "Got answer from the webservice" );
        // ByteArrayInputStream bis;
        // try
        // {
        //     bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        // }
        // catch( UnsupportedEncodingException uee )
        // {
        //     throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        // }
        // InputSource annotateSource = new InputSource( bis );

        // // Get number of records...
        // // create xpath exp
        // xpath = XPathFactory.newInstance().newXPath();
        // xpath.setNamespaceContext( nsc );

        // //String xpathString = "/docbook:article/docbook:title";
        // // \todo: Remove wildcards in xpath expression (something to do with default namespace-shite)
        // String xpathString = "/*/*[2]";
        // try
        // {
        //     xPathExpression_numOfRec = xpath.compile( xpathString );
        // }
        // catch( XPathExpressionException e )
        // {
        //     throw new PluginException( String.format( "Could not compile xpath expression '%s'", xmlString ), e );
        // }

        // int numOfRec = 0;

        // try
        // {
        //     numOfRec = Integer.parseInt( xPathExpression_numOfRec.evaluate( annotateSource ) );
        // }
        // catch( NumberFormatException nfe )
        // {
        //     log.error( String.format( "Could not format number of records returned by the webservice" ) );
        //     throw new PluginException( "Could not format number of records returned by the webservice", nfe );
        // }
        // catch( XPathExpressionException xpee )
        // {
        //     log.error( String.format( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ) ) );
        //     throw new PluginException( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ), xpee );
        // }

        // log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        // if( numOfRec == 0 ) // no hits. Make another search without serverchoice
        // {
        //     @SuppressWarnings( "unused" )
        //     String xmlStr = null;
        //     queryURL = null;
        //     try
        //     {
        //         //uhm, per instructions above, this is not a query with_out_ serverchoice, is it?
        //         queryURL = formURL( title, "" );
        //         xmlStr = httpGet( queryURL );
        //     }
        //     catch( IOException ioe )
        //     {
        //         log.error( String.format( "Caugth IOException: Could not get result from webservice = %s.", queryURL ) );
        //         throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe );
        //     }
        //     log.debug( String.format( "data: title='%s', serverChose(format)=\"\"\nxml retrieved\n%s", title, xmlString ) );
        // }

        // // put retrieved answer into inputsource object
        // try
        // {
        //     bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        // }
        // catch( UnsupportedEncodingException uee )
        // {
        //     log.error( String.format( "Could not convert string to UTF-8 ByteArrayInputStream" ) );
        //     throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        // }
        // annotateSource = new InputSource( bis );

        // // Get annotation if one is returned 

        // String xpath_evaluation = null;
        // try
        // {
        //     xpath_evaluation = xPathExpression_numOfRec.evaluate( annotateSource );
        //     numOfRec = Integer.parseInt( xpath_evaluation );
        // }
        // catch( NumberFormatException nfe )
        // {
        //     log.error( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int", xpath_evaluation ) );
        //     throw new PluginException( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int",
        //             xpath_evaluation ), nfe );
        // }
        // catch( XPathExpressionException xpe )
        // {
        //     log.error( String.format( "Could not evaluate xpath expression to find number of returned records" ) );
        //     throw new PluginException( "Could not evaluate xpath expression to find number of returned records", xpe );
        // }

        // log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        // if( numOfRec == 1 )
        // {
        //     log.trace( "Adding annotation to CargoContainer" );
        //     String isolatedDCData = isolateDCData( xmlString );

        //     ByteArrayInputStream bais = new ByteArrayInputStream( isolatedDCData.getBytes() );
        //     DublinCore dc;
        //     try
        //     {
        //         dc = new DublinCore( bais );
        //     }
        //     catch( XMLStreamException ex )
        //     {
        //         String error = String.format( "Failed to construct DublinCore stream from xml string: %s", ex.getMessage() );
        //         log.error( error );
        //         throw new PluginException( error );
        //     }

        //     cargo.addMetaData( dc );
        // }

        // return cargo;
    }



    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }

    public static IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new DocbookAnnotateEnvironment( repository, args );
    }

}
