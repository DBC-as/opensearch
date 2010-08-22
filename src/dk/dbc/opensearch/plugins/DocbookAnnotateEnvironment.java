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
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
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

// For class DublinCore:
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import dk.dbc.opensearch.common.metadata.MetaData;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map.Entry;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Characters;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;




public class DocbookAnnotateEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( DocbookAnnotateEnvironment.class );

    private final NamespaceContext nsc;

    public DocbookAnnotateEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
	nsc = new OpensearchNamespaceContext();
    }



    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {

        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

        if( co == null )
        {
            String error = "Could not retrieve CargoObject with original data from CargoContainer";
            log.error( error );
            throw new PluginException( String.format( error ) );
        }

        byte[] b = co.getBytes();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression;
        XPathExpression xPathExpression_numOfRec;

        try
        {
            xPathExpression = xpath.compile( "/docbook:article/docbook:title" );
        }
        catch( XPathExpressionException e )
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'", "/docbook:article/docbook:title" ), e );
        }

        InputSource docbookSource = new InputSource( new ByteArrayInputStream( b ) );

        // Find title of the docbook document
        String title;
        try
        {
            title = xPathExpression.evaluate( docbookSource ).replaceAll( "\\s", "+" );
        }
        catch( XPathExpressionException xpe )
        {
            throw new PluginException( "Could not evaluate xpath expression to find title", xpe );
        }

        // isolate format
        String serverChoice = co.getFormat();

        // Querying webservice
        log.debug( String.format( "querying the webservice with title='%s', serverChoice(format)='%s'", title, serverChoice ) );

        String xmlString = null;
        String queryURL = null;

        try
        {
            queryURL = formURL( title, serverChoice );
            xmlString = httpGet( queryURL );
        }
        catch( IOException ioe )
        {
            throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe );
        }
        log.debug( String.format( "data: title='%s', serverChoice(format)='%s', queryURL='%s', xml retrieved='%s'", title, serverChoice, queryURL, xmlString ) );


        // put retrieved answer into inputsource object
        log.debug( "Got answer from the webservice" );
        ByteArrayInputStream bis;
        try
        {
            bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        }
        catch( UnsupportedEncodingException uee )
        {
            throw new PluginException( "Could not convert string to UTF-8 ByteArrayInputStream", uee );
        }
        InputSource annotateSource = new InputSource( bis );

        // Get number of records...
        // create xpath exp
        xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );

        //String xpathString = "/docbook:article/docbook:title";
        // \todo: Remove wildcards in xpath expression (something to do with default namespace-shite)
        String xpathString = "/*/*[2]";
        try
        {
            xPathExpression_numOfRec = xpath.compile( xpathString );
        }
        catch( XPathExpressionException e )
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'", xmlString ), e );
        }

        int numOfRec = 0;

        try
        {
            numOfRec = Integer.parseInt( xPathExpression_numOfRec.evaluate( annotateSource ) );
        }
        catch( NumberFormatException nfe )
        {
            log.error( String.format( "Could not format number of records returned by the webservice" ) );
            throw new PluginException( "Could not format number of records returned by the webservice", nfe );
        }
        catch( XPathExpressionException xpee )
        {
            log.error( String.format( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ) ) );
            throw new PluginException( String.format( "The xpath %s failed with reason %s", xpathString, xpee.getMessage() ), xpee );
        }

        log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        if( numOfRec == 0 ) // no hits. Make another search without serverchoice
        {
            @SuppressWarnings( "unused" )
            String xmlStr = null;
            queryURL = null;
            try
            {
                //uhm, per instructions above, this is not a query with_out_ serverchoice, is it?
                queryURL = formURL( title, "" );
                xmlStr = httpGet( queryURL );
            }
            catch( IOException ioe )
            {
                log.error( String.format( "Caugth IOException: Could not get result from webservice = %s.", queryURL ) );
                throw new PluginException( String.format( "could not get result from webservice = %s", queryURL ), ioe );
            }
            log.debug( String.format( "data: title='%s', serverChose(format)=\"\"\nxml retrieved\n%s", title, xmlString ) );
        }

        // put retrieved answer into inputsource object
        try
        {
            bis = new ByteArrayInputStream( xmlString.getBytes( "UTF-8" ) );
        }
        catch( UnsupportedEncodingException uee )
        {
            log.error( String.format( "Could not convert string to UTF-8 ByteArrayInputStream" ) );
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
        catch( NumberFormatException nfe )
        {
            log.error( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int", xpath_evaluation ) );
            throw new PluginException( String.format( "Caught NumberFormatException: could not convert xpath evaluation '%s' to int",
                    xpath_evaluation ), nfe );
        }
        catch( XPathExpressionException xpe )
        {
            log.error( String.format( "Could not evaluate xpath expression to find number of returned records" ) );
            throw new PluginException( "Could not evaluate xpath expression to find number of returned records", xpe );
        }

        log.debug( String.format( "Number of record hits='%s', with format='%s'", numOfRec, serverChoice ) );

        if( numOfRec == 1 )
        {
            log.trace( "Adding annotation to CargoContainer" );
            String isolatedDCData = isolateDCData( xmlString );

            ByteArrayInputStream bais = new ByteArrayInputStream( isolatedDCData.getBytes() );
            DublinCore dc;
            try
            {
                dc = new DublinCore( bais );
            }
            catch( XMLStreamException ex )
            {
                String error = String.format( "Failed to construct DublinCore stream from xml string: %s", ex.getMessage() );
                log.error( error );
                throw new PluginException( error );
            }

            cargo.addMetaData( dc );
        }

        return cargo;

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
        log.trace( "isolateDCData( recordXMLString ) called" );

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
            log.error( String.format( "Caught error while trying to instanciate documentbuilder '%s'", pce ) );
            throw new PluginException( "Caught error while trying to instanciate documentbuilder", pce );
        }
        catch( SAXException se )
        {
            log.error( String.format( "Could not parse annotation data: '%s'", se ) );
            throw new PluginException( "Could not parse annotation data ", se );
        }
        catch( IOException ioe )
        {
            log.error( String.format( "Could not cast the bytearrayinputstream to a inputsource: '%s'", ioe ) );
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
            recordString = xPathExpression_record.evaluate( annotationDocument );
        }
        catch( XPathExpressionException e )
        {
            throw new PluginException( String.format( "Could not compile xpath expression '%s'", "/*/*[3]/*/*[3]" ), e );
        }
        log.trace( String.format( "IsolateDC returns xml: %s", recordString ) );

        return recordString;
    }


    /**
     * Forms the URL to use for annotate query.
     *
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
            queryURL = baseURL + preTitle + title + postTitle +
                    preRecords + maxRecords + postRecords;
        }
        else
        {
            queryURL = baseURL + preTitle + title + postTitle +
                    preServerChoice + serverChoice + postServerChoice + preRecords + maxRecords + postRecords;
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

        if( conn.getResponseCode() != 200 )
        {
            throw new IOException( conn.getResponseMessage() );
        }

        // Buffer the result into a string
        BufferedReader rd = new BufferedReader( new InputStreamReader( conn.getInputStream() ) );
        StringBuilder sb = new StringBuilder();
        String line;
        while( (line = rd.readLine()) != null )
        {
            sb.append( line );
        }

        rd.close();

        conn.disconnect();
        return sb.toString();
    }


}



/**
 * DublinCore is a class implementation of the Dublin Core Metadata Element Set,
 * Version 1.1 (http://dublincore.org/documents/dces/)
 *
 */
