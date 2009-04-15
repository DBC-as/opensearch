package dk.dbc.opensearch.common.types.tests;
/** \brief UnitTest for CargoMimeType */

import dk.dbc.opensearch.common.types.CargoMimeType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class CargoMimeTypeTest 
{
    @Before 
    public void SetUp() 
    {
        
    }

    
    @Test 
    public void testBasicSanityOfMimeTypeRepresentation()
    {
        assertTrue( "text/xml".equals( CargoMimeType.TEXT_XML.getMimeType() ) );
        assertTrue( "application/pdf".equals( CargoMimeType.APPLICATION_PDF.getMimeType() ) );
    }

    
    @Test 
    public void testBasicSanityOfMimeTypeDescription()
    {
        assertTrue( "XML Document".equals( CargoMimeType.TEXT_XML.getDescription() ) );
        assertTrue( "PDF Document".equals( CargoMimeType.APPLICATION_PDF.getDescription() ) );
    }

    
    @Test
    public void testValidMimetype()
    {
    	String validMimetype = "text/xml";
    	assertTrue( CargoMimeType.validMimetype( validMimetype ) );
    }
    
    
    @Test
    public void testInvalidMimetype()
    {
    	String invalidMimetype = "invalid/test";
    	assertFalse( CargoMimeType.validMimetype( invalidMimetype ) );
    }
    
    
    @Test
    public void testGetMimetypeFrom()
    {
    	String mimetype = "text/xml";
    	CargoMimeType cmt = CargoMimeType.TEXT_XML;
    	
    	assertTrue( CargoMimeType.getMimeFrom( mimetype ).equals( cmt ) );
    }
    
    
    @Test
    public void testNullGetMimetypeFrom()
    {
    	String mimetype = "null/test";
    	assertNull( CargoMimeType.getMimeFrom( mimetype ) );    	
    }
}