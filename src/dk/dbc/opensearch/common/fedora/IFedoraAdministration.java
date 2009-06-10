/**
 * @file   IFedoraAdministration.java
 * @brief  interface for interacting with the Fedora Commons repository
 */

package dk.dbc.opensearch.common.fedora;
/**
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


import java.util.ArrayList;
import java.util.Date;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;


import dk.dbc.opensearch.xsd.DigitalObject;


public interface IFedoraAdministration
{
    /**
     * method to delete a DigitalObject for good, based on the pid
     * @param pid, the identifier of the object to be removed
     * @return true if the object was removed
     */
    public boolean deleteDO( String pid );

     /**
     * method for setting the delete flag on DigitalObjects
     * @param pid, the identifier of the object to be marked as delete 
     * @return true if the DigitalObject is marked
     */
    public boolean markAsDeleteDO( String pid );

    /**
     * method for getting a DigitalObject in a CargoContainer based on its pid
     * @param pid, the identifier of the object to get
     * @return the CargoContainer representing the DigitalObject
     */
    public CargoContainer getDO( String pid );

    /**
     * method for storing a DigitalObject in the Fedora base
     * @param theCC the CargoContainer to store
     * @return true if the CargoContainer is stored
     */
    public boolean storeCC( CargoContainer theCC );
    /**
     * method to retrive a DataStream from a DigitalObject
     * @param pid, identifies the DO
     * @param streamtype, the name of the DataStream to get
     * @return a CargoObject containing a DataStream
     */
    public CargoObject getDataStream( String pid, DataStreamType streamtype );

    /**
     * method for saving a Datastream to a DigitalObject
     * @param stream, the DataStream to save to a DigitalObject
     * @param pid, the identifier of the object to save the dastream to
     * @param overwrite, tells whether to overwrite if there is a 
     * DataStream of the same type present
     * @return true if the operation succeded
     */
    public boolean saveDataStream( CargoObject stream, String pid, boolean overwrite );

    /**
     * method for storing removing a datastream form a DigitalObject in the Fedora base
     * @param pid, the indentifier of the object to remove from
     * @param streamtype, the type of the stream to remove
     * @param streamPid, the identifier of the stream to remove
     * @return true if the stream was removed
     */
    public boolean removeDataStream( String pid, DataStreamType streamtype, String stramPid );
}