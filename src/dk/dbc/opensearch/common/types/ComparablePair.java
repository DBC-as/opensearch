package dk.dbc.opensearch.common.types;


import dk.dbc.opensearch.common.types.Pair;

import java.lang.UnsupportedOperationException;

/**
 *  Use this class if you want a Pair class that can be sorted
 */

public class ComparablePair< E extends Comparable< E >, V extends Comparable< V > > implements Comparable
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

    public int compareTo( Object pair ){
        if ( ! ( pair instanceof ComparablePair ) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable type", pair.toString() ) );
        }

        ComparablePair newpair = (ComparablePair)pair;

        /** \todo: I'm fairly certain that the compiler would never allow an instance of ComparablePair with nonComparable objects, but then again, I don't trust my intuition _that_ much*/
        if ( newpair.getFirst() instanceof Comparable && newpair.getSecond() instanceof Comparable )
        {
            if ( first.equals( newpair.getFirst() ) )
            {
                return (int)second.compareTo( (V)newpair.getSecond() );
            }
            return (int)first.compareTo( (E)newpair.getFirst() );
        }
        else
        {
            /** \todo: this could really be more granular*/
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable type", newpair.toString().toString() ) );
        }
    }
}
