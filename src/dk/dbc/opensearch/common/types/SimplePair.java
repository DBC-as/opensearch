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
 * SimplePair is, as the name suggest, a very simple version of a Pair container.
 * You can add two (different) objects to SimplePair and retrieve them again.
 * After you have added the objects to SimplePair you can no longer modify them,
 * i.e. SimplePair is immutable, even though the objects inside SimplePair may be mutable (see below).
 * <p>
 * Please notice, SimplePair do not use defensive copying of its two elements. 
 * As a consequence if you modify the original objects after adding them to the SimplePair,
 * the objects inside SimplePair will also be changed. It is the responibility of the user of SimplePair
 * to ensure the correct behavior of the objects after adding them to SimplePair. This is of course only 
 * possible if you use mutable objects.
 * <p>
 * If you would like to have sorting done on a Pair type, please use
 * {@link ComparablePair} instead
 */
public final class SimplePair< E, V > implements Pair< E, V >
{
    /**
     *
     */

    private final E first;
    private final V second;

    public SimplePair( E first, V second ) 
    {
        this.first = first;
        this.second = second;
    }

    
    @Override
    public E getFirst()
    {
        return first;
    }

    
    @Override
    public V getSecond()
    {
        return second;
    }
    
       
    public String toString()
    {
        return String.format( "Pair< %s, %s >", first.toString(), second.toString() );
    }
    
    
    @Override
    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }


    @Override
    public boolean equals( Object obj )
    {
        if(!( obj instanceof SimplePair<?,?> ) )
        {
            return false;
        }
        else if(!( first.equals( ( (SimplePair<?, ?>)obj ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (SimplePair<?, ?>)obj ).getSecond() ) ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
