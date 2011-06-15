/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s, 
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/** \brief UnitTest for CargoMimeType */

package dk.dbc.opensearch.types;


import dk.dbc.opensearch.types.CargoMimeType;

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