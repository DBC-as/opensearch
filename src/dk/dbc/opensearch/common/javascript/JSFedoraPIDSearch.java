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
 * \brief
 */

package dk.dbc.opensearch.common.javascript;


import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.OpenSearchCondition;
import dk.dbc.opensearch.common.types.ITargetField;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.log4j.Logger;


public class JSFedoraPIDSearch
{
    private Logger log = Logger.getLogger( JSFedoraPIDSearch.class );
    private IObjectRepository repository;


    public JSFedoraPIDSearch( IObjectRepository repository ) 
    {
        this.repository = repository;
    }


    public String[] pid( String searchValue )
    {
        log.info( String.format( "PID called with: %s ", searchValue ) );
	if ( contains_wildcard( searchValue ) )
	{
	    // Use HAS-operator:
	    return single_field_search( (ITargetField)FedoraObjectFields.PID, OpenSearchCondition.Operator.CONTAINS, searchValue );
	}
	else
	{
	    // default: EQUALS-operator
	    return single_field_search( (ITargetField)FedoraObjectFields.PID, searchValue );
	}
    }
    public String[] label( String searchValue )
    {
        log.info( String.format( "LABEL called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.LABEL, searchValue );
    }
    @Deprecated
    public String[] state( String searchValue )
    {
        log.info( String.format( "STATE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.STATE, searchValue );
    }
    public String[] ownerid( String searchValue )
    {
        log.info( String.format( "OWNERID called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.OWNERID, searchValue );
    }
    public String[] cdate( String searchValue )
    {
        log.info( String.format( "CDATE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.CDATE, searchValue );
    }
    public String[] mdate( String searchValue )
    {
        log.info( String.format( "MDATE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.MDATE, searchValue );
    }
    public String[] title( String searchValue )
    {
        log.info( String.format( "TITLE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.TITLE, searchValue );
    }
    public String[] creator( String searchValue )
    {
        log.info( String.format( "CREATOR called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.CREATOR, searchValue );
    }
    public String[] subject( String searchValue )
    {
        log.info( String.format( "SUBJECT called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.SUBJECT, searchValue );
    }
    public String[] description( String searchValue )
    {
        log.info( String.format( "DESCRIPTION called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.DESCRIPTION, searchValue );
    }
    public String[] publisher( String searchValue )
    {
        log.info( String.format( "PUBLISHER called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.PUBLISHER, searchValue );
    }
    public String[] contributor( String searchValue )
    {
        log.info( String.format( "CONTRIBUTOR called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.CONTRIBUTOR, searchValue );
    }
    public String[] date( String searchValue )
    {
        log.info( String.format( "DATE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.DATE, searchValue );
    }
    public String[] type( String searchValue )
    {
        log.info( String.format( "TYPE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.TYPE, searchValue );
    }
    public String[] format( String searchValue )
    {
        log.info( String.format( "FORMAT called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.FORMAT, searchValue );
    }
    public String[] identifier( String searchValue )
    {
        log.info( String.format( "IDENTIFIER called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.IDENTIFIER, searchValue );
    }
    public String[] source( String searchValue )
    {
        log.info( String.format( "SOURCE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.SOURCE, searchValue );
    }
    public String[] language( String searchValue )
    {
        log.info( String.format( "LANGUAGE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.LANGUAGE, searchValue );
    }
    public String[] relation( String searchValue )
    {
        log.info( String.format( "RELATION called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.RELATION, searchValue );
    }
    public String[] coverage( String searchValue )
    {
        log.info( String.format( "COVERAGE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.COVERAGE, searchValue );
    }
    public String[] rights( String searchValue )
    {
        log.info( String.format( "RIGHTS called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.RIGHTS, searchValue );
    }
    public String[] dcmdate( String searchValue )
    {
        log.info( String.format( "DCMDATE called with: %s ", searchValue ) );
	return single_field_search( (ITargetField)FedoraObjectFields.DCMDATE, searchValue );
    }


    /**
     *  Given a {@code targetField} an {@code operator} and a {@code searchValue} this functions performs
     *  a search and returns the matching PIDs.
     *
     *  @param targetField the field in which to search. 
     *  @param operator the operator to use for comparison.
     *  @param searchValue the value to search for in the {@code targetField}.
     *
     *  @return An array of {@link String}s containg PIDs matching the search-query.
     */
    private String[] single_field_search( ITargetField targetField, OpenSearchCondition.Operator operator,  String searchValue)
    {
        log.info( String.format( "Entering with targetfield=[%s] operator=[%s] value=[%s]", targetField, operator, searchValue ) );
	
        //call the IObjectRepository.getIdentifiersByState method with the above values,
        //no cutIdentifier and the number of submitters in the maximumResults 
        OpenSearchCondition condition = new OpenSearchCondition( targetField, operator, searchValue );
        List< OpenSearchCondition > conditions = new ArrayList< OpenSearchCondition >(1);
        conditions.add( condition );

        Set< String > undeletedStates = new HashSet< String >();
        undeletedStates.add("I");
        undeletedStates.add("A");

        // \note: 10000 below is a hardcodet estimate on max amount of results:
        List< String > resultList = repository.getIdentifiersByState( conditions, 10000, undeletedStates );

        // Convert the List of Strings to a String array in order to satisfy javascripts internal types:
        String[] sa = new String[resultList.size()];
        int counter = 0;
        for( String str : resultList ) 
        {
            log.info( String.format( "returning pid: %s", str ) );
            sa[counter++] = str;
	    }
        log.info( String.format( "returned %s results", counter ) );

	    return sa;	
    }

    /**
     * A wrapper for {@link single_field_search( ITargetField, OpenSearchCondition.Operator, String)},
     * forcing the operator to {@link OpenSearchCondition.Operator.EQUALS}.
     */
    private String[] single_field_search( ITargetField targetField, String searchValue )
    {
	// Add default operator EQUALS:
	return single_field_search( targetField, OpenSearchCondition.Operator.EQUALS, searchValue );
    }


    /**
     *  Tests whether a {@link String} contains one of the two wildcards "*"
     *  or "?".  There is no possibility for escaping the two
     *  wildcards.
     *
     * @param s A {@link String}, not null.
     *
     * @return true if the {@link String} contains either of the wildcards. False otherwise.
     *
     * @throws IllegalArgumentException if the {@link String} is null.
     */
    private boolean contains_wildcard( String s )
    {
	if ( s == null )
	{
	    throw new IllegalArgumentException( "The input String can not be null." );
	}

	// test for wildcard "*":
	boolean contains = s.contains("*");

	// If no "*" found, test for wildcard "?"
	if ( !contains )
	{
	    contains = s.contains("?");
	}

	return contains;
    }


}
