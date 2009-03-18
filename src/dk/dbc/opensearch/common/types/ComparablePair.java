package dk.dbc.opensearch.common.types;


import dk.dbc.opensearch.common.types.Pair;

import java.lang.UnsupportedOperationException;

/**
 *  Use this class if you want a Pair class that can be sorted
 *  It sorts on the first element and only considers the second 
 *  if the two first elements are equal.
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
        else if(!( first.equals( ( (ComparablePair)cPair ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (ComparablePair)cPair ).getSecond() ) ) )
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
 
        if ( first.equals( newpair.getFirst() ) )
        {
                return (int)second.compareTo( (V)newpair.getSecond() );
        }
        return (int)first.compareTo( (E)newpair.getFirst() );
        
    }
}
