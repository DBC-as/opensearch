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


import dk.dbc.opensearch.common.config.FedoraConfig;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

import java.net.MalformedURLException;
import java.io.IOException;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class FedoraHandle
{
    Logger log = Logger.getLogger( FedoraHandle.class );

    protected static FedoraAPIM fem;
    protected static FedoraAPIA fea;
    protected static FedoraClient fc;


    private static FedoraHandle INSTANCE;


    private FedoraHandle() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String fedora_base_url;

        String host = FedoraConfig.getHost();
        String port = FedoraConfig.getPort();
        String user = FedoraConfig.getUser();
        String pass = FedoraConfig.getPassPhrase();

        fedora_base_url  = String.format( "http://%s:%s/fedora", host, port );

        log.debug( String.format( "connecting to fedora base using %s, user=%s, pass=%s", fedora_base_url, user, pass ) );

        fc = new FedoraClient( fedora_base_url, user, pass );
        fea = fc.getAPIA();
        fem = fc.getAPIM();
    }


    public static FedoraHandle getInstance() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        if ( INSTANCE == null )
        {
            INSTANCE = new FedoraHandle();
        }

        return INSTANCE;
    }


    public static FedoraAPIA getAPIA() throws ServiceException
    {
        return fea;
    }


    public static FedoraAPIM getAPIM()
    {
        return fem;
    }
}



/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer and facilitate file uploads.
 */
/*public abstract class FedoraHandle
{
    protected FedoraAPIM fem;
    protected FedoraAPIA fea;
    protected FedoraClient fc;

    Logger log = Logger.getLogger( FedoraHandle.class );
*/

    /**
     * The constructor handles the initiation of the connection with the
     * fedora base
     *
     * @throws ServiceException
     * @throws ConfigurationException 
     */
    /*  public FedoraHandle() throws ServiceException, java.net.MalformedURLException, java.io.IOException, ConfigurationException
    {
        String fedora_base_url;

        String host = FedoraConfig.getHost();
        String port = FedoraConfig.getPort();
        String user = FedoraConfig.getUser();
        String pass = FedoraConfig.getPassPhrase();

        fedora_base_url  = String.format( "http://%s:%s/fedora", host, port );

        log.debug( String.format( "connecting to fedora base using %s, user=%s, pass=%s", fedora_base_url, user, pass ) );

        fc = new FedoraClient( fedora_base_url, user, pass );
        fea = fc.getAPIA();
        fem = fc.getAPIM();

        }
}
    */

