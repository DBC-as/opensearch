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
 * \file   FedoraAdministration.java
 * \brief  The class for administrating the Fedora Commons repository
 */

package dk.dbc.opensearch.common.fedora;

import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.File;

import java.util.ArrayList;
import java.util.Date;

import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.net.URL;

import javax.xml.rpc.ServiceException;
import javax.xml.parsers.ParserConfigurationException;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.types.IndexingAlias;

import dk.dbc.opensearch.xsd.DigitalObject;

import fedora.server.types.gen.MIMETypedStream;
import org.exolab.castor.xml.MarshalException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.exolab.castor.xml.ValidationException;
import java.text.ParseException;
import javax.xml.transform.TransformerException;

public class FedoraAdministration extends FedoraHandle implements IFedoraAdministration
{

    Logger log = Logger.getLogger( FedoraAdministration.class );
    //private FedoraCommunication fedoraCommunication;
    private PIDManager pidManager;

    /**
     * The constructor initalizes the super class FedoraHandle which
     * handles the initiation of the connection with the fedora base
     *
     * @throws ConfigurationException  Thrown if the FedoraHandler Couldn't be initialized. @see FedoraHandle
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     * @throws MalformedURLException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     * @throws ServiceException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     */

    public FedoraAdministration()throws ConfigurationException, IOException, MalformedURLException, ServiceException
    {
        super();
        pidManager = new PIDManager();
        log.debug( "constructed" );
    }
    /**
     * method to delete a DigitalObject for good, based on the pid
     * @param pid, the identifier of the object to be removed
     * @param force, tells whether to purge the object even if it
     * breaks dependencies to other objects
     * @throws RemoteException if something on the serverside goes wrong.
     */
    public void deleteDO( String pid, boolean force ) throws RemoteException
    {
        String logm = "";
        super.fem.purgeObject( pid, logm, force );

    }

    /**
     * method for setting the delete flag on DigitalObjects
     * @param pid, the identifier of the object to be marked as delete
     * @return true if the DigitalObject is marked
     */
    public boolean markAsDeleteDO( String pid )
    {
        return true;
    }

    /**
     * method for getting a DigitalObject in a CargoContainer based on its pid
     * @param pid, the identifier of the object to get
     * @return the CargoContainer representing the DigitalObject
     * @throws RemoteException if something on the serverside goes wrong.
     */
    public CargoContainer getDO( String pid ) throws IOException, ParserConfigurationException, RemoteException, SAXException
    {

        log.debug( String.format( "entering getDO( '%s' )", pid ) );

        MIMETypedStream ds = super.fea.getDatastreamDissemination( pid, DataStreamType.AdminData.getName(), null );
        byte[] adminStream = ds.getStream();
        log.debug( String.format( "Got adminstream from fedora == %s", new String( adminStream ) ) );

        CargoContainer cc = new CargoContainer();

        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        log.debug( String.format( "Trying to get root element from adminstream with length %s", bis.available() ) );
        Element root = XMLFileReader.getDocumentElement( new InputSource( bis ) );

        log.debug( String.format( "root element from adminstream == %s", root ) );

        //Element indexingAliasElem = (Element)root.getElementsByTagName( "indexingalias" );

        NodeList indexingAliasElem = root.getElementsByTagName( "indexingalias" );
        if( indexingAliasElem == null )
        {
            /**
             * \Todo: this if statement doesnt skip anything. What should we do? bug: 8878
             */
            log.error( String.format( "Could not get indexingalias from adminstream, skipping " ) );
        }
        log.debug( String.format( "indexingAliasElem == %s", indexingAliasElem.item(0) ) );
        String indexingAliasName = ((Element)indexingAliasElem.item( 0 )).getAttribute( "name" );
        log.debug( String.format( "Got indexingAlias = %s", indexingAliasName ) );
        //Element filePathElem = (Element)root.getElementsByTagName( "filepath" ).item( 0 );
        //String filePath = filePathElem.getAttribute( "name" );
        //cc.setFilePath( filePath );
        //log.info( String.format( "The filepath of the file to index: %s ", filePath ) );

        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = (Element)streamsNL.item(0);
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        log.debug( String.format( "Iterating streams in nodelist" ) );

        int streamNLLength = streamNL.getLength();
        for(int i = 0; i < streamNLLength; i++ )
        {
            Element stream = (Element)streamNL.item(i);
            String streamID = stream.getAttribute( "id" );
            MIMETypedStream dstream = super.fea.getDatastreamDissemination( pid, streamID, null);
            cc.add( DataStreamType.getDataStreamNameFrom( stream.getAttribute( "streamNameType" ) ),
                    stream.getAttribute( "format" ),
                    stream.getAttribute( "submitter" ),
                    stream.getAttribute( "lang" ),
                    stream.getAttribute( "mimetype" ),
                    IndexingAlias.getIndexingAlias( indexingAliasName ),
                    dstream.getStream() );
        }
        return cc;
    }

