/**
 * \file FedoraClientFactory.java
 * \brief The FedoraClientFactory class
 * \package tools
 */
package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.config.FedoraConfig;

import fedora.client.FedoraClient;

import java.net.MalformedURLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief The factory spawns fedoraClients
 */
public class FedoraClientFactory 
{    
    Logger log = Logger.getLogger( FedoraClientFactory.class ); 
    
    private static String host = "";
    private static String port = "";
    private static String fedoraUrl = "";
    private static String user = "";
    private static String passphrase = "";
    
    
    /**
     * Builds And return a FedoraClient.   
     *
     * @throws ConfigurationException error reading configuration file
     * @throws MalformedURLException error obtaining fedora configuration
     */
    public FedoraClient getFedoraClient()throws ConfigurationException, MalformedURLException 
    {         
        log.debug( "Obtain config paramaters for configuring fedora connection");
        
        host       = FedoraConfig.getHost();
        port       = FedoraConfig.getPort();
        user       = FedoraConfig.getUser();
        passphrase = FedoraConfig.getPassPhrase();
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );
        
        log.debug( "Constructing FedoraClient" );
        
        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );

        log.debug( "Constructed FedoraClient" );
        return client;
    }
}