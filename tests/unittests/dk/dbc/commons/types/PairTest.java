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


package dk.dbc.commons.types;

import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import java.util.Set;
import java.util.HashSet;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author stm
 */
public class PairTest {

    @Test
    public void testAddingTwoIdenticalObjectsWorks()
    {
        new Pair<String, String>( "a", "a" );
    }

    
    @Test( expected=IllegalArgumentException.class )
    public void testConstructorDoesntAcceptNullValuesForFirstArgument()
    {
        new Pair<String, String>( null, "a" );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testConstructorDoesntAcceptNullValuesForSecondArgument()
    {
        new Pair<String, String>( "a", null );
    }

    @Test
    public void testNoDefensiveCopyingIsMadeInSimplePairConstructor()
    {
        List<String> stringlist = new ArrayList<String>();
        stringlist.add( "a" );
        Pair<String, List<String>> pair = new Pair<String, List<String>>( "a", stringlist );
        stringlist.remove( "a" );
        assertTrue( pair.getSecond().isEmpty() );
    }


    @Test
    public void testGetFirst()
    {
        Pair<String, Integer> names = new Pair<String, Integer>( "one", Integer.parseInt( "1" ) );
        String expResult = "one";
        String result = names.getFirst();
        assertEquals( expResult, result );
    }


    @Test
    public void testGetSecond()
    {
        Pair<String, Integer> names = new Pair<String, Integer>( "one", Integer.parseInt( "1" ) );
        Integer expResult = new Integer( 1 );
        Integer result = names.getSecond();
        assertEquals( expResult, result );
    }


    @Test
    public void testToString()
    {
        Pair<String, Integer> names = new Pair<String, Integer>( "one", Integer.parseInt( "1" ) );
        String expResult = "Pair< one, 1 >";
        String result = names.toString();
        assertEquals( expResult, result );
    }


    @Test
    public void testEquals()
    {
        Pair<Integer, Integer> ints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        Pair<Integer, Integer> moreints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        assertTrue( ints.equals( moreints) );
    }

    @Test public void testEqualsWorkWithValidArg(){
        Pair<String, String> one = new Pair<String, String>( "a", "1" );
        Pair<String, String> two = new Pair<String, String>( "a", "1" );

         assertTrue( one.equals( two ) );
    } 

    @Test public void testNonEqualObjectsForPairMakesUnequalPair(){
        Pair<String, String> one = new Pair<String, String>( "a", "1" );
        Pair<String, String> two = new Pair<String, String>( "b", "1" );
        Pair<String, String> three = new Pair<String, String>( "a", "2" );

        assertFalse( one.equals( two ) );
        assertFalse( one.equals( three ) );
    }


    @Test
    public void testEqualsRejectsOtherClass()
    {
        Pair<String, String> one = new Pair<String, String>( "a", "1" );
        assertFalse( one.equals( "test" ) );
    }

    /**
     * An explicit test for equality against zero.  We do not say
     * anything about what should happen, but it ought to just return
     * false.
     */
    @Test
    public void testEqualityAgainstNull()
    {
        Pair<String, String> one = new Pair<String, String>( "a", "1" );
	Pair<String, String> two = null;

        assertFalse( one.equals( two ) );
    }


    @Test
    public void testHashCode()
    {
        Pair<Integer, Integer> ints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        Pair<Integer, Integer> moreints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        assertEquals( ints.hashCode(), moreints.hashCode() );
    }

    /**
     * Using the Set type to externally guarantee the hash code generation
     */
    @Test
    public void testHashCodeworksForSets()
    {
        Pair<Integer, Integer> ints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        Pair<Integer, Integer> moreints = new Pair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        Set<Pair<Integer, Integer>> integers = new HashSet<Pair<Integer, Integer>>();
        integers.add( ints );
        integers.add( moreints );

        assertTrue( integers.size() == 1 );
    }


}