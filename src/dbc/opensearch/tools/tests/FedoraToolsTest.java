package dbc.opensearch.tools.tests;


import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import dbc.opensearch.tools.FedoraTools;
import dbc.opensearch.xsd.DigitalObject;

import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.assertTrue;

import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;


public class FedoraToolsTest
{
    Logger log = Logger.getLogger( "FedoraToolsTest" );


    FedoraTools fedoraToolsMock;
    CargoContainer cargo;
    String teststring;
    
    
    @Before public void SetUp() throws IllegalArgumentException, NullPointerException, IOException  
    {
        fedoraToolsMock = createMock( FedoraTools.class );
        
        teststring = "æøå";
        InputStream data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );
        cargo = new CargoContainer( data, "text/xml", "dk", "stm", "faktalink" );         
    }


    @After public void TearDown()
    {
		reset ( fedoraToolsMock );
    }


    @Ignore("to be implemented") 
    @Test public void constructFoxmlTest() throws IllegalArgumentException, NullPointerException, IOException, MarshalException, ValidationException
    {
        byte[] b = FedoraTools.constructFoxml( cargo, "", "", "" );
        int expectedLength = 6;
        System.out.println(b.length);
        assertTrue( expectedLength == b.length );
    }   
}