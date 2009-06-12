package dk.dbc.opensearch.common.fedora;

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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

import dk.dbc.opensearch.common.fedora.FedoraTools;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.DataStreamType;

import org.junit.*;

import static org.junit.Assert.*;
import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;


public class FedoraToolsTest
{
    //private Logger log = Logger.getLogger( "FedoraToolsTest" );
    

    private CargoContainer cargo;
    private Date now;
    private String timeNow;
    
    
    @Before public void SetUp() throws IllegalArgumentException, NullPointerException, IOException  
    {
    	now = new Date ( System.currentTimeMillis() );
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        timeNow = dateFormat.format( now );
    }


    @After public void TearDown()
    {
		//reset ( fedoraToolsMock );
    }


    /*@Ignore("to be implemented") 
    @Test public void constructFoxmlTest() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException
    {
        byte[] b = FedoraTools.constructFoxml( cargo, "", "", "" );
        int expectedLength = 6;
        System.out.println(b.length);
        assertTrue( expectedLength == b.length );
    }*/
    
    
    private CargoContainer constructCargo( String testStr ) throws IllegalArgumentException, NullPointerException, IOException
    {
    	byte[] cargoBytes =  testStr.getBytes( "UTF-8" );    	
    	//CargoContainer ret = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );
    	CargoContainer ret = new CargoContainer();
    ret.add( DataStreamType.getDataStreamNameFrom( "originalData" ), "test", "dbc", "eng", "text/xml", IndexingAlias.getIndexingAlias( "article" ) , cargoBytes);
    	
    	return ret;
    }

    @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring and the redesign of the FedoraTools file" )
    @Test public void testConstructFoxmlValidation_1() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException
    {
    	cargo = constructCargo( "æøå" );
        
        byte[] b = FedoraTools.constructFoxml( cargo, "dbc:1", "label_1", now );
        String bStr = new String( b );
        

        System.out.println( String.format("the constructed string: %s", bStr) );
        //need the adminstream as well

        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        		"<digitalObject xmlns=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\" PID=\"dbc:1\"><objectProperties><property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/><property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/><property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"user\"/><property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"" + timeNow + "\"/><property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"" + timeNow + "\"/></objectProperties><datastream ID=\"originalData.0\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"originalData.0.0\" LABEL=\"test [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"6\"><contentDigest DIGEST=\"w6bDuMOl\"/><binaryContent>w6bDuMOl</binaryContent></datastreamVersion></datastream><datastream ID=\"adminData\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"adminData.0\" LABEL=\"admin [text/xml]\" CREATED=\"" + timeNow + "+02:00\" MIMETYPE=\"text/xml\" SIZE=\"247\"><binaryContent>PD94bWwgdmVyc2lvbj0iMS4wIiBlbmNvZGluZz0iVVRGLTgiPz48YWRtaW4tc3RyZWFtPjxpbmRleGluZ2FsaWFzIG5hbWU9ImFydGljbGUiLz48c3RyZWFtcz48c3RyZWFtIGZvcm1hdD0idGVzdCIgaWQ9Im9yaWdpbmFsRGF0YS4wIiBpbmRleD0iMCIgbGFuZz0iZW5nIiBtaW1ldHlwZT0idGV4dC94bWwiIHN0cmVhbU5hbWVUeXBlPSJvcmlnaW5hbERhdGEiIHN1Ym1pdHRlcj0iZGJjIi8+PC9zdHJlYW1zPjwvYWRtaW4tc3RyZWFtPg==</binaryContent></datastreamVersion></datastream></digitalObject>";

        System.out.println( String.format( "the expected string: %s", expected ) );
        //assertEquals( expected, bStr );

        
        String expected1 = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
            "<digitalObject xmlns=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\" PID=\"dbc:1\">"+
            "<objectProperties>"+
            "<property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/>"+
            "<property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/>"+
            "<property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"user\"/>"+
            "<property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"";

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

        if ( bStr.indexOf( expected1 ) != 0 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml did not start with expected1" );
        }
        // remove expected1 substring from the string
        bStr = bStr.substring( expected1.length(), bStr.length() );

        if ( bStr.indexOf( expected2 ) == 0 || bStr.indexOf( expected2 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected2" );
        }
        // isolate date1 and remove i from bStr
        date1 = bStr.substring( 0, bStr.indexOf( expected2 ) );
        bStr = bStr.substring( bStr.indexOf( expected2 ), bStr.length() );

        if ( bStr.indexOf( expected3 ) == 0 || bStr.indexOf( expected3 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected3" );
        }
        // isolate date2 and remove expected2 from bStr
        bStr = bStr.substring( expected2.length(), bStr.length() );
        date2 = bStr.substring( 0, bStr.indexOf( expected3 ) );
        bStr = bStr.substring( bStr.indexOf( expected3 ), bStr.length() );

        if ( bStr.indexOf( expected4 ) == 0 || bStr.indexOf( expected4 ) == -1 ){
            fail( "Wrong return value from FedoraTools.constructFoxml. returned xml started or couldnt find expected4" );
        }
        // isolate date3 and remove expected3 from bStr
        bStr = bStr.substring( expected3.length(), bStr.length() );
        date3 = bStr.substring( 0, bStr.indexOf( expected4 ) );
        bStr = bStr.substring( bStr.indexOf( expected4 ), bStr.length() );
        assertEquals( expected4, bStr );


    }
    
    
    /*@Test public void testConstructFoxmlValidation() throws SAXException, IOException, MarshalException, ValidationException, ParseException
    {
   	 	// 1. Lookup a factory for the W3C XML Schema language
    	// http://www.ibm.com/developerworks/xml/library/x-javaxmlvalidapi.html
        SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");
        
        // 2. Compile the schema. 
        // Here the schema is loaded from a java.io.File, but you could use 
        // a java.net.URL or a javax.xml.transform.Source instead.
        // \todo: this test will break if the ant script is not invoked at top level. Paths should not be specified like this.
        File schemaLocation = new File("src/xsd/foxml1-1.xsd");
        Schema schema = factory.newSchema(schemaLocation);
    
        // 3. Get a validator from the schema.
        Validator validator = schema.newValidator();
        
        // 4. Parse the document you want to check.
        cargo = constructCargo( "test" );
        byte[] b = FedoraTools.constructFoxml( cargo, "nextPid_validation", "itemId_validation", "label_validation", now );
        ByteArrayInputStream xml = new ByteArrayInputStream( b );
        Source source = new StreamSource(xml);
        
        // 5. Check the document
        boolean valid;
        try {
            validator.validate(source);
            valid = true;
        }
        catch (SAXException ex) {
            valid = false; 
            //System.out.println(args0 + " is not valid because ");
            //System.out.println(ex.getMessage());
        }  
        
        assertTrue( valid );
    }*/
}