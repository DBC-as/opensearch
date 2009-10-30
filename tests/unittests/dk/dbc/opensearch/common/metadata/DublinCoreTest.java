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


package dk.dbc.opensearch.common.metadata;

import dk.dbc.opensearch.common.types.DataStreamType;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Date;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.HashMap;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.Before;
import static org.custommonkey.xmlunit.XMLAssert.assertXpathEvaluatesTo;
import org.junit.Test;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;

/**
 *
 */
public class DublinCoreTest {

    private static final String expectedDC = "<?xml version=\"1.0\" ?><dc xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\"><dc:identifier>test:1</dc:identifier></dc>";
    private DublinCore dc;

    @BeforeClass
    public static void SetupClass()
    {
        HashMap<String, String> m = new HashMap<String, String>();
        m.put( "x", "http://purl.org/dc/elements/1.1/" );
        SimpleNamespaceContext ctx = new SimpleNamespaceContext( m );
        XMLUnit.setXpathNamespaceContext( ctx );
    }

    @Before
    public void setUp()
    {
        dc = new DublinCore( "test:1" );
    }

    @Test
    public void testConstructorWithId() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        XMLUnit.compareXML( expectedDC, new String( baos.toByteArray() ) );
    }

    @Test
    public void testSetContributor() throws Exception
    {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.setContributor( "dbc" );
        dc.serialize( baos, null );

        assertXpathEvaluatesTo( "dbc", "/dc/x:contributor", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetCoverage() throws Exception
    {
        dc.setCoverage( "cover" );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "cover", "/dc/x:coverage", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetCreator() throws Exception
    {
        dc.setCreator( "creator" );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );

        assertXpathEvaluatesTo( "creator", "/dc/x:creator", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetDate() throws Exception
    {
        Date d = new Date( System.currentTimeMillis() );
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );
        String expectedDate = sdf.format( d ).toString();
        dc.setDate( d );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( expectedDate, "/dc/x:date", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetDescription() throws Exception
    {
        dc.setDescription( "description" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "description", "/dc/x:description", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetFormat()throws Exception
    {
        dc.setFormat( "format" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "format", "/dc/x:format", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetIdentifier() throws Exception
    {
        dc.setIdentifier( "test:2" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "test:2", "/dc/x:identifier", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetLanguage() throws Exception
    {
        dc.setLanguage( "da" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "da", "/dc/x:language", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetPublisher()throws Exception
    {
        dc.setPublisher( "publisher" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "publisher", "/dc/x:publisher", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetRelation()throws Exception
    {
        dc.setRelation( "rel" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "rel", "/dc/x:relation", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetRights()throws Exception
    {
        dc.setRights( "Rights" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "Rights", "/dc/x:rights", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetSource() throws Exception
    {
        dc.setSource( "source" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "source", "/dc/x:source", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetSubject() throws Exception
    {
        dc.setSubject( "subj" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "subj", "/dc/x:subject", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetTitle() throws Exception
    {
        dc.setTitle( "title" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "title", "/dc/x:title", new String( baos.toByteArray() ) );
    }


    @Test
    public void testSetType() throws Exception
    {
        dc.setType( "type" );

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        dc.serialize( baos, null );
        assertXpathEvaluatesTo( "type", "/dc/x:type", new String( baos.toByteArray() ) );
    }


    @Test
    public void testElementCount()
    {
        int expResult = 1;
        int result = dc.elementCount();
        assertEquals( expResult, result );
    }


    @Test
    public void testGetDCValue()
    {
        DublinCoreElement dcElement = DublinCoreElement.ELEMENT_IDENTIFIER;
        String expResult = "test:1";
        String result = dc.getDCValue( dcElement );
        assertEquals( expResult, result );
    }


    @Test
    public void testGetIdentifier()
    {
        String expResult = "test:1";
        String result = dc.getIdentifier();
        assertEquals( expResult, result );
    }


    @Test
    public void testGetType()
    {
        DataStreamType expResult = DataStreamType.DublinCoreData;
        DataStreamType result = dc.getType();
        assertEquals( expResult, result );
    }
}