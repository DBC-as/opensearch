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
        new SimplePair<String, String>( "a", "a" );
    }

    
    @Test( expected=IllegalArgumentException.class )
    public void testConstructorDoesntAcceptNullValuesForFirstArgument()
    {
        new SimplePair<String, String>( null, "a" );
    }

    @Test( expected=IllegalArgumentException.class )
    public void testConstructorDoesntAcceptNullValuesForSecondArgument()
    {
        new SimplePair<String, String>( "a", null );
    }

    @Test
    public void testNoDefensiveCopyingIsMadeInSimplePairConstructor()
    {
        List<String> stringlist = new ArrayList<String>();
        stringlist.add( "a" );
        SimplePair<String, List<String>> pair = new SimplePair<String, List<String>>( "a", stringlist );
        stringlist.remove( "a" );
        assertTrue( pair.getSecond().isEmpty() );
    }


    @Test
    public void testGetFirst()
    {
        SimplePair<String, Integer> names = new SimplePair<String, Integer>( "one", Integer.parseInt( "1" ) );
        String expResult = "one";
        String result = names.getFirst();
        assertEquals( expResult, result );
    }


    @Test
    public void testGetSecond()
    {
        SimplePair<String, Integer> names = new SimplePair<String, Integer>( "one", Integer.parseInt( "1" ) );
        Integer expResult = new Integer( 1 );
        Integer result = names.getSecond();
        assertEquals( expResult, result );
    }


    @Test
    public void testToString()
    {
        SimplePair<String, Integer> names = new SimplePair<String, Integer>( "one", Integer.parseInt( "1" ) );
        String expResult = "Pair< one, 1 >";
        String result = names.toString();
        assertEquals( expResult, result );
    }


    @Test
    public void testHashCode()
    {
        SimplePair<Integer, Integer> ints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        SimplePair<Integer, Integer> moreints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        assertEquals( ints.hashCode(), moreints.hashCode() );
    }

    /**
     * Using the Set type to externally guarantee the hash code generation
     */
    @Test
    public void testHashCodeworksForSets()
    {
        SimplePair<Integer, Integer> ints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        SimplePair<Integer, Integer> moreints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        Set<SimplePair<Integer, Integer>> integers = new HashSet<SimplePair<Integer, Integer>>();
        integers.add( ints );
        integers.add( moreints );

        assertTrue( integers.size() == 1 );
    }


    @Test
    public void testEquals()
    {
        SimplePair<Integer, Integer> ints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );
        SimplePair<Integer, Integer> moreints = new SimplePair<Integer, Integer>( Integer.MIN_VALUE, Integer.MAX_VALUE );

        assertTrue( ints.equals( moreints) );
    }
}