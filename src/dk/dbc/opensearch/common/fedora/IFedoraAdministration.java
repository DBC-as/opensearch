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


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;

import org.apache.commons.configuration.ConfigurationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;


/**
 * The purpose of the FedoraAdministration API is to provide a wrapper
 * around the communication with the Fedora Commons digital
 * repository. Objects are stored using the CargoContainer datatype
 * and objects are retrieved in this form.
 * Parts of objects (Datastreams) are worked with as CargoObjects 
 */
public interface IFedoraAdministration
{    
    /**
     * method for getting an object in a CargoContainer based on its pid
     * @param pid, the identifier of the object to get
     * @return the CargoContainer representing the DigitalObject
     * @throws RemoteException if something on the serverside goes wrong.
     */
    public CargoContainer retrieveCargoContainer( String pid ) throws IOException, ParserConfigurationException, RemoteException, ServiceException, SAXException, ConfigurationException;


    /**
     * method for storing an object in the Fedora base
     * @param theCC the CargoContainer to store
     * @param label, the label to put on the object
     * @return the pid of the object in the repository, null if unsuccesfull
     * @throws XPathExpressionException 
     */
    public String storeCargoContainer( CargoContainer cargo, String submitter) throws MalformedURLException, RemoteException, ServiceException, IOException, SAXException, ServiceException, MarshalException, ValidationException, ParseException, ParserConfigurationException, TransformerException, ConfigurationException, XPathExpressionException;
    

    /**
     * method to delete an object for good, based on the pid
     * @param pid, the identifier of the object to be removed
     * @param force, tells whether to purge the object even if it
     * breaks dependencies to other objects
     * @throws RemoteException if something on the serverside goes wrong.
     * @throws IOException 
     * @throws ServiceException 
     * @throws MalformedURLException 
     */
    public void deleteObject( String pid, boolean force ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException;

   
    /**
     * method for setting the delete flag on a designated digital object
     * @param pid, the identifier of the object to be marked as delete
     * @return true if the DigitalObject is marked
     */
    public boolean markObjectAsDeleted( String pid );


    /**
     * method to retrive all DataStreams of a DataStreamType from a
     * DigitalObject. The data is returned in a CargoContainer. Note
     * that the returned CargoContainer will not be identical with the
     * CargoContainer used for initially storing the data.
     * @param pid, identifies the object
     * @param streamtype, the name of the type of DataStream to get
     * @return a CargoContainer of CargoObjects each containing a DataStream,
     * is null if there are no DataStreams of the streamtype.
     */
    public CargoContainer getDataStreamsOfType( String pid, DataStreamType streamtype ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException, ServiceException, ConfigurationException;


    /*
     * method for getting a datastream identified by its streamID
     * //todo: more information is needed on what a streamID is.
     * @param streamID, the identifier of the datastream to be retrieved
     * @param pid, the identifier of the object to get the stream from
     * @return CargoContainer with the datastream
     */
    public CargoContainer getDataStream( String pid, String streamID ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException, ServiceException, ConfigurationException;


     /**
     * method for adding a Datastream to an object
     * @param cargo, the data to be added to the object
     * @param pid, the identifier of the object to save the datastream to
     * @param versionable, tells whether to keep track of old versions or not
     * @param overwrite, tells whether to overwrite if the datastream exists
     * @return the dataStreamID of the added stream
     * @throws ServiceException 
     */
    public String addDataStreamToObject( CargoObject cargo, String pid, boolean versionable, boolean overwrite ) throws RemoteException, MalformedURLException, ParserConfigurationException, TransformerConfigurationException, TransformerException, SAXException, IOException, ConfigurationException, ServiceException;


    /**
     * method for modifying an existing dataStream in an object
     * @param cargo, the CargoObject holding the data to update the object with
     * @param sID the id of the datastream to be modified
     * @param pid the id of the object to get a datastream updated
     * @param versionable, tells whether to keep track of old version of the stream
     * @param breakDependencies tells whether to update the datastream or not
     * if the operation breaks dependencies with other objects
     * @return the checksum of the datastream...
     * @throws ServiceException 
     * @throws ConfigurationException 
     */
    public String modifyDataStream( CargoObject cargo, String sID, String pid, boolean versionable, boolean breakdependencies ) throws RemoteException, MalformedURLException, IOException, ConfigurationException, ServiceException;


    /**
     * method for removing a datastream form an object in the Fedora base
     * @param pid, the indentifier of the object to remove from
     * @param sID, the identifier of the stream to remove
     * @param breakDependencies tells whether to break data contracts/dependencies
     * @param startDate, the earlyist date to remove stream versions from, can be null
     * @param endDate, the latest date to remove stream versions to, can be null
     * @return true if the stream was removed
     * @throws ServiceException 
     * @throws ConfigurationException 
     */
    public boolean removeDataStream( String pid, String sID, String startDate, String endDate, boolean breakDependencies ) throws RemoteException, ParserConfigurationException, TransformerConfigurationException, TransformerException, IOException, SAXException, ConfigurationException, ServiceException;

    
    /**
     * method for adding a relation to an object
     * @param pid, the identifier of the object to add the realtion to
     * @param predicate, the predicate of the relation to add
     * @param targetDCIdentifier, the object to relate the object to, can be a literal
     * @param literal, true if the targetDCIdentifier is a literal
     * @param datatype, the datatype of the literal, optional
     * @return true if the relation was added
     * @throws IOException 
     * @throws ServiceException 
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    //public boolean addRelation( String pid, String predicate, String targetDCIdentifier, boolean literal, String datatype ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException;

    
    /**
     * method for getting the relationships an object has
     * @param pid, the object to get relations for
     * @param predicate, the predicate to search for, null means all
     * @return RelationshipTuple[] containing the following for each relationship found:
     * String subject, the object this method was called on
     * String predicate, 
     * String object, the target of the predicate
     * boolean isLiteral, tells if the object is a literal and not a pid
     * String datatype, tells what datatype to pass the object as if it is a literal
     * @throws IOException 
     * @throws ServiceException 
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    //public RelationshipTuple[] getRelationships( String pid, String predicate) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException; 
    
    
    /**
     * method to see if an object has a certain relationship to another object
     * Its a filtered version of the method getRelationships
     * @param subject, the pid of the object in question
     * @param predicate, the relationship in queation
     * @param target, the target of the predicate
     * @param isLiteral, true if the target is not an object in the base
     * @return true if the relationship exists
     * @throws IOException 
     * @throws ServiceException 
     * @throws MalformedURLException 
     * @throws ConfigurationException 
     */
    //public boolean hasRelationship( String subject, String predicate, String target, boolean isLiteral ) throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException;

    
    /**
     * method for finding pids of objects based on object properties
     * @param property, the property to match
     * @param operator, the operator to apply, "has","eq","lt","le","gt" and "ge" are valid
     * @param value, the value the property adheres to
     * @return an array o pids of the matching objects
     */
    public String[] findObjectPids( String property, String operator, String value ) throws RemoteException, ConfigurationException, ServiceException, MalformedURLException, IOException;
}
