/**
 * \file FedoraHandle.java
 * \brief The FedoraHandle class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;


import org.exolab.castor.xml.ValidationException;
import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.rmi.RemoteException;
import java.sql.SQLException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import java.text.ParseException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import fedora.server.types.gen.MIMETypedStream;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import dk.dbc.opensearch.common.types.IndexingAlias;
import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import javax.xml.parsers.ParserConfigurationException;
import dk.dbc.opensearch.common.types.Pair;

/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer.
 */
public class FedoraHandle
{
    CargoContainer cc;

    Logger log = Logger.getLogger( FedoraHandle.class );

    /**
     * The constructor handles the initiation of the connection with the
     * fedora base
     *
     * @throws ServiceException
     * @throws ConfigurationException
     */
    public FedoraHandle() throws ServiceException, java.net.MalformedURLException, java.io.IOException, ConfigurationException 
    { 
        cc  = new CargoContainer(); 
    }

    public Pair<String, Float> storeContainer( CargoContainer cc, DatadockJob datadockJob, Processqueue queue, Estimate estimate) throws IOException, RemoteException, ClassNotFoundException, SQLException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerException
    {
        this.cc = cc;
        return new Pair<String, Float>( "nopid", 0f );
    }

    public CargoContainer retrieveContainer( String fedoraPid) throws RemoteException, ParserConfigurationException, IOException, SAXException
    {
        return cc;
    }
}


