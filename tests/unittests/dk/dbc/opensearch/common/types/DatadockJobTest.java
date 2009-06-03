/**
 * \file DatadockJobTest.java
 * \brief The DatadockJobTest class
 * \package tests;
 */

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

import java.net.URI;
import dk.dbc.opensearch.common.types.DatadockJob;
import java.net.URISyntaxException;

/** \brief UnitTest for DatadockJob **/

import static org.junit.Assert.*;
import org.junit.*;

public class DatadockJobTest {

    /**
     * Testing the getters and setters of DatadockJob
     */
    
    @Test 
    public void testSettersAndGetters() 
    {        
        String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        URI testURI = null;
        
        try
        {
            testURI = new URI( "testURI" );
        }
        catch( URISyntaxException use )
        {
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        DatadockJob datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);
        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getUri().equals( testURI ) );
        
        testSubmitter = "testSubmitter2";
        testFormat = "testFormat2";        
        try{
            testURI = new URI( "testURI2" );
        }catch( URISyntaxException use ){
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        datadockJob.setUri( testURI );
        datadockJob.setSubmitter( testSubmitter );
        datadockJob.setFormat( testFormat );

        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getUri().equals( testURI ) );
    }

    @Test public void testConstructorWithPid()
    {
      String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        URI testURI = null;
        String testPID1 = "dbc:1";
        String testPID2 = "dbc:2";
        
        try
        {
            testURI = new URI( "testURI" );
        }
        catch( URISyntaxException use )
        {
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }   

        DatadockJob ddj = new DatadockJob( testURI, testSubmitter, testFormat, testPID1 );

        assertEquals( ddj.getPID(), testPID1 );
        ddj.setPID( testPID2 );
        assertEquals( ddj.getPID(), testPID2 );
    }
}
