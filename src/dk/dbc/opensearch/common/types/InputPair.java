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


/**
 * InputPair
 * 
 * If you would like to have sorting done on the InputPair type, please use
 * dk.dbc.opensearch.common.types.ComparablePair type instead
 */
public class InputPair< E, V > implements Pair< E, V >
{
    /**
     *
     */

    private E first;
    private V second;

    public InputPair( E first, V second ) 
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
        if(!( obj instanceof InputPair<?,?> ) )
        {
            return false;
        }
        else if(!( first.equals( ( (InputPair<?, ?>)obj ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (InputPair<?, ?>)obj ).getSecond() ) ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }

}
