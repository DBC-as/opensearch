package dbc.opensearch.components.processqueue.tests.processqueue_test;
import dbc.opensearch.components.processqueue.*;
import org.apache.commons.configuration.*;
import org.apache.commons.lang.*;
import java.util.NoSuchElementException;

/* todo: write comment*/

public class QueueTest {

    public static void main(String[] args) {
        
        /**
         * TEST 
         */
        
        System.out.println( "\n-- Starting processqueue test\n" );
        
        Enqueue enq;
        
        Dequeue deq1 = null;
        Dequeue deq2 = null;
        try{
        deq1 = new Dequeue();
        deq2 = new Dequeue();
        }
        catch(Exception e){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        
        String handle1 = "testHandle_1";
        String handle2 = "testHandle_2";
        String ret_handle = null;
        boolean err = false;

        // Pushing elements to queue
        System.out.println( "pushing handle ["+handle1+"] to queue" );
        try{
            enq = new Enqueue( handle1 );
        }
        catch (Exception e){
            System.out.println( "test 1 FAILED");
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        System.out.println( "\ntest 1 passed\n");

        System.out.println( "pushing handle ["+handle2+"] to queue" );
        try{
            enq = new Enqueue( handle2 );
        }
        catch (Exception e){
            System.out.println( "test 2 FAILED");
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        System.out.println( "\ntest 2 passed\n");

        //popping from queue
        System.out.println( "Popping element from queue" );
        System.out.println( "Expected value: "+handle1 );
        try{
            ret_handle = deq1.pop();
            
        }
        catch( NoSuchElementException nse ){
            System.out.println( "NoSuchElementException: " + nse.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        System.out.println( "Got: "+ret_handle );
        if ( ret_handle.equals(handle1) ){
            System.out.println( "\ntest 3 passed\n");
        }
        else{            
            System.out.println( "test 3 FAILED|"+ret_handle+"|"+handle1+"|");
        }

        
        
        //popping from queue
        System.out.println( "Popping element from queue" );
        System.out.println( "Expected value: "+handle2 );
        try{
            ret_handle = deq2.pop();
            
        }
        catch( NoSuchElementException nse ){
            System.out.println( "NoSuchElementException: " + nse.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        System.out.println( "Got: "+ret_handle );
        if ( ret_handle.equals(handle2) ){
            System.out.println( "\ntest 4 passed\n");
        }
        else{
            System.out.println( "test 4 FAILED");
        }


        err = false;
        //popping from queue
        System.out.println( "Popping element from queue" );
        System.out.println( "Expected value: NoSuchElementException" );
        try{
            ret_handle = deq1.pop();
            
        }
        catch( NoSuchElementException nse ){
            err = true;
        }
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }


        if ( err ){
            System.out.println( "Got: NoSuchElementException" );
            System.out.println( "\ntest 5 passed\n");
        }
        else{
            System.out.println( "Got no Exception " );
            System.out.println( "test 5 FAILED");
        }

        // commiting to queue
        System.out.println( "Commiting element with handle "+handle1+" to queue" );
        try{
            deq1.commit();
        }
        catch( IllegalCallException ice ){
            System.out.println( "IllegalCallException: " + ice.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}

        // rollback
        System.out.println( "Rolling back element with handle "+handle2+" from  queue" );
        try{
            deq2.rollback();
        }
        catch( IllegalCallException ice ){
            System.out.println( "IllegalCallException: " + ice.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}
        
        //popping from queue
        System.out.println( "Popping element from queue" );
        System.out.println( "Expected value: "+handle2 );
        try{
            ret_handle = deq2.pop();
            
        }
        catch( NoSuchElementException nse ){
            System.out.println( "NoSuchElementException: " + nse.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }
        System.out.println( "Got: "+ret_handle );
        if ( ret_handle.equals(handle2) ){
            System.out.println( "\ntest 7 and 8 passed\n");
        }
        else{
            System.out.println( "test 7 and 8 FAILED");
        }

                // commiting to queue
        System.out.println( "Commiting element with handle "+handle2+" to queue" );
        try{
            deq2.commit();
        }
        catch( IllegalCallException ice ){
            System.out.println( "IllegalCallException: " + ice.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);}

        
        err = false;
        //popping from queue
        System.out.println( "Popping element from queue" );
        System.out.println( "Expected value: NoSuchElementException" );
        try{
            ret_handle = deq2.pop();
            
        }
        catch( NoSuchElementException nse ){
            err = true;
        }
        catch( Exception e ){
            System.out.println( "Exception: " + e.getMessage() );
            System.out.println( "Something weird happened - fix me!" );
            System.exit(1);
        }

        if ( err ){
            System.out.println( "Got: NoSuchElementException" );
            System.out.println( "\ntest 9 passed\n");
        }
        else{
            System.out.println( "test 9 FAILED");
        }

        System.out.println( "-- Ending processqueue test\n" );

    }
}
