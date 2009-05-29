/**
 * \file DatadockThreadTest.java
 * \brief The DatadockThreadTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock.tests;

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

/** \brief UnitTest for DatadockThread **/

import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.datadock.DatadockJobsMap;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.InputPair;

import dk.dbc.opensearch.common.pluginframework.PluginType;
//import dk.dbc.opensearch.common.pluginframework.IHarvestable;
//import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.plugins.DocbookHarvester;
import dk.dbc.opensearch.plugins.DocbookAnnotate;

import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginException;

import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraCommunication;

import java.util.ArrayList;
import org.apache.commons.configuration.ConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import org.xml.sax.SAXException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

import java.text.ParseException;
import java.sql.SQLException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.*;

/**
 * Unittest for the DatadockThread
 */
public class DatadockThreadTest {

    DatadockThread ddThread;
    static ArrayList< String > testArrayList = new ArrayList<String>();
    Estimate mockEstimate;
    DatadockJob mockDatadockJob;
    Processqueue mockProcessqueue;
    FedoraCommunication mockFedoraCom;
    static CargoContainer mockCC = createMock( CargoContainer.class);

    @MockClass( realClass = DatadockJobsMap.class )
    public static class MockDDJobsMap{
        @Mock public static ArrayList< String > getDatadockPluginsList( String submitter, String format )
        {
            //System.out.println( testArrayList.toString() );
            return testArrayList;
        }
    }

    @MockClass( realClass = DatadockJobsMap.class )
    public static class MockDDJobsMapNP{
        @Mock public static ArrayList< String > getDatadockPluginsList( String submitter, String format )
        {
            //System.out.println( "returning null" );
            return null;
        }
    }

    @MockClass( realClass = DocbookHarvester.class )
    public static class MockHarvest
    {
        PluginType pt = PluginType.HARVEST;

        @Mock( invocations = 3 )
        public PluginType getTaskName()
        {
            return pt;
        }

        @Mock( invocations = 1 )
        public CargoContainer getCargoContainer( DatadockJob job )
        {
            return mockCC;
        }
    }

    @MockClass( realClass = DocbookAnnotate.class )
    public static class MockAnnotate
    {
        PluginType pt = PluginType.ANNOTATE;

        @Mock( invocations = 3 )
        public PluginType getTaskName()
        {
            return pt;
        }

        @Mock( invocations = 1 )
        public CargoContainer getCargoContainer( CargoContainer cargo )
        {
            return mockCC;
        }
    }    
    
    @MockClass( realClass = DocbookAnnotate.class )
    public static class MockAnnotate2
    {
        PluginType pt = PluginType.ANNOTATE;

        @Mock( invocations = 3 )
        public PluginType getTaskName()
        {
            return pt;
        }

        @Mock( invocations = 0 )//safety...
        public CargoContainer getCargoContainer( CargoContainer cargo )
        {
            return mockCC;
        }
    }


    /**
     *
     */
    @Before public void SetUp() {
        mockDatadockJob = createMock( DatadockJob.class );
        mockEstimate = createMock( Estimate.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockFedoraCom = createMock( FedoraCommunication.class );
    }

    /**
     *
     */
    @After public void TearDown() {
        Mockit.tearDownMocks();
        reset( mockDatadockJob );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockFedoraCom );
        reset( mockCC );
        testArrayList.clear();
    }

    /**
     * Happy path for the constructor
     */

    @Test public void testConstructor() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        //System.out.println( "1");
        //Setup
        Mockit.setUpMocks( MockDDJobsMap.class );
        testArrayList.add( "testplugin1" );
        testArrayList.add( "testplugin2" );
        //System.out.println( testArrayList.toString() );
        //expectations
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //replay
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraCom );

        //do stuff

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraCom );

        //verify
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraCom );
    }


    /**
     * Testing the throwing of the NullPointerException in the constructor
     */

    @Test( expected = NullPointerException.class )
    public void testConstructorNPException() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, /*NullPointerException,*/ PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        //System.out.println( "2");
        //Setup
        Mockit.setUpMocks( MockDDJobsMapNP.class );
        //testArrayList.add( "testplugin1" );
        //testArrayList.add( "testplugin2" );
        //System.out.println( testArrayList.toString() );
        //expectations
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //replay
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraCom );

        //do stuff

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraCom );

        //verify
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraCom );
    }

    /**
     * Testing happy path of the call method going through the
     * options in the switch
     */
    @Test
    public void testCall() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException
    {
        //System.out.println( "3");
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockHarvest.class );
        Mockit.setUpMocks( MockAnnotate.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" );

        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //call
        expect( mockCC.getCargoObjectCount() ).andReturn( 1 );
        expect( mockFedoraCom.storeContainer( mockCC, mockDatadockJob, mockProcessqueue, mockEstimate ) ).andReturn( new InputPair< String, Float >( "test", 7f ) );

        /**
         *replay
         */
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraCom );
        replay( mockCC );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraCom );
        Float result = ddThread.call();

        assertTrue( result == 7f );
        /**
         *verify
         */
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraCom );
        verify( mockCC );
    }   

    @Test( expected = IllegalStateException.class )
    public void testCallIllegalState() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException
    {
        //System.out.println( "4");
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockHarvest.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" );

        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //call
        expect( mockCC.getCargoObjectCount() ).andReturn( 0 );


        /**
         *replay
         */
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraCom );
        replay( mockCC );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraCom );
        Float result = ddThread.call();

        /**
         *verify
         */
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraCom );
        verify( mockCC );
    }   

    @Test( expected = NullPointerException.class )
    public void testCallNullCargoContainer() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException
    {
        //System.out.println( "5");
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockAnnotate2.class );

        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" );

        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //call


        /**
         *replay
         */
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraCom );
        replay( mockCC );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraCom );
        Float result = ddThread.call();

        /**
         *verify
         */
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraCom );
        verify( mockCC );
    }
}