package dk.dbc.opensearch.helpers;

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


import java.lang.NullPointerException;
import java.util.Iterator;
import static org.junit.Assert.*;
import org.junit.*;

import org.apache.commons.lang.NotImplementedException;


/** \brief UnitTest for OpensearchNamespaceContext */
public class OpensearchNamespaceContextTest 
{
    OpensearchNamespaceContext nsc;
    static String uri = "http://docbook.org/ns/docbook";
 

    /**
     *
     */
    @Before 
    public void SetUp() 
    { 
        nsc = new OpensearchNamespaceContext();
    }


    /**
     *
     */
    @After 
    public void TearDown() 
    { 
        nsc = null;
    }


    /**
     * Not really doing a lot... 
     */
    @Test 
    public void testConstructor() 
    {
        assertTrue( nsc != null );
    }


    @Test 
    public void testGetNamespaceURI() 
    { 
        assertEquals( uri, nsc.getNamespaceURI( "docbook" ) );
    }

    /**
     * 
     */
    @Test(expected=NullPointerException.class)
    public void testGetNamespaceURIOnNonexistingPrefixReturnsNull()
    {
        //I can't get junit to assertNull on the return value, so this strange
        //construction is used instead
        String osns = nsc.getNamespaceURI( "i'm no prefix" );
    }


    /**
     * Tests the semantics of OpenSearchNamespaceContext; that there is only one
     * possible prefix per namespace
     */
    @Test
    public void testGetPrefixes() 
    {
        for( Iterator<String> pIter = nsc.getPrefixes( uri ); pIter.hasNext(); )
        {
            assertTrue( pIter.hasNext() );
            String prefix = pIter.next();
            assertFalse( pIter.hasNext() );
            assertEquals( "docbook", prefix );
        }
    }
    
    public void testGetPrefix() 
    {
        nsc = new OpensearchNamespaceContext();
        String prefix = nsc.getPrefix( "docbook" );
        assertEquals( uri, prefix );
    }
}