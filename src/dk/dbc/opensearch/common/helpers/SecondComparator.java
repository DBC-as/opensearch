package dk.dbc.opensearch.common.helpers;

import dk.dbc.opensearch.common.types.Pair;

import java.util.Comparator;

/**
 * helper class for the JobMapCreator class for use in sorting arraylists 
 * of Pair<String,Integer>
 * Is made a class on its on and not an inner class to prepare the JobMapCreator 
 * class to become static. See Todo in JobMapCreator
 */

public class SecondComparator implements Comparator
{
    public int compare( Object x, Object y )
    {
        if( ((Pair< String, Integer >)x).getSecond() < ((Pair< String, Integer >)y).getSecond() )
            {
                return -4;
            }
        else
            {
                if( ((Pair<String, Integer>)x).getSecond() == ((Pair<String, Integer>)y).getSecond() )
                    {
                        return 0;
                    }
            }
        return 4;
    }

}