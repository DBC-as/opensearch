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


import java.io.IOException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * FedoraCommunication has methods to facilitate storing and
 * retrieving of CargoContainers in the fedora Repository
 */
public class FedoraCommunication implements IFedoraCommunication 
{

    private static Logger log = Logger.getLogger( FedoraCommunication.class );

    /**
     * The constructor initalizes the super class FedoraHandle which
     * handles the initiation of the connection with the fedora base
     *
     * @throws ConfigurationException  Thrown if the FedoraHandler Couldn't be initialized. @see FedoraHandle
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     * @throws MalformedURLException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     * @throws ServiceException Thrown if the FedoraHandler Couldn't be initialized properly. @see FedoraHandle
     */
    public FedoraCommunication()
    {
        log.debug( "FedoraCommunication constructor() called" );
    }

    /**
     * storeContainer Stores the cargoContainer in the repository, and
     * returns a pair, where the first element is the fedoraPid, and
     * the second is a estimate for how long before the data in the
     * container is searchable.
     *
     * @param cc The cargoContainer to store in the repository
     * @param DatadockJob Contains the fedoraPid and other informnation about how to store the container
     * @param queue the queue to push a new indexable job to, corresponding to the stored container
     * @param estimate The estimate class used to retrieve a estimate before data in container is indexed
     *
     * @return A pair, where the first element is the fedoraPid, and the second element is the retrieved estimate.
     *
     * @throws ClassNotFoundException if the database could not be initialised in the Estimation class \see dk.dbc.opensearch.tools.Estimate
     * @throws ConfigurationException if the FedoraHandler could not be initialized. \see dk.dbc.opensearch.tools.FedoraHandler
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. \see FedoraHandle.
     * @throws MarshalException Thrown if something went wrong during the marshalling of the cargoContainer.
     * @throws ParseException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws ParserConfigurationException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws SAXException  Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws SQLException if something went wrong communicating with the database, either through the queue or estimate. \IProcessqueue, \see IEstimate.
     * @throws RemoteException Thrown if the fedora repository is unreachable
     * @throws TransformerException Thrown if the construction of the Foxml went wrong. \see FedoraTools
     * @throws ValidationExceptiom Thrown if the construction of the Foxml went wrong. \see FedoraTools
     */
    /*@Deprecated
    public InputPair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException, ConfigurationException, ServiceException
    {
        log.trace( "Entering storeContainer(CargoContainer, datadockJob, queue, estimate)" );

        if( cc.getCargoObjectCount() == 0 ) {
            log.error( String.format( "No data in CargoContainer, refusing to store nothing" ) );
            throw new IllegalStateException( String.format( "No data in CargoContainer, refusing to store nothing" ) );
        } 

        // obtain mimetype and length from CargoContainer
        String mimeType = null;
        long length = 0;
        for( CargoObject co : cc.getCargoObjects() )
        {
            if( co.getDataStreamType() == DataStreamType.OriginalData )
            {
                mimeType = co.getMimeType();
            }
            
            length += co.getContentLength();
        }
      
        // Store the CargoContainer in the fedora repository
        byte[] foxml = FedoraTools.constructFoxml( cc, datadockJob.getPID(), datadockJob.getFormat() );
        String logm = String.format( "%s inserted", datadockJob.getFormat() );

        String pid = FedoraHandle.getInstance().getAPIM().ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm);
        log.info( String.format( "Submitted data, returning pid %s", pid ) );

        // push to processqueue job to processqueue and get estimate
        queue.push( pid );
        Float est = estimate.getEstimate( mimeType, length );
        log.debug( String.format( "Got estimate of %s", est ) );
        
        return new InputPair<String, Float>( pid, est );
    }*/


