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
 * \brief functionality for javascripts to make cql-search 
 * statements in searches
 */

package dk.dbc.opensearch.javascript;

import dk.dbc.opensearch.fedora.FcrepoReader;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.helpers.ConvertCQLToFedoraConditions;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.log4j.Logger;


public class JSFedoraCQLSearch
{

    private final static Logger log = Logger.getLogger( JSFedoraCQLSearch.class );
    private final FcrepoReader reader;

    public JSFedoraCQLSearch( FcrepoReader reader )
    {
        this.reader = reader;
    }
    
    /**
     * Method that takes a query in a subset of CQL (AND, OR and parantheses are allowed) 
     * transforms it into a number of AND searches for the Fedora repository, executes 
     * the searches and merges the results into a union.
     * @param query {@code String} the cql query to be transformed and executed
     * @return {@code String[]} the pids of the objects matching the search
     */
    public String[] search( String query )
    {
        ArrayList< List< String > > resultLists = new ArrayList< List< String > >();
        query = query.trim();
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > queryList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        for( ArrayList< OpenSearchCondition> condList : queryList )
        {
            resultLists.add( doSearch( condList ) );
        }
        
        return mergeSearchResult( resultLists );
    }


    /**
     * merges strings from a list og lists into a {@code String} array 
     * with unique members
     * @param searchResult {@code ArrayList } of {@code List} of {@code String}, the 
     * container to be merged to an array of unique lists
     * @return {@code String[]} of unique Strings from the container
     */
    private String[] mergeSearchResult( ArrayList< List< String > > searchResults )
    {
        HashSet< String > resultSet = new HashSet< String >();

        for( List< String > results : searchResults )
        {
            for( String pid : results )
            {
                resultSet.add( pid );
            }
        }
        
        String[] a = {};
        return resultSet.toArray( a );
    }
    
    /**
     * searches the repository
     * @param conditionList {@code ArrayList} of {@link OpenSearchCondition} 
     * the conditions for the search
     * @return {@code List} of {@code String} where each {@code String} is a pid 
     * representing an object in the repository
     */
    private List< String > doSearch( ArrayList< OpenSearchCondition > conditionList )
    {
        HashSet< String > states = new HashSet< String >();
        //we dont wont delete marked objects in the result set, only actice and inactive
        states.add( "I" );
        states.add( "A" );
        
        return reader.getIdentifiersByState( conditionList, 10000, states );
    }
}