/**
 * \file JobMapCreatorTest.java
 * \brief UnitTest for JobMapCreator
 */
package dk.dbc.opensearch.common.pluginframework.tests;


import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.components.datadock.DatadockMain;
import dk.dbc.opensearch.components.pti.PTIMain;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.os.FileHandler;
//import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.PtiConfig;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import java.lang.IllegalStateException;
import java.lang.IllegalArgumentException;

import javax.xml.parsers.ParserConfigurationException;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * Class for testing tha JobMapCreator class
 */
public class JobMapCreatorTest 
{
    JobMapCreator jmc;
    /**
     * \Todo: The mockURL should be created in another way...
     */
    static File mockFile = createMock( File.class );
    static NodeList mockNodeList = createMock( NodeList.class );

    Element mockElement;
    



    @MockClass( realClass = XMLFileReader.class )
    public static class MockXMLFileReader
    {
    	@Mock public static NodeList getNodeList( File xmlFile, String tagName )
    	{
            //	System.out.println( "mockNodeList returned" );
    		return mockNodeList;
    	}
    }
    
    @MockClass( realClass = JobMapCreator.class )
        public static class MockJobMapCreator
        {
            @Mock public static File setJobFile( Class classType )
            {
                //  System.out.println( "mockFile returned" );
                return mockFile;
            }
        }

    @MockClass( realClass = FileHandler.class)
        public static class MockFH
        {
            @Mock public static File getFile( String path )
            {
                return mockFile;
            }
        }
    
    @MockClass( realClass = DatadockConfig.class )
        public static class MockDDConfig1
        {
            @Mock public static String getPath()
            {
                return "not null";
            }
        }
    @MockClass( realClass = PtiConfig.class )
        public static class MockPtiConfig1
        { 
            @Mock public static String getPath()
            {
                return "not null";
            }
        }

    @MockClass( realClass = DatadockConfig.class )
        public static class MockDDConfig2
        {
            @Mock public static String getPath()
            {
                return null;
            } 
        }
    @MockClass( realClass = PtiConfig.class )
        public static class MockPtiConfig2
        {
            @Mock public static String getPath()
            {
                return null;
            }
        }

    /**
     *
     */
    @Before public void SetUp() 
    {
        Mockit.setUpMocks( MockXMLFileReader.class );

        mockElement = createMock( Element.class );
        mockNodeList = createMock( NodeList.class );
    }

    
    /**
     *
     */
    @After public void TearDown() 
    {
        Mockit.tearDownMocks();

        reset( mockFile );
        reset( mockElement );
        reset( mockNodeList );
    }
    

