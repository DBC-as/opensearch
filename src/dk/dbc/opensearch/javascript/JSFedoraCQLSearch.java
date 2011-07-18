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
    
    /**
     * Method that takes a query in a subset of CQL (AND, OR and parantheses are allowed) 
     * transforms it into a number of AND searches for the Fedora repository, executes 
     * the searches and merges the results into a union.
     * @param query {@code String} the cql query to be transformed and executed
     * @return {@code String[]} the pids of the objects matching the search
     */
    public String[] search( String query )
    {
        ArrayList< String[] > resultList = new ArrayList< String[] >();
        query = query.trim();
        String[] queryParts = query.split( " " );
        ArrayList< ArrayList< OpenSearchCondition > > queryList = ConvertCQLToFedoraConditions.searchTransformer( queryParts );
        
        for( ArrayList< OpenSearchCondition> condList : queryList )
        {
            resultList.add( doSearch( condList ) );
        }
        
        return mergeSearchResult( resultList );
    }

    private String[] mergeSearchResult( ArrayList< String[] > searchResults )
    {
        HashSet< String > resultSet = new HashSet< String >();

        for( String[] result : searchResults )
        {
            for( String pid : result )
            {
                resultSet.add( pid );
            }
        }
        
        return ( String[] )resultSet.toArray();
    }
    
    private String[] doSearch( ArrayList< OpenSearchCondition > conditionList )
    {
        return null;
    }
}