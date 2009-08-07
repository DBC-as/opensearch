package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.helpers.XMLUtils;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.ComparablePair;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.Pair;

import fedora.server.utilities.DateUtility;
import fedora.utilities.Base64;
import fedora.utilities.NamespaceContextImpl;
import fedora.utilities.XmlTransformUtility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Class that represents the fedora object xml document. This class is a 
 * reimplementation of the Foxml11Document found in the fedora.utilities
 * namespace. It was largely inadequate for our purposes
 */
public final class FoxmlDocument
{
    static Logger log = Logger.getLogger( FoxmlDocument.class );
    public static final String FOXML_NS = "info:fedora/fedora-system:def/foxml#";
    private DocumentBuilder builder;
    private Document doc;
    private Element rootElement;
    private Element objectProperties;
    // I'm not quite sure of the wisdom in the following final
    // declarations, but I needed a way to guarantee that the
    // TransformerFactory will be available at the time {@link
    // #serialize(OutputStream)} is called independently of which
    // constructor was used
    private final XPathFactory factory = XPathFactory.newInstance();
    private final XPath xpath = factory.newXPath();
    private final TransformerFactory xformFactory = XmlTransformUtility.getTransformerFactory();

    public enum Property
    {

        STATE( "info:fedora/fedora-system:def/model#state" ),
        LABEL( "info:fedora/fedora-system:def/model#label" ),
        CONTENT_MODEL( "info:fedora/fedora-system:def/model#contentModel" ),
        CREATE_DATE( "info:fedora/fedora-system:def/model#createdDate" ),
        OWNERID( "info:fedora/fedora-system:def/model#ownerId" ),
        MOD_DATE( "info:fedora/fedora-system:def/view#lastModifiedDate" );
        private final String uri;

        Property( String uri )
        {
            this.uri = uri;
        }


        String uri()
        {
            return uri;
        }


    }

    public enum State
    {

        A, I, D;
    }

    public enum ControlGroup
    {

        X, M, E, R;
    }

    /**
     * Creates a fedora object xml document from a
     * CargoContainer. The xml representation of the FoxmlDocument can be
     * obtained via {@link #serialize(OutputStream)}.
     * For more control over the construction,
     * use {@link #FoxmlDocument(String) FoxmlDocument} constructor in
     * conjunction with
     * {@link #addDatastream(String, State, ControlGroup, String, CargoObject, boolean, boolean) addDatastream} method
     * @param cargo
     * @return
     */
//    public FoxmlDocument( CargoContainer cargo ) throws ServiceException, ConfigurationException, MalformedURLException, IOException, TransformerException, UnsupportedEncodingException, XPathExpressionException, ParserConfigurationException
//    {
//        String identifier = cargo.getDCIdentifier();
//
//        if( identifier == null || identifier.equals( "" ) )
//        {
//            cargo.setDCIdentifier( PIDManager.getInstance().getNextPID( cargo.getDCCreator() ) );
//        }
//        else if( identifier.split( ":" ).length == 0 )
//        {
//            throw new IllegalArgumentException( String.format( identifier ) );
//        }
//
//        initDocument( identifier );
//        // automatically contruct the whole document from the CargoContainer.
//        constructFoxml( cargo );
//
//    }


    /**
     * Creates a skeletal FedoraObject document. Its serialized representation
     * can be obtained through the {@link FoxmlDocument#serialize(java.io.OutputStream)} method
     * @param pid the pid to be the identifier for the Digital Object
     */
    public FoxmlDocument( String pid ) throws ParserConfigurationException
    {
        initDocument( pid );
    }

    private void initDocument( String id ) throws ParserConfigurationException
    {
        DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
        dbFactory.setNamespaceAware( true );

        builder = dbFactory.newDocumentBuilder();
        DOMImplementation impl = builder.getDOMImplementation();
        doc = impl.createDocument( FOXML_NS, "foxml:digitalObject", null );
        rootElement = doc.getDocumentElement();
        rootElement.setAttributeNS( "http://www.w3.org/2000/xmlns/",
                "xmlns:xsi",
                "http://www.w3.org/1999/XMLSchema-instance" );
        rootElement.setAttributeNS( "http://www.w3.org/1999/XMLSchema-instance",
                "xsi:schemaLocation",
                "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd" );
        rootElement.setAttribute( "VERSION", "1.1" );
        rootElement.setAttribute( "PID", id );



        NamespaceContextImpl nsCtx = new NamespaceContextImpl();
        nsCtx.addNamespace( "foxml", FOXML_NS );


        xpath.setNamespaceContext( nsCtx );
    }

    private void addObjectProperties()
    {
        if( objectProperties == null )
        {
            objectProperties = doc.createElementNS( FOXML_NS, "foxml:objectProperties" );
            rootElement.appendChild( objectProperties );
        }
    }


