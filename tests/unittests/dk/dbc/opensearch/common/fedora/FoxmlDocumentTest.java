/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */


package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;


/**
 * All the testAdd*() test methods implicitely tests the serialization method of the FedoraDocument class
 */
public class FoxmlDocumentTest
{

    CargoContainer cargo;
    String origStr;
    static final String pid = "test:1";
    static String utf8Str = "æøå";
    static long timestamp = System.currentTimeMillis();
    static String timeNow = (new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" )).format( new Date( timestamp ) );

    static String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"" + timeNow + "\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"" + timeNow + "\"/></foxml:objectProperties><foxml:datastream ID=\"originalData.0\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><foxml:datastreamVersion ID=\"originalData.0.0\" LABEL=\"test [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"6\"><foxml:binaryContent>w6bDuMOl</foxml:binaryContent></foxml:datastreamVersion></foxml:datastream><foxml:datastream ID=\"adminData\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><foxml:datastreamVersion ID=\"adminData.0\" LABEL=\"admin [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"247\"><foxml:binaryContent>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48YWRtaW4tc3RyZWFtPjxpbmRleGluZ2FsaWFzIG5hbWU9ImFydGljbGUiLz48c3RyZWFtcz48c3RyZWFtIGZvcm1hdD0idGVzdCIgaWQ9Im9yaWdpbmFsRGF0YS4wIiBpbmRleD0iMCIgbGFuZz0iZW5nIiBtaW1ldHlwZT0idGV4dC94bWwiIHN0cmVhbU5hbWVUeXBlPSJvcmlnaW5hbERhdGEiIHN1Ym1pdHRlcj0iZGJjIi8+PC9zdHJlYW1zPjwvYWRtaW4tc3RyZWFtPg==</foxml:binaryContent></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";
    static String skeletonFoxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testdata\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\""+timeNow+"\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\""+timeNow+"\"/></foxml:objectProperties></foxml:digitalObject>";

    static String expectedXmlContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testdata\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\""+timeNow+"\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\""+timeNow+"\"/></foxml:objectProperties><foxml:datastream CONTROL_GROUP=\"X\" ID=\"test:1\" STATE=\"A\" VERSIONABLE=\"false\"><foxml:datastreamVersion CREATED=\""+timeNow+"\" ID=\"test:1.0\" LABEL=\"testdata\" MIMETYPE=\"text/xml\" SIZE=\"52\"><foxml:xmlContent><root><elem attr=\"val\"/></root></foxml:xmlContent></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";

    static String expectedBinaryContent = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testdata\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\""+timeNow+"\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\""+timeNow+"\"/></foxml:objectProperties><foxml:datastream CONTROL_GROUP=\"M\" ID=\"test\" STATE=\"A\" VERSIONABLE=\"false\"><foxml:datastreamVersion CREATED=\""+timeNow+"\" ID=\"test.0\" LABEL=\"testdata\" MIMETYPE=\"text/xml\" SIZE=\"6\"><foxml:binaryContent>w6bDuMOl</foxml:binaryContent></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";

    static String expectedContentLocation = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testdata\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\""+timeNow+"\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\""+timeNow+"\"/></foxml:objectProperties><foxml:datastream CONTROL_GROUP=\"E\" ID=\"ReferencedContent\" STATE=\"A\" VERSIONABLE=\"true\"><foxml:datastreamVersion CREATED=\""+timeNow+"\" ID=\"ReferencedContent.0\" LABEL=\"testdata\" MIMETYPE=\"application/pdf\"><foxml:contentLocation REF=\"http://www.dbc.dk/document\" TYPE=\"URL\"/></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";

    static String expectedDublinCore = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\""+pid+"\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testdata\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\""+timeNow+"\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\""+timeNow+"\"/></foxml:objectProperties><foxml:datastream CONTROL_GROUP=\"X\" ID=\"DC\" STATE=\"A\" VERSIONABLE=\"true\"><foxml:datastreamVersion CREATED=\""+timeNow+"\" ID=\"DC.0\" LABEL=\"Dublin Core data\" MIMETYPE=\"text/xml\" SIZE=\"115\"><foxml:xmlContent><dcrecord xmlns:dc=\"http://purl.org/dc/elements/1.1/\"><dc:title>Hej Hest</dc:title></dcrecord></foxml:xmlContent></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";

    @BeforeClass
    public static void SetupClass()
    {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put( "x", "info:fedora/fedora-system:def/foxml#" );
        SimpleNamespaceContext ctx = new SimpleNamespaceContext( m );
        XMLUnit.setXpathNamespaceContext( ctx );
    }


    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }

