/** \brief UnitTest for Pair */
package dk.dbc.opensearch.common.types;

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


import java.util.HashMap;
import dk.dbc.opensearch.common.types.ImmutablePair;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class PairTest {

    ImmutablePair<String, String> p;

    /**
     *
     */
    @Before public void SetUp() {
        p = new ImmutablePair<String, String>( "a", "b");
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test 
    public void testTypeConsistency() {
        assertEquals( p.getFirst(), "a" );
        assertEquals( p.getSecond(), "b" );
    }

    @Test 
    public void testHashCode(){
        ImmutablePair<String, String> p2 = 
            new ImmutablePair<String, String>( "a", "b" );

        assertEquals( p.hashCode(), p2.hashCode() );

    }
    /**
     * happy path
     */

    @Test public void testEquals(){
        ImmutablePair<String, String> p3 = 
            new ImmutablePair<String, String>( "a", "b" );

        assertTrue( p3.equals( p ) );
    }
    /**
     * two non-equal pair
     */

    @Test public void testEqualsDifferent()
    {
        ImmutablePair<String, String> p3 = 
            new ImmutablePair<String, String>( "a", "a" );

        assertFalse( p3.equals( p ) ); 
    
    }
    /**
     * Calling equal with a non-pair
     */
   @Test public void testEqualsInvalid()
    {
        String test = "test";

        assertFalse( p.equals( test ) ); 
    
    }

    @Test public void testPairInHashMaps(){
        HashMap< ImmutablePair< String, String >, String > hm =
            new HashMap< ImmutablePair< String, String >, String >();

        hm.put( new ImmutablePair<String, String>( "a", "b" ), "c" );
        hm.put( new ImmutablePair<String, String>( "d", "e" ), "f" );

        assertNotNull( hm.get( new ImmutablePair<String, String>( "a", "b" ) ) );
    }
}