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
 * \file   IFedoraAdministration.java
 * \brief  interface for interacting with the Fedora Commons repository
 */
package dk.dbc.opensearch.common.fedora;


import java.util.ArrayList;
import java.util.Date;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;


import dk.dbc.opensearch.xsd.DigitalObject;
import java.io.IOException;
import java.io.File;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.rmi.RemoteException;
import java.net.MalformedURLException;
import org.exolab.castor.xml.MarshalException;
import javax.xml.rpc.ServiceException;
import org.exolab.castor.xml.ValidationException;
import java.text.ParseException;
import javax.xml.transform.TransformerException;

public interface IFedoraAdministration
{
    //public IFedoraAdministration();
    
    /**
     * method to delete a DigitalObject for good, based on the pid
     * @param pid, the identifier of the object to be removed
     * @param force, tells whether to purge the object even if it 
     * breaks dependencies to other objects
     */
    public void deleteDO( String pid, boolean force ) throws RemoteException;

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
    public CargoContainer getDO( String pid ) throws IOException, ParserConfigurationException, RemoteException, SAXException;

    /**
     * method for storing a DigitalObject in the Fedora base
     * @param theCC the CargoContainer to store
     * @param label, the label to put on the DigitalObject
     * @return the pid of the object in the repository, null if unsuccesfull
     */
    public String storeCC( CargoContainer theCC, String label )throws MalformedURLException, RemoteException, IOException, SAXException, MarshalException, ServiceException, ValidationException, ParseException, ParserConfigurationException, TransformerException;
    /**
     * method to retrive all DataStreams of a DataStreamType from a DigitalObject
     * @param pid, identifies the DO
     * @param streamtype, the name of type of the DataStream to get
     * @return anArrayList of CargoObjects eachcontaining a DataStream,
     * is empty if there are no DataStreams of the streamtype
     */
    public CargoContainer getDataStreamsOfType( String pid, DataStreamType streamtype ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException;

    /**
     * method for getting a datastream identified by its streamID
     * @param streamID, the identifier of the datastream to be retrieved
     * @param pid, the identifier of the object to get the stream from
     * @return CargoContainer with the datastream
     */
    public CargoContainer getDataStream( String streamID, String pid ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException;

    /**
     * method for saving a Datastream to a DigitalObject
     * @param stream, the DataStream to save to a DigitalObject
     * @param pid, the identifier of the object to save the dastream to
     * @param overwrite, tells whether to overwrite if there is a 
     * DataStream of the same type present
     * @return true if the operation succeded
     */
    public String addDataStreamToObject( File theFile, String sID, String pid, String label, boolean versionable, String mimetype, boolean overwrite )throws RemoteException, MalformedURLException;

    /**
     * method for modifying an existing datastream in a DigitalObject
     * @param theFile, the file to be added as a stream to the specified object
     * @param sID the id of the datastream to be modified
     * @param pid the id of the object to get a datastream updated
     * @param the label of the updated stream
     * @param versionable, tells whether to keep track of old version of the stream
     * @param mimetype, the mimetype of the stream
     * @param breakDependencies tells whether to update the datastream or not
     * if the operation breaks dependencies with other objects
     * @return the checksum of the datastream...
     */

    public String modifyDataStream( File theFile, String sID, String pid, String label, boolean versionable, String mimetype, boolean breakDependencies ) throws RemoteException, MalformedURLException;

    /**
     * method for storing removing a datastream form a DigitalObject in the Fedora base
     * @param pid, the indentifier of the object to remove from
     * @param streamtype, the type of the stream to remove
     * @param streamPid, the identifier of the stream to remove
     * @return true if the stream was removed
     */
    public boolean removeDataStream( String pid, DataStreamType streamtype, String streamID );
}