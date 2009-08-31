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


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.DataStreamType;

import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.components.datadock.DatadockJobsMap;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.plugins.DocbookAnnotate;
import dk.dbc.opensearch.plugins.Store;
import dk.dbc.opensearch.plugins.OwnerRelation;
import dk.dbc.opensearch.plugins.DocbookHarvester;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.JobStatus;
import dk.dbc.opensearch.components.harvest.UnknownIdentifierException;
import dk.dbc.opensearch.components.harvest.InvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.IIdentifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import static org.junit.Assert.*;
import org.junit.*;
import org.xml.sax.SAXException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

/**
 * methods are being ignored, bug 9385
 */


/**
 * Unittest for the DatadockThread
 */
public class DatadockThreadTest 
{
    DatadockThread ddThread;
    static ArrayList< String > testArrayList = new ArrayList<String>();
    static CargoContainer mockCC = createMock( CargoContainer.class);
    Estimate mockEstimate;
    DatadockJob mockDatadockJob;
    Processqueue mockProcessqueue;
    IHarvest mockHarvester;
    FedoraAdministration mockFedoraAdministration;
    IIdentifier mockIdentifier;
    CargoObject mockCargoObject;
    
    @MockClass( realClass = DatadockJobsMap.class )
    public static class MockDDJobsMap
    {
    	@Mock public static ArrayList< String > getDatadockPluginsList( String submitter, String format )
        {
            //System.out.println( testArrayList.toString() );
            return testArrayList;
        }
    }


    @MockClass( realClass = DatadockJobsMap.class )
    public static class MockDDJobsMapNP
    {
        @Mock public static ArrayList< String > getDatadockPluginsList( String submitter, String format )
        {
            //System.out.println( "returning null" );
            return null;
        }
    }


    @MockClass( realClass = DocbookHarvester.class )
    public static class MockDBHarvest
    {
        PluginType pt = PluginType.HARVEST;

        @Mock( invocations = 3 )
        public PluginType getPluginType()
        {
            return pt;
        }

        @Mock( invocations = 1 )
        public CargoContainer getCargoContainer( DatadockJob job, byte[] referenceData )
        {
            return mockCC;
        }
    }


    @MockClass( realClass = DocbookAnnotate.class )
    public static class MockAnnotate
    {
        PluginType pt = PluginType.ANNOTATE;

        @Mock( invocations = 3 )
        public PluginType getPluginType()
        {
            return pt;
        }

        @Mock( invocations = 1 )
        public CargoContainer getCargoContainer( CargoContainer cargo )
        {
            return mockCC;
        }
    } 
    
    @MockClass( realClass = Store.class )
    public static class MockStore
    {
        PluginType pt = PluginType.STORE;

        @Mock( invocations = 3 )
        public PluginType getPluginType()
        {
            return pt;
        }

        @Mock( invocations = 1 )
        public CargoContainer storeCargoContainer( CargoContainer cargo )
        {
            return mockCC;
        }
    } 
    
    @MockClass( realClass = OwnerRelation.class )
    public static class MockRelation
    {
        PluginType pt = PluginType.RELATION;

        @Mock( invocations = 3 )
        public PluginType getPluginType()
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
        public PluginType getPluginType()
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
    @Before public void SetUp() 
    {
        mockCargoObject = createMock( CargoObject.class );
        mockHarvester = createMock( IHarvest.class );
        mockDatadockJob = createMock( DatadockJob.class );
        mockEstimate = createMock( Estimate.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockFedoraAdministration = createMock( FedoraAdministration.class );
        mockIdentifier = createMock( IIdentifier.class );
    }


    /**
     *
     */
    @After public void TearDown() 
    {
        Mockit.tearDownMocks();
        reset( mockDatadockJob );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockFedoraAdministration );
        reset( mockCC );
        testArrayList.clear();
        reset( mockHarvester );
        reset( mockIdentifier );
        reset( mockCargoObject );
    }


    /**
     * Happy path for the constructor
     */

    @Test public void testConstructor() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
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
        replay( mockFedoraAdministration );

        //do stuff

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );

        //verify
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
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
      
