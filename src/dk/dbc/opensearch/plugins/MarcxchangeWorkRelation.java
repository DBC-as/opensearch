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


import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IRelation;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RelationshipTuple;

import fedora.server.types.gen.ComparisonOperator;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.Integer;
import java.lang.StringBuilder;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Vector;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.*;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Plugin for annotating docbook carcoContainers
 */
public class MarcxchangeWorkRelation implements IRelation
{
    static Logger log = Logger.getLogger( MarcxchangeWorkRelation.class );


    private PluginType pluginType = PluginType.WORKRELATION;
    private NamespaceContext nsc;
    private Vector< String > types;


    /**
     * Constructor for the DocbookAnnotate plugin.
     */
    public MarcxchangeWorkRelation()
    {
        log.debug( "DanmarcxchangeWorkRelation constructor called" );
        nsc = new OpensearchNamespaceContext();
        
        types = new Vector< String >();
        types.add( "Anmeldelse" );
        types.add( "Artikel" );
        types.add( "Avis" );
        types.add( "Avisartikel" );
        types.add( "Tidsskrift" );
        types.add( "Tidsskriftsartikel" );
    }


    /**
     * The "main" method of this plugin. Request a relation from
     * a webservice. If a relation is available it is added to the
     * cargocontainer in a new stream typed RelsExtData
     *
     * @param CargoContainer The CargoContainer to add relations to
     *
     * @returns A CargoContainer containing relations
     * 
     * @throws PluginException thrown if anything goes wrong during annotation.
     */
    public CargoContainer getCargoContainer( CargoContainer cargo ) throws PluginException, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
    	log.debug( "DanmarcxchangeWorkRelation -> getCargoContainer() called" );

        if ( cargo == null )
        {
            log.error( "DanmarcXchangeWorkRelation getCargoContainer cargo is null" );
            throw new PluginException( new NullPointerException( "DanmarcXchange getCargoContainer throws NullPointerException" ) );
        }
        else 
        {
            log.debug( "DanmarcXchangeWorkRelation getCargoContainer cargo is not null" );
        }
        
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        
        if ( co == null )
        {
            String error = "DanmarcXchangeWorkRelation getCargoContainer cargo object null";
            log.error( error );
            throw new PluginException( String.format( error ) );
        }
        
        byte[] b = co.getBytes();       
        log.debug( "CargoObject byteArray: " + new String( b ) );
        
        String typeXpathStr = "/ting:container/dkabm:record/dc:type[@xsi:type]";
        String dcType = getDCVariable( b, typeXpathStr );
        
        String titleXpathStr = "/ting:container/dkabm:record/dc:title[1]";
        String dcTitle = getDCVariable( b, titleXpathStr );
        
        String creatorXpathStr = "/ting:container/dkabm:record/dc:creator[1]";
        String dcCreator = getDCVariable( b, creatorXpathStr );

        String sourceXpathStr = "/ting:container/dkabm:record/dc:source[1]";
        String dcSource = getDCVariable( b, sourceXpathStr );
        
        FedoraAdministration fa = new FedoraAdministration();
        log.debug( String.format( "MWR dcType: '%s'", dcType ) );
        if ( ! types.contains( dcType ) )
        {
        	log.debug( String.format( "MWR entering findObjects, dcType: '%s' AND dcTitle: '%s'", dcType, dcTitle ) );
        	// match SOURCE: dcTitle on TARGET: dcTitle
        	String[] resultFields = { "pid", "title", "cDate" };
        	//dcTitle = "danmarcxchange";
        	ObjectFields[] objectFields = fa.findObjectFields( resultFields, "label", ComparisonOperator.eq, dcTitle, new NonNegativeInteger( "1" ) );
        	
        	if ( objectFields != null )
        	{
	        	int ofLength = objectFields.length;
	            String[] titles = new String[ ofLength ];
	            String[] pids = new String[ ofLength ];
	            String[] test = objectFields[ 0 ].getTitle();
	            log.debug( String.format( "ObjectFields titles: '%s'", test.toString() ) );
	            for( int i = 0; i < ofLength; i++ )
	            {
	            	String[] title = objectFields[ i ].getTitle();
	            	log.debug( String.format( "ObjectFields, title: '%s'", title[0]) );
	                //titles[ i ] = (String)objectFields[ i ].getTitle(i);
	                
	                String pid = (String)objectFields[ i ].getPid();
	                log.debug( String.format( "ObjectFields, pid: '%s'", pid) );
	                //pids[ i ] = (String)objectFields[ i ].getPid();
	                
	                String cdate = (String)objectFields[ i ].getCDate();
	                log.debug( String.format( "ObjectFields, cdate: '%s'", cdate ) );
	            }
	
	        	log.debug( String.format( "ObjectFields from findObjectFields, titles: '%s'", titles.toString() ) );
	        	log.debug( String.format( "ObjectFields from findObjectFields, pids: '%s'", pids.toString() ) );
        	}
        	// match SOURCE: dcSource on TARGET: dcTitle
        	
        	// match SOURCE: dcSource on TARGET: dcSource
        	
        	// match SOURCE: dcTitle on TARGET: dcSource
        }
        else 
        {
        	// match SOURCE: dcTile and dcCreator on TARGET dcTitle and dcCreator
        }
        