    /*@Deprecated
    public static synchronized String storeContainer( CargoContainer cargo, String submitter, String format )throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException, ServiceException, ConfigurationException
    {
        log.trace( "Entering storeContainer( CargoContainer )" );

        if( cargo.getCargoObjectCount() == 0 ) 
        {
            log.error( String.format( "No data in CargoContainer, refusing to store nothing" ) );
            throw new IllegalStateException( String.format( "No data in CargoContainer, refusing to store nothing" ) );
        } 

        // obtain mimetype and length from CargoContainer
        / *String mimeType = null;
        String format = null;
        String submitter = null;

        long length = 0;
        for( CargoObject co : cargo.getCargoObjects() )
        {
            if( co.getDataStreamType() == DataStreamType.OriginalData )
            {
                mimeType = co.getMimeType();
                format = co.getFormat();
                submitter = co.getSubmitter();
            }
            
            length += co.getContentLength();
        }* /
      
        // Store the CargoContainer in the fedora repository
        byte[] foxml = FedoraTools.constructFoxml( cargo, PIDManager.getInstance().getNextPID( submitter ), format );
        String logm = String.format( "%s inserted", format );

        String pid = FedoraHandle.getInstance().getAPIM().ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm);
        log.info( String.format( "Submitted data, returning pid %s", pid ) );

        return pid;
    }*/
    

    /**
     * The retrieveContainer method retrieves a digital obejct
     * matching the fedoraPid, and assembles the corresponding
     * CargoContainer.
     *
     * @param fedoraPid the Pid of the digital Object to retrieve.
     *
     * @return The retrieved cargoContainer.
     *
     * @throws IOException Thrown if the FedoraHandler Couldn't be initialized properly. \see FedoraHandle.
     * @throws ParserConfigurationException Thrown if the construction of the xml went wrong. \see FedoraTools
     * @throws RemoteException Thrown if the fedora repository is unreachable
     * @throws SAXException  Thrown if the construction of the xml went wrong. \see FedoraTools
     * @throws ServiceException 
     * @throws ConfigurationException 
     * @deprecated Use the FedoraAdministration.getDigitalObject( String pid ) method instead
     */
    /*@Deprecated
    public CargoContainer retrieveContainer( String fedoraPid ) throws IOException, ParserConfigurationException, RemoteException, SAXException, ConfigurationException, ServiceException
    {
        log.debug( String.format( "entering retrieveContainer( '%s' )", fedoraPid ) );
        log.debug( "FEDORACOM. fedoraPid: " + fedoraPid + "; AdminData.getName: " + DataStreamType.AdminData.getName() );
        MIMETypedStream ds = FedoraHandle.getInstance().getAPIA().getDatastreamDissemination( fedoraPid, DataStreamType.AdminData.getName(), null );
        byte[] adminStream = ds.getStream();
        log.debug( String.format( "Got adminstream from fedora == %s", new String( adminStream ) ) );

        CargoContainer cc = new CargoContainer();

        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        log.debug( String.format( "Trying to get root element from adminstream with length %s", bis.available() ) );
        Element root = XMLUtils.getDocumentElement( new InputSource( bis ) );

        log.debug( String.format( "root element from adminstream == %s", root ) );

             NodeList indexingAliasElem = root.getElementsByTagName( "indexingalias" );
        if( indexingAliasElem == null )
        {
            // \Todo: this if statement doesnt skip anything. What should we do? bug: 8878             
            log.error( String.format( "Could not get indexingalias from adminstream, skipping " ) );
        }
        log.debug( String.format( "indexingAliasElem == %s", indexingAliasElem.item(0) ) );
        String indexingAliasName = ((Element)indexingAliasElem.item( 0 )).getAttribute( "name" );
        log.debug( String.format( "Got indexingAlias = %s", indexingAliasName ) );
     
        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = (Element)streamsNL.item(0);
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        log.debug( String.format( "Iterating streams in nodelist" ) );
        
        int streamNLLength = streamNL.getLength();
        for(int i = 0; i < streamNLLength; i++ )
        {
            Element stream = (Element)streamNL.item(i);
            String streamID = stream.getAttribute( "id" );
            MIMETypedStream dstream = FedoraHandle.getInstance().getAPIA().getDatastreamDissemination(fedoraPid, streamID, null);
            cc.add( DataStreamType.getDataStreamTypeFrom( stream.getAttribute( "streamNameType" ) ),
                    stream.getAttribute( "format" ),
                    stream.getAttribute( "submitter" ),
                    stream.getAttribute( "lang" ),
                    stream.getAttribute( "mimetype" ),
                    IndexingAlias.getIndexingAlias( indexingAliasName ),
                    dstream.getStream() );
        }
        
        return cc;
    }*/
}