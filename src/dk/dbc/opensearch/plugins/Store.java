/**
 * \file Store.java
 * \brief The Store class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;
import java.rmi.RemoteException;
import java.text.ParseException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;

/**
 * Stores the CargoContainer in the repository.
 */
public class Store extends FedoraHandle implements IRepositoryStore
{
    Logger log = Logger.getLogger( Store.class );

    private PluginType pluginType = PluginType.STORE;

    public Store() throws ServiceException, java.net.MalformedURLException, java.io.IOException
    {
        super();
    }

    /**
     * storeCargoContainer stores the cargoContainer in the repository.
     *
     * @param cargo  the CargocContainer to store.
     * @param job  the datadockJob describing the job. using the format and the pid.
     *
     * @throws MarshalException
     * @throws ValidationException
     * @throws IllegalStateException
     * @throws ServiceException
     * @throws IOException
     * @throws ParseException
     */
    public String storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws PluginException//MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException
    {
        byte[] foxml = null;
        try {
            foxml = FedoraTools.constructFoxml( cargo, job.getPID(), job.getFormat() );
        } catch (MarshalException me) {
            throw new PluginException( String.format( "The constructed DigitalObject could be marshalled into xml" ), me );
        } catch (ValidationException ve) {
            throw new PluginException( String.format( "Could not validate xml from DigitalObject" ), ve );
        } catch (TransformerConfigurationException tce) {
            throw new PluginException( String.format( "Could not initialize the XML Transformer" ), tce );
        } catch (IOException ioe) {
            throw new PluginException( String.format( "Could not read from the cargocontainer" ), ioe );
        } catch (ParseException pe) {
            throw new PluginException( String.format( "Could not parse xml from the cargocontainer" ), pe );
        } catch (ParserConfigurationException pce) {
            throw new PluginException( String.format( "Failed to initialize the XMLParser" ), pce );
        } catch (SAXException saxe) {
            throw new PluginException( String.format( "Could not parse xml from the cargocontainer" ), saxe );
        } catch (TransformerException te) {
            throw new PluginException( String.format( "Could not append Administration streams to the digital object" ), te );
        }
        String logm = String.format( "%s inserted", job.getFormat() );

        log.debug( String.format( "Inserting data: %s", new String( foxml ) ) );
        String pid = null;
        try {
            pid = super.fem.ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm);
        } catch (RemoteException re) {
            throw new PluginException( String.format( "The Fedorabase could not complete the ingest request" ), re );
        }

        //String pid = fullPid.substring( ( fullPid.indexOf( ":" ) ) +1 );
        log.info( String.format( "Submitted data, returning pid %s", pid ) );
        return pid;
        //return Float.parseFloat( pid );
    }

    /**
     *
     */
    public PluginType getTaskName()
    {
        return pluginType;
    }
}

