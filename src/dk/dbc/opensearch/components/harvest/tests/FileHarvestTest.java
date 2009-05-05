/**
 * \file FileHarvestTest.java
 * \brief The FileHarvestTest class
 * \package tests;
 */
package dk.dbc.opensearch.components.harvest.tests;

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


//import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.helpers.XMLFileReader;

import javax.xml.stream.*;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import org.xml.sax.SAXException;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.*;



/** \brief UnitTest for FileHarvest */
public class FileHarvestTest {

    FileHarvest fileHarvest;
    static File harvestdir = new File( "harvesttestdir" );
    static File destDir = new File( "desttestdir" );
   
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

    @MockClass( realClass = FileHandler.class )
    public static class MockFileHandler
    {
        @Mock public static File getFile( String path )
        {
            return mockFile;
        }
    }

    @MockClass( realClass = HarvesterConfig.class )
    public static class MockHC
    {
        @Mock public static String getFolder()
        {
            return harvestdir.getAbsolutePath();
        }
        
        @Mock public static String getDoneFolder()
        {
            return destDir.getAbsolutePath();
        } 
        @Mock public static int getMaxToHarvest()
        {
            return 100;
        }
    }
    
    //has a getMaxToHarvest method that is needed only in 1 test case

    @MockClass( realClass = HarvesterConfig.class )
    public static class MockHC2
    {
        @Mock public static String getFolder()
        {
            return harvestdir.getAbsolutePath();
        }
        
        @Mock public static String getDoneFolder()
        {
            return destDir.getAbsolutePath();
        }
        @Mock public static int getMaxToHarvest()
        {
            return 1;
        }

    }

