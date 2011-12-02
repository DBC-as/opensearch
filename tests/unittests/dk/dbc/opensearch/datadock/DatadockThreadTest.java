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
 * \file
 * \brief
 */


package dk.dbc.opensearch.datadock;


import dk.dbc.opensearch.fedora.PID;
import dk.dbc.commons.xml.XMLUtils;
import dk.dbc.opensearch.harvest.ESHarvest;
import dk.dbc.opensearch.harvest.HarvesterIOException;
import dk.dbc.opensearch.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.harvest.HarvesterUnknownIdentifierException;
import dk.dbc.opensearch.harvest.IHarvest;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginResolver;
import dk.dbc.opensearch.pluginframework.PluginTask;
import dk.dbc.opensearch.plugins.ForceFedoraPid;
import dk.dbc.opensearch.plugins.SimpleGenericRelation;
import dk.dbc.opensearch.plugins.Store;
import dk.dbc.opensearch.plugins.XMLDCHarvester;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.DataStreamType;
import dk.dbc.opensearch.types.IIdentifier;
import dk.dbc.opensearch.types.TaskInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import mockit.Mockit;
import mockit.NonStrictExpectations;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;

/**
 * Unittest for the DatadockThread
 */
public class DatadockThreadTest 
{
    DatadockThread ddThread;
    static ArrayList< String > testArrayList = new ArrayList<String>();
    static CargoContainer mockCC;
    TaskInfo mockTaskInfo;
    @Mocked Map<String, List<PluginTask>> mockFlowmap;
    @Mocked ESHarvest mockHarvester;
    @Mocked IIdentifier mockIdentifier;
    @Mocked Map<String, String> mockArgsMap;
    PluginResolver mockPluginResolver;
    static final String refdata = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"710100\" format=\"katalog\" language=\"da\"/></referencedata>";
    static Document referenceData;
    static boolean isset;
    PluginTask pluginTask1;
    PluginTask pluginTask2;
    PluginTask pluginTask3;
    static List<PluginTask> pluginTaskList = new ArrayList<PluginTask>();
    

    @Before
    public void setup() throws Exception
    {
	// must be initialized before every test since nodes are removed from document
        referenceData = XMLUtils.documentFromString( refdata );
    }

    @BeforeClass
    public static void classSetup() throws Exception
    {
        /**
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel( Level.OFF );
        */
        mockCC = new CargoContainer();
        mockCC.setIdentifier( new PID( "710100:1" ) );
        mockCC.add( DataStreamType.OriginalData, "katalog", "710100", "da", "text/xml", "<orig><child></orig>".getBytes() );
        //    mockCC.setIndexingAlias( "dockbook", DataStreamType.OriginalData );
 }

    private class MockIdentifier implements IIdentifier{}
    private class UnknownIdentifier implements IIdentifier{}

    private class ExceptionHarvester implements IHarvest
    {
        public byte[] getData( IIdentifier id ) throws HarvesterUnknownIdentifierException
        {
            if( id.getClass() == UnknownIdentifier.class )
            {
                throw new HarvesterUnknownIdentifierException( "" );
            }
            return "".getBytes();
        }
        
