package dk.dbc.opensearch.common.types.tests;

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

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * The getTimestamp method is not tested
 */

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
        //InputStream data = new ByteArrayInputStream( teststring.getBytes( ) );
        byte[] data = teststring.getBytes( "UTF-8" );
    	DataStreamType dataStreamName = DataStreamType.OriginalData;
        cc.add( dataStreamName, test_format, test_submitter, test_lang, cmt.getMimeType(), data );    	
    }
    
    
    @Test
    public void testGetDataStreamName()
    {
    	for ( CargoObject co : cc.getData() )
    	{
    		assertEquals( DataStreamType.OriginalData, co.getDataStreamName( ) );
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
    public void testCheckSubmitterInvalid()
    {
    	for( CargoObject co : cc.getData() )
    		assertFalse( co.checkSubmitter( "invalid" ) );
    }
    
    @Test
    public void testCheckLanguageValid()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.checkLanguage( test_lang ) );
    }
     
    @Test
    public void testCheckLanguageInvalid()
    {
    	for( CargoObject co : cc.getData() )
    		assertFalse( co.checkLanguage( "invalid" ) );
    }
    
    @Test
    public void testValidMimetype()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.validMimetype( cmt.getMimeType() ) );
    }
}
