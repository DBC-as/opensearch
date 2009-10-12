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


package dk.dbc.opensearch.common.metadata;

/**
 *
 */
public enum DublinCoreElement {
    ELEMENT_TITLE( "title"),
    ELEMENT_CREATOR( "creator"),
    ELEMENT_SUBJECT( "subject"),
    ELEMENT_DESCRIPTION( "description"),
    ELEMENT_PUBLISHER( "publisher"),
    ELEMENT_CONTRIBUTOR( "contributor"),
    ELEMENT_DATE( "date"),
    ELEMENT_TYPE( "type"),
    ELEMENT_FORMAT( "format"),
    ELEMENT_IDENTIFIER( "identifier"),
    ELEMENT_SOURCE( "source"),
    ELEMENT_LANGUAGE( "language"),
    ELEMENT_RELATION( "relation"),
    ELEMENT_COVERAGE( "coverage"),
    ELEMENT_RIGHTS( "rights");

    private String localname;
    DublinCoreElement( String localName )
    {
        this.localname = localName;
    }
    public String localName()
    {
        return this.localname;
    }
    public static boolean hasLocalName( String name )
    {
        for( DublinCoreElement dcee: DublinCoreElement.values() )
        {
            if( dcee.localName().equals( name ) ){
                return true;
            }
        }
        return false;
    }
    public static DublinCoreElement fromString( String localName )
    {
        if( DublinCoreElement.hasLocalName( localName ) )
        {
            return DublinCoreElement.valueOf( "ELEMENT_"+localName.toUpperCase() );
        }
        throw new IllegalArgumentException( String.format( "No enum value %s", "ELEMENT_"+localName.toUpperCase() ) );
    }
}
