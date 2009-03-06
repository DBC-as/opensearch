/** \brief UnitTest for Pair */
package dk.dbc.opensearch.common.types.tests;


import java.util.HashMap;
import dk.dbc.opensearch.common.types.Pair;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class PairTest {

    Pair<String, String> p;

    /**
     *
     */
    @Before public void SetUp() {
        p = new Pair<String, String>( "a", "b");
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test 
    public void testTypeConsistency() {
        assertEquals( p.getFirst(), "a" );
        assertEquals( p.getSecond(), "b" );
    }

    @Test 
    public void testHashCode(){
        Pair<String, String> p2 = 
            new Pair<String, String>( "a", "b" );

        assertEquals( p.hashCode(), p2.hashCode() );

    }

    @Test public void testEquals(){
        Pair<String, String> p3 = 
            new Pair<String, String>( "a", "b" );

        assertTrue( p3.equals( p ) );
    }

    @Test public void testPairInHashMaps(){
        HashMap< Pair< String, String >, String > hm =
            new HashMap< Pair< String, String >, String >();

        hm.put( new Pair<String, String>( "a", "b" ), "c" );
        hm.put( new Pair<String, String>( "d", "e" ), "f" );

        assertNotNull( hm.get( new Pair<String, String>( "a", "b" ) ) );
    }
}