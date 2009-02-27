/**
 * \file Store.java
 * \brief The Store class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;
import java.text.ParseException;
import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

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
    public float storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, ParserConfigurationException, SAXException
    {
        byte[] foxml = FedoraTools.constructFoxml( cargo, job.getPID(), job.getFormat() );
        String logm = String.format( "%s inserted", job.getFormat() );

        log.debug( String.format( "Inserting data: %s", new String( foxml ) ) );
        String fullPid = super.fem.ingest( foxml, "info:fedora/fedora-system:FOXML-1.1", logm);

        String pid = fullPid.substring( ( fullPid.indexOf( ":" ) ) +1 );
        log.info( String.format( "Submitted data, returning pid %s", fullPid ) );

        return Float.parseFloat( pid );
    }

    /**
     *
     */
    public PluginType getTaskName()
    {
        return pluginType;
    }
}

