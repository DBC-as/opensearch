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
package dk.dbc.opensearch.tools;


import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.TargetFields;

public class ScriptMethodsForReviewRelation {
    /**
     *
     */

    private IObjectRepository repository;

    public ScriptMethodsForReviewRelation( IObjectRepository repository ) {
        this.repository = repository;
    }

    /**
     * Method exposed to the script for making relations
     * @param object, the pid of the object of the relation
     * @param relation, the name of the relation to make
     * @param subject, the pid of the target of the relation
     */
    public boolean setRelation( String object, String relation, String subject)
    {
        //check that the relation param is valid, should be either reviewOf, hasReview
        // or hasFullText
        //convert the object String to an ObjectIdentifier
        //convert the relation String to an IPredicate
        //the 3 relations to add: hasFullText, hasReview, reviewOf
        //call the addObjectRelation method

        return true;
    }

    /**
     * Method exposed to the script for finding pids of object in the objectrepository
     * @param streamType, the type of stream to search in the objects
     * @param field, the field in the stream to examine
     * @param value, the value to match
     * @return the pid of the object containing the value in the specified term
     */
    public String getPID( String streamType, String field, String value )
    {
        //convert field to the TargetFields type
        //create a List<InputPair<TargetFields, String>> with the converted field and
        //the value
        //call the IObjectRepository.getIdentifiers method with the above values,
        //no cutIdentifier and 2 in maximumResults (return error if more than is found)
        //return the pid if 1 is found else return an empty String

        return "";
    }
}