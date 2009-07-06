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
package dk.dbc.opensearch.common.fedora;



import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import org.junit.*;
import static org.junit.Assert.*;
import org.xml.sax.SAXException;
import org.custommonkey.xmlunit.*;
import org.custommonkey.xmlunit.exceptions.XpathException;
import java.util.HashMap;
import org.custommonkey.xmlunit.SimpleNamespaceContext;


/**
 *
 */
public class FedoraAdministrationTest// extends XMLTestCase
{

    CargoContainer cargo;
    String origStr;

    static Date now = new Date ( System.currentTimeMillis() );
    static String timeNow = (new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.S" )).format( now );
    static String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        "<digitalObject xmlns=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\" PID=\"dbc:1\"><objectProperties><property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/><property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/><property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"dbc\"/><property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"" + timeNow + "\"/><property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"" + timeNow + "\"/></objectProperties><datastream ID=\"originalData.0\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"originalData.0.0\" LABEL=\"test [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"6\"><binaryContent>w6bDuMOl</binaryContent></datastreamVersion></datastream><datastream ID=\"adminData\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"adminData.0\" LABEL=\"admin [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"247\"><binaryContent>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48YWRtaW4tc3RyZWFtPjxpbmRleGluZ2FsaWFzIG5hbWU9ImFydGljbGUiLz48c3RyZWFtcz48c3RyZWFtIGZvcm1hdD0idGVzdCIgaWQ9Im9yaWdpbmFsRGF0YS4wIiBpbmRleD0iMCIgbGFuZz0iZW5nIiBtaW1ldHlwZT0idGV4dC94bWwiIHN0cmVhbU5hbWVUeXBlPSJvcmlnaW5hbERhdGEiIHN1Ym1pdHRlcj0iZGJjIi8+PC9zdHJlYW1zPjwvYWRtaW4tc3RyZWFtPg==</binaryContent></datastreamVersion></datastream></digitalObject>";

    @BeforeClass
    public static void SetupClass()
    {
        HashMap m = new HashMap();
        m.put("x", "info:fedora/fedora-system:def/foxml#");
        SimpleNamespaceContext ctx = new SimpleNamespaceContext(m);
        XMLUnit.setXpathNamespaceContext( ctx );
    }

    @Before
    public void SetUp() throws IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {
        byte[] cargoBytes =  "æøå".getBytes( "UTF-8" );
        cargo = new CargoContainer( );
        cargo.add( DataStreamType.getDataStreamNameFrom( "originalData" ), "test", "dbc", "eng", "text/xml", IndexingAlias.getIndexingAlias( "article" ) , cargoBytes);
        byte[] b = FedoraAdministration.constructFoxml( cargo, "dbc:1", "label_1", now );
        origStr = new String( b );

    }

    @After
    public void TearDown()
    {
        cargo = null;
    }

    @Test public void testDigitalObjectSerialization() throws SAXException, IOException
    {
        XMLAssert.assertXMLEqual( "Comparing test xml to control xml", expected, origStr );
    }

    @Test
    public void testCorrectPIDInsertion() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( "dbc:1", "/x:digitalObject[1]/@PID", origStr);
    }

    @Test
    public void testState() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( "Active", "/x:digitalObject[1]/x:objectProperties[1]/x:property[1]/@VALUE", origStr );
    }

    @Test
    public void testLabel() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( "label_1", "/x:digitalObject[1]/x:objectProperties[1]/x:property[2]/@VALUE", origStr );
    }

    @Test
    public void testOwner() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( "dbc", "/x:digitalObject[1]/x:objectProperties[1]/x:property[3]/@VALUE", origStr );
    }

    @Test
    public void testTimestamp() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( timeNow, "/x:digitalObject[1]/x:objectProperties[1]/x:property[4]/@VALUE", origStr );
    }

    @Test
    public void testDatastreamID() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathEvaluatesTo( "originalData.0", "/x:digitalObject[1]/x:datastream[1]/@ID", origStr );

    }

    @Test
    public void testBinaryContent() throws SAXException, IOException, XpathException
    {
        XMLAssert.assertXpathExists( "/x:digitalObject[1]/x:datastream[1]/x:datastreamVersion[1]/x:binaryContent[1]", origStr );
    }

    @Ignore
    @Test public void testConstructFoxmlValidation_1() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {

        byte[] b = FedoraAdministration.constructFoxml( cargo, "dbc:1", "label_1", now );
        String origStr = new String( b );


        //        System.out.println( String.format( "the expected string: %s", expected ) );
        //assertEquals( expected, origStr );

        String expected2 = "\"/>"+
            "<property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"";

        String expected3= "\"/>"+
            "</objectProperties><datastream ID=\"originalData.0\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\">"+
            "<datastreamVersion ID=\"originalData.0.0\" LABEL=\"test [text/xml]\" CREATED=\"";

        String expected4 = " MIMETYPE=\"text/xml\" SIZE=\"6\">"+
            "<binaryContent>w6bDuMOl</binaryContent></datastreamVersion></datastream>" +
            "<datastream ID=\"adminData\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=" +
            "\"false\"><datastreamVersion ID=\"adminData.0\" LABEL=\"admin [text/xml]\"" +
            " CREATED=";
        String expected5 =" MIMETYPE=\"text/xml\" SIZE=\"247\"><binaryContent>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48YWRtaW4tc3RyZWFtPjxpbmRleGluZ2FsaWFzIG5hbWU9ImFydGljbGUiLz48c3RyZWFtcz48c3RyZWFtIGZvcm1hdD0idGVzdCIgaWQ9Im9yaWdpbmFsRGF0YS4wIiBpbmRleD0iMCIgbGFuZz0iZW5nIiBtaW1ldHlwZT0idGV4dC94bWwiIHN0cmVhbU5hbWVUeXBlPSJvcmlnaW5hbERhdGEiIHN1Ym1pdHRlcj0iZGJjIi8+PC9zdHJlYW1zPjwvYWRtaW4tc3RyZWFtPg==</binaryContent></datastreamVersion></datastream></digitalObject>";

        String date1;
        String date2;
        String date3;



        if ( origStr.indexOf( expected2 ) == 0 || origStr.indexOf( expected2 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected2" );
        }
        // isolate date1 and remove i from origStr
        date1 = origStr.substring( 0, origStr.indexOf( expected2 ) );
        origStr = origStr.substring( origStr.indexOf( expected2 ), origStr.length() );

        if ( origStr.indexOf( expected3 ) == 0 || origStr.indexOf( expected3 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected3" );
        }
        // isolate date2 and remove expected2 from origStr
        origStr = origStr.substring( expected2.length(), origStr.length() );
        date2 = origStr.substring( 0, origStr.indexOf( expected3 ) );
        origStr = origStr.substring( origStr.indexOf( expected3 ), origStr.length() );

        if ( origStr.indexOf( expected4 ) == 0 || origStr.indexOf( expected4 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected4" );
        }
        // isolate date3 and remove expected3 from origStr
        origStr = origStr.substring( expected3.length(), origStr.length() );
        date3 = origStr.substring( 0, origStr.indexOf( expected4 ) );
        origStr = origStr.substring( origStr.indexOf( expected4 ), origStr.length() );
        assertEquals( expected4, origStr );


    }
}