    private void addObjectProperty( Property name, String value )
    {
        addObjectProperties();
        Element property = doc.createElementNS( FOXML_NS, "foxml:property" );
        property.setAttribute( "NAME", name.uri );
        property.setAttribute( "VALUE", value );
        objectProperties.appendChild( property );
    }


    /**
     * Adds a Datastream to the DigitalObject Document
     *
     * Please note that this method also handles the construction of the
     * underlying DatastreamVersion
     *
     * @param id
     * @param state
     * @param controlGroup
     * @param versionable
     * @param label
     * @param co
     */
    public void addDatastream( String id,
                               State state,
                               ControlGroup controlGroup,
                               String label,
                               CargoObject co,
                               boolean versionable,
                               boolean externalData ) throws XPathExpressionException
    {
        Element ds = doc.createElementNS( FOXML_NS, "foxml:datastream" );
        ds.setAttribute( "ID", id );
        ds.setAttribute( "STATE", state.toString() );
        ds.setAttribute( "CONTROL_GROUP", controlGroup.toString() );
        ds.setAttribute( "VERSIONABLE", Boolean.toString( versionable ) );
        rootElement.appendChild( ds );

        String dsv_id = id + ".0";

        String mime = co.getMimeType();

        addDatastreamVersion( id,
                dsv_id,
                mime,
                label,
                co.getContentLength(),
                new Date( co.getTimestamp() ) );
    }


    private void addDublinCoreDatastream( CargoContainer cargo ) throws XPathExpressionException
    {
        String label = String.format( "Dublin Core data for %s", cargo.getDCTitle() );
        this.addDatastream( "DC",
                State.A,
                ControlGroup.X,
                label,
                cargo.getCargoObject( DataStreamType.DublinCoreData ),
                true,
                false );
    }


    private void addDatastreamVersion( String dsId,
                                       String dsvId,
                                       String mimeType,
                                       String label,
                                       int size,
                                       Date created ) throws XPathExpressionException
    {
        String expr = String.format( "//foxml:datastream[@ID='%s']", dsId );
        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Node node = nodes.item( 0 );
        if( node == null )
        {
            throw new IllegalArgumentException( dsId + "does not exist." );
        }
        Element dsv = doc.createElementNS( FOXML_NS, "foxml:datastreamVersion" );
        dsv.setAttribute( "ID", dsvId );
        dsv.setAttribute( "MIMETYPE", mimeType );
        dsv.setAttribute( "LABEL", label );
        dsv.setAttribute( "SIZE", Integer.toString( size ) );
        dsv.setAttribute( "CREATED", DateUtility.convertDateToString( created ) );
        node.appendChild( dsv );


    }


    private void addXmlContent( String dsvId, String xmlContent ) throws SAXException, IOException
    {
        Document contentDoc = builder.parse( new InputSource( new StringReader( xmlContent ) ) );
        Node importedContent = doc.adoptNode( contentDoc.getDocumentElement() );
        Node dsv = getDatastreamVersion( dsvId );
        Element content = doc.createElementNS( FOXML_NS, "foxml:xmlContent" );
        dsv.appendChild( content );
        content.appendChild( importedContent );

    }


    private void addBinaryContent( String dsvId, byte[] content ) throws SAXException, IOException
    {
        String b = Base64.encodeToString( content );
        Document contentDoc = builder.parse( new InputSource( new StringReader( b ) ) );
        Node b64content = doc.adoptNode( contentDoc.getDocumentElement() );
        Node dsv = getDatastreamVersion( dsvId );
        Element binelement = doc.createElementNS( FOXML_NS, "foxml:binaryContent" );
        dsv.appendChild( binelement );
        binelement.appendChild( b64content );
    }


    private void addContentLocation( String dsvId, String ref, String type ) throws XPathExpressionException
    {
        String expr = String.format( "//foxml:datastreamVersion[@ID='%s']/foxml:contentLocation", dsvId );


        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Element location = (Element) nodes.item( 0 );
        if( location == null )
        {
            location = setContentLocationElement( dsvId );
        }
        location.setAttribute( "REF", ref );
        location.setAttribute( "TYPE", type );


    }


    private Element setContentLocationElement( String dsvId )
    {
        Node node = getDatastreamVersion( dsvId );
        Element location = doc.createElementNS( FOXML_NS, "foxml:contentLocation" );
        node.appendChild( location );
        return location;
    }


    private Node getDatastreamVersion( String dsvId )
    {
        String expr = String.format( "//foxml:datastreamVersion[@ID='%s']", dsvId );

        try
        {
            NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
            Node node = nodes.item( 0 );
            if( node == null )
            {
                throw new IllegalArgumentException( String.format( "%s does not exist.", dsvId ) );
            }
            return node;
        }
        catch( XPathExpressionException e )
        {
            throw new IllegalArgumentException( String.format( "%s does not exist.", dsvId ) );
        }
    }


