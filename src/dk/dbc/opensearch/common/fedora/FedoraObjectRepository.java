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
 * \file
 * \brief
 */


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.metadata.AdministrationStream;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.metadata.MetaData;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.OpenSearchTransformException;

import fedora.common.Constants;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.FieldSearchResult;
import fedora.server.types.gen.ObjectFields;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

import javax.xml.rpc.ServiceException;
import javax.xml.stream.XMLStreamException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * FedoraObjectRepository
 */
public class FedoraObjectRepository implements IObjectRepository
{
    private static Logger log = Logger.getLogger( FedoraObjectRepository.class );


    /**
     * Dateformat conforming to the fedora requirements.
     */
    protected static final SimpleDateFormat dateFormat = new SimpleDateFormat( "yyyy-MM-dd'T'HH:mm:ss.SSS" );

    private final String has = "has";
    private final String pid = "pid";
    private FedoraHandle fedoraHandle;


    /**
     * Initializes the FedoraObjectRepository; tries to connect to the
     * underlying objectrepository. If this fails in some way, an
     * ObjectRepositoryException is thrown.
     */
    public FedoraObjectRepository() throws ObjectRepositoryException
    {
        this.fedoraHandle = new FedoraHandle();
    }


    @Override
    public void deleteObject( String identifier, String logmessage ) throws ObjectRepositoryException
    {
        String timestamp = null;
        try
        {
            //note that we're never forcing a deletion
            timestamp = this.fedoraHandle.purgeObject( identifier, logmessage, false );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Could not delete object referenced by pid '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Could not delete object referenced by pid '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Could not delete object referenced by pid '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( RemoteException ex )
        {
            String error = String.format( "Could not delete object referenced by pid '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not delete object referenced by pid '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        if( timestamp == null )
        {
            String error = String.format( "Could not delete object reference by pid %s", identifier );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }
    }


    /**
     * Stores data in {@code cargo} in the fedora repository
     *
     * @param cargo the data to store
     * @param logmessage message to log the operation with
     * @return the objectIdentifier of the stored object
     * @throws ObjectRepositoryException if the cargo could not be transformed
     * into foxml or the foxml could not be stored
     */
    @Override
    public String storeObject( CargoContainer cargo, String logmessage ) throws ObjectRepositoryException
    {
        if( cargo.getCargoObjectCount() == 0 )
        {
            log.error( String.format( "No data in CargoContainer, refusing to store nothing" ) );
            throw new IllegalStateException( String.format( "No data in CargoContainer, refusing to store nothing" ) );
        }

        String identifier = cargo.getIdentifier();
        if( identifier == null )
        {
            DublinCore dc = cargo.getDublinCoreMetaData();
            if( null == dc )
            {
                log.warn( "No dublin core stream found on CargoContainer, debug information will be severely impeded" );
            }
            else
            {
                String format = dc.getDCValue( DublinCoreElement.ELEMENT_FORMAT );
                String submitter = dc.getDCValue( DublinCoreElement.ELEMENT_CREATOR );
                log.info( String.format( "No objectIdentifier found in cargocontainer with format '%s', submitter '%s'.", format, submitter ) );
            }
            log.info( "We'll get an identifier from fedora" );
            identifier = "";
        }
        cargo.addMetaData( constructAdministrationStream( cargo ) );

        byte[] foxml;
        try
        {
            foxml = FedoraUtils.CargoContainerToFoxml( cargo );
        }
        catch( OpenSearchTransformException ex )
        {
            String error = String.format( "Failed in serializing CargoContainer with id '%s': %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        String returnedobjectIdentifier = null;
        try
        {
            returnedobjectIdentifier = this.fedoraHandle.ingest( foxml, Constants.FOXML1_1.toString(), logmessage );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to ingest object with pid '%s' into repository: %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to ingest object with pid '%s' into repository: %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to ingest object with pid '%s' into repository: %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to ingest object with pid '%s' into repository: %s", identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        if( "".equals( identifier ) )
        {
            log.info( String.format( "For empty identifier, we recieved '%s' from the ingest", returnedobjectIdentifier ) );
        }
        else if( !returnedobjectIdentifier.equals( identifier ) )
        {
            log.warn( String.format( "I expected pid '%s' to be returned from the repository, but got '%s'", identifier, returnedobjectIdentifier ) );
        }

        return returnedobjectIdentifier;
    }


    /**
     * Replaces object identified by {@code identifier} in the fedora repository
     * with {@code cargo}
     *
     * @param identifier the object to replace
     * @param cargo the data to replace object with
     *
     * @throws ObjectRepositoryException if either deletion of the old object or storage of the new went wrong
     */
    @Override
    public void replaceObject( String identifier, CargoContainer cargo ) throws ObjectRepositoryException
    {
        if( cargo.getCargoObjectCount() == 0 )
        {
            String error = String.format( "Identifier '%s' delivered with an empty CargoContainer", identifier );
            log.error( error );
            throw new IllegalStateException( error );
        }

        String cargoIdentifier = cargo.getIdentifier();

        if( cargoIdentifier == null )
        {
            String[] newPid = null;
            String prefix = cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter();
            try
            {
                newPid = fedoraHandle.getNextPID( 1, prefix );
            }
            catch( ServiceException ex )
            {
                String error = String.format( "Could not retrieve new pids from prefix '%s': %s ", prefix, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( ConfigurationException ex )
            {
                String error = String.format( "Could not retrieve new pids from prefix '%s': %s ", prefix, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( MalformedURLException ex )
            {
                String error = String.format( "Could not retrieve new pids from prefix '%s': %s ", prefix, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( IOException ex )
            {
                String error = String.format( "Could not retrieve new pids from prefix '%s': %s ", prefix, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( IllegalStateException ex )
            {
                String error = String.format( "Could not retrieve new pids from prefix '%s': %s ", prefix, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }

            if( null == newPid && 1 != newPid.length )
            {
                String error = String.format( "pid is empty for namespace '%s', but no exception was caught.", prefix );
                log.error( error );
                throw new ObjectRepositoryException( error );
            }

            cargoIdentifier = newPid[0];
            cargo.setIdentifier( cargoIdentifier );
        }

        String logm = String.format( "Replacing object referenced by pid %s with data previously identified by id %s", identifier, cargoIdentifier );
        deleteObject( identifier, logm );

        cargo.setIdentifier( identifier );

        String storedObjectPid = storeObject( cargo, logm );

        if( !storedObjectPid.equals( identifier ) )
        {
            String error = String.format( "Could not store replacement object with pid '%s': stored with pid '%s' instead", identifier, storedObjectPid );
            log.error( error );
            throw new ObjectRepositoryException( error );
            //rollback?
            // perhaps instead of a purge, we should use mark-as-deleted and then commit if storedObjectPid.equals( identifier )
        }
    }


    /**
     * Retrieves an object encoded as a {@link CargoContainer} from the fedora
     * object repository. The method also handles the information given in
     * the administration stream of the object
     * 
     * @param identifier the fedora pid identifying the object in the repository
     * @return the object encoded as a {@link CargoContainer}
     * @throws ObjectRepositoryException containing an exception explaining why
     * the object could not be retrieved
     */
    @Override
    public CargoContainer getObject( String identifier ) throws ObjectRepositoryException
    {
        byte[] adminbytes = getDataStream( identifier, DataStreamType.AdminData.getName() );

        if( adminbytes == null || adminbytes.length < 1 )
        {
            String error = String.format( "Failed to obtain data for administration stream, cannot retrieve data from '%s'", identifier );
            log.error( error );
            throw new IllegalStateException( error );
        }

        AdministrationStream adminStream;
        try
        {
            //log.trace( String.format( "Administration stream: %s", new String( adminbytes ) ) );
            adminStream = new AdministrationStream( new ByteArrayInputStream( adminbytes ), true );
        }
        catch( XMLStreamException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        List<InputPair<Integer, InputPair<String, CargoObject>>> adminstreamlist;
        try
        {
            adminstreamlist = adminStream.getStreams();
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to retrieve streams from administrationstream" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        CargoContainer cargo = new CargoContainer( identifier );

        for( InputPair<Integer, InputPair<String, CargoObject>> cargoobjects : adminstreamlist )
        {
            String streamId = cargoobjects.getSecond().getFirst();

            if( streamId.equals( DataStreamType.DublinCoreData.getName() + ".0" ) )
            {
                streamId = "DC";
            }
            log.debug( String.format( "id: %s, streamId: %s", identifier, streamId ) );

            byte[] datastream;
            try
            {
                datastream = this.fedoraHandle.getDatastreamDissemination( identifier, streamId, null );
            }
            catch( ConfigurationException ex )
            {
                String error = String.format( "Failed to retrieve datastream with id '%s' from pid '%s': %s", streamId, identifier, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( MalformedURLException ex )
            {
                String error = String.format( "Failed to retrieve datastream with id '%s' from pid '%s': %s", streamId, identifier, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( IOException ex )
            {
                String error = String.format( "Failed to retrieve datastream with id '%s' from pid '%s': %s", streamId, identifier, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
            catch( ServiceException ex )
            {
                String error = String.format( "Failed to retrieve datastream with id '%s' from pid '%s': %s", streamId, identifier, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }

            CargoObject co = cargoobjects.getSecond().getSecond();

            try
            {
                if( co.getDataStreamType() == DataStreamType.DublinCoreData )
                {
                    DublinCore dc;
                    log.trace( String.format( "Trying to contruct DublinCore element from string: %s", new String( datastream ) ) );
                    try
                    {
                        dc = new DublinCore( new ByteArrayInputStream( datastream ) );
                    }
                    catch( XMLStreamException ex )
                    {
                        String error = String.format( "Failed to construct Dublin Core object from datastream %s from pid '%s': %s", new String( datastream ), identifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    String dcid = dc.getDCValue( DublinCoreElement.ELEMENT_IDENTIFIER );
                    log.trace( String.format( "Got dc:identifier '%s' from datastream", dcid ) );
                    if( null == dcid )
                    {
                        log.warn( String.format( "Dublin Core data has no identifier, will use '%s' one from the CargoContainer", identifier ) );
                        dc.setIdentifier( identifier );
                    }
                    else
                    {
                        log.info( String.format( "Adding DublinCore data with id '%s' to CargoContainer", dcid ) );
                    }

                    cargo.addMetaData( dc );
                }
                else
                {
                    cargo.add( co.getDataStreamType(), co.getFormat(), co.getSubmitter(), co.getLang(), co.getMimeType(), co.getIndexingAlias(), datastream );
                }
            }
            catch( IOException ex )
            {
                String error = String.format( "Failed to add data with administrationstream id '%s' to CargoContainer: %s", streamId, ex.getMessage() );
                log.error( error );
                throw new ObjectRepositoryException( error, ex );
            }
        }

        if( cargo.getCargoObjectCount() < 1 && adminStream.getCount() > 0 )
        {
            throw new ObjectRepositoryException( "CargoContainer is empty, even though adminstream says it gave data" );

        }
        if( cargo.getDublinCoreMetaData() == null )
        {
            DublinCore dc = new DublinCore( identifier );
            dc.setCreator( cargo.getCargoObject( DataStreamType.OriginalData ).getSubmitter() );
            dc.setFormat( cargo.getCargoObject( DataStreamType.OriginalData ).getFormat() );
            dc.setLanguage( cargo.getCargoObject( DataStreamType.OriginalData ).getLang() );
        }

        return cargo;
    }


    /**
     * Beware that this method implements a costly search. The search can be
     * narrowed a bit in retrieval time by setting {@code maximumResults} to a
     * low-ish value (depending on the number of objects in the repository), but
     * the search time itself will be unaffected by this. It is preferrable and
     * advisable to use the {@link #getIdentifiers(java.lang.String)}.
     *
     * @param searchExpression the expression to match the returned results against
     * @param maximumResults the maximum number of hits to retrieve from the
     * repository (not neccesarily equal to the maximum hits to return from this
     * method)
     * @return a {@link List<String>} of matching pids from the repository
     */
    @Override
    public List< String > getIdentifiers( Pattern searchExpression, int maximumResults )
    {
        String[] resultFields =
        {
            pid
        };

        String[] searchFields =
        {
            "*"
        };

        ObjectFields[] objectFields = searchRepository( resultFields, pid, searchFields, has, maximumResults );

        int ofLength = objectFields.length;
        List< String > pids = new ArrayList< String >( ofLength );
        for( int i = 0; i < ofLength; i++ )
        {
            String pidToMatch = objectFields[i].getPid();
            if( searchExpression.matcher( pidToMatch ).matches() )
            {
                pids.add( pidToMatch );
            }
        }

        return pids;
    }


    @Override
    public List<String> getIdentifiers( String verbatimSearch, int maximumResults )
    {
        String[] resultFields =
        {
            pid
        };

        String[] searchFields =
        {
            verbatimSearch
        };

        ObjectFields[] objectFields = searchRepository( resultFields, pid, searchFields, has, maximumResults );

        int ofLength = objectFields.length;
        List<String> pids = new ArrayList<String>( ofLength );
        for( int i = 0; i < ofLength; i++ )
        {
            pids.add( objectFields[i].getPid() );
        }

        return pids;
    }


    @Override
    public List<String> getIdentifiers( String verbatimSearchString, List< String > searchableFields, int maximumResults )
    {
        String[] resultFields = new String[searchableFields.size()];
        int i = 0;
        for( String field : searchableFields )
        {
            resultFields[i] = field;
            i++;
        }

        ObjectFields[] objectFields = searchRepository( resultFields, pid, (String[])searchableFields.toArray(), has, maximumResults );

        int ofLength = objectFields.length;
        List< String > pids = new ArrayList< String >( ofLength );
        for( int j = 0; j < ofLength; j++ )
        {
            pids.add( objectFields[j].getPid() );
        }

        return pids;
    }


    @Override
    public List< String > getIdentifiers( List< String > searchStrings, List< String > searchableFields, int maximumResults )
    {
        String[] resultFields = new String[ searchableFields.size() ];
        int i = 0;
        for( String field : searchableFields )
        {
            resultFields[i] = field;
            i++;
        }

        ObjectFields[] objectFields = searchRepository( resultFields, pid, searchableFields.toArray( new String[searchableFields.size()] ), has, maximumResults );

        int ofLength = objectFields.length;
        List< String > pids = new ArrayList< String >( ofLength );
        for( int j = 0; j < ofLength; j++ )
        {
            pids.add( objectFields[j].getPid() );
        }

        return pids;
    }


    @Override
    public void storeDataInObject( String identifier, CargoObject object, boolean versionable, boolean overwrite ) throws ObjectRepositoryException
    {

        AdministrationStream admStream = getAdministrationStream( identifier );
        int count = admStream.getCount( object.getDataStreamType() );
        String dsId = object.getDataStreamType().getName() + Integer.toString( count + 1 );
        boolean addedData = addDataUpdateAdminstream( identifier, dsId, object );

        if( !addedData )
        {
            log.warn( String.format( "Could not add data or update administration stream for pid %s, dsId %s", identifier, dsId ) );
        }

        //upload the content

        /** \todo: the java.nio.channels should relieve the worl of this ugly hack. Please redeem this code*/
        ByteArrayInputStream bais = new ByteArrayInputStream( object.getBytes() );
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[1024];
        int len;
        try
        {
            while( (len = bais.read( buf )) > 0 )
            {
                baos.write( buf, 0, len );
            }
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not hack my way through outdated java api for copying inputstreams to outputstreams, sorry" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        
        /** end ugly code hack ;)*/
        String dsLocation = uploadDatastream( baos );
        String logm = String.format( "added %s to the object with pid: %s", dsLocation, identifier );
        String returnedSID = null;
        try
        {
            returnedSID = this.fedoraHandle.addDatastream( identifier, dsId, new String[]
                    {
                    }, object.getFormat(), versionable, object.getMimeType(), null, dsLocation, "M", "A", null, null, logm );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to add Datastream with id '%s' to object with pid '%s': %s", dsId, identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to add Datastream with id '%s' to object with pid '%s': %s", dsId, identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to add Datastream with id '%s' to object with pid '%s': %s", dsId, identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to add Datastream with id '%s' to object with pid '%s': %s", dsId, identifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }


        if( returnedSID == null )
        {
            String error = String.format( "Failed to add datastream to object with pid '%s'", identifier );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }
    }


    @Override
    public void deleteDataFromObject( String objectIdentifier, String dataIdentifier ) throws ObjectRepositoryException
    {

        String logm = String.format( "removed stream %s from object %s", dataIdentifier, objectIdentifier );
        String startDate = null;
        String endDate = null;
        String[] stamp;
        try
        {
            stamp = this.fedoraHandle.purgeDatastream( objectIdentifier, dataIdentifier, startDate, endDate, logm, true );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s': %s", dataIdentifier, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s': %s", dataIdentifier, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s': %s", dataIdentifier, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s': %s", dataIdentifier, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        if( stamp == null )
        {
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s'", dataIdentifier, objectIdentifier );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }

        boolean updated = removeDataUpdateAdminstream( objectIdentifier, dataIdentifier );

        if( !updated )
        {
            /** \todo: what to do here? the data was deleted, but we
             * could no update the administration stream. We're in
             * deep manure. We should do a rollback here or instead of
             * purging the datastream, we should mark it as deleted
             * and then try to unmark it here.*/
            String error = String.format( "Failed to remove data with id '%s' from object with pid '%s'", dataIdentifier, objectIdentifier );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }
    }


    @Override
    public CargoContainer getDataFromObject( String objectIdentifier, DataStreamType streamtype ) throws ObjectRepositoryException
    {

        if( null == streamtype )
        {
            String error = String.format( "DataStreamType was null, cannot perform search in repository" );
            log.error( error );
            throw new ObjectRepositoryException( new IllegalArgumentException( error ) );
        }
        AdministrationStream adminStream = getAdministrationStream( objectIdentifier );

        CargoContainer cargo = new CargoContainer( objectIdentifier );
        try
        {
            for( InputPair<Integer, InputPair<String, CargoObject>> pairs : adminStream.getStreams() )
            {
                DataStreamType datastreamtype = pairs.getSecond().getSecond().getDataStreamType();
                if( datastreamtype == streamtype )
                {
                    String dsId = pairs.getSecond().getFirst();
                    CargoObject co = pairs.getSecond().getSecond();
                    byte[] data;
                    try
                    {
                        data = this.fedoraHandle.getDatastreamDissemination( objectIdentifier, dsId, null );
                    }
                    catch( ConfigurationException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    catch( MalformedURLException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    catch( ServiceException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    cargo.add( datastreamtype, co.getFormat(), co.getSubmitter(), co.getLang(), co.getMimeType(), co.getIndexingAlias(), data );
                }
            }
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not retrieve information from administration stream" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        log.warn( String.format( "Returning empty CargoContainer for request of %s on %s", streamtype.getName(), objectIdentifier ) );
        return cargo;
    }


    @Override
    public CargoContainer getDataFromObject( String objectIdentifier, String dataIdentifier ) throws ObjectRepositoryException
    {
        AdministrationStream adminStream = getAdministrationStream( objectIdentifier );
        CargoContainer cargo = new CargoContainer( objectIdentifier );
        try
        {
            for( InputPair<Integer, InputPair<String, CargoObject>> pairs : adminStream.getStreams() )
            {
                String dsId = pairs.getSecond().getFirst();

                if( dataIdentifier.equals( dsId ) )
                {
                    CargoObject co = pairs.getSecond().getSecond();
                    byte[] data;
                    try
                    {
                        log.trace( String.format( "getDatastreamDissemination( %s, %s, null )", objectIdentifier, dsId ) );
                        data = this.fedoraHandle.getDatastreamDissemination( objectIdentifier, dsId, null );
                    }
                    catch( ConfigurationException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    catch( MalformedURLException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    catch( ServiceException ex )
                    {
                        String error = String.format( "Failed to retrieve data identified by '%s' from objectrepository pid '%s': %s", dsId, objectIdentifier, ex.getMessage() );
                        log.error( error );
                        throw new ObjectRepositoryException( error, ex );
                    }
                    cargo.add( co.getDataStreamType(), co.getFormat(), co.getSubmitter(), co.getLang(), co.getMimeType(), co.getIndexingAlias(), data );
                }
            }
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not retrieve information from administration stream" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        return cargo;
    }


    @Override
    public void replaceDataInObject( String objectIdentifier, String dataIdentifier, CargoObject cargo ) throws ObjectRepositoryException
    {

        /** \todo: this is just a wrapper for the two-phase operation
         * of deleting and storing data. It inherits the weaknesses
         * from both methods, as an exception thrown somewhere in the
         * operations renders the object repository in an inconsistent
         * state*/
        deleteDataFromObject( objectIdentifier, dataIdentifier );
        storeDataInObject( objectIdentifier, cargo, true, true );
    }


    private AdministrationStream constructAdministrationStream( CargoContainer cargo ) throws ObjectRepositoryException
    {
        IndexingAlias indexingAlias = cargo.getIndexingAlias( DataStreamType.OriginalData );
        if( indexingAlias == null )
        {
            log.warn( String.format( "Supplied CargoContainer (format %s) has no Original Data, I find it hard to construct an indexing alias given the circumstances. Instead, it'll be IndexingAlias.None", cargo.getCargoObject( DataStreamType.OriginalData ).getFormat() ) );
            indexingAlias = IndexingAlias.None;
        }
        AdministrationStream adminStream = new AdministrationStream( indexingAlias.getName() );

        if( 0 == cargo.getCargoObjectCount() )
        {
            String error = String.format( "Refusing to construct AdministrationStream when CargoContainer (%s) has no data in it", cargo.getIdentifier() );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }
        int streamtypecounter = 0;

        log.debug( String.format( "Constructing administration stream for %s cargo objects", cargo.getTotalObjectCount()-1 ) );
        for( CargoObject co : cargo.getCargoObjects() )
        {
            streamtypecounter = adminStream.getCount( co.getDataStreamType() );
            String id = co.getDataStreamType().getName() + "." + Integer.toString( streamtypecounter );
            log.debug( String.format( "Adding stream '%s' with id '%s' to adminstream", co.getDataStreamType().getName(), id ) );
            adminStream.addStream( co, id );
        }
        streamtypecounter = 0;
        for( MetaData meta : cargo.getMetaData() )
        {
            streamtypecounter = adminStream.getCount( meta.getType() );
            String id = meta.getType().getName() + "." + Integer.toString( streamtypecounter );
            log.debug( String.format( "Adding stream '%s' with id '%s' to adminstream", meta.getType().getName(), id ) );
            adminStream.addStream( meta, id );
        }
        return adminStream;
    }


    /**
     * Wrapper around {@link #getDataStream(java.lang.String, dk.dbc.opensearch.common.types.DataStreamType)}
     * that retrieves an {@link AdministrationStream} and handles intermediary
     * exceptions that surface from the lower layers.
     *
     * @param objectIdentifier the identifies the object in the repository
     * @return the AdministrationStream for the object identified by
     * {@code objectIdentifier}
     * @throws ObjectRepositoryException if the administrationstream could not
     * be retrieved
     */
    private AdministrationStream getAdministrationStream( String objectIdentifier ) throws ObjectRepositoryException
    {
        AdministrationStream adminStream;
        byte[] adminbytes = null;
        try
        {
            adminbytes = getDataStream( objectIdentifier, "adminData" );
            adminStream = new AdministrationStream( new ByteArrayInputStream( adminbytes ), true );
        }
        catch( XMLStreamException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to contruct administration stream from retrieved xml [%s]: %s", new String( adminbytes ), ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        return adminStream;
    }


    private boolean addDataUpdateAdminstream( String objectIdentifier, String dataIdentifier, CargoObject obj ) throws ObjectRepositoryException
    {
        AdministrationStream adminStream = getAdministrationStream( objectIdentifier );

        boolean added = adminStream.addStream( obj, dataIdentifier );

        if( !added )
        {
            log.warn( "Could not add information on added stream to administration stream" );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            adminStream.serialize( baos );
        }
        catch( OpenSearchTransformException ex )
        {
            String error = String.format( "Could not retrieve administration stream serialization" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        String label = "admin [text/xml]";
        String mime = "text/xml";
        String timeNow = dateFormat.format( new Date( System.currentTimeMillis() ) );
        String adminLogm = "admin stream updated with added stream data" + timeNow;
        String location = uploadDatastream( baos );
        String[] alternatedsids = new String[]
        {
        };

        try
        {
            this.fedoraHandle.modifyDatastreamByReference( objectIdentifier, DataStreamType.AdminData.getName(), alternatedsids, label, mime, "", location, null, null, adminLogm, true );
        }
        catch( RemoteException ex )
        {
            String error = String.format( "Failed to replace administration stream on object with pid '%s': %s", objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        return added;
    }


    private boolean removeDataUpdateAdminstream( String objectIdentifier, String dataIdentifier ) throws ObjectRepositoryException
    {

        AdministrationStream adminStream = getAdministrationStream( objectIdentifier );

        boolean removed = adminStream.removeStream( dataIdentifier );

        if( !removed )
        {
            log.warn( "Could not remove stream from adminstrationstream" );
        }

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        try
        {
            adminStream.serialize( baos );
        }
        catch( OpenSearchTransformException ex )
        {
            String error = String.format( "Could not retrieve administration stream serialization" );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        String adminLabel = "admin [text/xml]";
        String adminMime = "text/xml";
        String timeNow = dateFormat.format( new Date( System.currentTimeMillis() ) );
        String adminLogm = "admin stream updated with added stream data" + timeNow;

        String admLocation = uploadDatastream( baos );

        String[] empty = new String[]
        {
        };

        try
        {
            this.fedoraHandle.modifyDatastreamByReference( objectIdentifier, DataStreamType.AdminData.getName(), empty, adminLabel, adminMime, null, admLocation, null, null, adminLogm, true );
        }
        catch( RemoteException ex )
        {
            String error = String.format( "Failed to replace administration stream on object with objectIdentifier '%s'", objectIdentifier );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        return removed;
    }


    private byte[] getDataStream( String objectIdentifier, String dataStreamTypeName ) throws ObjectRepositoryException
    {
        if( null == objectIdentifier || null == dataStreamTypeName || objectIdentifier.isEmpty() || dataStreamTypeName.isEmpty() )
        {
            String error = String.format( "Necessary parameters for retrieving datastream was null" );
            log.error( error );
            throw new IllegalStateException( error );
        }

        byte[] ds = null;
        try
        {
            ds = this.fedoraHandle.getDatastreamDissemination( objectIdentifier, dataStreamTypeName, null );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamTypeName, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamTypeName, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamTypeName, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': %s", dataStreamTypeName, objectIdentifier, ex.getMessage() );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        if( null == ds )
        {
            String error = String.format( "Failed to retrieve datastream with name '%s' from object with objectIdentifier '%s': Got nothing back from the object repository", dataStreamTypeName, objectIdentifier );
            log.error( error );
            throw new ObjectRepositoryException( error );
        }
        return ds;
    }


    private String uploadDatastream( ByteArrayOutputStream datastream ) throws ObjectRepositoryException
    {
        File tmpFile;
        try
        {
            String timeStamp = Long.toString( System.currentTimeMillis() ) + Integer.toString( datastream.hashCode() );
            tmpFile = File.createTempFile( "datastream" + timeStamp, ".tmp" );
        }
        catch( IOException ex )
        {
            String error = "Could not create temporary file";
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        tmpFile.deleteOnExit();
        FileOutputStream faos;
        try
        {
            faos = new FileOutputStream( tmpFile );
        }
        catch( FileNotFoundException ex )
        {
            String error = "Could not write to temporary file";
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        try
        {
            datastream.writeTo( faos );
        }
        catch( IOException ex )
        {
            String error = "Failed to write administration stream to temporary file";
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        String location;
        try
        {
            location = this.fedoraHandle.uploadFile( tmpFile );
        }
        catch( IOException ex )
        {
            String error = "Failed to upload temporary file to fedora server";
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        return location;
    }


    ObjectFields[] searchRepository( String[] fieldsToReturn, String fieldsToSearch, String[] searchString, String comparisonOperator, int maximumResults )
    {
        // \Todo: check needed on the operator
        ComparisonOperator comp = ComparisonOperator.fromString( comparisonOperator );

        Condition[] cond = new Condition[searchString.length];
        for( int i = 0; i < searchString.length; i++ )
        {
            cond[i] = new Condition( fieldsToSearch, comp, searchString[i] );
        }

        FieldSearchQuery fsq = new FieldSearchQuery( cond, null );
        FieldSearchResult fsr = null;
        try
        {
            NonNegativeInteger maxResults = new NonNegativeInteger( Integer.toString( maximumResults ) );
            fsr = this.fedoraHandle.findObjects( fieldsToReturn, maxResults, fsq );
        }
        catch( ConfigurationException ex )
        {
            String warn = String.format( "Could not conduct query: %s", ex.getMessage() );
            log.warn( warn );
        }
        catch( ServiceException ex )
        {
            String warn = String.format( "Could not conduct query: %s", ex.getMessage() );
            log.warn( warn );
        }
        catch( MalformedURLException ex )
        {
            String warn = String.format( "Could not conduct query: %s", ex.getMessage() );
            log.warn( warn );
        }
        catch( IOException ex )
        {
            String warn = String.format( "Could not conduct query: %s", ex.getMessage() );
            log.warn( warn );
        }

        if( fsr == null )
        {
            log.warn( "Retrieved no hits from search, returning empty List<String>" );
            return new ObjectFields[] { };
        }
        
        ObjectFields[] objectFields = fsr.getResultList();

        return objectFields;
    }


}
