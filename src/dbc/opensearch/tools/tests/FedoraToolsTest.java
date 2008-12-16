package dbc.opensearch.tools.tests;


import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;

import dbc.opensearch.tools.FedoraTools;

import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;


public class FedoraToolsTest
{
    private Logger log = Logger.getLogger( "FedoraToolsTest" );
    

    private static FedoraTools fedoraToolsMock;
    private CargoContainer cargo;
    private String teststring;
    
    
    @Before public void SetUp() throws IllegalArgumentException, NullPointerException, IOException  
    {
        fedoraToolsMock = createMock( FedoraTools.class );
        
        /*teststring = "æøå";
        InputStream data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );
        cargo = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );*/
    }


    @After public void TearDown()
    {
		reset ( fedoraToolsMock );
    }


    /*@Ignore("to be implemented") 
    @Test public void constructFoxmlTest() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException
    {
        byte[] b = FedoraTools.constructFoxml( cargo, "", "", "" );
        int expectedLength = 6;
        System.out.println(b.length);
        assertTrue( expectedLength == b.length );
    }*/
    
    
    @Test public void testConstructFoxml_1() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException, ParseException
    {
    	teststring = "æøå";
        InputStream data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );
        
        Date now = new Date ( System.currentTimeMillis() );
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        String timeNow = dateFormat.format( now );
        Date now2 = dateFormat.parse( timeNow );
        
        cargo = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );
        
        byte[] b = fedoraToolsMock.constructFoxml( cargo, "nextPid_1", "itemId_1", "label_1", now );
        String bStr = new String( b );
        
        String expected = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
        		"<digitalObject xmlns=\"info:fedora/fedora-system:def/foxml#\" VERSION=\"1.1\"><objectProperties><property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"Active\"/><property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"label_1\"/><property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"user\"/><property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"" + timeNow + "\"/><property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"" + timeNow + "\"/></objectProperties><datastream ID=\"itemId_1\" CONTROL_GROUP=\"M\" STATE=\"A\" VERSIONABLE=\"false\"><datastreamVersion ID=\"itemId_1.0\" LABEL=\"itemId_1 [text/xml]\" CREATED=\"" + timeNow + "+01:00\" MIMETYPE=\"itemId_1 [text/xml]\" SIZE=\"6\"><contentDigest DIGEST=\"w6bDuMOl\"/><binaryContent>w6bDuMOl</binaryContent></datastreamVersion></datastream></digitalObject>";

        assertEquals( expected, bStr );
    }
}