/**
 * \file PIDManager.java
 * \brief The PIDManager class
 * \package fedora;
 */

package dk.dbc.opensearch.common.fedora;

import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;

import org.apache.axis.types.NonNegativeInteger;

import java.net.URL;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.io.IOException;
import java.net.HttpURLConnection;
//import java.io.BufferedReader;
//import java.io.InputStreamReader;

import fedora.client.FedoraClient;
import fedora.server.management.FedoraAPIM;


import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
//import java.lang.NullPointerException;
import org.apache.log4j.Logger;


public class PIDManager {

    HashMap <String, Vector< String > > pidMap;

    String host;
    String port;
    String user;
    String passphrase;

    NonNegativeInteger numPIDs;

    FedoraClient client;
    FedoraAPIM apim;

    static Logger log = Logger.getLogger("PIDManager");


    /**
     * Constructor for the PIDManager. Gets fedora connection inforamtion from configuration
     */

    public PIDManager() throws ConfigurationException//, IOException, ServiceException
    {
        log.debug( "Constructor() called" );
     
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );        
        host       = config.getString( "fedora.host" );
        port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        passphrase = config.getString( "fedora.passphrase" );
    
        numPIDs = new NonNegativeInteger( "10" ); // \todo: put in  configuration   
        pidMap = new HashMap <String, Vector< String > >();
    }


    /**
     * this Method provides a new PID for a digital object to store it
     * in the repository.
     * 
     * @param prefix The prefix for the new PID
     * 
     * @returns The next PID
     */

    public String getNextPID( String prefix ) throws MalformedURLException, ServiceException, RemoteException, IOException
    {
        log.debug( String.format( "getNextPid(prefix='%s') called", prefix ) );

        Vector prefixPIDs = null;
        
        if( pidMap.containsKey( prefix ) ){ // checks whether we already retrieved PIDs
            prefixPIDs = pidMap.get( prefix );
            
            if( prefixPIDs.isEmpty() ){ // checks if there are any PIDs left
                log.debug( "Used all the PIDs, retrieving new PIDs" );
                prefixPIDs = retrievePIDs( prefix );                
            }   
            pidMap.remove( prefix );
        }
        else{
            log.debug( "No PIDs for this namespace, retrieving new PIDs" );
            prefixPIDs = retrievePIDs( prefix );
        }
        
        String newPID = (String) prefixPIDs.remove( 0 );
        pidMap.put( prefix, prefixPIDs );
        
        log.debug( String.format( "returns PID='%s'", newPID ) );
        return newPID;
    }    

    /**
     * Method for retrieving new PIDs from the fedoraRepository
     * 
     * @param prefix The prefix for the new PID
     * 
     * @returns a vector containing new PIDs for the given namespace
     */
    private Vector< String > retrievePIDs( String prefix )
        throws MalformedURLException, ServiceException, RemoteException, IOException
    {
        log.debug( String.format( "retrievePIDs(prefix='%s') called", prefix ) );
        String fedoraUrl  = "http://" + host + ":" + port + "/fedora";
    
        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );
        apim = client.getAPIM();
        return new Vector< String >( Arrays.asList( apim.getNextPID( numPIDs, prefix ) ) );
    }

}
