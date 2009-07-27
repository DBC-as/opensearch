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
    private static Logger log = Logger.getLogger( FedoraHandle.class );

    private static FedoraAPIM fem;
    private static FedoraAPIA fea;
    private static FedoraClient fc;


    private static FedoraHandle INSTANCE = null;


    private FedoraHandle() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
    	log.debug( "FedoraHandle constructor" );
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


    public static synchronized FedoraHandle getInstance() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
    	log.debug( "FedoraHandle getInstance" );    	
        if ( INSTANCE == null )
        {
        	INSTANCE = new FedoraHandle();
        }
        
        return INSTANCE;
    }


    public FedoraAPIA getAPIA() throws ServiceException
    {
    	log.debug( "FedoraHandle getAPIA" );
    	return fea;
    }


    public FedoraAPIM getAPIM()
    {
    	log.debug( "FedoraHandle getAPIM" );
    	return fem;
    }


    public FedoraClient getFC()
    {
    	log.debug( "FedoraHandle getFC()" );
    	return fc;
    }
}