        public CargoContainer getCargoContainer( IIdentifier id ) throws HarvesterUnknownIdentifierException{
            if( id.getClass() == UnknownIdentifier.class )
            {
                throw new HarvesterUnknownIdentifierException( "" );
            }
            return new CargoContainer();
        }
        public void start() throws HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
        public void shutdown() throws HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
        public List<TaskInfo> getJobs( int maxAmount ) throws HarvesterIOException, HarvesterInvalidStatusChangeException{throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusFailure( IIdentifier jobId, String failureDiagnostic ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{ throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusSuccess( IIdentifier jobId, String PID ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusRetry( IIdentifier jobId ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
        public void releaseJob( IIdentifier jobId ) {throw new UnsupportedOperationException( "Mock method" );}
    }

    @MockClass( realClass = XMLDCHarvester.class )
    public static class MockDBHarvest
    {
        @Mock
	public CargoContainer runPlugin( IPluginEnvironment env, CargoContainer cargo )
        {
            return mockCC;
        }
    }

    @MockClass( realClass = Store.class )
    public static class MockStore
    {
        @Mock
	public CargoContainer runPlugin( IPluginEnvironment env, CargoContainer cargo )
        {
            return mockCC;
        }
    } 

    @MockClass( realClass = Store.class )
    public static class MockStoreIsDeleteRecord
    {
        @Mock
	public CargoContainer runPlugin( IPluginEnvironment env, CargoContainer cargo )
        {
            mockCC.setNoFurtherProcessing( true );
            return mockCC;
        }
    }
    
    @MockClass( realClass = SimpleGenericRelation.class )
    public static class MockRelation
    {
        @Mock
	public CargoContainer runPlugin( IPluginEnvironment env, CargoContainer cargo )
        {
            return mockCC;
        }
    }    
        
    @MockClass( realClass = ForceFedoraPid.class )
    public static class NullReturningPlugin
    {
        @Mock
	public CargoContainer runPlugin( IPluginEnvironment env, CargoContainer cargo )
        {
            return null;
        }
    }


    /**
     *
     */
    @Before public void SetUp() throws Exception
    {
        mockPluginResolver = new PluginResolver();
        Mockit.setUpMocks( MockDBHarvest.class );
        Mockit.setUpMocks( MockRelation.class );
        Mockit.setUpMocks( MockStore.class );
        Mockit.setUpMocks( NullReturningPlugin.class );
    }


    /**
     *
     */
    @After public void TearDown() 
    {
        Mockit.tearDownMocks();
        pluginTaskList.clear();
    }


    /**
     * Happy path for the constructor
     */

    @Test public void testConstructor() throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, ParserConfigurationException, SAXException, ServiceException
    {
        TaskInfo job = new TaskInfo( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
    }


    /**
     * Testing happy path of the call 
     */
    @Test
	public void testCall() throws Exception
    {
        new NonStrictExpectations()
        {{
            mockHarvester.getCargoContainer( mockIdentifier );returns( mockCC );
            mockFlowmap.get( anyString );returns( pluginTaskList );
        }};
        
        pluginTask1 = new PluginTask( mockPluginResolver.getPlugin("dk.dbc.opensearch.plugins.XMLDCHarvester"), null ); 
	pluginTask2 = new PluginTask( mockPluginResolver.getPlugin("dk.dbc.opensearch.plugins.SimpleGenericRelation"), null );
	pluginTask3 = new PluginTask( mockPluginResolver.getPlugin("dk.dbc.opensearch.plugins.Store"), null );
	pluginTaskList.add( pluginTask1 );
	pluginTaskList.add( pluginTask2 );
	pluginTaskList.add( pluginTask3 );

        TaskInfo job = new TaskInfo( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
        Boolean result = ddThread.call();

        assertTrue( result.booleanValue() == true );
    } 

    @Test( expected = NullPointerException.class )
    public void testCallNoFlowMap() throws Exception
    {
        new NonStrictExpectations()
        {{
            mockHarvester.getCargoContainer( mockIdentifier );returns( mockCC );
        }};

        TaskInfo job = new TaskInfo( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
        Boolean result = ddThread.call();
    } 

    /**
     * An IllegalStateException is thrown if one of the plugins 
     * returns an empty CargoContainer or a null reference
     */
    @Test( expected = IllegalStateException.class )
	public void testCallIllegalState() throws Exception
    {
        new NonStrictExpectations()
        {{
            mockHarvester.getCargoContainer( mockIdentifier );returns( mockCC );
            mockFlowmap.get( anyString );returns( pluginTaskList );
        }};
        
        pluginTask1 = new PluginTask( mockPluginResolver.getPlugin("dk.dbc.opensearch.plugins.ForceFedoraPid"), null ); 
      
        pluginTaskList.add( pluginTask1 );

        TaskInfo job = new TaskInfo( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
        ddThread.call();

    }   

    /**
     * A HarvesterUnknownIdentifierException will be thrown if the identifier
     * given to the thread is not recognized by the harvester
     */
    @Test( expected = HarvesterUnknownIdentifierException.class )
	public void testUnknownIdentifier() throws Exception
    {
        testArrayList.add( "dk.dbc.opensearch.plugins.XMLDCHarvester" );

        TaskInfo job = new TaskInfo( new UnknownIdentifier(), referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), new ExceptionHarvester(), mockFlowmap );
        ddThread.call();
    }

    /**
     * tests the situation where the datadockthread is trying to set the status 
     * of the job in the harvester to something invalid, ie. a status has
     * already been set on the Harvester for the job and the thread tries to set
     * it a second time. A HarvesterInvalidStatusChangeException
     * is thrown by the harvester
     */
    @Ignore @Test( expected=HarvesterInvalidStatusChangeException.class )
	public void testHarvesterInvalidStatusChangeException() throws Exception
    {

        testArrayList.add( "dk.dbc.opensearch.plugins.XMLDCHarvester" );
        testArrayList.add( "dk.dbc.opensearch.plugins.SimpleGenericRelation" );
        testArrayList.add( "dk.dbc.opensearch.plugins.Store" );

        TaskInfo job = new TaskInfo( new MockIdentifier(), referenceData );

        mockHarvester.setStatusSuccess( job.getIdentifier(), "" );

        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
        ddThread.call();
    }


    /**
     * Tests whether the plugin flow is stopped or not when a CargoContainer has the
     * noFurtherProcessing flag set to true.
     * If not, a IllegalStateException will be thrown when the second plugin is run 
     */
    @Test
    public void testNoFurtherProcessingRecordFromCargoContainer() throws Exception
    {
        Mockit.setUpMocks( MockStoreIsDeleteRecord.class );
        
        new NonStrictExpectations()
        {{
            mockHarvester.getCargoContainer( mockIdentifier );returns( mockCC );
            mockFlowmap.get( anyString );returns( pluginTaskList );
        }};
        
        pluginTask1 = new PluginTask( mockPluginResolver.getPlugin( "dk.dbc.opensearch.plugins.Store" ), null ); 
        pluginTask2 = new PluginTask( mockPluginResolver.getPlugin( "dk.dbc.opensearch.plugins.ForceFedoraPid" ), null ); 
      
        pluginTaskList.add( pluginTask1 );  
        pluginTaskList.add( pluginTask2 );  
        TaskInfo job = new TaskInfo( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockHarvester, mockFlowmap );
        ddThread.call();
        
    }
}