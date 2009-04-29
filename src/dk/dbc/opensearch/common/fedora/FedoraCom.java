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


package dk.dbc.opensearch.common.fedora;



import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.Pair;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.MIMETypedStream;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;
import java.text.ParseException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer.
 */
public class FedoraCom extends FedoraHandle
{

    Logger log = Logger.getLogger( FedoraCom.class );

    /**
     * The constructor handles the initiation of the connection with the
     * fedora base
     *
     * @throws ServiceException
     * @throws ConfigurationException
     */
    public FedoraCom() throws ConfigurationException, java.io.IOException, java.net.MalformedURLException, ServiceException
    {
        super();
    }

    public Pair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, IProcessqueue queue, IEstimate estimate ) throws ClassNotFoundException, IOException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException
    {

        // obtain mimetype and length from CargoContainer
        String mimeType = null;
        long length = 0;
        for ( CargoObject co : cc.getData() )
        {
            if ( co.getDataStreamName() == DataStreamType.OriginalData )
            {
                mimeType = co.getMimeType();
            }

            length += co.getContentLength();
        }

        //This should be caught in the harvest plugin!!!
        if ( cc.getItemsCount() < 1 )
        {
            log.error( String.format( "The cargocontainer for file %s has no data!", cc.getFilePath() ) );
        }

        // Store the CargoContainer in the fedora repository
        byte[] foxml = FedoraTools.constructFoxml( cc, datadockJob.getPID(), datadockJob.getFormat() );
        String logm = String.format( "%s inserted", datadockJob.getFormat() );

        // Beware of this innocent looking log line, it writes the
        //binary content of the stored data to the log
        String pid = fem.ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm );

        log.info( String.format( "Submitted data, returning pid %s", pid ) );

        // push to processqueue job to processqueue and get estimate
        queue.push( pid );
        Float est = estimate.getEstimate( mimeType, length );
        log.debug( String.format( "Got estimate of %s", est ) );

        return new Pair<String, Float>( pid, est );

    }

    public CargoContainer retrieveContainer( String fedoraPid ) throws IOException, ParserConfigurationException, RemoteException, SAXException
    {

        MIMETypedStream ds = fea.getDatastreamDissemination( fedoraPid, DataStreamType.AdminData.getName(), null );
        byte[] adminStream = ds.getStream();

        CargoContainer cc = new CargoContainer();

        ByteArrayInputStream bis = new ByteArrayInputStream( adminStream );
        Element root = XMLFileReader.getDocumentElement( new InputSource( bis ) );
        Element indexingAliasElem = ( Element )root.getElementsByTagName( "indexingalias" ).item( 0 );
        String indexingAliasName = indexingAliasElem.getAttribute( "name" );
        cc.setIndexingAlias( IndexingAlias.getIndexingAlias( indexingAliasName ) );

        Element filePathElem = ( Element )root.getElementsByTagName( "filepath" ).item( 0 );
        String filePath = filePathElem.getAttribute( "name" );
        cc.setFilePath( filePath );
        log.info( String.format( "The filepath of the file to index: %s ", filePath ) );

        NodeList streamsNL = root.getElementsByTagName( "streams" );
        Element streams = ( Element )streamsNL.item( 0 );
        NodeList streamNL = streams.getElementsByTagName( "stream" );
        for ( int i = 0; i < streamNL.getLength(); i++ )
        {
            Element stream = ( Element )streamNL.item( i );
            String streamID = stream.getAttribute( "id" );

            MIMETypedStream dstream = fea.getDatastreamDissemination( fedoraPid, streamID, null );

            cc.add( DataStreamType.getDataStreamNameFrom( stream.getAttribute( "streamNameType" ) ),
                    stream.getAttribute( "format" ),
                    stream.getAttribute( "submitter" ),
                    stream.getAttribute( "lang" ),
                    stream.getAttribute( "mimetype" ),
                    dstream.getStream() );
        }

        return cc;

    }
}

