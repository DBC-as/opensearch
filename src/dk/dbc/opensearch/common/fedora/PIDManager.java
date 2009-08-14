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
import java.util.Stack;

import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class PIDManager
{
    private static Logger log = Logger.getLogger( PIDManager.class );


    private Stack<String> pidList;
    private HashMap<String, Stack<String>> pidMap;
    private NonNegativeInteger numPIDs;


    private static PIDManager INSTANCE;


    private PIDManager() throws ConfigurationException
    {	
    	pidList = new Stack<String>();
    	pidMap = new HashMap<String, Stack<String>>();    	
        numPIDs =  new NonNegativeInteger( PidManagerConfig.getNumberOfPidsToRetrieve() );
    }


    public static synchronized PIDManager getInstance() throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        if ( INSTANCE == null )
        {	
            INSTANCE = new PIDManager();
        }

        return INSTANCE;
    }


    public String getNextPID( String prefix ) throws ServiceException, ConfigurationException, MalformedURLException, IOException, IllegalStateException
    {	
    	if( ! ( pidMap.containsKey( prefix ) ) ||  pidMap.get( prefix ).empty() )  
        {
            pidMap.put( prefix, retrievePIDStack( prefix ) );
        }
        
    	String ret = pidMap.get( prefix ).pop(); 
    	return ret;         
    }

    
    private Stack<String> retrievePIDStack( String prefix ) throws ServiceException, ConfigurationException, MalformedURLException, IOException, IllegalStateException
    {
        log.trace( "Entering retrievePIDStack( String prefix )" );
        if( prefix == null || prefix.isEmpty() )
        {
            log.error( "Prefix was not specified, and I have no default, exiting" );
            throw new IllegalStateException( "Prefix was not specified, and I have no default, exiting" );
        }

        log.trace( String.format( "Calling through FedoraHandle.getInstance().getAPIM().getNextPID( %s, %s)", numPIDs, prefix ) );

        Stack<String> pidStack = new Stack<String>();

        fedora.server.management.FedoraAPIM fem = FedoraHandle.getInstance().getAPIM();
        log.debug( "numPids: " + numPIDs.toString() );
        log.debug( "PIDManager prefix: " + prefix );
        String[] pids = fem.getNextPID( numPIDs, prefix );
        log.debug( "pids: " + pids.toString() );
        if ( pids == null )
        {
            log.error( "Could not retrieve pids from Fedora repository" );
            throw new IllegalStateException( "Could not retrieve pids from Fedora repository" );
        }

        for( String pid : pids )
        { 	
            log.debug( String.format( "pushing pid '%s' onto pidstack", pid ) );
            pidStack.push( pid );
        }

        return pidStack;
    }
}