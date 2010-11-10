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

package dk.dbc.opensearch.common.compass;


import java.io.IOException;

import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.EntityResolver;
import static org.junit.Assert.*;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class CompassEntityResolverTest
{
    static String publicId;
    static String systemId;
    static String publicURL;
    static String systemURL;
    
    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }


    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }


    /**
     * Test of resolveEntity method, of class CompassEntityResolver.
     */
    @Test
    public void testResolveEntityDefaultResolver() throws SAXException, IOException
    {
        publicId = "http://a";
        systemId = "b";
        publicURL = "http://b";
        systemURL = "a";
        EntityResolver resolver = new CompassEntityResolver( publicURL, systemURL );
        InputSource result = resolver.resolveEntity( publicId, systemId );
        assertEquals( null, result );
    }

    /**
     * Test of resolveEntity method, of class CompassEntityResolver.
     */
    @Test
    public void testResolveEntityResolverNotNull() throws SAXException, IOException
    {
        publicId = "http://a";
        systemId = "http://b";
        publicURL = "http://b";
        systemURL = "a";
        EntityResolver resolver = new CompassEntityResolver( publicURL, systemURL );
        InputSource result = resolver.resolveEntity( publicId, systemId );
        assertNotNull( result );
    }

        /**
     * Test of resolveEntity method, of class CompassEntityResolver.
     */
    @Test
    public void testResolveEntityCompassResolver() throws SAXException, IOException
    {
        publicId = "http://a";
        systemId = "http://b";
        publicURL = "http://b";
        systemURL = "a";
        EntityResolver resolver = new CompassEntityResolver( publicURL, systemURL );
        InputSource result = resolver.resolveEntity( publicId, systemId );
        assertEquals( systemURL, result.getSystemId() );
    }
}