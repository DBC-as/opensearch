package dk.dbc.opensearch.common.types;

/**
 * InputPair
 * not for use when values are to be compared until an equals() method is implemented
 */
public class Pair<E, V> 
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
    
    
    @Override 
    public boolean equals( Object obj )
    {
        if(!( obj instanceof Pair ) )
            return false;
        else if(!( first.equals( ( (Pair)obj ).getFirst() ) ) )
            return false;
        else if(!( second.equals( ( (Pair)obj ).getSecond() ) ) )
            return false;
        else
        	return true;
    }
    
    
    @Override 
    public String toString()
    {
        return first.toString() + ", " + second.toString();
    }
    
    
    @Override 
    public int hashCode()
    {
        return first.hashCode() + second.hashCode();
    }

}