        log.debug( String.format( "MWR found dcVariables: '%s', '%s', '%s', and '%s'", dcTitle, dcType, dcCreator, dcSource ) );
                
        // get relationships        
        log.debug( String.format( "MWR: Trying to obtain pid" ) );
        String pid = "shite";
        try
        {
        	pid = cargo.getPid();
        }
        catch ( Exception ex )
        {
        	log.error( String.format( "Error in getting pid for cargo: ", ex.getMessage() ) );
        	StackTraceElement[] stackTraceElements = ex.getStackTrace();
        	for ( int i = 0; i < stackTraceElements.length; i++ )
        	{
        		log.error( String.format( "STACKTRACE %s: %s", i, stackTraceElements[ i ] ) );
        	}
        }
        log.debug( String.format( "MWR pid: '%s'", pid ) );
        String predicate = null;
        RelationshipTuple[] relTuple = null;
        try
        {
        	//relTuple = fa.getRelationships( pid, predicate );
        }
        catch( Exception ex )
        {
        	log.error( String.format( "ERROR in getting RelationshipTuple", "" ) );
        	StackTraceElement[] elems = ex.getStackTrace();
        	for ( int i = 0; i < elems.length; i++ )
        	{
        		log.error( String.format( "RelationshipTuple error element: '%s'", elems[ i ] ) );
        	}
        }
        
        if ( relTuple != null )
        {
        	log.debug( String.format( "RelationshipTuple returned, length: '%s'", relTuple.length ) );
        }
        else
        {
        	log.debug( String.format( "RelationshipTuple is null", "" ) );
        }
        
