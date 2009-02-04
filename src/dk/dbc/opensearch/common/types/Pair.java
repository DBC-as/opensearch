package dk.dbc.opensearch.common.types;

/**
 * InputPair
 */
public class Pair<E, V> {
    /**
     * 
     */

    private E first;
    private V second;

    public Pair( E first, V second ) {
        this.first = first;
        this.second = second;     
    }

    public E getFirst(){
        return first;
    }

    public V getSecond(){
        return second;
    }
}
