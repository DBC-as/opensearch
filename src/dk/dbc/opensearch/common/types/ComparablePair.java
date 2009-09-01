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
 * \brief  This is an implementation of the Pair interface guaranteeing that elements can be compared, e.g. using Collection.sort
 * \file
 */


package dk.dbc.opensearch.common.types;

/**
 * Use this class if you want a {@link Pair} class that can be sorted. It
 * sorts on the first element and only considers the second if the two
 * first elements are equal. If the client needs sorting on the second
 * element and not the first, please consider using a
 * {@link http://java.sun.com/javase/6/docs/api/java/util/Comparator.html}
 * Please bear in mind, that {@link java.lang.Comparable} is used to define
 * the natural sort order of a class. In contrast,
 * java.util.Comparator is used to define an alternative sort order for
 * a class.
 *
 * @param <E>
 * @param <V>
 */
public final class ComparablePair<E extends Comparable<E>, V extends Comparable<V>> implements Comparable<ComparablePair<E, V>>, Pair<E, V>
{

    private final E first;
    private final V second;

    /**
     * Cosntructs the {@link ComparablePair} with arguments E as first member of
     * the pair and V as the second member.
     *
     * The {@link ComparablePair} is immutable and as such usable as a key in
     * Hash structures.
     *
     * @param first
     * @param second
     */
    public ComparablePair( E first, V second )
    {
        this.first = first;
        this.second = second;
    }


    /**
     * @return the first value of the {@link ComparablePair}
     */
    @Override
    public E getFirst()
    {
        return first;
    }


    /**
     * @return the second value of the {@link ComparablePair}
     */
    @Override
    public V getSecond()
    {
        return second;
    }


    /**
     * Tests the equality of this object and {@code cPair}
     * @param cPair the object to test for equality with
     * @return true if the objects are equal, false otherwise
     */
    @Override
    public boolean equals( Object cPair )
    {
        if( cPair == null )
        {
            return false;
        }
        else if( !(cPair instanceof ComparablePair<?, ?>) )
        {
            return false;
        }
        else if( !(first.equals( ((ComparablePair<?, ?>) cPair).getFirst() )) )
        {
            return false;
        }
        else if( !(second.equals( ((ComparablePair<?, ?>) cPair).getSecond() )) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }


    /**
     * Renders as {@code ComparablePair< E, V >}
     * @return a String representation of the object
     */
    @Override
    public String toString()
    {
        return String.format( "ComparablePair< %s, %s >", first.toString(), second.toString() );
    }


    /**
     *
     * @return
     */
    @Override
    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }

    
    /**
     * Compares the object with a {@link ComparablePair} returning values in
     * accordance with the {@link Comparable#compareTo(java.lang.Object)}
     * specification
     * 
     * @param pair the {@link ComparablePair} to compare
     * @return
     */
    @Override
    public int compareTo( ComparablePair<E, V> pair )
    {
        if( !(pair instanceof ComparablePair<?, ?>) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable type", pair.toString() ) );
        }

        ComparablePair<E, V> newpair = pair;

        if( !(newpair.getFirst().getClass() == first.getClass()) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable to type %s", newpair.getFirst().getClass(), first.getClass() ) );
        }

        if( first.equals( newpair.getFirst() ) )
        {
            return second.compareTo( newpair.getSecond());
        }

        return first.compareTo( newpair.getFirst());
    }
}
