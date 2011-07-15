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
 * \file
 * \brief this class exposes functionality to javascripts that takes
 * a cql-search statement and transforms it into a number of searches in
 * the object repository and returns the joined members of the
 * searchresults to the script
 */

package dk.dbc.opensearch.javascript;

import dk.dbc.opensearch.fedora.FedoraObjectFields;
import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.helpers.ConvertCQLToFedoraConditions;
import dk.dbc.opensearch.types.ITargetField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


public class JSFedoraCQLSearch
{

    private Logger log = Logger.getLogger( JSFedoraCQLSearch.class );
    private IObjectRepository repository;

    public JSFedoraCQLSearch( IObjectRepository repository )
    {
        this.repository = repository;
    }

    ArrayList< ArrayList< OpenSearchCondition > > searchTransformer( String[] theQuery )
    {
        ArrayList< ArrayList< OpenSearchCondition > > queries = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< ArrayList< OpenSearchCondition > > currentConditionLists = new ArrayList< ArrayList< OpenSearchCondition > >();
        ArrayList< OpenSearchCondition > tempConditionList = new ArrayList< OpenSearchCondition >();
        //Array< String > currentConditionStrings = new ArrayList< String >();
        int queryCount = 0;
        int currentCount = 0;
        String inHand = null;
        int next = 0;
        String part = null;

        int queryLength = theQuery.length;

        //lav en statemachine, der producerer et antal søgninger
        //vi starter med et field, derefter en operator, derefter en value,
        //derefter enten AND eller OR eller intet.
        //paranteser kan optræde før field og AND og OR og efter value
        //alt mellem to paranteser behandles som en sub-query og
        //vil blive sendt som en ny query til metoden (uden paranteser om)
        //For hvert elem der returneres i det rekursive kald skal queries
        //have sit originale indhold kopieret. Alle returnerede elem
        //behandles som hvis de lå i currentConditions og man stod med AND.
        //queryCount holder styr på hvor mange elementer der var i
        //queries ved sidste "AND skrivning" og afgør hvor mange gange
        //hvert elem fra currentConditions skal skrives til queries ved AND.
        //Når man har et OR skal indholdet af queries dubleres
        //og den condition man har i hånden skrives til currentConditions.
        //Når et AND findes skal alle currentConditions kopieres ned i
        //queries således at der dannes unikke statements. queryCount
        //opdateres nu til at være antallet af elems i queries


        while( next < queryLength )
        {
            part = theQuery[ next ];

            if( part.equals( "AND" ) )
            {
                //if we have a condition in inHand we transform it to a condition list
                if( inHand != null )
                {
                    currentConditionLists.add( ConvertCQLToFedoraConditions.transformConditionStringToConditionList ( inHand ) );
                    inHand = null;
                }

                //write all lists of conditions to all queries to get unique lists of conditions
                queries = ConvertCQLToFedoraConditions.makeListPermutations( queries, currentConditionLists );

                //reset the list of list of conditions
                currentConditionLists.clear();

                next++;
            }

            if( part.equals( "OR" ) )
            {
                //if we have a condition in inHand we transform it to a condition list
                if( inHand != null )
                {
                    currentConditionLists.add( ConvertCQLToFedoraConditions.transformConditionStringToConditionList( inHand ) );
                    inHand = null;
                }

                next++;
            }

            if( part.equals( "(" ) )
            {
                //call method that gets the subquery between the parantheses
                String[] subQuery = ConvertCQLToFedoraConditions.getQueryInParantheses( next, theQuery );

                //increase next with the length of subQuery + 1, we need to get past the right paranthese
                next = next + subQuery.length + 1;

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
        queries = ConvertCQLToFedoraConditions.makeListPermutations( queries, currentConditionLists );

        return queries;
    }
}