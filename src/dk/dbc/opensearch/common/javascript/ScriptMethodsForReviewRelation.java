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
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.TargetFields;
import dk.dbc.opensearch.common.metadata.IPredicate;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import dk.dbc.opensearch.common.types.InputPair;

import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public class ScriptMethodsForReviewRelation {

    private Logger log = Logger.getLogger( ScriptMethodsForReviewRelation.class );

    private IObjectRepository repository;
    private CargoContainer cc;
    private String DCRelation;

    public ScriptMethodsForReviewRelation( IObjectRepository repository ) {
        this.repository = repository;
        DCRelation = "";
    }

    /**
     * Method exposed to the script for making relations
     * @param object, the pid of the object of the relation
     * @param relation, the name of the relation to make
     * @param subject, the pid of the target of the relation
     */
    public boolean setRelation( String subject, String relation, String object)
    {
        //convert the relation String to an IPredicate/DBCBIB
        //check that the relation param is valid, should be either reviewOf, hasReview
        // or hasFullText
        IPredicate predicate;

        //Find another way to make the enums
        if( relation.equals( "reviewOf" ) )
        {
            predicate = (IPredicate)DBCBIB.REVIEW_OF;
        }
        else
        {
            if( relation.equals( "hasReview" ) )
            {
                predicate = (IPredicate)DBCBIB.HAS_REVIEW;
            }
            else
            {
                return false;
            }
        }
        //convert the object String to an ObjectIdentifier
        ObjectIdentifier subjectPID = new PID( subject );

        //call the addObjectRelation method
        try
        {
            repository.addObjectRelation( subjectPID, predicate, object );
        }
        catch( ObjectRepositoryException ore )
        {
            return false;
        }
        return true;
    }

    /**
     * Method exposed to the script for finding pids of object in the objectrepository
     * @param streamType, the type of stream to search in the objects
     * @param field, the field in the stream to examine
     * @param value, the value to match
     * @return the pid of the object containing the value in the specified term
     */
    public String getPID( String value )
    {
        //convert field to the TargetFields type
        //create a List<InputPair<TargetFields, String>> with the converted field and
        //the value
        TargetFields targetField = (TargetFields)FedoraObjectFields.IDENTIFIER;

        //call the IObjectRepository.getIdentifiers method with the above values,
        //no cutIdentifier and the number of submitters in the maximumResults 
        List<InputPair<TargetFields, String>> searchFields = new ArrayList<InputPair<TargetFields, String>>();
        searchFields.add( new InputPair<TargetFields, String>( targetField, value ) );

        List<String> resultList = repository.getIdentifiers( searchFields, value, 2 );

        //return the pid if 1 is found else return an empty String
        if( resultList.isEmpty() )
        {
            return "";
        }
        return resultList.get( 0 );
//         System.out.println( "bring deres klæder i orden hr " +value+ "!" );
//         return "";
    }

    /**
     * Method to set the cd.relation on the dc-stream of the cargocontainer 
     * @param value, the value to set in dc.relation
     */
    public void setDCRelation( String value )
    {
        DCRelation = value;
    }

    /**
     * Method for getting the value the javascript has set as relation
     * @return the value of the DCRelation
     */
    public String getDCRelation()
    {
        return DCRelation;
    }
}