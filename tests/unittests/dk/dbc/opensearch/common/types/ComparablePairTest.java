/*
 *   
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s, 
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

/** \brief UnitTest for ComparablePair **/

package dk.dbc.opensearch.common.types;


import java.io.IOException;

import java.util.ArrayList;
import java.util.Collections;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class ComparablePairTest {

    
    /**
     * Verify that null values are not comparable
     */
    @Test( expected = IllegalArgumentException.class )
    public void testNullNotAcceptedInConstructor()
    {
        ComparablePair<String, String> one = new ComparablePair<String, String>( "a", null );
        ComparablePair<String, String> two = null;

        assertTrue( ! one.equals( two ) );
    }

    /**
     * Test of {@code toString()} according to the documentation.
     */
    @Test public void testToString()
    {
        String test = "string";
        int testInt = 1;
        String match = String.format( "ComparablePair< %s, %s >", test.toString(), testInt );
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, testInt );
        assertEquals( match, one.toString() );
        
    }
    

    /**
     * Test of the compareTo method, when the first elements of the 
     * comparablePair are unequal 
     */
    @Test public void testCompareToNonequalFirsts() throws IOException
    {
        String testSmall = "a";
        String testLarge = "b";
        int testInt = 1;

        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( testLarge, testInt );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer >( testSmall, testInt );
        
        assertTrue( one.compareTo( two ) > 0 );
        assertTrue( two.compareTo( one ) < 0 );
    }


    /**
     * Test of the compareTo method, when the first elements of the 
     * comparablePair are equal 
     */
    @Test public void testCompareToEqualFirsts() //throws IOException
    {
        String test = "equal";
      
        int smallInt = 1;
        int largeInt = 9;

        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, largeInt );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer>( test, smallInt );
        
        assertTrue( one.compareTo( two ) > 0 );
        assertTrue( two.compareTo( one ) < 0 );
    }


    /**
     * Testing the case where the first and the second element in in the two
     * pairs are equal
     */
    @Test public void testCompareToEqualPairs()
    {
        String test = "equal";
        int equal = 1;
        
        ComparablePair<String, Integer> one = new ComparablePair<String, Integer>( test, equal );
        ComparablePair<String, Integer> two = new ComparablePair<String, Integer>( test, equal );
    
        assertTrue( 0 == one.compareTo( two ) );
        assertTrue( 0 == two.compareTo( one ) );
    }


    /**
     * Test that an array of ComparablePair objects can be sorted
     * according to the contents of {@code first}.
     */
    @Test public void testSortableOnFirst(){
        ArrayList< ComparablePair<String, Integer> > apairlist =
            new ArrayList< ComparablePair<String, Integer> >();
        apairlist.add( new ComparablePair< String, Integer > ( "b", 1 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "c", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 3 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
        assertEquals( apairlist.get( 1 ).getFirst(), "b" );
        assertEquals( apairlist.get( 2 ).getFirst(), "c" );

        assertEquals( (Object)apairlist.get( 0 ).getSecond(), 3 );
        assertEquals( (Object)apairlist.get( 1 ).getSecond(), 1 );
        assertEquals( (Object)apairlist.get( 2 ).getSecond(), 2 );
    }

    /**
     * Test that an array of ComparablePair objects can be sorted
     * according to the contents of {@code first}, and then {@code
     * second}
     */
    @Test public void testSortableOnSecond(){
        ArrayList< ComparablePair<String, Integer> > apairlist =
            new ArrayList< ComparablePair<String, Integer> >();
        apairlist.add( new ComparablePair< String, Integer > ( "a", 2 ) );
        apairlist.add( new ComparablePair< String, Integer > ( "a", 1 ) );

        Collections.sort( apairlist );

        assertEquals( apairlist.get( 0 ).getFirst(), "a" );
      
        assertEquals( (Object)apairlist.get( 0 ).getSecond(), 1 );
        assertEquals( (Object)apairlist.get( 1 ).getSecond(), 2 );
    }
}