    @Before
    public void SetUp() throws UnsupportedEncodingException, IOException
    {
        byte[] cargoBytes = utf8Str.getBytes( "UTF-8" );
        cargo = new CargoContainer( );
        cargo.setIdentifier( new PID( pid ));
        
        cargo.add( DataStreamType.OriginalData, "test", "dbc", "da", "text/xml", IndexingAlias.Article , cargoBytes);
    }

    /**
     * tests basic constructor sanity on {@link FoxmlDocument#FoxmlDocument(String)}
     */
    @Test
    public void testConstructorWithPidSkeletalFoxml() throws ParserConfigurationException, TransformerConfigurationException, TransformerException, SAXException, IOException
    {
        FoxmlDocument f = new FoxmlDocument( FoxmlDocument.State.A, pid, "testdata", "dbc", timestamp );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        f.serialize( baos, null );
        String xml = new String( baos.toByteArray() );
        assertEquals( skeletonFoxml, xml );
    }


    /**
     * Test of addDatastream method, of class FoxmlDocument.
     */
    @Test
    public void testAddXmlContent() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerConfigurationException, TransformerException
    {
        String label = "testdata";
        FoxmlDocument f = new FoxmlDocument( FoxmlDocument.State.A, pid, label, "dbc", timestamp );
        f.addXmlContent( pid, "<?xml version=\"1.0\"?><root><elem attr=\"val\"/></root>", label, timestamp, false );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        f.serialize( baos, null );
        String xml = new String( baos.toByteArray() );
        assertEquals( expectedXmlContent, xml );
    }

    /**
     *
     */
    @Test
    public void testAddBinaryContent() throws ParserConfigurationException, SAXException, IOException, XPathExpressionException, TransformerConfigurationException, TransformerException
    {
        String label = "testdata";
        FoxmlDocument f = new FoxmlDocument( FoxmlDocument.State.A, pid, label, "dbc", timestamp );
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );
        f.addBinaryContent( co.getFormat(), co.getBytes(), label, co.getMimeType(), timestamp);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        f.serialize( baos, null);

        String xml = new String( baos.toByteArray() );
        assertEquals( expectedBinaryContent, xml );
    }

    /**
     *
     */
    @Test
    public void testAddContentLocation() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, TransformerConfigurationException, TransformerException
    {
        String label = "testdata";
        FoxmlDocument f = new FoxmlDocument( FoxmlDocument.State.A, pid, label, "dbc", timestamp );
        f.addContentLocation( "ReferencedContent", "http://www.dbc.dk/document", label, "application/pdf", FoxmlDocument.LocationType.URL, timestamp );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        f.serialize( baos, null );
        String xml = new String( baos.toByteArray() );
        assertEquals( expectedContentLocation, xml );
    }

    /**
     *
     */
    @Test
    public void testAddDublinCoreStream() throws ParserConfigurationException, XPathExpressionException, SAXException, IOException, TransformerConfigurationException, TransformerException
    {
        String label = "testdata";
        FoxmlDocument f = new FoxmlDocument( FoxmlDocument.State.A, pid, label, "dbc", timestamp );
        String dcContent = "<?xml version=\"1.0\"?><dcrecord xmlns:dc=\"http://purl.org/dc/elements/1.1/\"><dc:title>Hej Hest</dc:title></dcrecord>";
        f.addDublinCoreDatastream( dcContent, timestamp );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        f.serialize( baos, null );
        String xml = new String( baos.toByteArray() );

        assertEquals( expectedDublinCore, xml );
    }


    /**
     *
     */
    @Test
    public void testContentLocationWithWrongType()
    {

    }

    /**
     * 
     */
    @Test
    public void testIllegalArgumentInN()
    {

    }

}
