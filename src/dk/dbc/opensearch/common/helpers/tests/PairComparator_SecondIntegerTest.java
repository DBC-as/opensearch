/** \brief UnitTest for PairComparator_FirstString */
package dk.dbc.opensearch.common.helpers.tests;

import dk.dbc.opensearch.common.helpers.PairComparator_SecondInteger;
import dk.dbc.opensearch.common.types.Pair;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PairComparator_SecondIntegerTest {

    PairComparator_SecondInteger pcsi;
    Pair<String, Integer> small;
    Pair<String, Integer> large;
    /**
     *
     */
    @Before public void SetUp() 
    {
        pcsi = new PairComparator_SecondInteger();
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
        assertTrue( pcsi != null );
    }

    @Test public void testcompareLargerThan()
    {
        small = new Pair<String, Integer>( "a", 1 );
        large = new Pair<String, Integer>( "a", 25 );
        
        assertTrue( pcsi.compare( large, small ) > 0 );
    }
    
    @Test public void testcompareSmallerThan()
    {
            small = new Pair<String, Integer>( "a", 1 );
            large = new Pair<String, Integer>( "a", 25 );
            
            assertTrue( pcsi.compare( small, large ) < 0 );
    }

    @Test public void testcompareEquals()
    {
            small = new Pair<String, Integer>( "a", 1 );

            assertTrue( pcsi.compare( small, small ) == 0 );
    }
}