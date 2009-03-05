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
    
    
    public boolean equals( Pair<E, V> pair )
    {
        if( pair == null )
        {
            return false;
        }
        if ( ( first == pair.getFirst() ) && ( second ==  pair.getSecond() ) )
        {
            return true;
        }
        else
        {
            return false;
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