        //expectations
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        //replay
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraAdministration );

        //do stuff

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );

        //verify
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
    }


    /**
     * Testing happy path of the call method going through the
     * options in the switch
     */

    @Test
    public void testCall() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException, UnknownIdentifierException, InvalidStatusChangeException
    {
        //System.out.println( "3");
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockDBHarvest.class );
        Mockit.setUpMocks( MockAnnotate.class );
        Mockit.setUpMocks( MockRelation.class );
        Mockit.setUpMocks( MockStore.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" ); 
        testArrayList.add( "dk.dbc.opensearch.plugins.OwnerRelation" );
        testArrayList.add( "dk.dbc.opensearch.plugins.Store" );
        String dataString = "testData";
        byte[] data = dataString.getBytes();
        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );
        //call
        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        expect( mockHarvester.getData ( mockIdentifier ) ).andReturn( data );

        expect( mockCC.getCargoObjectCount() ).andReturn( 1 );
     
        expect( mockCC.getCargoObject( DataStreamType.OriginalData ) ).andReturn( mockCargoObject );
        expect( mockCargoObject.getContentLength() ).andReturn( 5 );

        expect( mockCC.getDCIdentifier() ).andReturn( "DCIdentifier" );

        expect( mockEstimate.getEstimate( null, 5 ) ).andReturn( 1F );
        expect( mockCC.getDCIdentifier() ).andReturn( "DCIdentifier" );
        mockProcessqueue.push( isA( String.class ) );
        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockHarvester.setStatus( mockIdentifier , JobStatus.SUCCESS );
     

        /**
         *replay
         */
        replay( mockHarvester );
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraAdministration );
        replay( mockCC );
        replay( mockCargoObject );
        replay( mockIdentifier );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );
        Float result = ddThread.call();

        assertTrue( result == 1f );
        /**
         *verify
         */
        verify( mockHarvester );
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
        verify( mockCC );
        verify( mockCargoObject );
        verify( mockIdentifier );
    } 
  

    @Test( expected = IllegalStateException.class )
    public void testCallIllegalState() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException, UnknownIdentifierException, InvalidStatusChangeException
    {
        //System.out.println( "4");
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockDBHarvest.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" );
        String dataString = "testData";
        byte[] data = dataString.getBytes();

        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );
        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        expect( mockHarvester.getData ( mockIdentifier ) ).andReturn( data );
        //call
        expect( mockCC.getCargoObjectCount() ).andReturn( 0 );


        /**
         *replay
         */
        replay( mockHarvester );
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraAdministration );
        replay( mockCC );
        replay( mockIdentifier );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );
        Float result = ddThread.call();

        /**
         *verify
         */
        verify( mockHarvester );
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
        verify( mockCC );
        verify( mockIdentifier );
    }   


    @Test( expected = NullPointerException.class )
    public void testCallNullCargoContainer() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException, UnknownIdentifierException, InvalidStatusChangeException
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
        replay( mockFedoraAdministration );
        replay( mockCC );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );
        Float result = ddThread.call();

        /**
         *verify
         */
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
        verify( mockCC );
    }

    @Test( expected = UnknownIdentifierException.class )
    public void testUnknownIdentifier() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException, InvalidStatusChangeException, UnknownIdentifierException
    {
        //System.out.println( "unknownidentifiertest" );
        //setup
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockDBHarvest.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" ); 
        

        //expectations  
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );

        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        expect( mockHarvester.getData ( mockIdentifier ) ).andThrow( new UnknownIdentifierException( "testexception" ) );

        //replay
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraAdministration );
        replay( mockCC );
        replay( mockHarvester );

        //do stuff
        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );
        Float result = ddThread.call();

        //verify
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
        verify( mockHarvester );
        verify( mockCC );
    }

    /**
     * tests the situation where the datadockthread is trying to set the status 
     * of the job in the harvester to something invalid. A InvalidStatusChangeException
     * is thrown by the harvester and a log.error is created and 0F is returned.
     */

    @Test
    public void testInvalidStatusChangeException() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, PluginException, InstantiationException, MarshalException, IllegalAccessException, ValidationException, ParseException, XPathExpressionException, SQLException, TransformerException, InvalidStatusChangeException, UnknownIdentifierException
    {
        /**
         *setup
         */
        Mockit.setUpMocks( MockDDJobsMap.class );
        Mockit.setUpMocks( MockDBHarvest.class );
        Mockit.setUpMocks( MockAnnotate.class );
        Mockit.setUpMocks( MockRelation.class );
        Mockit.setUpMocks( MockStore.class );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.DocbookAnnotate" ); 
        testArrayList.add( "dk.dbc.opensearch.plugins.OwnerRelation" );
        testArrayList.add( "dk.dbc.opensearch.plugins.Store" );
        String dataString = "testData";
        byte[] data = dataString.getBytes();
        Float estimateval = 12F;
        /**
         *expectations
         */
        //constructor
        expect( mockDatadockJob.getSubmitter() ).andReturn( "testSubmitter" );
        expect( mockDatadockJob.getFormat() ).andReturn( "testFormat" );
        //call
        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        expect( mockHarvester.getData ( mockIdentifier ) ).andReturn( data );

        expect( mockCC.getCargoObjectCount() ).andReturn( 1 );
     
        expect( mockCC.getCargoObject( DataStreamType.OriginalData ) ).andReturn( mockCargoObject );
        expect( mockCargoObject.getContentLength() ).andReturn( 5 );

        expect( mockCC.getDCIdentifier() ).andReturn( "DCIdentifier" );

        expect( mockEstimate.getEstimate( null, 5 ) ).andReturn( estimateval );
        expect( mockCC.getDCIdentifier() ).andReturn( "DCIdentifier" );
        mockProcessqueue.push( isA( String.class ) );
        expect( mockDatadockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockHarvester.setStatus( mockIdentifier , JobStatus.SUCCESS );
        expectLastCall().andThrow( new InvalidStatusChangeException ( "test invalidstatuschangeexception" ) );

        /**
         *replay
         */
        replay( mockHarvester );
        replay( mockDatadockJob );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockFedoraAdministration );
        replay( mockCC );
        replay( mockCargoObject );
        replay( mockIdentifier );

        /**
         * do stuff
         */

        ddThread = new DatadockThread( mockDatadockJob, mockEstimate, mockProcessqueue, mockFedoraAdministration, mockHarvester );
        Float result = ddThread.call();

        assertFalse( result == estimateval );
        /**
         *verify
         */
        verify( mockHarvester );
        verify( mockDatadockJob );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockFedoraAdministration );
        verify( mockCC );
        verify( mockCargoObject );
        verify( mockIdentifier );
    }

}