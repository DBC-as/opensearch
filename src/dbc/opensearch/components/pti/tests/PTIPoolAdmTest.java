package dbc.opensearch.components.pti.tests;
/** \brief UnitTest for PTIPoolAdm class */

import dbc.opensearch.components.pti.PTIPoolAdm;
import dbc.opensearch.components.pti.PTIPool;;
import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.Estimate;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import org.apache.log4j.Logger;

import org.compass.core.Compass;
import org.compass.core.CompassSession;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import com.mallardsoft.tuple.Pair;
import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;

import java.util.Vector;
import java.util.Iterator;

import dbc.opensearch.tools.PrivateAccessor;

import java.lang.reflect.InvocationTargetException;
import java.util.NoSuchElementException;

public class PTIPoolAdmTest {

    PTIPoolAdm ptiPoolAdm;
    PTIPool mockPTIPool;
    Processqueue mockProcessqueue;
    Estimate mockEstimate;
    Compass mockCompass;
    FutureTask mockFuture;
    Pair mockPair;

    @Before public void setUp(){
        
        mockPTIPool = createMock( PTIPool.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockEstimate = createMock( Estimate.class );
        mockCompass = createMock( Compass.class );
        mockPair = createMock( Pair.class );
        mockFuture = createMock( FutureTask.class );

        try{
            ptiPoolAdm = new PTIPoolAdm( mockPTIPool, mockProcessqueue, mockEstimate, mockCompass );
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }
    }
    @After public void tearDown(){

        reset( mockProcessqueue );
        reset( mockEstimate );
        reset( mockCompass );
        reset( mockFuture );
        reset( mockPair );
    }

    /**
     * Tests the general functionality of the PITPoolAdm when 
     * there is nothing on the processqueue
     */
    @Test public void emptyProcessqueueTest()throws Exception{
      
        /**1 setup: most done in setUp()
         */
        /**2 expectations
         */
        expect( mockProcessqueue.deActivate() ).andReturn( 2 );
        // call to startThreads
        expect( mockProcessqueue.pop() ).andThrow( new NoSuchElementException( "Exception!!!" ) );
        

        replay( mockProcessqueue );
        /**3 execution
         */
        try{
        ptiPoolAdm.mainLoop();
        }catch( Exception e ) {
            Assert.fail( "unexpected exception" );
                }

        /**4 verify
         */        
        verify( mockProcessqueue );
       
    }
    @Test public void checkThreadsNonEmptyActiveThreadsTest()throws Exception{
    }


    @Test public void createAndJoinThreadTest(){}    
}