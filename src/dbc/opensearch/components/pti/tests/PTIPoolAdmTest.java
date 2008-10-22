package dbc.opensearch.components.pti.tests;
/** \brief UnitTest for PTIPoolAdm class */

import dbc.opensearch.components.pti.PTIPoolAdm;
import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.Estimate;
import java.lang.reflect.InvocationTargetException;
// import java.util.concurrent.Executors;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import dbc.opensearch.tools.Estimate;


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

public class PTIPoolAdmTest {

    PTIPoolAdm ptiPoolAdm;

    Processqueue mockProcessqueue;
    Estimate mockEstimate;
    FedoraHandler mockFedoraHandler;
    Compass mockCompass;
    Vector mockVector1;
    Vector mockVector2;
    Iterator mockIterator1;
    Iterator mockIterator2;

    FutureTask mockFuture;
    Pair mockPair;

    @Before public void Setup(){
        int numberOfThreads = 10;
        mockProcessqueue = createMock( Processqueue.class );
        mockEstimate = createMock( Estimate.class );
        mockFedoraHandler = createMock( FedoraHandler.class );
        mockCompass = createMock( Compass.class );
        
       //  mockPair = createMock( Pair.class );
        mockVector1 = createMock( Vector.class );
        mockVector2 = createMock( Vector.class );
        //mockIterator1 = createMock( Iterator.class );
        //mockIterator2 = createMock( Iterator.class );

        try{
            ptiPoolAdm = new PTIPoolAdm( numberOfThreads, mockProcessqueue, mockEstimate, mockFedoraHandler, mockCompass );
        }
        catch(Exception e){
            fail( String.format( "Caught Error, Should not have been thrown ", e.getMessage() ) ); 
        }
    }
    
    //    @Test public void 
    @Test public void checkThreadsEmptyActiveThreadsTest()throws Exception{
      
        Vector vector1 = new Vector();
        Vector vector2 = new Vector();
       
        ptiPoolAdm.checkThreads( mockProcessqueue, vector1, vector2 );

       
    }
    @Test public void checkThreadsNonEmptyActiveThreadsTest()throws Exception{

        int test_queueid = 10;
        Pair<FutureTask, Integer> testPair = Tuple.from( mockFuture, test_queueid ); 
     
        Vector vector1 = new Vector();
        Vector vector2 = new Vector();
        vector1.add( testPair );

//         expect( mockFuture.isDone() ).andReturn( true );
//         replay( mockFuture );
        

        ptiPoolAdm.checkThreads( mockProcessqueue, vector1, vector2 );



        //mockVector2.clear()
    
        //expect( mockVector1.iterator() ).andReturn( mockIterator1 );
        //expect( mockIterator1.hasNext() ).andReturn( true );
        //expect( mockIterator1.next() ).andReturn( mockPair );
        

        //replay( mockVector1 );
        //replay( mockIterator1 );


        //        PrivateAccessor.invokePrivateMethod( ptiPoolAdm, "checkThreads", mockProcessqueue, mockVector1, mockVector2 ); 
        //ptiPoolAdm.checkThreads( mockProcessqueue, mockVector1, mockVector2 );
    }


    @Test public void createAndJoinThreadTest(){}    
}