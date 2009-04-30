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

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import static org.junit.Assert.*;



/** \brief UnitTest for FileHarvest */
public class FileHarvestTest {

    FileHarvest fileHarvest;
    static File harvestdir = new File( "harvesttestdir" );
    static File destDir = new File( "desttestdir" );

    @MockClass( realClass = HarvesterConfig.class )
    public static class mockHC
    {
        @Mock public static String getFolder()
        {
            return harvestdir.getAbsolutePath();
        }
        
        @Mock public static String getDoneFolder()
        {
            return destDir.getAbsolutePath();
        }
    }

    XMLOutputFactory factory;
    XMLStreamWriter writer;
    @Before public void SetUp() throws Exception 
    { 

        Mockit.setUpMocks( mockHC.class );
        factory = XMLOutputFactory.newInstance(); 
       
        harvestdir.mkdir();
        harvestdir.deleteOnExit();
        destDir.mkdir();
        destDir.deleteOnExit();

    }

    
    @After public void TearDown() { }

    
   @Ignore
    @Test
    public void testCheckFormat() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
    	String pollTestPath = HarvesterConfig.getFolder();
    	File pollTestFile = new File( pollTestPath );
    	FileHarvest fh = new FileHarvest( );
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
    

    @Test 
        public void testConstructor() throws IOException, IllegalArgumentException, ParserConfigurationException, SAXException, ConfigurationException, XMLStreamException
    {        
     

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
}
