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


package dk.dbc.opensearch.fedora;


import dk.dbc.opensearch.common.types.ITargetField;

/**
 * OpenSearchCondition is a container for a search condition
 * consisting of the three elements: Field, Operator and Value, where
 * Field is the field you want to search in, operator is the operator
 * you want to use and value is the value you wish to search for.
 * E.g. 
 * <p> 
 * title = "Lord of the Rings"
 * <p>
 * In the above condition "title" is the field, = (equal) is the
 * operator and "Lord of the Rings" is the value.
 * <p>
 * No checks are made on the contents of the value.
 */
public final class OpenSearchCondition
{

    /**
     * Operator to use in an {@link OpenSearchCondition}.
     */
    public enum Operator
    {
	/**
	 * Equality operator (=). Used to find exact values in fields.
	 */
	EQUALS, 
	/**
	 * Contains operator (~). Also known as the has-operator. Used to find values contained in fields.
	 */
	CONTAINS,
	GREATER_THAN,
	GREATER_OR_EQUAL,
	LESS_THAN,
	LESS_OR_EQUAL;
    }
    
    private final ITargetField field;
    private final Operator operator;
    private final String value;

    /**
     * Constructs an OpenSearchCondition.
     *
     * @param field The field to search in.
     * @param operator The operator to search with.
     * @param value The value to search for.
     *
     * @throws IllegalArgumentException if either of the three arguments are null.
     */ 
    public OpenSearchCondition( ITargetField field, Operator operator, String value ) throws IllegalArgumentException
    {
	if ( field == null ) 
	{
	    throw new IllegalArgumentException( "Argument \"field\" cannot be null" );
	}
	if ( operator == null ) 
	{
	    throw new IllegalArgumentException( "Argument \"operator\" cannot be null" );
	}
	if ( value == null ) 
	{
	    throw new IllegalArgumentException( "Argument \"value\" cannot be null" );
	}

	this.field = field;
	this.operator = operator;
	this.value = value;
    }

    public ITargetField getField()
    {
	return this.field;
    }

    public Operator getOperator()
    {
	return this.operator;
    }

    public String getValue()
    {
	return this.value;
    }

   
}