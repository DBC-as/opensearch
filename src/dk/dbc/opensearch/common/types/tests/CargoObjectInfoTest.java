package dk.dbc.opensearch.common.types.tests;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DataStreamNames;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import org.junit.*;


public class CargoObjectInfoTest
{
    CargoMimeType cmt;
    CargoObjectInfo coi;
    CargoContainer cc;
    
    String test_submitter = "DBC";
    String test_format = "test_format";
    String test_lang = "DA";

    
    @Before 
    public void SetUp() throws UnsupportedEncodingException, IOException
    {
        cmt =  CargoMimeType.TEXT_XML;
        cc = new CargoContainer();
        
        String teststring = "æøå";
        InputStream data = new ByteArrayInputStream( teststring.getBytes( ) );
    	DataStreamNames dataStreamName = DataStreamNames.OriginalData;
        cc.add( dataStreamName, test_format, test_submitter, test_lang, cmt.getMimeType(), data );    	
    }
    
    
    @Test
    public void testGetDataStreamName()
    {
    	for ( CargoObject co : cc.getData() )
    	{
    		assertEquals( DataStreamNames.OriginalData, co.getDataStreamName( ) );
    	}
    }
    
    
    @Test 
    public void testCorrectnessOfgetFormat() 
    {
        for( CargoObject co : cc.getData() )
        	assertTrue( test_format.equals( co.getFormat() ) );
    	       
    }
    
    
    @Test 
    public void testCorrectnessOfgetSubmitter() 
    {    	
    	for( CargoObject co : cc.getData() )
    		assertTrue( test_submitter.equals( co.getSubmitter() ) );
    }
    
    
    @Test 
    public void testCorrectnessOfgetMimeType() 
    {
        for( CargoObject co : cc.getData() )
        	assertTrue( cmt.getMimeType().equals( co.getMimeType() ) );
    	       
    }
    
    
    @Test
    public void testCheckSubmitter()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.checkSubmitter( test_submitter ) );
    }
    
    
    @Test
    public void testCheckLanguage()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.checkLanguage( test_lang ) );
    }
    
    
    @Test
    public void testValidMimetype()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.validMimetype( cmt.getMimeType() ) );
    }
}