    /**
     * Testing the happy path
     */
    @Test public void testConstructor() throws Exception
    {
        jmc = new JobMapCreator();
    }
    
    
    /**
     * Testing the method that builds the jobmap.
     * We mock the setJobFile method in the JobMapCreator
     * happy path.
     */
    @Test public void testGetMapMethod() throws Exception 
    {
        /**
         * Setup
         */
        Mockit.setUpMocks( MockJobMapCreator.class ); //mocks the getFile method only

        String testString = "test";
        String testString1 = "test1";
        String testString2 = "test2";
        String position1 = "1";
        String position2 = "2";
        HashMap< Pair< String, String >, ArrayList<String> > jobMap;
        /**
         * Exepctations
         */
        expect( mockFile.getPath() ).andReturn( "unittestPath" ); //logging
        expect( mockNodeList.getLength() ).andReturn( 1 ); //outer loop
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 2 );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.getLength() ).andReturn( 2 ); //inner loop
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );        
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString2 );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( position2 );
        expect( mockNodeList.item( 1 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString1 );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( position1 );
        
        /**
         * replay
         */
        replay( mockFile );
        replay( mockElement );
        replay( mockNodeList );

        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jobMap = jmc.getMap( DatadockMain.class );
        //this tests both the sorting and the building
        assertTrue(jobMap.get( new Pair<String, String >( testString, testString ) ).get( 0 ) == testString1 );
        /**
         * Verify
         */
        verify( mockFile );
        verify( mockElement );
        verify( mockNodeList );
    }
    
    /**
     * Provokes the IllegalStateException by returning a NodeList with length 0
     * so that no the jobMap will be empty
     */

    @Test( expected = IllegalStateException.class ) 
        public void testISexceptionFromGetMap() throws ParserConfigurationException, SAXException, IOException
    {
        /**
         * Setup
         */
        Mockit.setUpMocks( MockJobMapCreator.class );
       
        /**
         * Exepctations
         */
        expect( mockFile.getPath() ).andReturn( "unittestPath" ); //logging
        expect( mockNodeList.getLength() ).andReturn( 0 );
        
        /**
         * replay
         */
        replay( mockFile );

        replay( mockNodeList );

        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( DatadockMain.class );
  
        /**
         * Verify
         */
        verify( mockFile );

        verify( mockNodeList );

    } 

    /**
     * Tests the happy paths of the getFile method
     * using both DatadockMain and PTIMain classes as argument. 
     * I terminate computation by provoking an IllegalStateException in 
     * the second call to getMap after setJobFile method has been called
     */

    @Test( expected = IllegalStateException.class ) 
        public void testSetJobFile() throws ParserConfigurationException, IOException, SAXException
    {
         /**
         * Setup
         */
        Mockit.setUpMocks( MockFH.class );
        Mockit.setUpMocks( MockDDConfig1.class );
        Mockit.setUpMocks( MockPtiConfig1.class );

        HashMap< Pair< String, String >, ArrayList<String> > jobMap;

        String test1 = "test1";
        String position = "0";

        /**
         * Exepctations
         */    
        expect( mockFile.getPath() ).andReturn( "getFile method test" ); //logging
        expect( mockNodeList.getLength() ).andReturn( 1 ); //outer loop
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( test1 );        
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( test1 );        
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
         expect( mockNodeList.getLength() ).andReturn( 1 );
         expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
         expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( test1 );
         expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( position );
        
        //calling getMap again

        expect( mockFile.getPath() ).andReturn( "getFile method test" ); //logging
        expect( mockNodeList.getLength() ).andReturn( 0 );

        /**
         * replay
         */ 
        replay( mockFile );
        replay( mockElement );
        replay( mockNodeList );
        
        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( DatadockMain.class);
        jmc.getMap( PTIMain.class );
        /**
         * Verify
         */
        verify( mockFile );
        verify( mockElement );
        verify( mockNodeList );
    }

    /**
     * Setting the datadockJobPath to null through the MockFSC2 class
     */

    @Test( expected= IllegalArgumentException.class )
        public void testSetJobFileException1() throws ParserConfigurationException, SAXException, IOException
    {
 /**
         * Setup
         */
        Mockit.setUpMocks( MockFH.class );
        Mockit.setUpMocks( MockDDConfig2.class );

        /**
         * Exepctations
         */    
      
        /**
         * replay
         */ 
              
        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( DatadockMain.class);
      
        /**
         * Verify
         */
      
    }

    /**
     * Setting the ptiJobPath to null through the MockFSC2 class
     */

  @Test( expected= IllegalArgumentException.class )
        public void testSetJobFileException2() throws ParserConfigurationException, SAXException, IOException
    {
 /**
         * Setup
         */
        Mockit.setUpMocks( MockFH.class );
        Mockit.setUpMocks( MockPtiConfig2.class );

        /**
         * Exepctations
         */    
      
        /**
         * replay
         */ 
              
        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( PTIMain.class);
      
        /**
         * Verify
         */
      
    }

    /**
     * Giving the setJobFile a class other than DatadockMain and PTIMain
     */
 @Test( expected= IllegalArgumentException.class )
        public void testSetJobFileException3() throws ParserConfigurationException, SAXException, IOException
    {
 /**
         * Setup
         */
        
        /**
         * Exepctations
         */    
      
        /**
         * replay
         */ 
              
        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( FileHandler.class);
      
        /**
         * Verify
         */
      
    }

}