/** \brief UnitTest for ThrownInfo */
package dk.dbc.opensearch.common.types.tests;

import dk.dbc.opensearch.common.types.ThrownInfo;

import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;



/**
 * tests the constructor, and two getter functions
 */
public class ThrownInfoTest {

    /**
     *
     */
    String testString = "test string";
    IOException ioe = new IOException( testString );
    ThrownInfo ti = new ThrownInfo( ioe, testString );

    /**
     * 
     */
    @Test public void testGetThrowable() 
    {
        assertTrue( ti.getThrowable().getClass() == IOException.class );    
    }
    @Test public void testGetInfo() 
    {
        assertEquals( ti.getInfo(), testString );
    }
}