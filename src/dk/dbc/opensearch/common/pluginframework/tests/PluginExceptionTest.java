/** \brief UnitTest for PluginException */
package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginException;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PluginExceptionTest 
{
    PluginException pe;

    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pe = null;
    }

    /**
     * 
     */
    @Test public void testConstructorNoMsg() 
    {
        pe = new PluginException( new IOException( "test" ) );
        assertTrue( pe.getException().getClass() == IOException.class );
        assertTrue( pe.getException().getMessage() == "test" );
        assertTrue( pe.getMessage() == null ); 
    }
    
    /**
     * 
     */
    @Test public void testConstructorNoExp() 
    {
        pe = new PluginException( "test" );
        assertTrue( pe.getException() == null );
        assertTrue( pe.getMessage() == "test" ); 
    }
    /**
     * 
     */
    @Test public void testConstructor() 
    {
        pe = new PluginException( "test", new IOException( "test" ) );
        assertTrue( pe.getException().getClass() == IOException.class );
        assertTrue( pe.getException().getMessage() == "test" );
        assertTrue( pe.getMessage() == "test" ); 
    }
}