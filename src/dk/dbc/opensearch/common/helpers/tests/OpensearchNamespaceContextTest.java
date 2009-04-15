/** \brief UnitTest for OpensearchNamespaceContext */
package dk.dbc.opensearch.common.helpers.tests;

import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;

import static org.junit.Assert.*;
import org.junit.*;

import org.apache.commons.lang.NotImplementedException;
/**
 * 
 */
public class OpensearchNamespaceContextTest {


    OpensearchNamespaceContext nsc;
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
     * Not really doing a lot... 
     */
    @Test public void testConstructor() {
        nsc = new OpensearchNamespaceContext();
        assertTrue( nsc != null );
    }

    @Test public void testGetNamespaceURI() 
    { 
        String uri = "http://docbook.org/ns/docbook";
        nsc = new OpensearchNamespaceContext();
        assertEquals( uri, nsc.getNamespaceURI( "docbook" ) );
        assertTrue( null == nsc.getNamespaceURI( "anything else" ) );
    }

    @Test(expected=NotImplementedException.class) 
        public void testGetPrefixes() 
    {
        nsc = new OpensearchNamespaceContext();
        nsc.getPrefixes( "anything" );
    }
    @Test(expected=NotImplementedException.class)
 public void testGetPrefix() 
    {
        nsc = new OpensearchNamespaceContext();
        nsc.getPrefix( "anything" );
    }
}