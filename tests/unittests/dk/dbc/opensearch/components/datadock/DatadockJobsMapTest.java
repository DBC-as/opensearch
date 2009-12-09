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
 * \file FileHarvestTest.java
 * \brief The FileHarvestTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;

import java.util.List;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import org.w3c.dom.NodeList;
import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;
import static org.junit.Assert.*;
import org.junit.*;


/**
 * 
 */
public class DatadockJobsMapTest 
{
    DatadockJobsMap DDJobsMap;
 
    @MockClass( realClass = DatadockConfig.class )
    public static class MockDDConfig
    {
        @Mock public String getPath()
        {
            return "testString";
        }
    }


    @MockClass( realClass = FileHandler.class)
    public static class MockFH
    {
    	@Mock public static File getFile( String path )
    	{
            return new File( path );
        }
    }


    @MockClass( realClass = XMLUtils.class )
    public static class MockXMLUtils
    {
    	@Mock public static NodeList getNodeList( File xmlFile, String tagName ) throws Exception
    	{
            String jobs_xml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?><job_list xmlns=\"info:opensearch.dbc.dk#\"><job submitter=\"dbc\" format=\"faktalink\"><plugin name=\"docbookmerger\" classname=\"dk.dbc.opensearch.plugins.DocbookMerger\" /><plugin name=\"indexerxsem\" classname=\"dk.dbc.opensearch.plugins.IndexerXSEM\" /> </job></job_list>";
            NodeList nodelist =  DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new ByteArrayInputStream( jobs_xml.getBytes() ) ).getElementsByTagName( "job" );

            return nodelist;
    	}
    }


    @MockClass( realClass = JobMapCreator.class )
    public static class MockJobMapCreator
    {
        @Mock public void validateXsdJobXmlFile( String XMLPath, String XSDPath )
        {
            System.out.println( "Validates just fine, thank you." );
        }
    }


    /**
     *
     */
    @Before 
    public void SetUp() 
    {
        Mockit.setUpMocks( MockFH.class );
        Mockit.setUpMocks( MockXMLUtils.class );
        Mockit.setUpMocks( MockDDConfig.class );
        Mockit.setUpMocks( MockJobMapCreator.class );
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
     *  The order of the tests are important. The map tested on is private and static 
     *  and cannot be touched through this test
     */
    @Test
    public void testGetDatadockPluginsListHappyPath() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    { 
        String submitter = "dbc";
        String format = "faktalink";

        List< String > aList = DatadockJobsMap.getDatadockPluginsList( submitter, format );

        assertEquals( "There should be two plugins in the list", 2, aList.size() );
    }

    @Test( expected = IllegalStateException.class )
    public void testGetDatadockPluginsListUnknownSubmitter() throws ConfigurationException, IOException, SAXException, ParserConfigurationException
    {
        String submitter = "unknown";
        String format = "faktalink";

        DatadockJobsMap.getDatadockPluginsList( submitter, format );

    }
    @Test( expected = IllegalStateException.class )
    public void testGetDatadockPluginsListUnknownFormat() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        String submitter = "dbc";
        String format = "unknown";

        DatadockJobsMap.getDatadockPluginsList( submitter, format );

    }
    
    @Test( expected = IllegalStateException.class )
    public void testGetDatadockPluginsListNullValues() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        DatadockJobsMap.getDatadockPluginsList( null, null );
    }
}