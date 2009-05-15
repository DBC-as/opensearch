/**
 * \file FileHarvestTest.java
 * \brief The FileHarvestTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.pti.tests;

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

import dk.dbc.opensearch.components.pti.PTIJobsMap;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.helpers.XMLFileReader;

import java.io.File;
import java.io.IOException;
import org.w3c.dom.NodeList;
import org.w3c.dom.Element;

import org.apache.commons.configuration.ConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;

import java.util.ArrayList;
import java.util.HashMap;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.*;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

/**
 * \brief UnitTest for PTIJobsMap 
 */

public class PTIJobsMapTest {


    PTIJobsMap ptiJobsMap;
    static File mockFile = createMock( File.class );
    static NodeList mockNodeList = createMock( NodeList.class );
    Element mockElement = createMock( Element.class );


    @MockClass( realClass = PtiConfig.class )
    public static class MockPtiConfig
    {
        @Mock public String getPath()
        {
            return "testString";
        }
    }

    @MockClass( realClass = FileHandler.class )
    public static class MockFH
    {
        public File getFile( String path )
        {
            return mockFile;
        }

    }

    @MockClass( realClass = XMLFileReader.class )
    public static class MockXMLFileReader
    {
        @Mock public NodeList getNodeList( File xmlFile, String tagName )
        {
            return mockNodeList;
        }

    }

    @MockClass( realClass = JobMapCreator.class )
    public static class MockJobMapCreator
    {
        @Mock public void validateJobXmlFile( String path )
       {
        
        }
    }

    /**
     *
     */
    @Before public void SetUp() 
    {

    }

    /**
     *
     */
    @After public void TearDown() 
    {
        Mockit.tearDownMocks();
        reset( mockElement );
        reset( mockFile );
        reset( mockNodeList );

    }

    /**
     *  
     */    
    @Test( expected = IllegalStateException.class ) 
    public void testGetPtiPluginsListIllegalStateException() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    { 
        //  System.out.println( "1" );
        /**
         * setup
         */
        Mockit.setUpMocks( MockPtiConfig.class );
        Mockit.setUpMocks( MockJobMapCreator.class );
        Mockit.setUpMocks( MockXMLFileReader.class );
        Mockit.setUpMocks( MockFH.class );

        String sub1 = "dbc";
        String form1 = "test";
        
        /**
         * Expectations
         */
        expect( mockNodeList.getLength() ).andReturn( 0 );

        /**
         * replay
         */
        replay( mockNodeList );

        /**
         * do stuff
         */
        ArrayList< String > aList;
        ptiJobsMap = new PTIJobsMap();
        aList = ptiJobsMap.getPtiPluginsList( sub1, form1 );
        
        /**
         * verify
         */

        verify( mockNodeList );

    }
    
    @Test 
    public void testGetPtiPluginsList() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        //System.out.println( "2" );
        /**
         * setup
         */
        Mockit.setUpMocks( MockPtiConfig.class );
        Mockit.setUpMocks( MockJobMapCreator.class );
        Mockit.setUpMocks( MockXMLFileReader.class );
        Mockit.setUpMocks( MockFH.class );

        String sub1 = "dbc";
        String form1 = "test";
        String pluginString0 = "testclass";
        String pluginString1 = "testClass2";
        String positionString0 = "0";
        String positionString1 = "1";
        
        /**
         * Expectations
         */
        expect( mockNodeList.getLength() ).andReturn( 1 );
        //outer loop in JobMapCreator
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "submitter" ) ).andReturn( sub1 );
        expect( mockElement.getAttribute( "format" ) ).andReturn( form1 );
        expect( mockElement.getElementsByTagName( "plugin" ) ).andReturn( mockNodeList );
        expect( mockNodeList.getLength() ).andReturn( 2 );
        //inner loop
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "classname" ) ).andReturn( pluginString0 );
        expect( mockElement.getAttribute( "position" ) ).andReturn( positionString0 );
        expect( mockNodeList.item( 1 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "classname" ) ).andReturn( pluginString1 );
        expect( mockElement.getAttribute( "position" ) ).andReturn( positionString1 );
        /**
         * replay
         */
        replay( mockFile );
        replay( mockNodeList );
        replay( mockElement );


        /**
         * do stuff
         */
        ArrayList< String > aList;
        ptiJobsMap = new PTIJobsMap();
        aList = ptiJobsMap.getPtiPluginsList( sub1, form1 );
        
        assertTrue ( aList.size() == 2 );        
        assertTrue ( aList.get( 0 ).equals( pluginString0 ) );
        assertTrue ( aList.get( 1 ).equals( pluginString1 ) );        

        /**
         * verify
         */
        verify( mockFile );
        verify( mockNodeList );
        verify( mockElement );
    }  
}