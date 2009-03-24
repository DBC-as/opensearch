/**
 *
 */
package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.config.FedoraConfig;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

import javax.xml.rpc.ServiceException;

import org.apache.log4j.Logger;


/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer.
 */
public abstract class FedoraHandle
{
    protected FedoraAPIM fem;
    protected FedoraAPIA fea;

    Logger log = Logger.getLogger( FedoraHandle.class );

    /**
     * The constructor handles the initiation of the connection with the
     * fedora base
     *
     * @throws ServiceException
     */
    public FedoraHandle() throws ServiceException, java.net.MalformedURLException, java.io.IOException
    {
        String fedora_base_url;

        String host = FedoraConfig.getHost();
        String port = FedoraConfig.getPort();
        String user = FedoraConfig.getUser();
        String pass = FedoraConfig.getPassPhrase();

        fedora_base_url  = String.format( "http://%s:%s/fedora", host, port );

        log.debug( String.format( "connecting to fedora base using %s, user=%s, pass=%s", fedora_base_url, user, pass ) );

        FedoraClient fc = new FedoraClient( fedora_base_url, user, pass );
        fea = fc.getAPIA();
        fem = fc.getAPIM();

    }
}
