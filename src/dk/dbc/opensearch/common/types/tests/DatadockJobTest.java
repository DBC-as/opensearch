/**
 * \file DatadockJobTest.java
 * \brief The DatadockJobTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.types.tests;

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
    
    @Test public void testSettersAndGetters() {
        
        String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        URI testURI = null;
        

        try{
            testURI = new URI( "testURI" );
        }catch( URISyntaxException use ){
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        DatadockJob datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);
        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getPath().equals( testURI ) );
        
        testSubmitter = "testSubmitter2";
        testFormat = "testFormat2";        
        try{
            testURI = new URI( "testURI2" );
        }catch( URISyntaxException use ){
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        datadockJob.setPath( testURI );
        datadockJob.setSubmitter( testSubmitter );
        datadockJob.setFormat( testFormat );

        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getPath().equals( testURI ) );
    }
}
