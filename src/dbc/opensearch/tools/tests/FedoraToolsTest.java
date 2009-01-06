package dbc.opensearch.tools.tests;


import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import dbc.opensearch.tools.FedoraTools;

import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;

public class FedoraToolsTest
{
    private Logger log = Logger.getLogger( "FedoraToolsTest" );
    

    //private static FedoraTools fedoraToolsMock;
    private CargoContainer cargo;
    private Date now;
    private String timeNow;
    
    
    @Before public void SetUp() throws IllegalArgumentException, NullPointerException, IOException  
    {
    	now = new Date ( System.currentTimeMillis() );
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        timeNow = dateFormat.format( now );
        //fedoraToolsMock = createMock( FedoraTools.class );
        
        /*teststring = "æøå";
        InputStream data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );
        cargo = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );*/
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
    	InputStream data = new ByteArrayInputStream( testStr.getBytes( "UTF-8" ) );    	
    	CargoContainer ret = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );
    	
    	return ret;
    }
    
    @Test public void testConstructFoxml_1() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException, ParseException
    {
    	cargo = constructCargo( "æøå" );
        
        byte[] b = FedoraTools.constructFoxml( cargo, "nextPid_1", "itemId_1", "label_1", now );
        String bStr = new String( b );
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        		"<digitalObject xmlns=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\"><objectProperties><property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/><property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/><property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"user\"/><property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"" + timeNow + "\"/><property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"" + timeNow + "\"/></objectProperties><datastream ID=\"itemId_1\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"itemId_1.0\" LABEL=\"itemId_1 [text/xml]\" CREATED=\"" + timeNow + "+01:00\" MIMETYPE=\"itemId_1 [text/xml]\" SIZE=\"6\"><contentDigest DIGEST=\"w6bDuMOl\"/><binaryContent>w6bDuMOl</binaryContent></datastreamVersion></datastream></digitalObject>";

        assertEquals( expected, bStr );
    }
    
    
    @Test public void testConstructFoxmlValidation() throws SAXException, IOException, MarshalException, ValidationException, ParseException
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
    }
}