    private void constructFoxml( CargoContainer cargo ) throws TransformerException, UnsupportedEncodingException, IOException, XPathExpressionException
    {
        constructFoxmlProperties( cargo );

        cargo = constructAdminstream( cargo );

        for( CargoObject co : cargo.getCargoObjects() )
        {
            this.addDatastream( co.getDataStreamType().getName(),
                    State.A,
                    ControlGroup.M,
                    co.getFormat(),
                    co,
                    false,
                    false );
        }

        if( cargo.hasCargo( DataStreamType.DublinCoreData ) ){
            this.addDublinCoreDatastream( cargo );
        }
        else
        {
            log.warn( String.format( "Data '%s' ( '%s' ) has no associated Dublin Core data", cargo.getDCTitle(), cargo.getDCIdentifier() ) );
        }
        //\todo: shold we implement some sort of event notification, such that 
        // serialize will not be called before constructFoxml has finished?
    }


    private void constructFoxmlProperties( CargoContainer cargo )
    {
        String timestamp = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" ).format( new Date( System.currentTimeMillis() ) );
        //\todo: we always create active objects (until told otherwise)
        addObjectProperty( Property.STATE, "Active" );
        //\todo: by convention:
        addObjectProperty( Property.LABEL, cargo.getCargoObject( DataStreamType.OriginalData ).getFormat() );
        //\todo: hardcoding owner value of digital objects
        addObjectProperty( Property.OWNERID, "dbc" );
        addObjectProperty( Property.CREATE_DATE, timestamp );
        // Upon creation, the last modified date == created date
        addObjectProperty( Property.MOD_DATE, timestamp );
    }


    private CargoContainer constructAdminstream( CargoContainer cargo ) throws TransformerException, UnsupportedEncodingException, IOException
    {
        int cargo_count = cargo.getCargoObjectCount();

        // Constructing list with datastream indexes and id
        List<ComparablePair<String, Integer>> lst = new ArrayList<ComparablePair<String, Integer>>();
        for( int i = 0; i < cargo_count; i++ )
        {
            CargoObject c = cargo.getCargoObjects().get( i );
            lst.add( new ComparablePair<String, Integer>( c.getDataStreamType().getName(), i ) );
        }

        Collections.sort( lst );

        // Add a number to the id according to the number of
        // datastreams with this datastreamtype name
        int j = 0;
        DataStreamType dsn = null;

        List<ComparablePair<Integer, String>> lst2 = new ArrayList<ComparablePair<Integer, String>>();
        for( Pair<String, Integer> p : lst )
        {
            if( dsn != DataStreamType.getDataStreamTypeFrom( p.getFirst() ) )
            {
                j = 0;
            }
            else
            {
                j += 1;
            }

            dsn = DataStreamType.getDataStreamTypeFrom( p.getFirst() );

            lst2.add( new ComparablePair<Integer, String>( p.getSecond(), p.getFirst() + "." + j ) );
        }

        lst2.add( new ComparablePair<Integer, String>( lst2.size(), DataStreamType.AdminData.getName() ) );

        Collections.sort( lst2 );

        //DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        //DocumentBuilder builder = factory.newDocumentBuilder();

        Document admStream = builder.newDocument();
        Element root = admStream.createElement( "admin-stream" );

        Element indexingaliasElem = admStream.createElement( "indexingalias" );
        indexingaliasElem.setAttribute( "name", cargo.getIndexingAlias( DataStreamType.OriginalData ).getName() );
        root.appendChild( (Node) indexingaliasElem );

        Node streams = admStream.createElement( "streams" );

        int counter = cargo.getCargoObjectCount();

        for( int i = 0; i < counter; i++ )
        {
            CargoObject c = cargo.getCargoObjects().get( i );

            Element stream = admStream.createElement( "stream" );

            stream.setAttribute( "id", lst2.get( i ).getSecond() );
            stream.setAttribute( "lang", c.getLang() );
            stream.setAttribute( "format", c.getFormat() );
            stream.setAttribute( "mimetype", c.getMimeType() );
            stream.setAttribute( "submitter", c.getSubmitter() );
            stream.setAttribute( "index", Integer.toString( lst2.get( i ).getFirst() ) );
            stream.setAttribute( "streamNameType", c.getDataStreamType().getName() );
            streams.appendChild( (Node) stream );
        }

        root.appendChild( streams );

        byte[] admByteArray = XMLUtils.getByteArray( root );

        cargo.add( DataStreamType.AdminData, "admin", "dbc", "da", "text/xml", IndexingAlias.None, admByteArray );

        return cargo;

    }


    /**
     * Serializes the FoxmlDocument into a foxml 1.1 string representation that
     * is written to the OutputStream
     * @param out the OutputStream to write the foxml serialization to
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void serialize( OutputStream out ) throws TransformerConfigurationException, TransformerException
    {
        /** \todo serialize should construct an admin stream upon invocation*/
        Transformer idTransform;
        idTransform = xformFactory.newTransformer();
        Source input = new DOMSource( doc );
        Result output = new StreamResult( out );
        idTransform.transform( input, output );

    }


}
