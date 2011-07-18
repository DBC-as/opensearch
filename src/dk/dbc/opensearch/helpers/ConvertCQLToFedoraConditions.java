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

package dk.dbc.opensearch.helpers;

import dk.dbc.opensearch.fedora.FedoraObjectFields;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.types.ITargetField;

import java.util.ArrayList;

public class ConvertCQLToFedoraConditions {
    
    public static ArrayList< ArrayList< OpenSearchCondition > > searchTransformer( String[] theQuery )
    {
        ArrayList< ArrayList< OpenSearchCondition > > queries = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > currentConditionLists = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< OpenSearchCondition > tempConditionList = new ArrayList< OpenSearchCondition >();
        int queryCount = 0;
        int currentCount = 0;
        String inHand = null;
        int next = 0;
        String part = null;

        int queryLength = theQuery.length;

        while( next < queryLength )
        {
            part = theQuery[ next ];

            if( part.equals( "AND" ) )
            {
                //if we have a condition in inHand we transform it to a condition list
                if( inHand != null )
                {
                    currentConditionLists.add( transformConditionStringToConditionList ( inHand ) );
                    inHand = null;
                }

                //write all lists of conditions to all queries to get unique lists of conditions
                queries = makeListPermutations( queries, currentConditionLists );
                // System.out.println( "in AND" );
                // for( ArrayList< OpenSearchCondition > list : queries )
                // {
                //     for( OpenSearchCondition cond: list )
                //     {
                //         System.out.println( cond.toString() );
                //     }
                // }
                //reset the list of list of conditions
                currentConditionLists.clear();
                // System.out.println( "in AND, after reset" );
                // for( ArrayList< OpenSearchCondition > list : queries )
                // {
                //     for( OpenSearchCondition cond: list )
                //     {
                //         System.out.println( cond.toString() );
                //     }
                // }
                next++;
            }

            if( part.equals( "OR" ) )
            {
                //if we have a condition in inHand we transform it to a condition list
                if( inHand != null )
                {
                    currentConditionLists.add( transformConditionStringToConditionList( inHand ) );
                    inHand = null;
                }

                next++;
            }

            if( part.equals( "(" ) )
            {
                //call method that gets the subquery between the parantheses
                String[] subQuery = getQueryInParantheses( next, theQuery );

                //increase next with the length of subQuery + 2, we need to get past the right paranthese
                next = next + subQuery.length + 2;

                //We transform the subquery to lists of conditions, by recursivly calling this method
                ArrayList < ArrayList < OpenSearchCondition > > subQueries = searchTransformer( subQuery );

                //We add all the resulting lists of conditions to currentConditionLists
                for( ArrayList< OpenSearchCondition > condList : subQueries )
                {
                    currentConditionLists.add( condList );
                }

            }

            //we are in a condition
            if( !part.equals( "AND" ) && !part.equals( "OR" ) && !part.equals( "(" ) )
            {

                if ( inHand == null )
                {
                    //stat of new condition
                    inHand = part;
                    //System.out.println( "part: " + part );
                }
                else
                {
                    //add more to the current condition
                    inHand = inHand + " " + part;
                }
                next++;
            }

        }

        if( inHand != null )
        {
            currentConditionLists.add( ConvertCQLToFedoraConditions.transformConditionStringToConditionList( inHand ) );
        }

        // System.out.println( "queries" );        
        // for( ArrayList< OpenSearchCondition > list : queries )
        // {

        //     for( OpenSearchCondition cond: list )
        //     {
        //         System.out.println( cond.toString() );
        //     }
        // }
        
        // System.out.println( "currentConditionLists" );
        // for( ArrayList< OpenSearchCondition > list : currentConditionLists )
        // {
            
        //     for( OpenSearchCondition cond: list )
        //     {
        //         System.out.println( cond.toString() );
        //     }
        // }
        
        queries = ConvertCQLToFedoraConditions.makeListPermutations( queries, currentConditionLists );
        
        // System.out.println( "before return" );
        // for( ArrayList< OpenSearchCondition > list : queries )
        // {
            
        //     for( OpenSearchCondition cond: list )
        //     {
        //         System.out.println( cond.toString() );
        //     }
        // }
        
        return queries;
    }


