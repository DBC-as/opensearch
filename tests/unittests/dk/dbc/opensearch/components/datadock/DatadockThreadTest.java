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


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.pluginframework.PluginTask;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.IJob;
import dk.dbc.opensearch.common.xml.XMLUtils;
import dk.dbc.opensearch.components.harvest.ESHarvest;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;
import dk.dbc.opensearch.components.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.HarvesterUnknownIdentifierException;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.plugins.DocbookAnnotate;
import dk.dbc.opensearch.plugins.OwnerRelation;
import dk.dbc.opensearch.plugins.Store;
import dk.dbc.opensearch.plugins.XMLDCHarvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import mockit.Mockit;
import mockit.NonStrictExpectations;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
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
    DatadockJob mockDatadockJob;
    @Mocked Map<String, List<PluginTask>> mockFlowmap;
    @Mocked Processqueue mockProcessqueue;
    @Mocked ESHarvest mockHarvester;
    @Mocked IIdentifier mockIdentifier;
    @Mocked IObjectRepository mockObjectRepository;
    @Mocked Map<String, String> mockArgsMap;
    PluginResolver mockPluginResolver;
    static final String refdata = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"710100\" format=\"katalog\" lang=\"dk\"/></referencedata>";
    static Document referenceData;
    static boolean isset;
    PluginTask pluginTask1;
    PluginTask pluginTask2;
    PluginTask pluginTask3;
    static List<PluginTask> pluginTaskList = new ArrayList<PluginTask>();
    
    @BeforeClass
    public static void classSetup() throws Exception
    {
        /**
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel( Level.OFF );
        */
        referenceData = XMLUtils.documentFromString( refdata );
        mockCC = new CargoContainer();
        mockCC.setIdentifier( new PID( "710100:1" ) );
        mockCC.add( DataStreamType.OriginalData, "katalog", "710100", "da", "text/xml", "<orig><child></orig>".getBytes() );
        //    mockCC.setIndexingAlias( "dockbook", DataStreamType.OriginalData );
 }

    private class MockIdentifier implements IIdentifier{}
    private class UnknownIdentifier implements IIdentifier{}

    @MockClass( realClass = Processqueue.class )
    public static class MockProcessqueue
    {
        @Mock public void push( String id ){}
    }

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
        public List<IJob> getJobs( int maxAmount ) throws HarvesterIOException, HarvesterInvalidStatusChangeException{throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusFailure( IIdentifier jobId, String failureDiagnostic ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{ throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusSuccess( IIdentifier jobId, String PID ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
        public void setStatusRetry( IIdentifier jobId ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{throw new UnsupportedOperationException( "Mock method" );}
    }

    @MockClass( realClass = XMLDCHarvester.class )
    public static class MockDBHarvest
    {
        @Mock
        public PluginType getPluginType()
        {
            return PluginType.HARVEST;
        }

        @Mock
        public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap )
        {
            return mockCC;
        }
    }

    @MockClass( realClass = Store.class )
    public static class MockStore
    {
        @Mock
        public PluginType getPluginType()
        {
            return PluginType.STORE;
        }

        @Mock
        public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap )
        {
            return mockCC;
        }
    } 
    
    @MockClass( realClass = OwnerRelation.class )
    public static class MockRelation
    {
        @Mock
        public PluginType getPluginType()
        {
            return PluginType.RELATION;
        }

        @Mock
        public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap )
        {
            return mockCC;
        }
    }    
        
    @MockClass( realClass = DocbookAnnotate.class )
    public static class NullReturningPlugin
    {
        @Mock
        public PluginType getPluginType()
        {
            return PluginType.ANNOTATE;
        }

        @Mock
        public CargoContainer runPlugin( CargoContainer cargo, Map<String, String> argsMap )
        {
            return null;
        }
    }


    /**
     *
     */
    @Before public void SetUp() throws Exception
    {
        mockPluginResolver = new PluginResolver( mockObjectRepository );
        Mockit.setUpMocks( MockProcessqueue.class );
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
        DatadockJob job = new DatadockJob( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, mockHarvester, mockPluginResolver, mockFlowmap );
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
        
        pluginTask1 = new PluginTask( "dk.dbc.opensearch.plugins.XMLDCHarvester", mockArgsMap ); 
	pluginTask2 = new PluginTask( "dk.dbc.opensearch.plugins.OwnerRelation", mockArgsMap );
	pluginTask3 = new PluginTask( "dk.dbc.opensearch.plugins.Store", mockArgsMap );
	pluginTaskList.add( pluginTask1 );
	pluginTaskList.add( pluginTask2 );
	pluginTaskList.add( pluginTask3 );

        DatadockJob job = new DatadockJob( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, mockHarvester, mockPluginResolver, mockFlowmap );
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

        DatadockJob job = new DatadockJob( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, mockHarvester, mockPluginResolver, mockFlowmap );
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
        
        pluginTask1 = new PluginTask( "dk.dbc.opensearch.plugins.DocbookAnnotate", mockArgsMap ); 
      
        pluginTaskList.add( pluginTask1 );

        DatadockJob job = new DatadockJob( mockIdentifier, referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, mockHarvester, mockPluginResolver, mockFlowmap );
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

        DatadockJob job = new DatadockJob( new UnknownIdentifier(), referenceData );
        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, new ExceptionHarvester(), mockPluginResolver, mockFlowmap );
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
        testArrayList.add( "dk.dbc.opensearch.plugins.OwnerRelation" );
        testArrayList.add( "dk.dbc.opensearch.plugins.Store" );

        DatadockJob job = new DatadockJob( new MockIdentifier(), referenceData );

        mockHarvester.setStatusSuccess( job.getIdentifier(), "" );

        ddThread = new DatadockThread( job.getIdentifier(), mockProcessqueue, mockHarvester, mockPluginResolver, mockFlowmap );
        ddThread.call();
    }
}