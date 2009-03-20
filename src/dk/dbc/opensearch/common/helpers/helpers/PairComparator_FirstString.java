package dk.dbc.opensearch.common.helpers;


import dk.dbc.opensearch.common.types.Pair;

import java.util.Comparator;


/**
 * Helper class for the FedoraTolls class. Used to sorting arraylists
 * of Pair<String, Object>. Sorting on the first element, the String in
 * the Pair
 */
public class PairComparator_FirstString implements Comparator
{
    public int compare( Object x, Object y )
    {
        return ((Pair< String, Object >) x).getFirst().compareTo( ( (Pair<String, Object>)y).getFirst() );
    }
}