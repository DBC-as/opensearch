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


/**
 * \file JobMapCreatorTest.java
 * \brief UnitTest for JobMapCreator
 */

package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.io.InputStream;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;
import mockit.Mocked;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Class for testing the JobMapCreator class
 */
public class JobMapCreatorTest 
{
    JobMapCreator jmc;

    @Mocked static File mockFile;

    private static String xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><job_list xmlns=\"info:opensearch.dbc.dk#\"><job submitter=\"dbc\" format=\"faktalink\"><plugin name=\"docbookmerger\" classname=\"dk.dbc.opensearch.plugins.DocbookMerger\" /><plugin name=\"indexerxsem\" classname=\"dk.dbc.opensearch.plugins.IndexerXSEM\" /> </job></job_list>";
    private static String empty_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><job_list xmlns=\"info:opensearch.dbc.dk#\"></job_list>";


    @MockClass( realClass = XMLUtils.class )
    public static class MockXMLUtils
    {
    	@Mock public static NodeList getNodeList( File xmlFile, String tagName ) throws Exception
    	{
            String jobsxml = null;
            if( xmlFile.getAbsolutePath().equals( "/path/to/file/that/exists" ) )
            {
                jobsxml = xml;
            }
            else if( xmlFile.getAbsolutePath().equals( "/path/to/empty/file") )
            {
                jobsxml = empty_xml;
            }

            DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
            InputStream bais = new ByteArrayInputStream( jobsxml.getBytes() );
            Document jobDocument = docBuilder.parse( bais );

            return jobDocument.getElementsByTagName( tagName );
    	}
    }

//
//    @MockClass( realClass = FileHandler.class)
//    public static class MockFH
//    {
//    	@Mock public static File getFile( String path )
//    	{
//            return new File( path );
//        }
//    }

    
    /**
     *
     */
    @Before 
    public void SetUp() 
    {
        //Mockit.setUpMocks( MockFH.class );
        Mockit.setUpMocks( MockXMLUtils.class );
    }

    
    /**
     *
     */
    @After 
    public void TearDown() 
    {
        Mockit.tearDownMocks();
    }
    

    /**
     * Testing the happy path of init
     */
    @Test 
    public void testInitWithGoodPath() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        String path = "/path/to/file/that/exists";
        JobMapCreator.init( path );
    }


    @Test( expected = IllegalStateException.class )
    public void testEmptyFileThrowsIllegalStateException() throws ConfigurationException, IOException, ParserConfigurationException, SAXException, IllegalStateException
    {
        String path = "/path/to/empty/file";
        JobMapCreator.init( path );
    }

    @Test( expected = NullPointerException.class )
    public void testNonexistantFileThrows() throws ConfigurationException, IOException, ParserConfigurationException, SAXException, IllegalStateException
    {
        String path = "idontexist";
        JobMapCreator.init( path );
    }

}