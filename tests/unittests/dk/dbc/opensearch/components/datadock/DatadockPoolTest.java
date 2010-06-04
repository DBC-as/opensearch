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


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.PluginTask;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.IJob;
import dk.dbc.opensearch.common.xml.XMLUtils;
import dk.dbc.opensearch.components.harvest.ESHarvest;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;
import dk.dbc.opensearch.components.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.HarvesterUnknownIdentifierException;
import dk.dbc.opensearch.components.harvest.IHarvest;

import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.FutureTask;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;

import java.util.concurrent.TimeUnit;
import org.junit.*;

import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import org.w3c.dom.Document;


/**
 * This unittest does not test the getTask method, since this methods has 
 * conditionals and are creating new complex objects. The method is mocked 
 * through a mockclass for this test.
 */
public class DatadockPoolTest
{
    /**
     * The (mock)objects we need for the most of the tests
     */

    Processqueue mockProcessqueue;
    CargoContainer mockCargoContainer;
    FutureTask mockFutureTask;
    ThreadPoolExecutor mockThreadPool;
    DatadockJob mockDatadockJob;
    DatadockPool datadockPool;
    DatadockThread datadockThread;
    //FedoraAdministration mockFedoraAdministration;
    //IHarvest mockHarvester;

    @Mocked static FutureTask<Boolean> mockFuture;
    @Mocked ESHarvest mockHarvester;
    //static FutureTask mockFuture = createMock( FutureTask.class );

    
    @MockClass( realClass = DatadockPool.class )
    public static class MockDatadockPool
    {
//        private ThreadPoolExecutor ThreadPoolExecutor;
//        @Mock(invocations = 1)
//        public void $init(ThreadPoolExecutor threadpool, IProcessqueue processqueue, IObjectRepository fedoraObjectRepository, IHarvest harvester, PluginResolver pluginResolver)
//        {
//            threadpool = new ThreadPoolExecutor( 1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>( 1 ) );
//        }
    }

    @MockClass(realClass=DatadockThread.class)
    public static class MockDatadockThread implements Callable<Boolean>
    {
        @Mock
            public void $init( IIdentifier identifier, IProcessqueue processqueue, IHarvest harvester, PluginResolver pluginResolver, Map<String, List<PluginTask>> flowMap )
        { 
        }

        @Override
        public Boolean call() throws Exception
        {
            return new Boolean( true );
        }
    }

    @MockClass(realClass=DatadockThread.class)
    public static class MockNullDatadockThread implements Callable<Boolean>
    {
        @Mock
            public void $init( IIdentifier identifier, IProcessqueue processqueue, IHarvest harvester, PluginResolver pluginResolver, Map<String, List<PluginTask>> flowMap )
        {
        }

        @Override
        public Boolean call() throws Exception
        {
            System.out.println( "returning null" );
            return null;
        }
    }

    @MockClass( realClass=DatadockJob.class )
    public static class MockDatadockJob
    {
        @Mock
        public void $init( IIdentifier id, Document ref ){}
    }

    @MockClass( realClass=ThreadPoolExecutor.class)
    public static class MockThreadPool
    {
    }

    // @MockClass( realClass = ESHarvest.class )
    // public static class MockHarvester
    // {

    //     @Mock
    //     public byte[] getData( IIdentifier id ) throws HarvesterUnknownIdentifierException
    //     {
    //         return "".getBytes();
    //     }

    //     @Mock
    //     public void setStatusSuccess( IIdentifier jobId, String PID ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{
    //     }
        
    //     @Mock
    //     public void setStatusFailure( IIdentifier jobId, String PID ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException{
    //     }
    // }

    private class MockIdentifier implements IIdentifier{}

    @Before 
    public void setUp()
    {
        setUpMocks( MockDatadockThread.class );
        setUpMocks( MockThreadPool.class );
        setUpMocks( MockDatadockPool.class );
        setUpMocks( MockDatadockJob.class );
        //setUpMocks( MockHarvester.class );
    }


    @After 
    public void tearDown()
    {
        tearDownMocks();
    }

  
     @Test
     public void submitTest() throws Exception
     {
         ThreadPoolExecutor tpe = new ThreadPoolExecutor( 1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1) );
         DatadockPool pool = new DatadockPool( tpe, null, null, null, null );
	 pool.submit( new MockIdentifier() );
     }

     @Test( expected=IllegalArgumentException.class)
     public void submitWithNullJobFails() throws Exception
     {
         ThreadPoolExecutor tpe = new ThreadPoolExecutor( 1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1) );
         DatadockPool pool = new DatadockPool( tpe, null, null, null, null );
         pool.submit( null );
     }

     @Test @Ignore( "successfull test up until DatadockPool.checkJobs() calls the thread FutureTask.get() and recieves a NullPointerException" )
     public void checkJobsWithOneJob() throws Exception
     {
         final String refdata = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"710100\" format=\"katalog\" lang=\"dk\"/></referencedata>";
         Document referenceData = XMLUtils.documentFromString( refdata );

	 //         IJob job = new DatadockJob( new MockIdentifier(), referenceData );
         ThreadPoolExecutor tpe = new ThreadPoolExecutor( 1, 1, 1, TimeUnit.SECONDS, new LinkedBlockingQueue<Runnable>(1) );
         DatadockPool pool = new DatadockPool( tpe, null, mockHarvester, null, null );
	 //         pool.submit( job.getIdentifier() );
         pool.submit( new MockIdentifier() );
         pool.checkJobs();
     }

}
