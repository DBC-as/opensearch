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
 *
 */
package dk.dbc.opensearch.javascript;


import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;

import org.apache.log4j.Logger;


public class JSRelationFunctions
{
    private Logger log = Logger.getLogger( JSRelationFunctions.class );

    private FcrepoModifier modifier;


    public JSRelationFunctions( FcrepoModifier modifier )
    {
        this.modifier = modifier;
    }


    /**
     * Method exposed to the script for creating relations
     * 
     * @param object the pid of the object of the relation
     * @param relation the name of the relation to make
     * @param subject the pid of the target of the relation
     * 
     * @return true if the relation is created, false otherwise
     */
    public boolean createRelation( String subject, String relation, String object)
    {
        try
        {
            modifier.addUncheckedObjectRelation( subject, relation, object );
        }
        catch( ObjectRepositoryException ore )
        {
            return false;
        }

        return true;
    }
}