    /**
     * method for storing a DigitalObject in the Fedora base
     * @param theCC the CargoContainer to store
     * @param label, the label to put on the DigitalObject
     * @return the pid of the object in the repository, null if unsuccesfull
     */
    public String storeCC( CargoContainer theCC, String label ) throws MalformedURLException, RemoteException, IOException, SAXException, ServiceException, MarshalException, ValidationException, ParseException, ParserConfigurationException, TransformerException
    {
        log.debug( "entering storeCC" );

        String returnVal = null;
        //get a submitter from the cc
        String submitter = theCC.getCargoObject( DataStreamType.OriginalData ).getSubmitter();
        //get a pid for the object
        String pid = pidManager.getNextPID( submitter );
        byte[] foxml = FedoraTools.constructFoxml( theCC, pid, label );
        String logm = String.format( "%s inserted", label );

        String returnPid = super.fem.ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm );
        if( pid.equals( returnPid ) )
        {
            returnVal = pid;
        }

        return returnVal;

    }

    /**
     * method to retrive all DataStreams of a DataStreamType from a DigitalObject
     * @param pid, identifies the DO
     * @param streamtype, the name of the type of DataStream to get
     * @return a CargoContainer of CargoObjects each containing a DataStream,
     * is null if there are no DataStreams of the streamtype.
     */
    public CargoContainer getDataStreamsOfType( String pid, DataStreamType streamtype ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException
    {
        CargoContainer cc = new CargoContainer();
        //get the adminstream
        MIMETypedStream ds = super.fea.getDatastreamDissemination( pid,DataStreamType.AdminData.getName(), null );
        byte[] adminStream = ds.getStream();
        log.debug( String.format( "Got adminstream from fedora == %s", new String( adminStream ) ) );
        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        log.debug( String.format( "Trying to get root element from adminstream with length %s", bis.available() ) );
        Element root = XMLFileReader.getDocumentElement( new InputSource( bis ) );

        log.debug( String.format( "root element from adminstream == %s", root ) );

        //Element indexingAliasElem = (Element)root.getElementsByTagName( "indexingalias" );

        NodeList indexingAliasElem = root.getElementsByTagName( "indexingalias" );
        if( indexingAliasElem == null )
        {
            /**
             * \Todo: this if statement doesnt skip anything. What should we do? bug: 8878
             */
            System.out.println("no indexing alias");

            log.error( String.format( "Could not get indexingalias from adminstream, skipping " ) );
        }
        String indexingAliasName = ((Element)indexingAliasElem.item( 0 )).getAttribute( "name" );
        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = (Element)streamsNL.item(0);
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        log.debug( "iterating streams in nodelist to get the right streamtype" );

        int length = streamNL.getLength();
        System.out.println( String.format( "NodeList is %s long", length ) );
        for( int i = 0; i < length; i++ )
        {
            Element stream = (Element)streamNL.item(i);
            String typeOfStream = stream.getAttribute( "streamNameType" );
            System.out.println( String.format( "got streamType: %s", typeOfStream ) );
            if( typeOfStream.equals( streamtype.getName() ) )
            {

                System.out.println( "got the right streamType" );
                //build the CargoObject and add it to the list
                String streamID = stream.getAttribute( "id");
                System.out.println( String.format( "streamid: %s", streamID ) );
                MIMETypedStream dstream = super.fea.getDatastreamDissemination( pid, streamID, null );
                byte[] bytestream = dstream.getStream();

                cc.add( streamtype,
                        stream.getAttribute( "format" ),
                        stream.getAttribute( "lang" ),
                        stream.getAttribute( "submitter" ),
                        stream.getAttribute( "mimetype" ),
                        IndexingAlias.getIndexingAlias( indexingAliasName ),
                        bytestream );
            }
        }


        return cc;
    }

    //create another method for getting a datastream form a DO identified by the streamID.

    /**
     * method for getting a datastream identified by its streamID
     * @param streamID, the identifier of the datastream to be retrieved
     * @param pid, the identifier of the object to get the stream from
     * @return CargoContainer with the datastream
     */

    public CargoContainer getDataStream( String streamID, String pid ) throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException
    {
        CargoContainer cc = new CargoContainer();
        //get the adminstream
        MIMETypedStream ds = super.fea.getDatastreamDissemination( pid,DataStreamType.AdminData.getName(), null );
        byte[] adminStream = ds.getStream();
        log.debug( String.format( "Got adminstream from fedora == %s", new String( adminStream ) ) );
        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        log.debug( String.format( "Trying to get root element from adminstream with length %s", bis.available() ) );
        Element root = XMLFileReader.getDocumentElement( new InputSource( bis ) );

        log.debug( String.format( "root element from adminstream == %s", root ) );

        //Element indexingAliasElem = (Element)root.getElementsByTagName( "indexingalias" );

        NodeList indexingAliasElem = root.getElementsByTagName( "indexingalias" );
        if( indexingAliasElem == null )
        {
            /**
             * \Todo: this if statement doesnt skip anything. What should we do? bug: 8878
             */
            log.error( String.format( "Could not get indexingalias from adminstream, skipping " ) );
        }
        String indexingAliasName = ((Element)indexingAliasElem.item( 0 )).getAttribute( "name" );
        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = (Element)streamsNL.item(0);
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        log.debug( "iterating streams in nodelist to get info" );

        int length = streamNL.getLength();
        for( int i = 0; i < length; i++ )
        {
            Element stream = (Element)streamNL.item(i);
            String idOfStream = stream.getAttribute( "id" );
            if( streamID.equals( idOfStream ) )
            {
                //build the CargoObject and add it to the list
                MIMETypedStream dstream = super.fea.getDatastreamDissemination( pid, streamID, null );
                byte[] bytestream = dstream.getStream();

                cc.add( DataStreamType.getDataStreamNameFrom( stream.getAttribute( "streamNameType" ) ),
                        stream.getAttribute( "format" ),
                        stream.getAttribute( "lang" ),
                        stream.getAttribute( "submitter" ),
                        stream.getAttribute( "mimetype" ),
                        IndexingAlias.getIndexingAlias( indexingAliasName ),
                        bytestream );
            }
        }
        return cc;
    }
    /**
     * method for saving a Datastream to a DigitalObject
     * @param theFile, the file to save as a DataStream in a specified DigitalObject
     * @param sID the id of the stream, if null is given, the method returns a sID
     * @param pid, the identifier of the object to save the datastream to
     * @param label the label to give the stream
     * @param versionable, tells whether to keep track of old versions or not
     * @param overwrite, tells whether to overwrite if the datastream exists
     * @return the dataStreamID of the added stream
     */
    public String addDataStreamToObject( File theFile, String sID, String pid, String label, boolean versionable, String mimetype, boolean overwrite ) throws RemoteException, MalformedURLException
    {
        String logm = "";

        //make the file into an URL
        URL theURL = theFile.toURI().toURL();
        String[] altIDs;

        //get the adminstream and modify it...

        //addDataStream

        logm =String.format( "added %s to the digitalobject with pid: %s", theFile.getAbsolutePath(), pid );


        String returnedSID = super.fem.addDatastream( pid, sID, new String[] {}, label, versionable, mimetype, null, theURL.getPath(), "M", "A", null, null, logm );

            return returnedSID;

    }
    /**
     * method for modifying an existing dataStream in a DigitalObject
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

    public String modifyDataStream( File theFile, String sID, String pid, String label, boolean versionable, String mimetype, boolean breakDependencies ) throws RemoteException, MalformedURLException
    { 
        String logm =String.format( "modified the digitalobject with pid: %s", pid );
        String[] altIDs;
        //make the file into an URL
        URL theURL = theFile.toURI().toURL();
        super.fem.modifyDatastreamByReference( pid, sID, new String[] {}, label,  mimetype, null, theURL.getPath(), null, null, logm, breakDependencies );
        return "";

    }

    /**
     * method for storing removing a datastream form a DigitalObject in the Fedora base
     * @param pid, the indentifier of the object to remove from
     * @param streamtype, the type of the stream to remove
     * @param streamPid, the identifier of the stream to remove
     * @return true if the stream was removed
     */
    public boolean removeDataStream( String pid, DataStreamType streamtype, String streamID )
    {
        return true;
    }
}