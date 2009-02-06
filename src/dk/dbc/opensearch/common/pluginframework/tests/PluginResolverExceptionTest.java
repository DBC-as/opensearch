/** \brief UnitTest for PluginResolverException */
package dk.dbc.opensearch.common.pluginframework.tests;

import static org.junit.Assert.*;
import org.junit.*;
import mockit.Mockit;
import static org.easymock.classextension.EasyMock.*;

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import java.util.Vector;

/**
 * Class to test the PluginResolverException
 */
public class PluginResolverExceptionTest {

    PluginResolverException pre;
    String message = "message";
    Vector<ThrownInfo> exceptionVector = new Vector();

    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     *
     */
    @Test public void pluginResolverExceptionTwoArgsConstructorTest() {
        ThrownInfo testInfo = new ThrownInfo( new NullPointerException( "test" ), message );
        exceptionVector.add( testInfo );
        pre = new PluginResolverException( exceptionVector, message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == exceptionVector );
    }
    /**
     *
     */
    @Test public void pluginResolverExceptionOneArgConstructorTest() {

        pre = new PluginResolverException( message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == null );
    }
}