/**
 * \brief UnitTest for IndexingAlias 
 */
package dk.dbc.opensearch.common.types.tests;

import dk.dbc.opensearch.common.types.IndexingAlias;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class IndexingAliasTest {

    /**
     *
     */

    IndexingAlias testIA;
    String articleDescString = "the docbook/ting xml alias";
 
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
    @Test public void testGetDescription() 
    {
        testIA = IndexingAlias.getIndexingAlias( "article" );
        assertEquals( testIA.getDescription(), articleDescString );
    }

    @Test public void testValidIA() 
    {  
        assertTrue( IndexingAlias.validIndexingAlias( "article" ) );
    }  
    
    @Test public void testValidIAinvalidArg() 
    {  
        assertFalse( IndexingAlias.validIndexingAlias( "invalid" ) );
    }
}