    public static ArrayList< OpenSearchCondition >  transformConditionStringToConditionList( String condition ) {
        //the string contains three parts: first a field name, then
        //the = operator and the the value of the condition.
        ArrayList < OpenSearchCondition > returnList = new ArrayList< OpenSearchCondition >();
        if( condition.isEmpty() )
        {
            return returnList;
        }

        int equalPos = condition.indexOf( "=" );
        String fieldname = condition.substring( 0 , equalPos - 1 );
        String value = condition.substring( equalPos + 2 );

        FedoraObjectFields field = FedoraObjectFields.getFedoraObjectFields( fieldname );

        OpenSearchCondition theCondition = new OpenSearchCondition( (ITargetField)field, OpenSearchCondition.Operator.EQUALS, value );

        returnList.add( theCondition );
        return returnList;
    }

    /**
     * Finds the query between parantheses
     * We start the search after the first left paranthese
     */
    public static String[] getQueryInParantheses( int start, String[] query )
    {
        int position = ++start;
        int rightPar = 0;
        int leftPar = 1;
        String queryString = "";
        String part = "";

        while( leftPar > rightPar )
        {
            queryString = queryString + " " + part;
            part = query[ position ];

            if( part.equals( "(" ) )
            {
                leftPar++;
            }
            if( part.equals( ")" ) )
            {
                rightPar++;
            }
            position++;
            queryString = queryString.trim();
        }

        queryString = queryString.substring( 0, queryString.length() );
       
        String[] returnArray = queryString.split( " " );

        return returnArray;
    }


    /**
     * Method for creating a list of unique lists of conditions from two
     * lists of lists of conditions where in the following way:
     * List x contains the lists a b c and list y contains d and e. The
     * result will be 6 lists: ad ae bd be cd ce
     * @param baseLists {@link ArrayList} of {@link ArrayList} of {@code openSearchCondition}
     * the base lists to append lista to
     * @param appendLists {@link ArrayList} of {@link ArrayList} of {@code openSearchCondition}
     * the lists to append to the base lists
     * @return {@link ArrayList} of {@link ArrayList} of {@code openSearchCondition}
     */
    public static ArrayList< ArrayList < OpenSearchCondition > > makeListPermutations( ArrayList< ArrayList< OpenSearchCondition > > baseLists, ArrayList< ArrayList< OpenSearchCondition > > appendLists )
    {

        //System.out.println( "baseLists size: " + baseLists.size() );
        //System.out.println( "appendLists size: " + appendLists.size() );
        //if there are no condition lists in queries return conditionLists
        if( baseLists.size() == 0 )
        {
            return new ArrayList< ArrayList< OpenSearchCondition > >( appendLists );
        }

        if( appendLists.size() == 0 )
        {
            return baseLists;
        }


        int appendListCount = appendLists.size();
        int baseCount = baseLists.size();
        OpenSearchCondition theCondition = null;
        ArrayList< ArrayList< OpenSearchCondition > > returnListList = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< OpenSearchCondition > tempQueryList = new ArrayList< OpenSearchCondition >();
        ArrayList< OpenSearchCondition > tempConditionList = new ArrayList< OpenSearchCondition >();

        for( int i = 0; i < appendListCount; i++ )
        {
            tempConditionList = appendLists.get( i );
            //System.out.println( "tempConditionList size: " + tempConditionList.size() );
            for( int r = 0; r < baseCount; r++ )
            {
                tempQueryList = new ArrayList< OpenSearchCondition > ( baseLists.get( r ) );
                //System.out.println( "tempQueryList size: " + tempQueryList.size() );
                for( OpenSearchCondition condition : tempConditionList )
                {
                    tempQueryList.add( condition );
                    //System.out.println( "tempList size after add: " + tempQueryList.size() );
                    //System.out.println( "baseLists size after add: " + baseLists.size() );
                }

                //returnListList.add( tempQueryList );
                returnListList.add( new ArrayList< OpenSearchCondition > ( tempQueryList ) );

            }
        }
           
        return returnListList;
    }

}