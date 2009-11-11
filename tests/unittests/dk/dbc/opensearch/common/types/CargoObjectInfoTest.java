package dk.dbc.opensearch.common.types;

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
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import org.junit.*;

import org.apache.log4j.Logger;


/**
 * The getTimestamp method is not tested
 */

public class CargoObjectInfoTest
{

    Logger log = Logger.getLogger("CargoObjectInfoTest");

    CargoMimeType cmt;
    CargoObjectInfo coi;
    CargoContainer cc;
    
    String test_submitter = "DBC";
    String test_format = "test_format";
    String test_lang = "DA";
    String teststring;
    byte[] data;
    long id;

    @Before 
    public void SetUp() throws UnsupportedEncodingException, IOException
    {
        cmt =  CargoMimeType.TEXT_XML;
        cc = new CargoContainer();
        
        teststring = "æøå";
        data = teststring.getBytes( "UTF-8" );
    	DataStreamType dataStreamName = DataStreamType.OriginalData;
        id = cc.add( dataStreamName, test_format, test_submitter, test_lang, cmt.getMimeType(), IndexingAlias.Article, data );    	
    }
    
    
    @Test
    public void testGetDataStreamType()
    {
    	for ( CargoObject co : cc.getCargoObjects() )
    	{
    		assertEquals( DataStreamType.OriginalData, co.getDataStreamType() );
    	}
    }
    
    @Test
    public void testIndexingAliasGetFromId() throws IOException
    {
        CargoContainer cc = new CargoContainer();
        IndexingAlias ia = IndexingAlias.getIndexingAlias( "article" );
        /* \todo: re bug #8719 uncomment when finished refatoring*/
        long id = cc.add( DataStreamType.OriginalData, test_format, test_submitter, test_lang, cmt.getMimeType(), ia, data );

        log.debug( String.format( "cc.getIndexingAlias( id )==%s",cc.getIndexingAlias( DataStreamType.OriginalData ) ) );
        log.debug( String.format( "ia==%s", ia ) );

        assertTrue( ia == cc.getIndexingAlias( DataStreamType.OriginalData ) );
    }

    @Test
    public void testGetDataStreamTypeFromId()
    {
        
    }
    
    @Test 
    public void testCorrectnessOfgetFormat() 
    {
        for( CargoObject co : cc.getCargoObjects() )
        	assertTrue( test_format.equals( co.getFormat() ) );
    	       
    }
    
    
    @Test 
    public void testCorrectnessOfgetSubmitter() 
    {    	
    	for( CargoObject co : cc.getCargoObjects() )
    		assertTrue( test_submitter.equals( co.getSubmitter() ) );
    }
    
    
    @Test 
    public void testCorrectnessOfgetMimeType() 
    {
        for( CargoObject co : cc.getCargoObjects() )
        	assertTrue( cmt.getMimeType().equals( co.getMimeType() ) );
    	       
    }
    
    
    @Test @Ignore( "We do not have a list of valid submitters" )
    public void testCheckSubmitter()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertTrue( co.validSubmitter( test_submitter ) );
    }
    
    @Test @Ignore( "We do not have a list of valid submitters" )
    public void testCheckSubmitterInvalid()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertFalse( co.validSubmitter( "invalid" ) );
    }
    
    @Test @Ignore( "we do not have a list of valid languages" )
    public void testCheckLanguageValid()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertTrue( co.checkLanguage( test_lang ) );
    }
     
    @Test @Ignore( "we do not have a list of valid languages" )
    public void testCheckLanguageInvalid()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertFalse( co.checkLanguage( "invalid" ) );
    }
    
    @Test
    public void testValidMimetype()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertTrue( co.validMimetype( cmt.getMimeType() ) );
    }

    @Test
    public void testInvalidMimetype()
    {
    	for( CargoObject co : cc.getCargoObjects() )
    		assertFalse( co.validMimetype( "foo/bar" ) );
    }
}
