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
 * \file dk.dbc.opensearch.tools.ScriptsMethodsForReviewRelation
 * \brief class that contains the methods needed by the javascript invoked in
 * the ReviewRelation plugin
 */
package dk.dbc.opensearch.common.javascript;


import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import dk.dbc.opensearch.common.types.TargetFields;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ScriptMethodsForReviewRelation {

    private Logger log = Logger.getLogger( ScriptMethodsForReviewRelation.class );

    private IObjectRepository repository;
    public ScriptMethodsForReviewRelation( IObjectRepository repository ) 
    {
        this.repository = repository;
    }

    /**
     * Method exposed to the script for creating relations
     * @param object the pid of the object of the relation
     * @param relation the name of the relation to make
     * @param subject the pid of the target of the relation
     */
    public boolean createRelation( String subject, String relation, String object)
    {

        ObjectIdentifier subjectPID = new PID( subject );
	try
	{
	    repository.addUncheckedObjectRelation( subjectPID, relation, object );
        }
        catch( ObjectRepositoryException ore )
        {
            return false;
        }
	return true;

    }

    /**
     * Method exposed to the script for finding pids of object in the objectrepository
     * @param value the value to match
     * @return the pids of the objects containing the value in the specified term
     */
    public String[] getPID( String value )
    {

        log.info( String.format( "getPID called with: %s ", value ) );
        //convert field to the TargetFields type
        //create a List<Pair<TargetFields, String>> with the converted field and
        //the value
        // TargetFields targetField = (TargetFields)FedoraObjectFields.IDENTIFIER;
        TargetFields targetField = (TargetFields)FedoraObjectFields.PID;
        String searchValue = "*:" + value;
        //call the IObjectRepository.getIdentifiers method with the above values,
        //no cutIdentifier and the number of submitters in the maximumResults 
        List<Pair<TargetFields, String>> searchFields = new ArrayList<Pair<TargetFields, String>>();
        searchFields.add( new Pair<TargetFields, String>( targetField, searchValue ) );

	// \note: 10000 below is a hardcodet estimate on max amount of results:
        List<String> resultList = repository.getIdentifiers( searchFields, null, 10000 );

	// Convert the List of Strings to a String array in order to satisfy javascripts internal types:
	String[] sa = new String[resultList.size()];
	int counter = 0;
	for( String str : resultList ) 
	{
	    log.info( String.format( "returning pid: %s", str ) );
	    sa[counter++] = str;
	}
        log.info( String.format( "getPID returned %s results", counter ) );
	return sa;
	
    }

}