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

import dk.dbc.opensearch.common.types.ObjectIdentifier;


/**
 *
 */
public class PID implements ObjectIdentifier
{

    private String id;

    public PID( String identifier )
    {
        if( identifier.length() > 64 )
        {
            throw new IllegalArgumentException( "The identifier for fedora objects cannot be longer than 25 characters" );
        }
        else if( identifier.isEmpty() )
        {
            throw new IllegalArgumentException( "The identifier for fedora objects cannot be empty" );
        }
        else if( identifier.indexOf( ":" ) < 0 )
        {
            throw new IllegalArgumentException( "The identifier for fedora objects must satisfy the pattern \"([A-Za-z0-9]|-|\\.)+:(([A-Za-z0-9])|-|\\.|~|_|(%[0-9A-F]{2}))+\"" );
        }

        this.id = identifier;
    }

    public String getIdentifier()
    {
        return this.id;
    }


    public String getNamespace()
    {
        char splitter = ':';
        int index = this.id.indexOf( splitter );
        return this.id.substring( 0, index );
    }

}