    XMLOutputFactory factory;
    XMLStreamWriter writer;
    @Before public void SetUp() throws Exception 
    { 

        mockElement = createMock( Element.class );
        
        factory = XMLOutputFactory.newInstance(); 
       
        harvestdir.mkdir();
        harvestdir.deleteOnExit();
        destDir.mkdir();
        destDir.deleteOnExit();

    }

    
    @After public void TearDown() 
    { 
        Mockit.tearDownMocks();

        reset( mockFile );
        reset( mockElement );
        reset( mockNodeList );
        // removing the moved files and created directories
        for( File submitterFile : destDir.listFiles() )
        {
            submitterFile.deleteOnExit();

            for( File formatFile : submitterFile.listFiles() )
            {
                formatFile.deleteOnExit();
                
                for( File file : formatFile.listFiles() )
                {
                    file.deleteOnExit();
                }
            }
        }

    }
    
    
    /**
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws ConfigurationException 
     * 
     */
@Ignore
    @Test(expected = IllegalArgumentException.class) 
        public void testConstructorException() throws ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
        harvestdir = new File( "test" );
            fileHarvest = new FileHarvest();
    }

    /**
     * Test a happy path where the FileHarvest is initialized, started and asked for jobs
     */
    /**
     * \Todo the test is reading the actual datadock_jobs file and thereby dependant 
     * on the filesystem. Fix: mock the XMLFileReader it uses to get the values.
     */

    @Test 
        public void testHappyRunPath() throws IOException, IllegalArgumentException, ParserConfigurationException, SAXException, ConfigurationException, XMLStreamException
    {        
        
        /**
         * setup
         */
        Mockit.setUpMocks( MockHC.class );

        File sub1 = new File( harvestdir, "dbc" );
        sub1.mkdir();
        sub1.deleteOnExit();


        File format1 = new File( sub1, "docbook_faktalink" );
        format1.mkdir();
        format1.deleteOnExit();
        
        fileHarvest = new FileHarvest();
        fileHarvest.start();
        
        File file1 = new File( format1, "file1" );
        file1.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file1 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();

        /**
         * do stuff
         */

        //System.out.println( String.format( "size of file1: %s", file1.length() )  );
        
        Vector<DatadockJob> result1 = fileHarvest.getJobs();
        //System.out.println( String.format( "size of result1: %s ", result1.size() ) );
        assertTrue( result1.size() == 1 );
        
        Vector<DatadockJob> result2 = fileHarvest.getJobs();
        //System.out.println( String.format( "size of result2: %s ", result2.size() ) );
        assertTrue( result2.size() == 0 );        
        
        File file2 = new File( format1, "file2" ); 
        file2.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file2 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        //System.out.println( String.format( "size of file2: %s", file2.length() )  );

        Vector<DatadockJob> result3 = fileHarvest.getJobs();
        assertTrue( result3.size() == 1 ); 
        Vector<DatadockJob> result4 = fileHarvest.getJobs();
        //System.out.println( result4.size() );
        assertTrue( result4.size() == 0 );

        fileHarvest.shutdown();
        // System.out.println( String.format( "size of result2:%s ", result2.size() ) );
       
        //System.out.println( String.format( "length of file: %s", sub1format1File.length() ) );
      //   String[] files = format1.list();
//         for( int i = 0; i < files.length; i++ )
//         {
//             System.out.println( String.format( "File %s : %s," , i, files[i] ) );
//         }
        //System.out.println( String.format( "length of file: %s", sub1format1File.length() ) );
        
        
    }

    /**
     * This test gives the same submitter format pair twice to the initVectors
     * method. Only the first should be put into the submittersFormatsVector.
     * Can only verify the behaviour in the coverage report. The else case of 
     * the test only results in a warning in the log.
     * It also tests the iniVectors methods treatment of non directory files in the 
     * harvest directory, i.e. files that shouldnt be there. This case is ignored so 
     * no way to verify except for the coverage report
     * Tests the case where the file system has a dir under a submitter dir, that is 
     * not present in the in the submittersFormatVector build on basis of the 
     * datadock_jobs file. So the submitter, format pair is not in the vector
     */

    @Test
    public void testIfClausesWithoutElseStatement() throws Exception
    {
        /**
         * setup
         */
        Mockit.setUpMocks( MockHC.class );
        Mockit.setUpMocks( MockXMLFileReader.class );

        //File system setup
        
        File sub1 = new File( harvestdir, "dbc" );
        sub1.mkdir();
        sub1.deleteOnExit();

        File format1 = new File( sub1, "docbook_faktalink" );
        format1.mkdir();
        format1.deleteOnExit();
         
        File format2 = new File( sub1, "not_a_format" );
        format2.mkdir();
        format2.deleteOnExit();
        
        File file1 = new File( format1, "file1" );
        file1.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file1 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        
        File file2 = new File( format1, "file2" ); 
        file2.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file2 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        /**
         * the folowing file is added to the harvestdirectory to test the if 
         * statement that check that files in the harvestdirectory are directories
         * themselves before adding then to the submitters vector
         */

        File notDir = new File( harvestdir, "notDir" );
        notDir.deleteOnExit();
        writer = factory.createXMLStreamWriter( new FileOutputStream( notDir ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        /**
         * Exepctations
         */
        expect( mockNodeList.getLength() ).andReturn( 2 );
        expect( mockNodeList.item ( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "docbook_faktalink" ); //format
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "dbc" ); //submitter
        expect( mockNodeList.item ( 1 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "docbook_faktalink" ); //format
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "dbc" ); //submitter

        /**
         * replay
         */
        replay( mockFile );
        replay( mockElement );
        replay( mockNodeList );

        /**
         * Do stuff
         */
        fileHarvest = new FileHarvest();
        fileHarvest.start();
        fileHarvest.shutdown();
        
        // for( File file : harvestdir.listFiles() )
//         {
//             System.out.println( file );
        // }
        /**
         * Verify
         */

        verify( mockElement );
        verify( mockFile );
        verify( mockNodeList );
    }

    /**
     * Test the case of the getNewJobs method when there are more files in the folder 
     * than the max config value specifies 
     * We verify this by having 3 files that should be harvested, but only get 1 
     * because thats the max to harvest at a time
     */

    @Test
    public void testGetNewJobsMax() throws Exception
    { 
        /**
         * setup
         */
        Mockit.setUpMocks( MockXMLFileReader.class );
        Mockit.setUpMocks( MockHC2.class );


        //File system setup
        
        File sub1 = new File( harvestdir, "dbc" );
        sub1.mkdir();
        sub1.deleteOnExit();

        File format1 = new File( sub1, "docbook_faktalink" );
        format1.mkdir();
        format1.deleteOnExit();
        
        File file1 = new File( format1, "file1" );
        file1.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file1 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        
        File file2 = new File( format1, "file2" ); 
        file2.deleteOnExit();
        
        writer = factory.createXMLStreamWriter( new FileOutputStream( file2 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();

        File file3 = new File( format1, "file3" );
        file3.deleteOnExit();

        writer = factory.createXMLStreamWriter( new FileOutputStream( file3 ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();
        
        /**
         * Exepctations
         */
        expect( mockNodeList.getLength() ).andReturn( 1 );
        expect( mockNodeList.item ( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "docbook_faktalink" ); //format
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "dbc" ); //submitter
        
        /**
         * replay
         */
        replay( mockFile );
        replay( mockElement );
        replay( mockNodeList );

        /**
         * Do stuff
         */
        fileHarvest = new FileHarvest();
        fileHarvest.start();

        Vector<DatadockJob> result1 = fileHarvest.getJobs();
        assertTrue( result1.size() == 1 );
        result1 = fileHarvest.getJobs();
        assertTrue( result1.size() == 1 );
        result1 = fileHarvest.getJobs();
        assertTrue( result1.size() == 1 );
        result1 = fileHarvest.getJobs();
        assertTrue( result1.size() == 0 );

        fileHarvest.shutdown();
      
        /**
         * Verify
         */
        verify( mockElement );
        verify( mockFile );
        verify( mockNodeList );
    }
}
