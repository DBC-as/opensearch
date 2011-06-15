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
 * \file DatadockManagerTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */
package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.components.harvest.HarvesterIOException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.apache.log4j.BasicConfigurator;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.OracleDBPooledConnection;
import dk.dbc.opensearch.pluginframework.PluginTask;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.TaskInfo;
import dk.dbc.opensearch.common.types.TaskInfo;
import dk.dbc.commons.xml.XMLUtils;
import dk.dbc.opensearch.components.harvest.ESHarvest;
import dk.dbc.opensearch.components.harvest.IHarvest;

import java.util.ArrayList;
import java.util.Map;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import mockit.NonStrictExpectations;

import org.junit.*;
import org.w3c.dom.Document;

import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;
import static org.junit.Assert.*;


/**
 *
 */
public class DatadockManagerTest
{

    @Mocked IHarvest mockHarvester;
    @Mocked static IIdentifier mockIdentifier;
    @Mocked DatadockJobsMap jobMapHandler;
    @Mocked Map< String, List< PluginTask > > mockFlowMap;

    DatadockPool mockDatadockPool;
    static List<TaskInfo> mockJobs = new ArrayList<TaskInfo>();
    static List<IIdentifier> mockIdentifiers = new ArrayList<IIdentifier>();

    TaskInfo mockTaskInfo;
    TaskInfo mockJob;
    static final String referenceData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"dk\"/></referencedata>";
    private static Document xmldata;

    @BeforeClass
    public static void classSetup() throws Exception
    {
        // BasicConfigurator.configure();
        // LogManager.getRootLogger().setLevel( Level.TRACE );
         xmldata = XMLUtils.documentFromString( referenceData );
    }

    @Before
    public void Setup() throws ConfigurationException, HarvesterIOException
    {
        mockJobs.clear();
        setUpMocks( MockHarvester.class );
        setUpMocks( MockDatadockPool.class );
        mockHarvester = new ESHarvest( null, null, false );
        mockDatadockPool = new DatadockPool( null, null, mockHarvester, null );
    }

    @After
    public void tearDown()
    {
        tearDownMocks();
    }


    @Test
    public void testConstructor() throws Exception
    {
        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester, mockFlowMap );
        datadockManager.shutdown();

    }


    @Ignore
    @Test
    public void testUpdate() throws Exception
    {

        TaskInfo job = new TaskInfo( mockIdentifier, xmldata );

        mockDatadockPool.submit( job.getIdentifier() );

        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester, mockFlowMap );

        new NonStrictExpectations()
        {{
            mockFlowMap.get( anyString );returns( true );
            DatadockJobsMap.hasPluginList( anyString, anyString );returns( true );
        }};

        int update = datadockManager.update();
        datadockManager.shutdown();

        assertEquals( 1, update );
    }


    @Test
    public void testShutdown() throws Exception
    {
        DatadockManager datadockmanager = new DatadockManager( mockDatadockPool, mockHarvester, mockFlowMap );
        datadockmanager.shutdown();
    }

    ///////////////////////////////////////////
    // Mock Class implementations follow below

    @MockClass( realClass = ESHarvest.class )
    public static class MockHarvester
    {
        static List<TaskInfo> list;

        @Mock
        public void $init( OracleDBPooledConnection connectionPool, String databasename, boolean usePriorityFlag )
        {
            list = new ArrayList<TaskInfo>();
        }

        @Mock( invocations = 1 )
        public void start(){}

        @Mock( invocations = 1 )
        public void shutdown(){}

        @Mock
        public List<TaskInfo> getJobs( int number )
        {
            for( TaskInfo job : mockJobs )
            {
                list.add( job );//new Job( mockIdentifier, xmldata ) );
            }
            return list;
        }
    }

    @MockClass( realClass = DatadockPool.class )
    public static class MockDatadockPool
    {
        @Mock
        public void $init( ThreadPoolExecutor threadpool, IProcessqueue processqueue, IHarvest harvester, Map<String, List<PluginTask>> flowMap ){}

        @Mock
        public void submit( IIdentifier identifier )
        {
            mockIdentifiers.add( identifier );
        }

        @Mock
        public void checkJobs(){}

        @Mock( invocations = 1 )
        public void shutdown(){}
    }


}
