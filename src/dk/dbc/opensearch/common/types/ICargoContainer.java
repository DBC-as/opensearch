package dk.dbc.opensearch.common.types;

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

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.IOException;

import java.util.List;

/**
 * \brief The CargoContainer interface defines the interactions that
 * can be made with a CargoContainer. The CargoContainer holds zero or
 * more CargoObjects and the methods defined in the CargoContainer
 * aims to provide simple and uniform access to the data contained in
 * the CargoContainer.
 */

public interface ICargoContainer
{


    /** 
     * Adds a 'datastream' to the CargoContainer; a datastream is any
     * kind of data which, for the duration of the CargoContainer
     * object to which it is attached will be treated as binary
     * data. To ensure (and guarantee) that the program does not
     * meddle with the data, it is added as a byte[] and returned as a
     * byte[]. No attempts are made to interpret the contained data at
     * any times.
     *      
     * The returned id uniquely identifies the data and makes it
     * available as a CargoObject structure that encapsulates the
     * information given to this method through the getCargoObject()
     * and getCargoObjects().
     * 
     * 
     * @param dataStreamName 
     * @param format 
     * @param submitter 
     * @param language 
     * @param mimetype 
     * @param data 
     * 
     * @return a unique id identifying the submitted data
     */
    public int add( DataStreamType dataStreamName, 
                     String format, 
                     String submitter, 
                     String language, 
                     String mimetype, 
                     byte[] data ) 
        throws IOException;


    /** 
     * Gets a specific CargoObject based on the id that was returned
     * from the add method. Please note, that if the id does not map
     * to a CargoObject, the method returns null.
     * 
     * @param id The id returned from the add method
     * 
     * @return CargoObject or a null CargoObject if id isn't found
     */    
    public CargoObject getCargoObject( int id );


    /** 
     * Based on the DataStreamType, the first CargoObject matching the
     * type is returned. This method should only be used, if you know
     * that there is exactly one CargoObject with the type in the
     * CargoContainer. If there are more, or if you are unsure, please
     * use the getCargoObjects() method instead. Use the
     * getCargoObjectCount() method to find out how many CargoObjects
     * matching a specific DataStreamType that resides in the
     * CargoContainer.
     * 
     * @param type The DataStreamType to find the CargoObject from
     * 
     * @return The first CargoObject that matches the DataStreamType
     */
    public CargoObject getCargoObject( DataStreamType type);


    /** 
     * Returns a List of CargoObjects that matches the
     * DataStreamType. If you know that there are only one CargoObject
     * matching the DataStreamType, use getCargoObject() instead. If
     * no CargoObjects match the DataStreamType, this method returns
     * null.
     * 
     * @param type The DataStreamType to find the CargoObject from
     * 
     * @return a List of CargoObjects or a null List if none were
     * found
     */
    public List<CargoObject> getCargoObjects( DataStreamType type );


    /** 
     * Returns a List of all the CargoObjects that are contained in
     * the CargoContainer. If no CargoObjects are found, a null List
     * object is returned
     * 
     * @return a List of all CargoObjects from the CargoContainer or a
     * null List object if none are found
     */
    public List<CargoObject> getCargoObjects();


    /** 
     * Get the count of all CargoObjects that have type as their
     * DataStreamType.
     * 
     * @param type The DataStreamType to match in the CargoObjects
     * 
     * @return the count of CargoObjects matching the type
     */
    public int getCargoObjectCount( DataStreamType type );


    /** 
     * Get the total count of CargoObjects in the CargoContainer
     * 
     * @return the count of CargoObjects matching the type
     */
    public int getCargoObjectCount();


    
}