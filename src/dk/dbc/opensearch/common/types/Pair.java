package dk.dbc.opensearch.common.types;

import java.lang.UnsupportedOperationException;
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
