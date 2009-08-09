package dk.dbc.opensearch.common.fedora;

import fedora.utilities.Base64;
import fedora.utilities.NamespaceContextImpl;
import fedora.utilities.XmlTransformUtility;

import java.io.IOException;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;

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
    private XPathFactory factory;
    private XPath xpath;
    private TransformerFactory xformFactory;

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

    /**
     *
     */
    public enum State
    {

        /**
         * Active
         */
        A,
        /**
         * Inactive
         */
        I,
        /**
         * Deleted
         */
        D;
    }

    /**
     *
     */
    public enum ControlGroup
    {

        /**
         *
         */
        X,
        /**
         * 
         */
        M,
        /**
         *
         */
        E,
        /**
         * 
         */
        R,
        /**
         *
         */
        B;
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
     * Creates a skeletal FedoraObject document with a timestamp of System.currentTimeMillis().
     * Its serialized representation
     * can be obtained through the {@link FoxmlDocument#serialize(java.io.OutputStream)} method
     *
     * @param pid the pid to be the identifier for the Digital Object
     * @param label short description of the contents of the Digital Object
     * @param owner the owner of the Digital Object
     * @throws ParserConfigurationException
     */
//    public FoxmlDocument( String pid, String label, String owner ) throws ParserConfigurationException
//    {
//        initDocument( pid );
//        constructFoxmlProperties( label, owner, getTimestamp( 0l ) );
//    }
//

    /**
     * Creates a skeletal FedoraObject document. Its serialized representation
     * can be obtained through the
     * {@link FoxmlDocument#serialize(java.io.OutputStream)} method
     * 
     * @param pid the pid to be the identifier for the Digital Object
     * @param label short description of the contents of the Digital Object
     * @param owner the owner of the Digital Object
     * @param timestamp the time of creation of the Digital Object,
     *        System.currentTimeMillis() is a fine choice
     * @throws ParserConfigurationException
     */
    public FoxmlDocument( String pid, String label, String owner, long timestamp ) throws ParserConfigurationException
    {
        /** \todo: a fedora document v1.1 pid must conform to the following rules
         * a maximum length of 64 chars
         * must satisfy the pattern "([A-Za-z0-9]|-|\.)+:(([A-Za-z0-9])|-|\.|~|_|(%[0-9A-F]{2}))+"
         */
        initDocument( pid );
        constructFoxmlProperties( label, owner, getTimestamp( timestamp ) );
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

        factory = XPathFactory.newInstance();
        xpath = factory.newXPath();
        xformFactory = XmlTransformUtility.getTransformerFactory();

        NamespaceContextImpl nsCtx = new NamespaceContextImpl();
        nsCtx.addNamespace( "foxml", FOXML_NS );


        xpath.setNamespaceContext( nsCtx );
    }


    private void constructFoxmlProperties( String label, String owner, String timestamp )
    {
        //\todo: we always create active objects (until told otherwise)
        addObjectProperty( Property.STATE, "Active" );
        addObjectProperty( Property.LABEL, label );
        addObjectProperty( Property.OWNERID, owner );
        addObjectProperty( Property.CREATE_DATE, timestamp );
        // Upon creation, the last modified date == created date
        addObjectProperty( Property.MOD_DATE, timestamp );
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
    private String addDatastream( String id,
                                  State state,
                                  ControlGroup controlGroup,
                                  boolean versionable ) throws XPathExpressionException, SAXException, IOException
    {
        Element ds = doc.createElementNS( FOXML_NS, "foxml:datastream" );
        ds.setAttribute( "ID", id );
        ds.setAttribute( "STATE", state.toString() );
        ds.setAttribute( "CONTROL_GROUP", controlGroup.toString() );
        ds.setAttribute( "VERSIONABLE", Boolean.toString( versionable ) );
        rootElement.appendChild( ds );

        return id;
    }


    private void addDatastreamVersion( String dsId,
                                       String dsvId,
                                       String mimeType,
                                       String label,
                                       int size,
                                       String created ) throws XPathExpressionException
    {
        String expr = String.format( "//foxml:datastream[@ID='%s']", dsId );
        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Node node = nodes.item( 0 );
        if( node == null )
        {
            throw new IllegalArgumentException( dsId + "does not exist." );
        }
        if( dsvId == null || dsvId.equals( "" ) )
        {
            dsvId = dsId + ".0";
        }

        Element dsv = doc.createElementNS( FOXML_NS, "foxml:datastreamVersion" );
        dsv.setAttribute( "ID", dsvId );
        dsv.setAttribute( "MIMETYPE", mimeType );
        dsv.setAttribute( "LABEL", label );
        if( size != 0 )
        {
            dsv.setAttribute( "SIZE", Integer.toString( size ) );
        }
        dsv.setAttribute( "CREATED", created );
        node.appendChild( dsv );
    }


    /**
     * 
     * @param cargo
     * @throws XPathExpressionException
     */
    public void addDublinCoreDatastream( String dcdata, long timenow ) throws XPathExpressionException, SAXException, IOException
    {
        String label = "Dublin Core data";
        String id = "DC";
        this.addXmlContent( id, dcdata, label, timenow, true );
    }


    /**
     * Constructs a datastream and a datastreamversion in the Digital Object
     * @param datastreamId
     * @param xmlContent
     * @param versionable
     * @throws SAXException
     * @throws IOException
     */
    public void addXmlContent( String datastreamId, String xmlContent, String label, long timenow, boolean versionable ) throws SAXException, IOException, XPathExpressionException
    {
        String dsId = addDatastream( datastreamId, State.A, ControlGroup.X, versionable );
        String dsvId = dsId + ".0";
        addDatastreamVersion( dsId, dsvId, "text/xml", label, xmlContent.length(), getTimestamp( timenow ) );
        Document contentDoc = builder.parse( new InputSource( new StringReader( xmlContent ) ) );
        Node importedContent = doc.adoptNode( contentDoc.getDocumentElement() );
        Node dsv = getDatastreamVersion( dsvId );
        Element content = doc.createElementNS( FOXML_NS, "foxml:xmlContent" );
        dsv.appendChild( content );
        content.appendChild( importedContent );
    }


    /**
     *
     * @param dsvId
     * @param content
     * @throws SAXException
     * @throws IOException
     */
    public void addBinaryContent( String datastreamId, byte[] content, String label, long timenow ) throws SAXException, IOException, XPathExpressionException
    {
        String dsId = addDatastream( datastreamId, State.A, ControlGroup.X, false );
        String dsvId = dsId + ".0";
        addDatastreamVersion( dsId, dsvId, "application/octet-stream", label, content.length, getTimestamp( timenow ) );
        String b = Base64.encodeToString( content );
//        Document contentDoc = builder.parse( new InputSource( new StringReader( b ) ) );

//        Node b64content = doc.adoptNode( contentDoc.getDocumentElement() );

        //Node b64content =
        Node dsv = getDatastreamVersion( dsvId );
        Element binelement = doc.createElementNS( FOXML_NS, "foxml:binaryContent" );
        dsv.appendChild( binelement );
        binelement.setTextContent( b );
//        binelement.appendChild( b64content );
    }


    /**
     *
     * @param dsvId
     * @param ref
     * @param type
     * @throws XPathExpressionException
     */
    public void addContentLocation( String datastreamId, String ref, String label, String mimetype, String type, long timenow ) throws XPathExpressionException, SAXException, IOException
    {
        String dsId = addDatastream( datastreamId, State.A, ControlGroup.E, true );
        String dsvId = dsId + ".0";
        addDatastreamVersion( dsId, dsvId, mimetype, label, 0, getTimestamp( timenow ) );
        String expr = String.format( "//foxml:datastreamVersion[@ID='%s']/foxml:contentLocation", datastreamId );

        NodeList nodes = (NodeList) xpath.evaluate( expr, doc, XPathConstants.NODESET );
        Element location = (Element) nodes.item( 0 );
        if( location == null )
        {
            location = setContentLocationElement( dsvId );
        }
        location.setAttribute( "REF", ref );
        if( ! type.equals( "URL" ) || type.equals( "INTERNAL_ID" ) )
        {
            throw new IllegalArgumentException( "Type must be either 'URL' or 'INTERNAL_REF'");
        }
        location.setAttribute( "TYPE", type );
    }


    private Element setContentLocationElement( String dsvId )
    {
        Node node = getDatastreamVersion( dsvId );
        Element location = doc.createElementNS( FOXML_NS, "foxml:contentLocation" );
        node.appendChild( location );
        return location;
    }


    /**
     * Get datastreamversion identified by dsvId
     */
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


    /**
     * gets a string representation of a timestamp. If 0l is given as an
     * argument, a timestamp constructed from System.currentTimeMillis is
     * returned
     */
    private String getTimestamp( long time )
    {
        if( time == 0 )
        {
            time = System.currentTimeMillis();
        }
        return new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" ).format( new Date( time ) );
    }


    /**
     * Serializes the FoxmlDocument into a foxml 1.1 string representation that
     * is written to the OutputStream
     * @param out the OutputStream to write the foxml serialization to
     * @param schemaurl Schema to validate the serialization against. If null, no validation will be performed
     * @throws TransformerConfigurationException
     * @throws TransformerException
     */
    public void serialize( OutputStream out, URL schemaurl ) throws TransformerConfigurationException, TransformerException, SAXException, IOException
    {
        Transformer idTransform;
        idTransform = xformFactory.newTransformer();
        Source input = new DOMSource( doc );
        if( schemaurl != null )
        {
            SchemaFactory schemaf = javax.xml.validation.SchemaFactory.newInstance( FOXML_NS );
            Schema schema = schemaf.newSchema( schemaurl );
            Validator validator = schema.newValidator();
            validator.validate( input );
        }
        Result output = new StreamResult( out );
        idTransform.transform( input, output );

    }


}
