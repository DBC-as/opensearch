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


import dk.dbc.opensearch.common.config.PidManagerConfig;

import java.util.HashMap;
import java.util.Arrays;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import java.util.Stack;
import java.util.ArrayList;
import java.util.List;


public class PIDManager
{
    static Logger log = Logger.getLogger( PIDManager.class );

    private Stack<String> pidList;
    private HashMap<String, Stack<String>> pidMap;
    private NonNegativeInteger numPIDs;


    private static PIDManager INSTANCE;


    private PIDManager() throws ConfigurationException
    {
        numPIDs =  new NonNegativeInteger( PidManagerConfig.getNumberOfPidsToRetrieve() );
    }


    public static PIDManager getInstance() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        System.out.println( "getInstance" );
        if ( INSTANCE == null )
        {
            INSTANCE = new PIDManager();
        }

        return INSTANCE;
    }


    public String getNextPID( String prefix ) throws ServiceException, RemoteException
    {

        if( ! ( pidMap.containsKey( prefix ) ||  pidMap.get( prefix ).empty() ) ) {
            pidMap.put( prefix, retrievePIDStack( prefix ) );
        }

        return pidMap.get( prefix ).pop();
    }

    
    private Stack<String> retrievePIDStack( String prefix ) throws ServiceException, RemoteException
    {
        log.trace( String.format( "Entering retrievePIDs(prefix='%s')", prefix ) );
        log.debug( String.format( "Calling through FedoraHandle.HANDLE.dem.getNextPID( %s, %s)", numPIDs, prefix ) );

        Stack<String> pidStack = new Stack<String>();

        List< String > pidlist = new ArrayList< String >( Arrays.asList( FedoraHandle.fem.getNextPID( numPIDs, prefix ) ) );

        log.debug( String.format( "Got pidlist=%s", pidlist.toString() ) );

        for( String pid : pidList ){
            pidStack.push( pid );
        }

        return pidStack;
    }
}



// public class PIDManager// extends FedoraHandle 
// {
//     static Logger log = Logger.getLogger( PIDManager.class );
    
//     HashMap <String, Vector<String>> pidMap;
//     NonNegativeInteger numPIDs;


//     /**
//      * Constructor for the PIDManager. Gets fedora connection inforamtion from configuration
//      */
//     public PIDManager() throws ConfigurationException, ServiceException, java.net.MalformedURLException, java.io.IOException
//     {
//     	//super();
    	
//         log.debug( "Constructor() called" );
     
//         numPIDs =  new NonNegativeInteger( PidManagerConfig.getNumberOfPidsToRetrieve() );
//         pidMap = new HashMap <String, Vector< String > >();
//     }


//     /**
//      * this Method provides a new PID for a digital object to store it
//      * in the repository.
//      * 
//      * @param prefix The prefix for the new PID
//      * 
//      * @returns The next PID
//      */
//     public String getNextPID( String prefix ) throws MalformedURLException, ServiceException, RemoteException, IOException
//     {
//         log.debug( String.format( "getNextPid(prefix='%s') called", prefix ) );

//         Vector< String > prefixPIDs = null;
        
//         if( pidMap.containsKey( prefix ) ) // checks whether we already retrieved PIDs
//         {   
//             prefixPIDs = pidMap.get( prefix );
            
//             if( prefixPIDs.isEmpty() ) // checks if there are any PIDs left
//             { 
//                 log.debug( "Used all the PIDs, retrieving new PIDs" );
//                 prefixPIDs = retrievePIDs( prefix );                
//             }   

//             pidMap.remove( prefix );
//         }
//         else
//         {
//             log.debug( "No PIDs for this namespace, retrieving new PIDs" );
//             prefixPIDs = retrievePIDs( prefix );
//         }
        
//         String newPID = prefixPIDs.remove( 0 );
//         pidMap.put( prefix, prefixPIDs );
        
//         log.debug( String.format( "returns PID='%s'", newPID ) );
//         return newPID;
//     }    

    
//     /**
//      * Method for retrieving new PIDs from the fedoraRepository
//      * 
//      * @param prefix The prefix for the new PID
//      * 
//      * @returns a vector containing new PIDs for the given namespace
//      */
//     private Vector< String > retrievePIDs( String prefix ) throws MalformedURLException, ServiceException, RemoteException, IOException
//     {
//         log.debug( String.format( "retrievePIDs(prefix='%s') called", prefix ) );
//         log.debug( String.format( "Calling through FedoraHandle.HANDLE.dem.getNextPID( %s, %s)", numPIDs, prefix ) );

//         Vector< String > pidlist = new Vector< String >( Arrays.asList( FedoraHandle.HANDLE.fem.getNextPID( numPIDs, prefix ) ) );

//         log.debug( String.format( "Got pidlist=%s",pidlist.toString() ) );

//         return pidlist;
//     }
// }
