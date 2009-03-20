/**
 * \file JobMapCreatorTest.java
 * \brief UnitTest for JobMapCreator
 */
package dk.dbc.opensearch.common.pluginframework.tests;


import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.components.datadock.DatadockMain;
import dk.dbc.opensearch.components.pti.PTIMain;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URL;

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

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;


/**
 * Class for testing tha JobMapCreator class
 */
public class JobMapCreatorTest 
{
    JobMapCreator jmc;
    /**
     * \Todo: The mockURL should be created in another way...
     */
    //URL mockURL = getClass().getResource( "/datadock_jobs.xml" );
    static File mockFile = createMock( File.class );
    static Document mockDocument = createMock( Document.class );
    static DocumentBuilder mockDocumentBuilder = createMock( DocumentBuilder.class );

    FileHandler mockFH;
    Element mockElement;
    NodeList mockNodeList;



    @MockClass( realClass = FileHandler.class )
    public static class MockFileHandler
    {
    	@Mock public static File getFile( String path )
    	{
    		System.out.println( "mockFile returned" );
    		return mockFile;
    	}
    }
    

    @MockClass( realClass = DocumentBuilderFactory.class )
    public static class MockDocumentBuilderFactory
    {
    	@Mock public static DocumentBuilder newDocumentBuilder()
    	{
    		System.out.println( "mockDocumentBuilder returned" );
    		return mockDocumentBuilder;
        }
    }
    

    // @MockClass( realClass = ClassLoader.class )
    //         public class MockClassLoader
    //         {
    //             @Mock public URL getResource()
    //             {
    //                 return mockURL;
    //             }

    //         }

    
    /**
     *
     */
    @Before public void SetUp() 
    {
        Mockit.setUpMocks( MockDocumentBuilderFactory.class, MockFileHandler.class );

        mockDocument = createMock( Document.class );
        mockElement = createMock( Element.class );
        mockNodeList = createMock( NodeList.class );
    }

    
    /**
     *
     */
    @After public void TearDown() 
    {
        Mockit.tearDownMocks();

        //reset( mockURL );
        reset( mockFile );
        reset( mockDocumentBuilder );
        reset( mockDocument );
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
     * Testing the method that builds the jobmap
     * happy path.
     */
    @Ignore
    @Test public void testGetMapMethod() throws Exception 
    {
        /**
         * Setup
         */
        String testString = "test";
        String position = "0";
        /**
         * Exepctations
         */
        //expect( mockURL.getPath() ).andReturn( "testPath" ).times( 2 );
        expect( mockFile.getPath() ).andReturn( "unittestPath" );
        expect( mockDocumentBuilder.parse( isA( File.class ) ) ).andReturn( mockDocument );
        expect( mockDocument.getDocumentElement() ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.getLength() ).andReturn( 1 );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString ).times( 2 );
        expect( mockElement.getElementsByTagName( isA( String.class ) ) ).andReturn( mockNodeList );
        expect( mockNodeList.getLength() ).andReturn( 1 );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( testString );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( position );

        /**
         * replay
         */
        //replay( mockURL );
        replay( mockFile );
        replay( mockDocumentBuilder );
        replay( mockDocument );
        replay( mockElement );
        replay( mockNodeList );

        //replay();
        /**
         * Do stuff
         */
        jmc = new JobMapCreator();
        jmc.getMap( DatadockMain.class );

        /**
         * Verify
         */
        //verify( mockURL );
        verify( mockFile );
        verify( mockDocumentBuilder );
        verify( mockDocument );
        verify( mockElement );
        verify( mockNodeList );
    }
}