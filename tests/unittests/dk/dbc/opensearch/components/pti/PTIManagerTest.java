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


package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.common.config.PTIManagerConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
import dk.dbc.opensearch.common.fedora.IFedoraAdministration;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.pti.PTIManager;
import dk.dbc.opensearch.components.pti.PTIPool;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.MalformedURLException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Vector;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.rpc.ServiceException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.Compass;
import org.junit.*;

import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class PTIManagerTest 
{
    PTIManager ptiManager;
    Processqueue mockPQ = createMock( Processqueue.class );
    PTIPool mockPTIPool = createMock( PTIPool.class);
    CompletedTask mockCompletedTask = createMock( CompletedTask.class );
    FedoraAdministration mockFedoraAdministration = createMock( FedoraAdministration.class );

    static FutureTask mockFuture = createMock( FutureTask.class );
    static CompletedTask dummyTask = new CompletedTask( mockFuture, new InputPair< Long, Integer >( 1l, 1 ) );
    static Vector< CompletedTask > checkJobsVector =  new Vector< CompletedTask >();


    @MockClass( realClass = PTIManagerConfig.class )
    public static class MockPTIManagerConfig
    {
        @Mock public static String getQueueResultsetMaxSize()
        {
            return "2";
        }
        @Mock public static String getRejectedSleepTime()
        {
            return "5";
        }
 
    }


    ThreadPoolExecutor mockExecutor = createMock( ThreadPoolExecutor.class );
    Estimate mockEstimate = createMock( Estimate.class );
    Compass mockCompass = createMock( Compass.class );
    InputPair< String, Integer > mockInputPair = createMock( InputPair.class );


    @MockClass( realClass =  PTIPool.class )
    public static class MockPTIPool
    {
        @Mock public void submit( String fedoraHandle, Integer queueID ) 
        {
            if( queueID == 1 )
            {
                throw new RejectedExecutionException( "test" );
            }
        }

        @Mock public Vector< CompletedTask > checkJobs()
        {
            checkJobsVector.add( dummyTask );
            //System.out.println( "size of checkJobs: " + checkJobsVector.size() );
            return checkJobsVector;
        }
    
    }

    @MockClass( realClass = PTIManager.class )
    public static class MockPTIManager
    {
        @Mock public void update()
        {
        }
    }  

    @MockClass( realClass = PTIManager.class )
    public static class MockPTIManager2
    {
        @Mock public void update()
        {
            throw new NullPointerException( "test" );
        }
    }


    /**
     *
     */
    @Before 
    public void SetUp() 
    { 
    }

    /**
     *
     */
    @After 
    public void TearDown() 
    {
        Mockit.tearDownMocks();
        reset( mockPQ );
        reset( mockPTIPool );
        reset( mockCompletedTask );
        reset( mockInputPair );
        reset( mockEstimate );
        reset( mockCompass );
        reset( mockFuture );
        reset( mockExecutor );
        reset( mockFedoraAdministration );
    }

    /**
     * Testing the instantiation of the PTIManager.
     */
    @Test 
    public void testConstructor() throws ClassNotFoundException, SQLException, ConfigurationException 
    {
        /**
         * setup
         */
        Mockit.setUpMocks( MockPTIManagerConfig.class );

        /**
         * expectations
         */
        expect( mockPQ.deActivate() ).andReturn( 0 );

        /**
         * replay
         */
        replay( mockPQ );
        replay( mockPTIPool );

        /**
         * do stuff
         */
        ptiManager = new PTIManager( mockPTIPool, mockPQ );

        /**
         * verify
         */
        verify( mockPQ );
        verify( mockPTIPool );
      
    }

    /**
     * tests the update methods happy path
     */

    @Test public void testUpdateMethodHappyPath() throws ClassNotFoundException, SQLException, ConfigurationException, InterruptedException, ServiceException, MalformedURLException, IOException
    {
        /**
         * setup
         */
        Mockit.setUpMocks( MockPTIManagerConfig.class );
        Vector< InputPair< String, Integer > > newJobs = new Vector< InputPair< String, Integer > >();
        newJobs.add( new InputPair< String, Integer >( "test1", 1 ) );
        newJobs.add( new InputPair< String, Integer >( "test2", 2 ) );
       

        Vector< CompletedTask<InputPair<Long, Integer>> > finishedJobs =  new Vector< CompletedTask<InputPair<Long, Integer>> >();
        finishedJobs.add( mockCompletedTask );

        /**
         * expectations
         */
        //constructor
        expect( mockPQ.deActivate() ).andReturn( 0 );
        //update method
        expect( mockPQ.pop( 2 ) ).andReturn( newJobs );
        //while loop on newJobs
        mockPTIPool.submit( "test1", 1 );
        mockPTIPool.submit( "test2", 2 );

        //out of while loop
        expect( mockPTIPool.checkJobs() ).andReturn( finishedJobs );
        expect( mockCompletedTask.getResult() ).andReturn( new InputPair< Long, Integer >( 1l, 1 ) );
        mockPQ.commit( 1 );
        
        /**
         * replay
         */
        
        replay( mockPQ );
        replay( mockPTIPool);
        replay( mockCompletedTask );
            
        /**
         * do stuff
         */

        ptiManager = new PTIManager( mockPTIPool, mockPQ );
        ptiManager.update();

        /**
         * verify
         */
        verify( mockPTIPool );
        verify( mockPQ );
        verify( mockCompletedTask );
    }

    /**
     * Tests the behaviour of the checkJobs method when the finishedjobs contains a 
     * CompletedTask with a null value 
     */
    @Test
    public void updateGetsNullFromTask() throws ClassNotFoundException, SQLException, ConfigurationException, InterruptedException, ServiceException, MalformedURLException, IOException
    { 
        /**
         * setup
         */
        Mockit.setUpMocks( MockPTIManagerConfig.class );
        Vector< InputPair< String, Integer > > newJobs = new Vector< InputPair< String, Integer > >();
        newJobs.add( new InputPair< String, Integer >( "test1", 1 ) );
        newJobs.add( new InputPair< String, Integer >( "test2", 2 ) );
       

        Vector< CompletedTask<InputPair<Long, Integer>> > finishedJobs =  new Vector< CompletedTask<InputPair<Long, Integer>> >();
        finishedJobs.add( mockCompletedTask );

        /**
         * expectations
         */
        //constructor
        expect( mockPQ.deActivate() ).andReturn( 0 );
        //update method
        expect( mockPQ.pop( 2 ) ).andReturn( newJobs );
        //while loop on newJobs
        mockPTIPool.submit( "test1", 1 );
        mockPTIPool.submit( "test2", 2 );

        //out of while loop
        expect( mockPTIPool.checkJobs() ).andReturn( finishedJobs );
        expect( mockCompletedTask.getResult() ).andReturn( new InputPair< Long, Integer >( null, 1 ) );
        mockPQ.notIndexed( 1 );
        
        /**
         * replay
         */
        
        replay( mockPQ );
        replay( mockPTIPool);
        replay( mockCompletedTask );
            
        /**
         * do stuff
         */

        ptiManager = new PTIManager( mockPTIPool, mockPQ );
        ptiManager.update();

        /**
         * verify
         */
        verify( mockPTIPool );
        verify( mockPQ );
        verify( mockCompletedTask );
    }

    @Test
    public void shutdownTest() throws ClassNotFoundException, SQLException, ConfigurationException, InterruptedException, ServiceException, MalformedURLException, IOException
    {
        /**
         * setup
         */
       Mockit.setUpMocks( MockPTIManagerConfig.class ); 
       Mockit.setUpMocks( MockPTIManager.class );//avoids the update method call

        /**
         * expectations
         */
        //constructor
        expect( mockPQ.deActivate() ).andReturn( 0 );
        //shutdown
        mockPTIPool.shutdown();
       
        /**
         * replay
         */
        
        replay( mockPQ );
        replay( mockPTIPool);
       
        /**
         * do stuff
         */

        ptiManager = new PTIManager( mockPTIPool, mockPQ );
        ptiManager.shutdown();

        /**
         * verify
         */
        verify( mockPTIPool );
        verify( mockPQ );
    }

    @Test
    public void shutdownExceptionTest()throws ClassNotFoundException, SQLException, ConfigurationException, InterruptedException, ServiceException, MalformedURLException, IOException
    {
        //setup 
        Mockit.setUpMocks( MockPTIManagerConfig.class );
        Mockit.setUpMocks( MockPTIManager2.class );//avoids the update method call
        
        //expectations 
        expect( mockPQ.deActivate() ).andReturn( 0 );
        mockPTIPool.shutdown();

        //replay
        replay( mockPTIPool );
        replay( mockPQ );
        //do stuff
        ptiManager = new PTIManager( mockPTIPool, mockPQ );
        ptiManager.shutdown();
        
        //verify
        verify( mockPQ );
        verify( mockPTIPool );
    }
}
