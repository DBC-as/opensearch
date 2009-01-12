package dk.dbc.opensearch.components.pti.tests;
/** \brief UnitTest for PTIPool class */

import dk.dbc.opensearch.components.pti.PTIPool;
import dk.dbc.opensearch.common.fedora.FedoraHandler;

import java.util.concurrent.Executors;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import dk.dbc.opensearch.common.statistics.Estimate;


import org.apache.log4j.Logger;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import java.util.concurrent.ExecutorService;

import java.util.concurrent.FutureTask;

public class PTIPoolTest {
    FedoraHandler mockFedoraHandler; 

    @Before public void Setup(){
        mockFedoraHandler = createMock( FedoraHandler.class );
    }
    @Test public void constructorTest()throws Exception{
        try{
            PTIPool ptiPool = new PTIPool( 10, mockFedoraHandler );
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }
    }

    @Test public void illegalArgumentExceptionTest(){
        try{
            PTIPool ptiPool = new PTIPool( -1, mockFedoraHandler );
            fail("Expected IllegalArgumentException. no exception thrown"); 
        }
        catch(IllegalArgumentException iae){
            // Expected exception
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }        
    }
    
    @Test public void createAndJoinThreadTest(){
        PTIPool ptiPool = null;
        try{
            ptiPool = new PTIPool( 1, mockFedoraHandler );
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }
        
        Compass mockCompass = createMock( Compass.class );
        CompassSession mockCompassSession = createMock( CompassSession.class );
        Estimate mockEstimate = createMock( Estimate.class );
        ExecutorService mockExecutorService = createMock( ExecutorService.class );

        expect( mockCompass.openSession() ).andReturn( mockCompassSession );
        replay( mockCompass );
        mockExecutorService.submit( isA( FutureTask.class ) );
        
         try{
             ptiPool.createAndJoinThread( "test_handle", "test_itemID", mockEstimate, mockCompass);
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }

         verify( mockCompass );
 
    }    
}