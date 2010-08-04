/**
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


package dk.dbc.opensearch.common.types;


/**
 * Pair is, as the name suggest, a very simple version of a IPair container.
 * You can add two objects to Pair and retrieve them again.
 * The objects may be different, identical, or the actual same object.
 * After you have added the objects to Pair you can no longer modify them,
 * i.e. Pair is immutable, even though the objects inside Pair may be mutable (see below).
 * <p>
 * Please notice, Pair do not use defensive copying of its two elements.
 * As a consequence if you modify the original objects after adding them to the Pair,
 * the objects inside Pair will also be changed. It is the responibility of the user of Pair
 * to ensure the correct behavior of the objects after adding them to Pair. This is of course only
 * possible if you use mutable objects.
 * <p>
 * If you would like to have sorting done on a IPair type, please use
 * {@link ComparablePair} instead
 */
public class Pair< E, V >
{
    private final E first;
    private final V second;

    /**
     *  Constructor taking two objects. The Objects may be different objects, 
     *  equal objects, or the same actual object. Neither of the objects may be null.
     * 
     *  @param first The first object.
     *  @param second The second object.
     *
     *  @throws IllegalArgumentException if either first or second are null.
     */
    public Pair( final E first, final V second ) throws IllegalArgumentException
    {
	if ( first == null || second == null )
	{
	    throw new IllegalArgumentException( "null values are not accepted as elements in SimplePair." );
	}
        this.first = first;
        this.second = second;
    }

    /**
     *  Retrieves the first element of the pair.
     *  
     *  @return The first element of the pair.
     */
    public E getFirst()
    {
        return first;
    }

    /**
     *  Retrieves the second element of the pair.
     *  
     *  @return The second element of the pair.
     */
    public V getSecond()
    {
        return second;
    }
    
    /**
     *  A string representation of the pair in the following format:
     *  <pre>
     *  {@code
     *     Pair< String-representation-of-first-element, String-representaion-of-second-element >
     *  }
     *  </pre>
     *  If the Pair as the first element contains a String with value "FancyPants",
     *  and as its second element the Integer value 42, then the toString will return:
     *  <pre>
     *  {@code
     *     Pair< FancyPants, 42 >
     *  }
     *  </pre>
     */
    @Override
    public String toString()
    {
        return String.format( "Pair< %s, %s >", first.toString(), second.toString() );
    }
    
    /**
     *  Returns a unique hashcode for the specific combination of elements in this Pair.
     *  Notice, if you use the same two objects in the same order in two different SimplePairs, 
     *  then the two SimplePairs will return the same hashcode.
     */    
    @Override
    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }

    /**
     *  Asserts equality of the Pair object and another Pair object,
     *  based on equality of the contained elements. The elements are testeted against each other 
     *  in the same order they appear in the Pair. That is, Pair< E, V > and Pair < V, E >
     *  are not equal even though it is the same objects (E and V) that are contained in the Pair,
     *  assuming E and V are nonequal.
     */
    @Override
    public boolean equals( Object obj )
    {
        if(!( obj instanceof Pair<?,?> ) )
        {
            return false;
        }
        else if(!( first.equals( ( (Pair<?, ?>)obj ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (Pair<?, ?>)obj ).getSecond() ) ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
