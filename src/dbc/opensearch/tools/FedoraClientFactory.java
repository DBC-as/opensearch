package dbc.opensearch.tools;

import fedora.client.FedoraClient;

import java.net.URL;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.ConfigurationException;
import java.net.MalformedURLException;

/**
 * \ingroup tools
 * \brief The factory spawns fedoraHandlers
 */

public class FedoraClientFactory {
    
    Logger log = Logger.getLogger("FedoraClientFactory"); 
    
    private static String host = "";
    private static String port = "";
    private static String fedoraUrl = "";
    private static String user = "";
    private static String passphrase = "";
    
    public FedoraClient getFedoraClient()throws ConfigurationException, MalformedURLException {
         
        log.debug( "Obtain config paramaters for configuring fedora connection");
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );
        
        host       = config.getString( "fedora.host" );
        port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        passphrase = config.getString( "fedora.passphrase" );
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );
        
        log.debug( "Constructing FedoraClient" );
        
        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );

        log.debug( "Constructed FedoraClient" );
        return client;
    }

}