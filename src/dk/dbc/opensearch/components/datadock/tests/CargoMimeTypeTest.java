package dbc.opensearch.components.datadock.tests;
/** \brief UnitTest for CargoMimeType */

import dbc.opensearch.components.datadock.CargoMimeType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class CargoMimeTypeTest {

    /**
     * construct a mimetype object
     */
    @Before public void SetUp() {
        
    }

    @Test public void testBasicSanityOfMimeTypeRepresentation(){
        assertTrue( "text/xml".equals( CargoMimeType.TEXT_XML.getMimeType() ) );
    }

    @Test public void testBasicSanityOfMimeTypeDescription(){
        assertTrue( "XML Document".equals( CargoMimeType.TEXT_XML.getDescription() ) );
    }

}