class DublinCore implements MetaData
{
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );

    private Map< DublinCoreElement, String > dcvalues;

    public static final DataStreamType type = DataStreamType.DublinCoreData;

    private Logger log = Logger.getLogger( DublinCore.class );
    private static FedoraNamespace dc = new FedoraNamespaceContext().getNamespace( "dc" );
    private static FedoraNamespace oai_dc = new FedoraNamespaceContext().getNamespace( "oai_dc" );

    /**
     * Initializes an empty Dublin Core element
     */   
    public DublinCore( ) {
        dcvalues = new HashMap< DublinCoreElement, String >();
    }
    /**
     * Initializes an empty Dublin Core element, identified by {@code identifier}
     * @param identifier An unambiguous reference to the resource within a given
     * context. Recommended best practice is to use the digital repository object
     * identifier.
     */
    public DublinCore( String identifier )
    {
        dcvalues = new HashMap< DublinCoreElement, String >();
        dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier);
    }


    /**
     * Initializes a Dublin Core element with values taken from {@code
     * inputValues}, identified by {@code identifier}
     * @param identifier An unambiguous reference to the resource within a given
     * context. Recommended best practice is to use the digital repository object
     * identifier.
     */
    public DublinCore( String identifier, Map< DublinCoreElement, String > inputValues )
    {
        dcvalues = new HashMap< DublinCoreElement, String >( inputValues );
        dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier);
    }


    /**
     * Initializes a dublin core object based on the dublin core
     * document provided with {@code cdDocument}. No validation is performed on
     * the InputStream but values outside the dublin core metadata element set
     * is ignored.
     */
    public DublinCore( InputStream dcIn ) throws XMLStreamException
    {
        dcvalues = new HashMap< DublinCoreElement, String >();
        
        XMLInputFactory infac = XMLInputFactory.newInstance();
        XMLEventReader eventReader = infac.createXMLEventReader( dcIn );

        StartElement element;
        Characters chars;

        DublinCoreElement elementName = null;
        String elementText = null;
        XMLEvent event = null;
        while ( eventReader.hasNext())
        {
            try
            {
                event = (XMLEvent) eventReader.next();
            }
            catch( NoSuchElementException ex )
            {
                String error = String.format( "Could not parse incoming data, previously correctly parsed content from stream was: %s", event.toString() );
                log.error( error, ex );
                throw new IllegalStateException( error, ex );
            }
            
            switch ( event.getEventType() )
            {
                case XMLStreamConstants.START_ELEMENT:
                    element = event.asStartElement();
                    log.trace( String.format( "Got element: %s", element.getName().toString() ) );
                    if ( element.getName().getPrefix().equals( dc.getPrefix() ) )
                    {
                        if( DublinCoreElement.hasLocalName( element.getName().getLocalPart() ) )
                        {
                            elementName = DublinCoreElement.fromString( element.getName().getLocalPart() );
                        }
                    }
                    break;
                case XMLStreamConstants.CHARACTERS:
                    chars = event.asCharacters();
                    if( ! chars.getData().trim().isEmpty() )
                    {
                        elementText = chars.getData();
                        log.trace( String.format( "Got text: %s", elementText ) );
                    }
                    break;
            }

            if ( elementName != null && elementText != null )
            {
                log.trace( String.format( "Adding element: '%s':'%s'", elementName, elementText ) );
                dcvalues.put( elementName, elementText );
                elementName = null;
                elementText = null;
            }
        }

        eventReader.close();
    }

    
    public void setContributor( String contributor )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_CONTRIBUTOR, contributor );
    }


    public void setCoverage( String coverage )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_COVERAGE, coverage );
    }


    public void setCreator( String creator )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_CREATOR, creator );
    }


    public void setDate( Date date )
    {
        String stringdate = dateFormat.format( date );
        dcvalues.put( DublinCoreElement.ELEMENT_DATE, stringdate );
    }


    public void setDescription( String description )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_DESCRIPTION, description );
    }


    public void setFormat( String format )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_FORMAT, format );
    }
    

    public void setIdentifier( String identifier )
    {        
        dcvalues.put( DublinCoreElement.ELEMENT_IDENTIFIER, identifier );
    }


    public void setLanguage( String language )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_LANGUAGE, language );
    }


    public void setPublisher( String publisher)
    {
        dcvalues.put( DublinCoreElement.ELEMENT_PUBLISHER, publisher );
    }


    public void setRelation( String relation )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_RELATION, relation );
    }


    public void setRights( String rights )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_RIGHTS, rights );
    }


    public void setSource( String source )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_SOURCE, source );
    }


    public void setSubject( String subject )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_SUBJECT, subject );
    }


    public void setTitle( String title )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_TITLE, title );
    }


    public void setType( String type )
    {
        dcvalues.put( DublinCoreElement.ELEMENT_TYPE, type );
    }


    public int elementCount()
    {
        return dcvalues.size();
    }



    /** 
     * Retrieves values associated with the {@link DublinCoreElement} {@code
     * dcElement}. If no value was registered with the element, this
     * method will return an empty String.
     * 
     * @param dcElement a {@link DublinCoreElement}
     * 
     * @return the value associated with {@code dcElement} or an empty String
     */
    public String getDCValue( DublinCoreElement dcElement )
    {
        String retval = dcvalues.get( dcElement );
        if( retval == null )
        {
            log.warn( String.format( "No value registered for element %s", dcElement ) );
            retval = "";
        }
        
        return retval;
    }

    /* Example of output:
    <dc xmlns:dc="http://purl.org/dc/elements/1.1/"
    xmlns:oai_dc="http://www.openarchives.org/OAI/2.0/oai_dc/"
    xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
    xsi:schemaLocation="http://www.openarchives.org/OAI/2.0/oai_dc/
    http://www.openarchives.org/OAI/2.0/oai_dc.xsd">
    <dc:title>Harry Potter og Fønixordenen</dc:title>
    <dc:creator>Joanne K. Rowling</dc:creator>
    <dc:type>Bog</dc:type>
    <dc:identifier>710100:25082427</dc:identifier>
    <dc:source>Harry Potter and the Order of the Phoenix</dc:source>
    <dc:relation/>
    </dc>
     */
    @Override
    public void serialize( OutputStream out, String identifier )throws XMLStreamException
    {
        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;

        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );

            xmlw.setDefaultNamespace( oai_dc.getURI() );

            xmlw.writeStartDocument();
            xmlw.writeStartElement( oai_dc.getURI(), dc.getPrefix() );
            xmlw.writeNamespace( oai_dc.getPrefix(), oai_dc.getURI() );
            xmlw.writeNamespace( dc.getPrefix(), dc.getURI() );

            for( Entry<DublinCoreElement, String> set : dcvalues.entrySet() )
            {
                xmlw.writeStartElement( dc.getURI(), set.getKey().localName() );
                if ( set.getValue() != null )
                {
                    xmlw.writeCharacters( set.getValue() );
                }

                xmlw.writeEndElement();
            }

            xmlw.writeEndElement();//closes "oai_dc:dc" element
            xmlw.writeEndDocument();//closes document
            xmlw.flush();
        }
        catch( XMLStreamException ex )
        {
            String error = "Could not write to stream writer";
            log.error( error );
            throw ex;
        }
    }

    
    public String getIdentifier()
    {
        return dcvalues.get( DublinCoreElement.ELEMENT_IDENTIFIER );
    }


    /**
     *
     * @return the type of this metadata element
     */
    public DataStreamType getType()
    {
        return type;
    }    
}






