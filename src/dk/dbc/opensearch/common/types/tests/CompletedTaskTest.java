/**
 * \file CompletedTaskTest.java
 * \brief The CompletedTaskTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.types.tests;


/** \brief UnitTest for CompletedTask **/

import static org.junit.Assert.*;
import org.junit.*;

import dk.dbc.opensearch.common.types.CompletedTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;

import static org.easymock.classextension.EasyMock.*;

public class CompletedTaskTest {

    FutureTask mockFutureTask;


    /**
     * Testing the getters and setters of CompletedTask.
     */
    @Test public void testSettersAndGetters(){
        mockFutureTask = createMock( FutureTask.class );

        //FutureTask testFuture = new FutureTask( new FutureTest( 10f ) );
        float testResult = 10f;
        
        CompletedTask completedTask = new CompletedTask( mockFutureTask, testResult );
        
        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( completedTask.getResult(), testResult, 0f );

        testResult = 30f;
        completedTask = new CompletedTask( mockFutureTask, testResult );
        
        completedTask.setFuture( mockFutureTask );
        completedTask.setResult( testResult );

        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( completedTask.getResult(), testResult, 0f );
    }

//     private class FutureTest implements Callable<Float>{
//         float res;
//         FutureTest( float result ){
//             res = result;
//         }

//         public Float call(){
//             return res;
//         }
//     }
    
}
