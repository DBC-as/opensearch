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

import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext;
import dk.dbc.opensearch.common.fedora.FedoraNamespaceContext.FedoraNamespace;
import dk.dbc.opensearch.common.javascript.E4XXMLHeaderStripper;
import dk.dbc.opensearch.common.javascript.JSFedoraPIDSearch;
import dk.dbc.opensearch.common.javascript.ScriptMethodsForReviewRelation;
import dk.dbc.opensearch.common.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
//import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.SimplePair;
import dk.dbc.opensearch.common.types.OpenSearchTransformException;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.InputSource;


public class XMLDCHarvesterEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( XMLDCHarvesterEnvironment.class );

    private IObjectRepository repository;
    private SimpleRhinoWrapper jsWrapper = null;
    private Map<String, String> args;
    private String script = null;

    public XMLDCHarvesterEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        log.trace( "Constructor called" );
        this.repository = repository;
        this.args = args;

        log.trace( "Validating args");
        //validate method call

        this.script = args.get( "script" );

        String jsFileName = this.script;
        List< Pair< String, Object > > objectList = new ArrayList< Pair< String, Object > >();
        objectList.add( new SimplePair< String, Object >( "Log", log ) );

        try
        {
            jsWrapper = new SimpleRhinoWrapper( FileSystemConfig.getScriptPath() + jsFileName, objectList );
        }
        catch( FileNotFoundException fnfe )
        {
            String errorMsg = String.format( "Could not find the file: %s", jsFileName );
            log.error( errorMsg, fnfe );
            throw new PluginException( errorMsg, fnfe );
        }
        catch( ConfigurationException ce )
        {
            String errorMsg = String.format( "A ConfigurationExcpetion was cought while trying to construct the path+filename for javascriptfile: %s", jsFileName );
            log.fatal( errorMsg, ce );
            throw new PluginException( errorMsg, ce );
        }


    }

    /**
     * Temporary naming until the script is ready. Look at bug 10936 for more info
     */

    public CargoContainer myRun( CargoContainer cargo ) throws PluginException
    {
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        String XML = new String( E4XXMLHeaderStripper.strip( co.getBytes() ) ); // stripping: <?xml...?>

        String DCXMLString = (String)jsWrapper.run( args.get( "entyrfunction" ), XML );

        try
        {
            cargo.add( DataStreamType.DublinCoreData,
                       "dublinCoreData",
                       "dbc",
                       "da",
                       "text/xml",
                       DCXMLString.getBytes() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "could not add dublincore data: '%s'to the cargocontainer with id: '%s'", DCXMLString, cargo.getIdentifier().getIdentifier() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        return cargo;

    }

    /**
     * Main method that constructs dc data and adds it to the cargocontainer 
     * The dc is created from data in the originaldata.
     * The dc:identifier is taken directly from the cargocontainers identifier, 
     * so this method is dependant on that such a identifier previously have 
     * been added to the cargocontainer
     * @param cargo, a CargoContainer with the originaldata
     * @return a cargocontainer with a dc stream added.
     */

    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {

        log.trace( "Constructing DC datastream" );

        ByteArrayOutputStream newStream = createDCStream( cargo );
        log.trace( String.format( "the new stream: '%s'", newStream ) );

        try
        {
            cargo.add( DataStreamType.DublinCoreData,
                       "dublinCoreData",
                       "dbc",
                       "da",
                       "text/xml",
                       newStream.toByteArray() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "error while adding the dc stream: '%s' to cargocontainer with id: '%s'", newStream, cargo.getIdentifier().getIdentifier() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        log.trace(String.format( "num of objects in cargo: %s", cargo.getCargoObjectCount() ) );

        return cargo;
    }

    private String getDCVariable( byte[] bytes, String xPathStr ) throws PluginException
    {
        NamespaceContext nsc = new OpensearchNamespaceContext();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression = null;

        InputSource workRelationSource = new InputSource( new ByteArrayInputStream( bytes ) );
        String dcVariable = null;

        log.debug( String.format( "xpathStr = '%s'", xPathStr ) );
        try
        {
            xPathExpression = xpath.compile( xPathStr );
        }
        catch ( XPathExpressionException xpee )
        {
            String msg = String.format( "Could not compile xpath expression '%s'", xPathStr );
            log.error( msg, xpee );
            throw new PluginException( msg, xpee );
        }

        try
        {
            //This line writes a fatal error when given an '&' as the only content in the xml
            dcVariable = xPathExpression.evaluate( workRelationSource );
        }
        catch ( XPathExpressionException xpee )
        {
            String msg = String.format( "Could not evaluate with xpath expression '%s'", xPathStr );
            log.error( msg, xpee );
            throw new PluginException( msg, xpee );
        }

        log.debug( String.format( "Found dcVariable: '%s'", dcVariable ) );

        return dcVariable;
    }

    private ByteArrayOutputStream createDCStream( CargoContainer cargo) throws PluginException
    {
        //creating namespaces
        FedoraNamespace dc = new FedoraNamespaceContext().getNamespace( "dc" );
        FedoraNamespace oai_dc = new FedoraNamespaceContext().getNamespace( "oai_dc" );

        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

        byte[] b = co.getBytes();

        String titleXpathStr = "/ting:container/dkabm:record/dc:title[1]";
        log.trace( String.format( "finding dcTitle using xpath: '%s'", titleXpathStr ) );
        String dcTitle = getDCVariable( b, titleXpathStr );
        log.trace( String.format( "cargo setting dcTitle with value '%s'", dcTitle ) );

        String creatorXpathStr = "/ting:container/dkabm:record/dc:creator[1]";
        log.trace( String.format( "finding dcCreator using xpath: '%s'", creatorXpathStr ) );
        String dcCreator = getDCVariable( b, creatorXpathStr );
        log.trace( String.format( "cargo setting dcCreator with value '%s'", dcCreator ) );

        String typeXpathStr = "/ting:container/dkabm:record/dc:type[@xsi:type]";
        log.trace( String.format( "finding dcType using xpath: '%s'", typeXpathStr ) );
        String dcType = getDCVariable( b, typeXpathStr );
        log.trace( String.format( "cargo setting dcType with value '%s'", dcType ) );

        String sourceXpathStr = "/ting:container/dkabm:record/dc:source[1]";
        log.trace( String.format( "finding dcSource using xpath: '%s'", sourceXpathStr ) );
        String dcSource = getDCVariable( b, sourceXpathStr );
        log.trace( String.format( "cargo setting dcSource with value '%s'", dcSource ) );

        String relationXpathStr = "/*/*/*/*[@tag='014']/*[@code='a']";
        log.trace( String.format( "finding dcRelation using xpath: '%s'", relationXpathStr ) );
        String dcRelation = getDCVariable( b, relationXpathStr );
        log.trace( String.format( "cargo setting dcRelation with value '%s'", dcRelation ) );

        log.debug( String.format( "setting variables in cargo container: dcTitle '%s'; dcCreator '%s'; dcType '%s'; dcSource '%s'", dcTitle, dcCreator, dcType, dcSource ) );
        //*/
        //create the stream representation of the dc

        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;
        ByteArrayOutputStream out = new ByteArrayOutputStream();

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

        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );

            xmlw.setDefaultNamespace( oai_dc.getURI() );

            xmlw.writeStartDocument();
            xmlw.writeStartElement( oai_dc.getURI(), dc.getPrefix() );
            xmlw.writeNamespace( oai_dc.getPrefix(), oai_dc.getURI() );
            xmlw.writeNamespace( dc.getPrefix(), dc.getURI() );

            xmlw.writeStartElement( dc.getURI(), "title" );
            xmlw.writeCharacters( dcTitle );
            xmlw.writeEndElement();
            xmlw.writeStartElement( dc.getURI(), "creator" );
            xmlw.writeCharacters( dcCreator );
            xmlw.writeEndElement();
            xmlw.writeStartElement( dc.getURI(), "type" );
            xmlw.writeCharacters( dcType );
            xmlw.writeEndElement();
            xmlw.writeStartElement( dc.getURI(), "identifier" );
            xmlw.writeCharacters( cargo.getIdentifier().getIdentifier() );
            xmlw.writeEndElement();
            xmlw.writeStartElement( dc.getURI(), "source" );
            xmlw.writeCharacters( dcSource );
            xmlw.writeEndElement();
            xmlw.writeStartElement( dc.getURI(), "relation" );
            xmlw.writeCharacters( dcRelation );
            xmlw.writeEndElement();

            xmlw.writeEndElement();//closes "oai_dc:dc" element
            xmlw.writeEndDocument();
            xmlw.flush();
        }
        catch( XMLStreamException xse )
        {
            String error = String.format( "Error while creating the dc stream for materiel with identifier: '%s'", cargo.getIdentifier() );
            log.error( error, xse );
            throw new PluginException( error, xse );
        }

        return out;
    }

}
