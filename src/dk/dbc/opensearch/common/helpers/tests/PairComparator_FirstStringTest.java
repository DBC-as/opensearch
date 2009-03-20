/** \brief UnitTest for PairComparator_FirstString */
package dk.dbc.opensearch.common.helpers.tests;

import dk.dbc.opensearch.common.helpers.PairComparator_FirstString;
import dk.dbc.opensearch.common.types.Pair;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PairComparator_FirstStringTest {

    PairComparator_FirstString pcfs;
    Pair<String, Integer> small;
    Pair<String, Integer> large;
    /**
     *
     */
    @Before public void SetUp() 
    {
        pcfs = new PairComparator_FirstString();
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test public void testConstructor() 
    {
        assertTrue( pcfs != null );
    }

    @Test public void testcompareLargerThan()
    {
        small = new Pair<String, Integer>( "a", 1 );
        large = new Pair<String, Integer>( "b", 1 );
        
        assertTrue( pcfs.compare( large, small ) > 0 );
    }
    
    @Test public void testcompareSmallerThan()
    {
            small = new Pair<String, Integer>( "a", 1 );
            large = new Pair<String, Integer>( "b", 1 );
            
            assertTrue( pcfs.compare( small, large ) < 0 );
    }

    @Test public void testcompareEquals()
    {
            small = new Pair<String, Integer>( "a", 1 );

            assertTrue( pcfs.compare( small, small ) == 0 );
    }
}