        // isolate format
        /*String serverChoice = co.getFormat();
        String xmlString = null;
        String queryURL = null;

        try 
        {
            //queryURL = formURL( title, serverChoice );
            xmlString = httpGet( queryURL );
        } 
        catch ( IOException ioe ) 
        {
            throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe);
        }

        // put retrieved answer into inputsource object
        log.debug( "Got answer from the webservice" );
        ByteArrayInputStream bis;
        try 
        {
            bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        } 
        catch ( UnsupportedEncodingException uee ) 
        {
            throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        }
        InputSource annotateSource = new InputSource( bis );

        // Get number of records...
        // create xpath exp
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression_numOfRec;
        
        //String xpathString = "/docbook:article/docbook:title";
        // \todo: Remove wildcards in xpath expression (something to do with default namespace-shite)
        String xpathString = "/* /*[2]";
        try 
        {
            xPathExpression_numOfRec = xpath.compile( xpathString );
        } 
        catch ( XPathExpressionException e ) 
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'",  xmlString ), e );
        }

        int numOfRec = 0;        

        try 
        {
            numOfRec = Integer.parseInt( xPathExpression_numOfRec.evaluate( annotateSource ) );
        } 
        catch ( NumberFormatException nfe ) 
        {
            log.fatal( String.format( "Could not format number of records returned by the webservice" ) );
            throw new PluginException( "Could not format number of records returned by the webservice", nfe );
        } 
        catch ( XPathExpressionException xpee ) 
        {
            log.fatal( String.format( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ) ) );
            throw new PluginException( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ), xpee );
        }

        log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        if( numOfRec == 0 ) // no hits. Make another search without serverchoice
        { 
            @SuppressWarnings("unused")
			String xmlStr = null;
            queryURL = null;
            try 
            {
                //uhm, per instructions above, this is not a query with_out_ serverchoice, is it?
                //queryURL = formURL( title, "" );
                xmlStr = httpGet( queryURL );
            } 
            catch (IOException ioe) 
            {
                log.fatal( String.format( "Caugth IOException: Could not get result from webservice = %s.", queryURL ) );
                throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe);
            }
            //log.debug( String.format( "data: title='%s', serverChose(format)=\"\"\nxml retrieved\n%s", title, xmlString ) );
        }

        // put retrieved answer into inputsource object
        try
        {
            bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        } 
        catch ( UnsupportedEncodingException uee ) 
        {
            log.fatal( String.format( "Could not convert string to UTF-8 ByteArrayInputStream" ) );
            throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        }
        annotateSource = new InputSource( bis );

        // Get annotation if one is returned 
           
        String xpath_evaluation = null;
        try
        {
            xpath_evaluation = xPathExpression_numOfRec.evaluate( annotateSource );
            numOfRec = Integer.parseInt( xpath_evaluation );
        } 
        catch ( NumberFormatException nfe ) 
        {
            log.fatal( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int", xpath_evaluation ) );
            throw new PluginException( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int",
                                                      xpath_evaluation ), nfe );
        } 
        catch ( XPathExpressionException xpe ) 
        {
            log.fatal( String.format( "Could not evaluate xpath expression to find number of returned records" ) );
            throw new PluginException( "Could not evaluate xpath expression to find number of returned records", xpe );
        }

        log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        if ( numOfRec == 1 )
        {
            try 
            {
                log.debug( "Adding annotation to CargoContainer" );
                String isolatedDCData = isolateDCData( xmlString );   
                /** \todo: use of deprecated method from CargoContainer * /
                cargo.add( DataStreamType.DublinCoreData, co.getFormat(), co.getSubmitter(), "da", "text/xml", IndexingAlias.None, isolatedDCData.getBytes() );
            } 
            catch ( IOException ioe ) 
            {
                log.fatal( "Could not add DC data to CargoContainer" );
                throw new PluginException( "Could not add DC data to CargoContainer", ioe );
            }
        }
        */
        return cargo;
    }
    
    
    private String getDCVariable( byte[] bytes, String xPathStr ) throws PluginException
    {
    	XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression;        
        
        InputSource workRelationSource = new InputSource( new ByteArrayInputStream( bytes ) );        
        String dcVariable = null;
        try
        {
        	log.debug( String.format( "MarcxchangeWorkRelation xpathStr = '%s'", xPathStr ) );
        	xPathExpression = xpath.compile( xPathStr );
            dcVariable = xPathExpression.evaluate( workRelationSource );            
            log.debug( String.format( "MarcxchangeWorkRelation found dcVariable: '%s'", dcVariable ) );
        } 
        catch ( XPathExpressionException e ) 
        {
        	String error = String.format( "Could not compile xpath expression '%s'",  xPathStr );
        	log.error( error );
            throw new PluginException( error, e );
        }
        
        return dcVariable;
    }


    /**
     * Isolates the Dublin Core data from the data retrieved from the
     * webservice.
     *
     *
     * @param The xml String retrieved from the webservice
     * 
     * @throws PluginException Thrown if something goes wrong during xml parsing
     */
    private String isolateDCData( String recordXmlString ) throws PluginException
    {
        log.debug( "isolateDCData( recordXMLString ) called" );
        
        // building document 
        Document annotationDocument = null;
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            annotationDocument = builder.parse( new InputSource( new ByteArrayInputStream( recordXmlString.getBytes() ) ) );
        }
        catch( ParserConfigurationException pce )
        {
            log.fatal( String.format( "Caught error while trying to instanciate documentbuilder '%s'", pce ) );
            throw new PluginException( "Caught error while trying to instanciate documentbuilder", pce );
        }
        catch( SAXException se)
        {
            log.fatal( String.format( "Could not parse annotation data: '%s'", se ) );
            throw new PluginException( "Could not parse annotation data ", se );
        }
        catch( IOException ioe )
        {
            log.fatal( String.format( "Could not cast the bytearrayinputstream to a inputsource: '%s'", ioe ) );
            throw new PluginException( "Could not cast the bytearrayinputstream to a inputsource", ioe );
        }
        
        log.debug( String.format( "Isolate Dublin Core from annotation data." ) );
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression_record;
        String recordString = null;
        
        try 
        {
            // \todo: Remove wildcards in xpath expression (something to do with default namespace-shite)
            xPathExpression_record = xpath.compile( "/*/*[3]/*/*[3]" );
            recordString  = xPathExpression_record.evaluate( annotationDocument );
        } 
        catch ( XPathExpressionException e) 
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'",  "/*/*[3]/*/*[3]" ), e );
        }
        
        log.debug( String.format( "IsolateDC returns xml: %s", recordString ) );
        return recordString;
    }


    /**
     * Forms the URL to use for annotate query.
     *
     * @param title the title to query.
     * @param serverChoice This correspond to submitter field (eg. faktalink). Can be empty.
     */
    private String formURL( String title, String serverChoice )
    {
        int maxRecords = 1;

        String baseURL = "http://koncept.dbc.dk/~fvs/webservice.bibliotek.dk/";

        String preTitle = "?version=1.1&operation=searchRetrieve&query=dc.title+%3D+%28%22";
        String postTitle = "%22%29";

        //using docbook forfatterweb, the following lines will cause the webservice to (wrongly) return 0 results
        String preServerChoice = "+and+cql.serverChoice+%3D+%28";
        String postServerChoice = "%29";

        String preRecords = "&startRecord=1&maximumRecords=";
        String postRecords = "&recordSchema=dc&stylesheet=default.xsl&recordPacking=string";

        String queryURL;
        if( serverChoice.equals( "" ) )
        {
            queryURL = baseURL + preTitle + title + postTitle + preRecords + maxRecords + postRecords;
        }
        else
        {
            queryURL = baseURL + preTitle + title + postTitle + preServerChoice + serverChoice + postServerChoice + preRecords + maxRecords + postRecords;
        }
        return queryURL;
    }


    /**
     *  Performs a http call and returns the answer.
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

    
    public PluginType getPluginType()
    {
        return pluginType;
    }

}
