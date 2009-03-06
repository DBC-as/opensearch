/** \brief UnitTest for ComparablePair **/
package dk.dbc.opensearch.common.types.tests;


import dk.dbc.opensearch.common.types.ComparablePair;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class ComparablePairTest {

    /**
     * 
     */


    @Test public void testSortableOnFirst(){
        ArrayList< ComparablePair > apairlist = 
            new ArrayList< ComparablePair >();
        apairlist.add( new ComparablePair< String, Integer > ( "b", 1 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "c", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 3 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
        assertEquals( apairlist.get( 1 ).getFirst(), "b" );
        assertEquals( apairlist.get( 2 ).getFirst(), "c" );

        assertEquals( apairlist.get( 0 ).getSecond(), 3 );
        assertEquals( apairlist.get( 1 ).getSecond(), 1 );
        assertEquals( apairlist.get( 2 ).getSecond(), 2 );
    }

    @Test public void testSortableOnSecond(){
        ArrayList< ComparablePair > apairlist = 
            new ArrayList< ComparablePair >();
        apairlist.add( new ComparablePair< String, Integer > ( "a", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 1 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
      
        assertEquals( apairlist.get( 0 ).getSecond(), 1 );
        assertEquals( apairlist.get( 1 ).getSecond(), 2 );
    }

    /**
     * Verify that null values are not comparable
     */
    @Test public void testNullDoesNotEqual(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", null );
        ComparablePair<String, String> two = null;//new ComparablePair<String, String>( "a", null );

        assertTrue( ! one.equals( two ) );
    }

    @Test public void testEqualsWork(){
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", "1" );
        ComparablePair<String, String> two = new ComparablePair<String, String>( "a", "1" );

        assertTrue( one.equals( two ) );
    }

}
