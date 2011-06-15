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

/**
 * \file FedoraHandle.java
 * \brief
 */


package dk.dbc.opensearch.fedora;


import dk.dbc.opensearch.config.FedoraConfig;

import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.Datastream;
import org.fcrepo.server.types.gen.DatastreamDef;
import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.fcrepo.server.types.gen.RelationshipTuple;

import java.io.File;
import java.net.MalformedURLException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.Map;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.trippi.TupleIterator;


public class FedoraHandle
{
    private static Logger log = Logger.getLogger( FedoraHandle.class );


    private FedoraAPIM fem;
    private FedoraAPIA fea;
    private FedoraClient fc;

    private String fedora_base_url;


    FedoraHandle() throws ObjectRepositoryException
    {
        String host;
        String port;
        String user;
        String pass;

        log.debug( "FedoraHandle constructor" );

        try
        {
            host = FedoraConfig.getHost();
            port = FedoraConfig.getPort();
            user = FedoraConfig.getUser();
            pass = FedoraConfig.getPassPhrase();
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to obtain configuration values for FedoraHandle");
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        this.fedora_base_url = String.format( "http://%s:%s/fedora", host, port );
        log.debug( String.format( "connecting to fedora base using %s, user=%s, pass=%s", fedora_base_url, user, pass ) );
        try
        {
            fc = new FedoraClient( fedora_base_url, user, pass );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to obtain connection to fedora repository: %s", ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        try
        {
            fea = fc.getAPIA();
            fem = fc.getAPIM();
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to obtain connection to fedora repository: %s", ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to obtain connection to fedora repository: %s", ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
    }

    private FedoraAPIA getAPIA() throws ServiceException
    {
        log.trace( "FedoraHandle getAPIA" );
        return fea;
    }


    private FedoraAPIM getAPIM()
    {
        log.trace( "FedoraHandle getAPIM" );
        return fem;
    }


    private FedoraClient getFC()
    {
        log.trace( "FedoraHandle getFC()" );
        return fc;
    }


    String ingest( byte[] data, String datatype, String logmessage ) throws ConfigurationException, ServiceException, ServiceException, IOException
    {
	long timer = System.currentTimeMillis();

        String pid = this.getAPIM().ingest( data, datatype, logmessage );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s f) %s", this.getClass(), timer ) );

        return pid;
    }


    String uploadFile( File fileToUpload ) throws IOException
    {
	long timer = System.currentTimeMillis();

        String msg = this.getFC().uploadFile( fileToUpload );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return msg;
    }


    String modifyDatastreamByReference( String pid, String datastreamID, String[] alternativeDsIds, String dsLabel, String MIMEType, String formatURI, String dsLocation, String checksumType, String checksum, String logMessage, boolean force ) throws RemoteException
    {

	long timer = System.currentTimeMillis();

        String timestamp = this.getAPIM().modifyDatastreamByReference( pid, datastreamID, alternativeDsIds, dsLabel, MIMEType, formatURI, dsLocation, checksumType, checksum, logMessage, force);

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return timestamp;
    }

    String addDatastream( String pid, String datastreamID, String[] alternativeDsIds, String dsLabel, boolean versionable, String MIMEType, String formatURI, String dsLocation, String controlGroup, String datastreamState, String checksumType, String checksum, String logmessage ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	long timer = System.currentTimeMillis();

        String returnedSID = this.getAPIM().addDatastream( pid, datastreamID, alternativeDsIds, dsLabel, versionable, MIMEType, formatURI, dsLocation, controlGroup, datastreamState, checksumType, checksum, logmessage );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return returnedSID;
    }


    byte[] getDatastreamDissemination( String pid, String datastreamId, String asOfDateTime ) throws ConfigurationException, MalformedURLException, IOException, ServiceException
    {
	long timer = System.currentTimeMillis();

        MIMETypedStream ds = this.getAPIA().getDatastreamDissemination( pid, datastreamId, asOfDateTime );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return ds.getStream();
    }


    Datastream getDatastream( String pid, String dsID ) throws RemoteException
    {
	long timer = System.currentTimeMillis();

        Datastream res = getAPIM().getDatastream( pid, dsID, null );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return res;
    }


    Datastream[] getDataStreamsXML( String pid ) throws IOException
    {
	long timer = System.currentTimeMillis();

        Datastream[] res = getAPIM().getDatastreams( pid, null, null );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return res;
    }


    String[] getNextPID( int numberOfPids, String prefix ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	long timer = System.currentTimeMillis();

        String[] pidlist = this.getAPIM().getNextPID( new NonNegativeInteger( Integer.toString( numberOfPids ) ) , prefix);

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        if ( pidlist == null )
        {
            log.error( "Could not retrieve pids from Fedora repository" );
            throw new IllegalStateException( "Could not retrieve pids from Fedora repository" );
        }

        return pidlist;
    }


    TupleIterator getTuples( Map< String,String > params ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	long timer = System.currentTimeMillis();

        TupleIterator tuples = this.getFC().getTuples( params );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return tuples;
    }


    boolean addRelationship( String pid, String predicate, String object, boolean isLiteral, String datatype ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	// There is a check in this function in order to avoid having
	// realations from an object to itself.
	
	log.debug( String.format( "Adding Relationship: [%s][%s] => [%s]", pid, predicate, object ) );

	long timer = System.currentTimeMillis();
	
	if ( pid.equals( object ) ) 
	{
	    log.warn( String.format( "We do not allow for a relation=[%s] to have identical subject=[%s] and object=[%s]", predicate, pid, object ) );
	    return false;
	}

        boolean ret = this.getAPIM().addRelationship( pid, predicate, object, isLiteral, datatype );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return ret;
    }


    boolean purgeRelationship( String pid, String predicate, String object, boolean isLiteral, String datatype ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	long timer = System.currentTimeMillis();

        boolean ret = this.getAPIM().purgeRelationship( pid, predicate, object, isLiteral, datatype );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return ret;
    }


    FieldSearchResult findObjects( String[] resultFields, NonNegativeInteger maxResults, FieldSearchQuery fsq ) throws ConfigurationException, MalformedURLException, IOException, ServiceException
    {
	long timer = System.currentTimeMillis();

        FieldSearchResult fsr = this.getAPIA().findObjects( resultFields, maxResults, fsq );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return fsr;
    }


    FieldSearchResult resumeFindObjects( String token ) throws ConfigurationException, MalformedURLException, IOException, ServiceException
    {
	long timer = System.currentTimeMillis();

        FieldSearchResult fsr = this.getAPIA().resumeFindObjects( token );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return fsr;
    }


    RelationshipTuple[] getRelationships( String subject, String predicate ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	log.debug( String.format( "calling with subject '%s' and predicate '%s'", subject, predicate ) );
	long timer = System.currentTimeMillis();

        RelationshipTuple[] rt = this.getAPIM().getRelationships( subject, predicate );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return rt;
    }


    String[] purgeDatastream( String pid, String sID, String startDate, String endDate, String logm, boolean breakDependencies ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
	long timer = System.currentTimeMillis();

        String[] rt = this.getAPIM().purgeDatastream( pid, sID, startDate, endDate, logm, breakDependencies);

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return rt;
    }


    String purgeObject( String identifier, String logmessage, boolean force ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, RemoteException
    {
	long timer = System.currentTimeMillis();

        String timestamp = this.getAPIM().purgeObject( identifier, logmessage, force );

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return timestamp;
    }


    boolean hasObject( String identifier ) throws RemoteException
    {
	long timer = System.currentTimeMillis();

	boolean retValue = true;
        try
        {
            DatastreamDef[] d = this.fea.listDatastreams( identifier, null );
            log.debug( String.format( "length of DatastreamDef: '%s'", d.length ) );
        }
        catch ( IOException ioe )
        {
            log.info( String.format( "Could not list datastreams for object %s. We take this as an indication that the object doesn't exist", identifier ) );
            retValue = false;
        }

	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );

        return retValue;
    }


    /**
     * Wrapper function to FedoraAPIM.
     * No magic here.
     * <p>
     * The functions primary functionality is to modify the {@code state} of an object, but it can also be 
     * used to modify the {@code label} or the {@code ownerId}. If you intende to modify an then please notice,
     * that it is unknown what happens if you change the {@code label} or the {@code ownerId}.
     * The reason it is unknown is because the internal datastreams such as adminStream probably does
     * not change its submitter or format through this function.
     * </p>
     * @param pid The identifier for the object to modify.
     * @param state The state you wish to change into. The legal states are: {@code A}, {@code I} or {@code D}. 
     * @param label The label you wish to change into. Commonly this is the format.
     * @param ownerId The ownerid you wish to change into. commonly this is the submitter.
     * @param logMessage Any message you want to pass on to fedoras log.
     *
     * @return Server-date of modification as a {@link String}.
     *
     * @throws RemoteException if an error of any kind occurs. This is probably both communication errors and malformed data errors.
     */
    String modifyObject( String pid, String state, String label, String ownerId, String logMessage ) throws RemoteException
    {
	long timer = System.currentTimeMillis();

        String date = this.getAPIM().modifyObject( pid, state, label, ownerId, logMessage );
	
	timer = System.currentTimeMillis() - timer;
	log.info( String.format( "HANDLE Timing: ( %s ) %s", this.getClass(), timer ) );
	
	return date;
    }

}