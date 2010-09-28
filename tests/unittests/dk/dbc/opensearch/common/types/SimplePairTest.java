/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.dbc.opensearch.common.types;

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
public class SimplePairTest {

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