/**
 * \file PIDManager.java
 * \brief The PIDManager class
 * \package fedora;
 */
package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.config.PidManagerConfig;

import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;

import info.fedora.www.definitions._1._0.api.FedoraAPIA;
import info.fedora.www.definitions._1._0.api.FedoraAPIAServiceLocator;
import info.fedora.www.definitions._1._0.api.FedoraAPIM;
import info.fedora.www.definitions._1._0.api.FedoraAPIMServiceLocator;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class PIDManager  extends FedoraHandle 
{
    HashMap <String, Vector< String > > pidMap;

    // protected FedoraAPIM fem;
    // protected FedoraAPIA fea;

    String host;
    String port;
    String user;
    String passphrase;

    NonNegativeInteger numPIDs;

    //FedoraClient client;
    //FedoraAPIM apim;

    static Logger log = Logger.getLogger("PIDManager");


    /**
     * Constructor for the PIDManager. Gets fedora connection inforamtion from configuration
     */

    public PIDManager() throws ConfigurationException, ServiceException, java.net.MalformedURLException, java.io.IOException
    {
    	super();
    	
        log.debug( "Constructor() called" );
     
        host = FedoraConfig.getFedoraHost();
        port = FedoraConfig.getFedoraPort();
        user = FedoraConfig.getFedoraUser();
        passphrase = FedoraConfig.getFedoraPassPhrase();
        numPIDs =  new NonNegativeInteger( PidManagerConfig.getPidManagerNumberOfPidsToRetrieve() );

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
        
        return new Vector< String >( Arrays.asList( super.fem.getNextPID( numPIDs, prefix ) ) );                    
    }
}
