package dk.dbc.opensearch.common.types;

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


import java.lang.UnsupportedOperationException;

/**
 *  Use this class if you want a Pair class that can be sorted
 *  It sorts on the first element and only considers the second 
 *  if the two first elements are equal.
 */

public class ComparablePair< E extends Comparable< E >, V extends Comparable< V > > implements Comparable, Pair< E, V >
{
    private E first;
    private V second;

    public ComparablePair( E first, V second ) 
    {
        this.first = first;
        this.second = second;
    }

    
    public E getFirst()
    {
        return first;
    }

    
    public V getSecond()
    {
        return second;
    }
    
    @Override
    public boolean equals( Object cPair )
    {
        if( cPair == null )
        {
            return false;
        }
        else if( ! ( cPair instanceof ComparablePair ) )
        {
            return false;
        }
        else if( !( first.equals( ( (ComparablePair)cPair ).getFirst() ) ) )
        {
            return false;
        }
        else if( !( second.equals( ( (ComparablePair)cPair ).getSecond() ) ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    
    public String toString()
    {
        return String.format( "ComparablePair< %s, %s >", first.toString(), second.toString() );
    }
    
    
    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }

    public int compareTo( Object pair ){
        if ( ! ( pair instanceof ComparablePair ) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable type", pair.toString() ) );
        }

        ComparablePair newpair = (ComparablePair)pair;

        if( !( newpair.getFirst().getClass() == first.getClass() ) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable to type %s", newpair.getFirst().getClass(), first.getClass() ) );
        }
 
        if ( first.equals( newpair.getFirst() ) )
        {
                return (int)second.compareTo( (V)newpair.getSecond() );
        }
        return (int)first.compareTo( (E)newpair.getFirst() );
        
    }
}
