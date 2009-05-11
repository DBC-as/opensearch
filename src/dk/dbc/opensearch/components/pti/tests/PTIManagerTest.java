/**
 * file PTIManagerTest.java
 * \brief the PTIManagerTest class
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

import dk.dbc.opensearch.components.pti.PTIManager;
import dk.dbc.opensearch.components.pti.PTIPool;
import dk.dbc.opensearch.common.config.PTIManagerConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;

import java.util.Vector;
import java.util.NoSuchElementException;
import java.util.concurrent.FutureTask;

import static org.junit.Assert.*;
import org.junit.*;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;

import static org.easymock.classextension.EasyMock.*;

import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import org.apache.commons.configuration.ConfigurationException;
import java.net.MalformedURLException;
import java.util.concurrent.RejectedExecutionException;
import java.io.IOException;
import javax.xml.rpc.ServiceException;

/** \brief UnitTest for PTIManager */

/**
 * 
 */
public class PTIManagerTest {

    PTIManager ptiManager;
    Processqueue mockPQ = createMock( Processqueue.class );
    PTIPool mockPTIPool = createMock( PTIPool.class);
    CompletedTask mockCompletedTask = createMock( CompletedTask.class );
    Vector<InputPair<String, Integer>> mockNewJobs;

    static FutureTask mockFuture = createMock( FutureTask.class );
    static CompletedTask hat = new CompletedTask( mockFuture, new InputPair< Long, Integer >( 1l, 1 ) );
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

    @MockClass( realClass =  PTIPool.class )
    public static class MockPTIPool
    {
        @Mock public void submit( String fedoraHandle, Integer queueID ) 
        {
            if( queueID == 1 )
            {
                //System.out.println( "submit hat" );
                throw new RejectedExecutionException( "test" );
            }
        }
        @Mock public Vector< CompletedTask > checkJobs()
        {
            //System.out.println( "hat" );
            checkJobsVector.add( hat );
            return checkJobsVector;
        }
    
    }
    // @MockClass( realClass =  .class )

    /**
     *
     */
    @Before public void SetUp() {
        //mockNewJobs = createMock( Vector<InputPair<String, Integer>>.class );
    }

    /**
     *
     */
    @After public void TearDown() {

        Mockit.tearDownMocks();
        reset( mockPQ );
        reset( mockPTIPool );
        reset( mockCompletedTask );
        // reset();
    }

    /**
     * Testing the instantiation of the PTIManager.
     */
    @Test public void testConstructor() throws ClassNotFoundException, SQLException, ConfigurationException 
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

        Vector< CompletedTask > finishedJobs =  new Vector< CompletedTask >();
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
        //        replay( mockNewJobs )
            
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
     * Tests the handling of the RejectedExecutionException in the update method
     */
@Ignore
    @Test public void testUpdateMethodRejectedExecutionException() throws ClassNotFoundException, SQLException, ConfigurationException, InterruptedException, ServiceException, MalformedURLException, IOException
    {
    /**
         * setup
         */
        Mockit.setUpMocks( MockPTIManagerConfig.class );
        Mockit.setUpMocks( MockPTIPool.class );
        Vector< InputPair< String, Integer > > newJobs = new Vector< InputPair< String, Integer > >();
        newJobs.add( new InputPair< String, Integer >( "test1", 1 ) );
        newJobs.add( new InputPair< String, Integer >( "test2", 2 ) );

        System.out.println(checkJobsVector.size());
        /**
         * expectations
         */
        //constructor
        expect( mockPQ.deActivate() ).andReturn( 0 );
        //update method
        expect( mockPQ.pop( 2 ) ).andReturn( newJobs );
        //while loop on newJobs
        
        //out of while loop
        //expect( mockCompletedTask.getResult() ).andReturn( new InputPair< Long, Integer >( 1l, 1 ) );
        mockPQ.commit( 1 );
        
        /**
         * replay
         */
        
        replay( mockPQ );
        replay( mockCompletedTask );
            
            /**
         * do stuff
         */

        ptiManager = new PTIManager( mockPTIPool, mockPQ );
        ptiManager.update();

        /**
         * verify
         */
        verify( mockPQ );
        verify( mockCompletedTask );

    }
}
