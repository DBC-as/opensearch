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

import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.fedora.PID;
import dk.dbc.opensearch.types.IObjectIdentifier;

import org.apache.log4j.Logger;

public class JSFedoraObjectModification
{
    private Logger log = Logger.getLogger( JSFedoraObjectModification.class );
    private IObjectRepository repository;
    
    public JSFedoraObjectModification( IObjectRepository repository )
    {
        this.repository = repository;
    }
    
    /**
     * Method exposed to scripts for delete marking objects in the fcrepo
     * 
     * @param pid {@link String} the identifier of the object to be marked
     * @param logmessage {@link String} log for the obejct
     */
    public boolean deleteMarkObject( String pid, String logmessage )
    {
        boolean success = true;
        
        try
        {
        repository.deleteObject( pid, logmessage);
        }
        catch( ObjectRepositoryException e )
        {
            success = false;
        }
        return success;
    } 
}