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


package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.types.TargetFields;


/**
 * Enum class for representing the searchable fields in a fedora
 * object repository.
 */
public enum FedoraObjectFields implements TargetFields
{
    PID( "pid" ),
    LABEL( "label" ),
    STATE( "state" ),
    OWNERID( "ownerid" ),
    CDATE( "cdate" ),
    MDATE( "mdate" ),
    TITLE( "title" ),
    CREATOR( "creator" ),
    SUBJECT( "subject" ),
    DESCRIPTION( "description" ),
    PUBLISHER( "publisher" ),
    CONTRIBUTOR( "contributor" ),
    DATE( "date" ),
    TYPE( "type" ),
    FORMAT( "format" ),
    IDENTIFIER( "identifier" ),
    SOURCE( "source" ),
    LANGUAGE( "language" ),
    RELATION( "relation" ),
    COVERAGE( "coverage" ),
    RIGHTS( "rights" ),
    DCMDATE( "dcmdate" );

    private String fieldname;


    FedoraObjectFields( String fieldname )
    {
        this.fieldname = fieldname;
    }

    
    public String fieldname()
    {
        return this.fieldname;
    }
}
