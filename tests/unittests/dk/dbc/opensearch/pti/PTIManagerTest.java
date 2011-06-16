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


import dk.dbc.opensearch.config.PTIManagerConfig;
import dk.dbc.opensearch.db.Processqueue;
import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.commons.types.Pair;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;

import org.junit.*;
import org.compass.core.Compass;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;


import java.sql.SQLException;
import org.apache.commons.configuration.ConfigurationException;
import java.net.MalformedURLException;
import java.util.concurrent.RejectedExecutionException;
import java.io.IOException;
import javax.xml.rpc.ServiceException;

import static org.easymock.classextension.EasyMock.*;


/**
 * 
 */
public class PTIManagerTest 
{
    PTIManager ptiManager;
    Processqueue mockPQ = createMock( Processqueue.class );
    PTIPool mockPTIPool = createMock( PTIPool.class);

    IObjectRepository objectRepository = createMock( IObjectRepository.class );

    static FutureTask mockFuture = createMock( FutureTask.class );


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
    Compass mockCompass = createMock( Compass.class );


    @MockClass( realClass =  PTIPool.class )
    public static class MockPTIPool
    {
        @Mock public void $init( ThreadPoolExecutor threadpool, Compass compass, HashMap< Pair < String, String >, ArrayList< String > > jobMap )
        {
        
        }

        @Mock public void submit( String fedoraHandle, Integer queueID ) 
        {
            if( queueID == 1 )
            {
                throw new RejectedExecutionException( "test" );
            }
        }

    }


    /**
     *
     */
    @Before 
    public void SetUp() { }

    /**
     *
     */
    @After public void TearDown() {

        Mockit.tearDownMocks();
        reset( mockPQ );
        reset( mockPTIPool );
        reset( mockCompass );
        reset( mockFuture );
        reset( mockExecutor );
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

}
