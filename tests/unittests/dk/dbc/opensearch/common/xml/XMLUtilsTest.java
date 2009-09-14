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


package dk.dbc.opensearch.common.xml;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Namespace;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamSource;

import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/** \brief UnitTest for XMLUtils
 * \todo make it possible to the DocumentBuilderFactory or the DocumentBuilder...
 * If that is not possible it is only possible to test the methods with real files */
public class XMLUtilsTest
{
    
    String xmlString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><catalog><cd><title>Empire Burlesque</title><artist>Bob Dylan</artist><country>USA</country><company>Columbia</company><price>10.90</price><year>1985</year></cd></catalog>";

    String xsltString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:template match=\"/\"><html><body><h2>My CD Collection</h2><table border=\"1\"><tr bgcolor=\"#9acd32\"><th>Title</th><th>Artist</th></tr><xsl:for-each select=\"catalog/cd\"><tr><td><xsl:value-of select=\"title\"/></td><td><xsl:value-of select=\"artist\"/></td></tr></xsl:for-each></table></body></html></xsl:template></xsl:stylesheet>";

    String expectedResult = "<html><body><h2>My CD Collection</h2><table border=\"1\"><tr bgcolor=\"#9acd32\"><th>Title</th><th>Artist</th></tr><tr><td>Empire Burlesque</td><td>Bob Dylan</td></tr></table></body></html>";   
    

        
    /**
     *
     */
    @Before
    public void SetUp()
    {} 


    /**
     *
     */
    @After
    public void TearDown() { }


    /**
     *
     */
    @Test
    public void testGetDocumentElement() { }


    @Test
    public void testGetNodeList() { }


    @Test
    public void testXmlToString() throws IOException, ParserConfigurationException, SAXException, TransformerException, UnsupportedEncodingException
    {
        DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();
        Document jobDocument = documentBuilder.parse( new ByteArrayInputStream( expectedResult.getBytes( "UTF-8" ) ) );
        String result = XMLUtils.xmlToString( jobDocument );
        assertEquals( result, expectedResult );
    }
    
    
    @Test
    public void testTransform() throws IOException, ParserConfigurationException, SAXException, TransformerException
    {
        String xsltString = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?><xsl:stylesheet version=\"1.0\" xmlns:xsl=\"http://www.w3.org/1999/XSL/Transform\"><xsl:template match=\"/\"><html><body><h2>My CD Collection</h2><table border=\"1\"><tr bgcolor=\"#9acd32\"><th>Title</th><th>Artist</th></tr><xsl:for-each select=\"catalog/cd\"><tr><td><xsl:value-of select=\"title\"/></td><td><xsl:value-of select=\"artist\"/></td></tr></xsl:for-each></table></body></html></xsl:template></xsl:stylesheet>";

        String expectedResult = "<html><body><h2>My CD Collection</h2><table border=\"1\"><tr bgcolor=\"#9acd32\"><th>Title</th><th>Artist</th></tr><tr><td>Empire Burlesque</td><td>Bob Dylan</td></tr></table></body></html>";
        InputStream xsltStringStream = new ByteArrayInputStream( xsltString.getBytes() );
        InputStream xmlStringStream = new ByteArrayInputStream( xmlString.getBytes() );

        Source xsltSource = new StreamSource( xsltStringStream );
        Source xmlSource = new StreamSource( xmlStringStream );
        Document result = XMLUtils.transform( xmlSource, xsltSource ); 

        assertEquals( XMLUtils.xmlToString( result ), expectedResult );
    }
    
    @Test
    public void testGetNamespaces() throws XMLStreamException
    {
        String nsXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><ting:container xmlns:ting=\"http://www.dbc.dk/ting\" xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns=\"http://www.bs.dk/standards/MarcXchange\"><ting:dc xmlns:nested=\"info:hej\"></ting:dc></ting:container>";
        Map<String,String> expectedNsList = new HashMap<String, String>();
        expectedNsList.put( "", "http://www.bs.dk/standards/MarcXchange" );
        expectedNsList.put( "ting", "http://www.dbc.dk/ting" );
        expectedNsList.put( "dc", "http://purl.org/dc/elements/1.1/" );
        expectedNsList.put( "nested", "info:hej" );

        InputStream nsXmlStream = new ByteArrayInputStream( nsXml.getBytes() );
        List<Namespace> nsList = XMLUtils.getNamespaces( nsXmlStream );

        assertTrue( expectedNsList.size() == nsList.size() );

        for( Namespace ns : nsList )
        {
            // we'll not test for prefixes, as they're just rubble
            assertTrue( expectedNsList.containsValue( ns.getNamespaceURI() ) );
        }
    }
}