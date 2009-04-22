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


/**
 * InputPair
 * 
 * If You would like to have sorting done on the Pair type, please use
 * dk.dbc.opensearch.common.types.ComparablePair type instead
 */
public class Pair< E, V >// implements Comparator
{
    /**
     *
     */

    private E first;
    private V second;

    public Pair( E first, V second ) 
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
    
    
    public boolean equals( Object obj )
    {
        if(!( obj instanceof Pair ) )
        {
            return false;
        }
        else if(!( first.equals( ( (Pair)obj ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (Pair)obj ).getSecond() ) ) )
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
        return String.format( "Pair< %s, %s >", first.toString(), second.toString() );
    }
    
    
    public int hashCode()
    {
        return first.hashCode() + second.hashCode();
    }

}