/**
 *
 */
enum DublinCoreElement
{
    ELEMENT_TITLE( "title"),
    ELEMENT_CREATOR( "creator"),
    ELEMENT_SUBJECT( "subject"),
    ELEMENT_DESCRIPTION( "description"),
    ELEMENT_PUBLISHER( "publisher"),
    ELEMENT_CONTRIBUTOR( "contributor"),
    ELEMENT_DATE( "date"),
    ELEMENT_TYPE( "type"),
    ELEMENT_FORMAT( "format"),
    ELEMENT_IDENTIFIER( "identifier"),
    ELEMENT_SOURCE( "source"),
    ELEMENT_LANGUAGE( "language"),
    ELEMENT_RELATION( "relation"),
    ELEMENT_COVERAGE( "coverage"),
    ELEMENT_RIGHTS( "rights");


    private String localname;
    DublinCoreElement( String localName )
    {
        this.localname = localName;
    }


    public String localName()
    {
        return this.localname;
    }


    public static boolean hasLocalName( String name )
    {
        for( DublinCoreElement dcee : DublinCoreElement.values() )
        {
            if ( dcee.localName().equals( name ) )
            {
                return true;
            }
        }

        return false;
    }


    public static DublinCoreElement fromString( String localName )
    {
        if ( DublinCoreElement.hasLocalName( localName ) )
        {
            return DublinCoreElement.valueOf( "ELEMENT_" + localName.toUpperCase() );
        }

        throw new IllegalArgumentException( String.format( "No enum value %s", "ELEMENT_" + localName.toUpperCase() ) );
    }
}
