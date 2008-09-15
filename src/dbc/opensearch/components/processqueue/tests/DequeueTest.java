package dbc.opensearch.components.processqueue.tests;

import dbc.opensearch.components.processqueue.*;
import static org.junit.Assert.*;
import org.junit.*;
import org.apache.commons.configuration.*;
import org.apache.commons.lang.*;

/**
 * Test fro the Dequeue class.
 */
public class DequeueTest {

    /**
     * Setting up a Dequeue
     */

    Dequeue dequeue;


    @Before public void setUp(){
        try{
            dequeue = new Dequeue();
        }
        catch (Exception e){}


    }
    /**
     * Testing whether Dequeue allows commit being called without pop being called first
     */

    @Test
        public void testNoUpdatePriorToPop() throws Exception {
        try {
            dequeue.commit();
            fail( "Dequeue.commit should have thrown an exception" );
        }
        catch(IllegalCallException expected){}
        catch(Exception expected){
            fail( "Dequeue.commit should have thrown an exception" );
        }
    }
    /**
     * Testing whether Dequeue allows rollback being called without pop being called first
     */

    @Test
        public void testNoRollbackPriorToPop() throws Exception {
        try {
            dequeue.rollback();
            fail( "Dequeue.Rollback should have thrown an exception" );
        }
        catch(IllegalCallException expected){}
        catch(Exception expected){
            fail( "Dequeue.Rollback should have thrown an exception" );
        }
    }
}