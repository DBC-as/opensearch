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
package dk.dbc.opensearch.common.types;

import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.metadata.MetaData;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import java.util.Map;
import java.util.Map.Entry;
import org.apache.log4j.Logger;


/**
 * \ingroup common.types
 * \brief CargoContainer is a data structure used throughout
 *  OpenSearch for carrying information submitted for
 *  indexing. CargoContainer retains data in a private data structure
 *  consisting of CargoObject objects. Although the CargoContainer
 *  class is mutable, the CargoObjects themselves are immutable. Any
 *  updates made on these objects must be made by the client as
 *  deletes and adds.  All verification and work with the data 
 *  are done through the CargoObject objects.
 *
 * Although the CargoContainer can hold an unlimited amount of data of
 * the same type - defined by the add method with the type defined by
 * DataStreamType - a CargoContainer instance can only hold on
 * MetaData element of a given type at a time. Any subsequent calls to
 * the addMetaData method with MetaData with identical types will
 * overwrite existing metadata of this type in the CargoContainer.
 */
public class CargoContainer
{

    Logger log = Logger.getLogger( CargoContainer.class );
    /** The internal representation of the data contained in the CargoContainer*/
    private ArrayList<CargoObject> data;
    private Map<DataStreamType, MetaData> metadata;

    /** place holder for PID / Identifier namespace */
    private ObjectIdentifier identifier = null;
    

    /**
     * Constructor initializes internal representation of data, i.e.,
     * ArrayList of CargoObjects
     */
    public CargoContainer()
    {
        data = new ArrayList<CargoObject>();
        metadata = new HashMap<DataStreamType, MetaData>();
        this.addMetaData( new DublinCore() );
        log.trace( String.format( "Constructing new CargoContainer" ) );
    }


    public ObjectIdentifier getIdentifier()
    {
        return this.identifier;
    }


    /**
     * 
     * @return String version of the Identifier or empty string.
     */    
    public String getIdentifierAsString()
    {
        if (this.identifier == null)
        {
            return "";
        }
        
        return this.identifier.getIdentifier();
    }

    public void setIdentifier( ObjectIdentifier new_identifier )
    {
        this.identifier  = new_identifier; 
        
        this.getDublinCoreMetaData().setIdentifier( identifier.getIdentifier() );
    }

    
    /**
     * Adds a metadata element conforming to the {@link MetaData}
     * interface. If this class already contains a {@link MetaData}
     * element with the same identifier, the supplied metadata will
     * overwrite the existing metadata in this {@link CargoContainer}
     * 
     * @param metadataelement the MetaData element to be added to this CargoContainer
     */
    public void addMetaData( MetaData metadataelement )
    {
        for( Entry<DataStreamType, MetaData> meta : metadata.entrySet() )
        {
            if( meta.getValue().getClass() == metadataelement.getClass() )
            {
                log.info( String.format( "CargoContainer already contains the metadata element. Will overwrite with metadata type '%s'", metadataelement.getClass() ) );
            }
        }
        metadata.put( metadataelement.getType(), metadataelement );
    }


    /**
     * Adds a 'datastream' to the CargoContainer; a datastream is any kind of
     * data which, for the duration of the CargoContainer object to which it is
     * attached will be treated as binary data. To ensure (and guarantee) that
     * the program does not meddle with the data, it is added as a byte[] and
     * returned as a byte[]. No attempts are made to interpret the contained
     * data at any times.
     *
     * The returned id uniquely identifies the data and makes it available as a
     * CargoObject structure that encapsulates the information given to this
     * method through the getCargoObject() and getCargoObjects().
     *
     *
     * @param dataStreamName
     *            defines the name (type, really) of the datastream which is
     *            added to the CargoContainer
     * @param format
     *            specifies the type of material, which the datastream contains
     * @param submitter
     *            specifies the submitter (and legal owner) of the submitted
     *            data
     * @param language
     *            specifies the language of the datastream
     * @param mimetype
     *            specifies the MIME (really the Internet Media Type) of the
     *            datastream (see http://tools.ietf.org/html/rfc2388)
     * @param data
     *            contains the datastream to be added to the cargocontainer. The
     *            data is submitted as a byte[] and throughout the lifetime of
     *            the CargoContainer, it is treated as binary data; ie. not
     *            touched.
     *
     * @return a unique id identifying the submitted data
     */
    public long add( DataStreamType dataStreamName,
                     String format,
                     String submitter,
                     String language,
                     String mimetype,
                     byte[] data ) throws IOException
    {
        if( dataStreamName == null )
        {
            log.fatal( "dataStreamName cannot be null" );
            throw new IllegalArgumentException( "dataStreamName cannot be null" );
        }
        else if( (mimetype == null) || ( "".equals( mimetype.trim() ) ) )
        {
            log.fatal( "mimetype must be specified" );
            throw new IllegalArgumentException( "mimetype must be specified" );
        }
        else if( (language == null) || ( "".equals( language.trim() ) ) )
        {
            log.fatal( "language must be specified" );
            throw new IllegalArgumentException( "language must be specified" );
        }
        else if( (submitter == null) || ( "".equals( submitter.trim() ) ) )
        {
            log.fatal( "submitter must be specified" );
            throw new IllegalArgumentException( "submitter must be specified" );
        }
        else if( (format == null) || ( "".equals( format.trim() ) ) )
        {
            log.fatal( "format must be specified" );
            throw new IllegalArgumentException( "format must be specified" );
        }
        else if( (data == null) || (data.length <= 0) )
        {
            log.fatal( "data must be present " );
            throw new IllegalArgumentException( "data must be present " );
        }

        CargoObject co = new CargoObject( dataStreamName,
                mimetype,
                language,
                submitter,
                format,
                data );

        this.data.add( co );
        log.debug( String.format( "cargoObject with id '%s' added to container", co.getId() ) );
        log.debug( String.format( "number of CargoObjects: %s", getCargoObjectCount() ) );

        return co.getId();
    }


    /**
     * Given an id, this method removes a CargoObject from the
     * CargoContainer. If the id can be found, and the object can be
     * deleted, this method returns true, all other scenarios will
     * return false.
     *
     * @param id of the data to be removed from the CargoContainer
     * @return true iff the referenced data could be deleted from the CargoContainer, false otherwise
     */
    public boolean remove( long id )
    {
        if( !this.hasCargo( id ) )
        {
            log.warn( String.format( "No CargoObject with id %s", id ) );
            return false;
        }
        else
        {
            return this.data.remove( this.getCargoObject( id ) );
        }

    }


    /**
     * Removes metadata with type {@code metadatatype} from this CargoContainer.
     * If the CargoContainer does not contains the metadata referenced, this
     * method will return false. If the method successfully removed the
     * metadata, it returns true
     *
     * @param metadatatype the type of the MetaData element to be removed
     * @return true if the MetaData element could be removed, false otherwise
     */
    public boolean removeMetaData( DataStreamType metadatatype )
    {
        if ( this.hasMetadata( metadatatype ) )
        {
            this.metadata.remove( metadatatype );
            return true;
        }else
        {
            return false;
        }
    }

    /**
     * Given an id, this method returns true if a CargoObject with the
     * id was found, false otherwise
     *
     * @param id the id to match in the CargoObjects
     *
     * @return true iff a CargoObject matched the id, false otherwise
     */
    public boolean hasCargo( long id )
    {
        for( CargoObject co : data )
        {
            if( co.getId() == id )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Given a DataStreamTyped, this method returns true if a
     * CargoObject with the type was found, false otherwise
     *
     * @param type the DataStreamType to match in the CargoObjects
     *
     * @return true iff a CargoObject matched the DataStreamType,
     * false otherwise
     */
    public boolean hasCargo( DataStreamType type )
    {
        for( CargoObject co : data )
        {
            if( co.getDataStreamType() == type )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Gets a specific CargoObject based on the id that was returned from the
     * add method. Please note, that if the id does not map to a CargoObject,
     * the method returns null.
     *
     * @param id The id returned from the add method
     *
     * @return CargoObject or a null CargoObject if id isn't found
     */
    public CargoObject getCargoObject( long id )
    {
        CargoObject ret_co = null;
        for( CargoObject co : data )
        {
            if( id == co.getId() )
            {
                ret_co = co;
            }
        }

        if( null == ret_co )
        {
            log.warn( String.format( "Could not retrieve CargoObject with id %s", id ) );
            //we'll let the client deal with null.
        }
        return ret_co;
    }


    /**
     * Based on the {@link DataStreamType}, the first {@link
     * CargoObject} matching the type is returned. This method should
     * only be used, if you know that there is exactly one {@link
     * CargoObject} with the type in the {@link CargoContainer}. If
     * there are more, or if you are unsure, please use the {@link
     * #getCargoObjects()} method instead. Use the {@link
     * #getCargoObjectCount()} method to find out how many
     * CargoObjects matching a specific DataStreamType that resides in
     * the CargoContainer.
     *
     * Please note that this method returns null if no matching
     * CargoObjects were found. Use {@link #hasCargo(DataStreamType)}
     * to check beforehand or check for nulls afterward.
     *
     * @param type The DataStreamType to find the CargoObject from
     *
     * @return The first CargoObject that matches the DataStreamType
     * or null if no matches were found
     */
    public CargoObject getCargoObject( DataStreamType type )
    {
        CargoObject ret_co = null;
        for( CargoObject co : data )
        {
            if( type == co.getDataStreamType() )
            {
                ret_co = co;
            }
        }

        if( null == ret_co )
        {
            log.warn( String.format( "Could not retrieve CargoObject with DataStreamType %s", type ) );
            //we'll let the client deal with null.
        }

        return ret_co;
    }


    /**
     * Get the count of all CargoObjects that have type as their DataStreamType.
     *
     * @param type The DataStreamType to match in the CargoObjects
     *
     * @return the count of CargoObjects matching the type
     */
    public int getCargoObjectCount( DataStreamType type )
    {
        int count = 0;
        for( CargoObject co : data )
        {
            if( type == co.getDataStreamType() )
            {
                count++;
            }
        }

        return count;
    }


    /**
     * Get the total number of CargoObjects in the CargoContainer
     *
     * @return the count of CargoObjects in the CargoContainer
     */
    public int getCargoObjectCount()
    {
        return data.size();
    }

    /**
     * Get the total number of Objects in the CargoContainer, with the
     * return value representing the CargoObjects together with the
     * MetaData objects
     *
     * @return the count of CargoObjects in the CargoContainer
     */
    public int getTotalObjectCount()
    {
        return data.size() + metadata.size();
    }

    /**
     * Returns a List of CargoObjects that matches the DataStreamType. If you
     * know that there are only one CargoObject matching the DataStreamType, use
     * getCargoObject() instead. If no CargoObjects match the DataStreamType,
     * this method returns null.
     *
     * @param type The DataStreamType to find the CargoObject from
     *
     * @return a List of CargoObjects or a null List if none were found
     */
    public List<CargoObject> getCargoObjects( DataStreamType type )
    {
        List<CargoObject> ret_list = new ArrayList<CargoObject>();
        for( CargoObject co : data )
        {
            if( type == co.getDataStreamType() )
            {
                ret_list.add( co );
            }
        }

        // the returned list must contain the same number of CargoObjects
        // that getCargoObjectCount(DataStreamType type) does
        assert (getCargoObjectCount( type ) == ret_list.size());

        return ret_list;
    }


    /**
     * Returns a List of all the CargoObjects that are contained in the
     * CargoContainer. If no CargoObjects are found, a null List object is
     * returned
     *
     * @return a List of all CargoObjects from the CargoContainer or a null List
     *         object if none are found
     */
    public List<CargoObject> getCargoObjects()
    {
        return data;
    }


    /**
     * Given a DataStreamType of a CargoObject, this method returns the IndexingAlias
     * for the data in the CargoObject
     *
     * @param dataStreamType the DataStreamType to match the CargoObject with
     * @return the alias that is used to index the data in the CargoObject with
     */
    public String getIndexingAlias( DataStreamType dataStreamType )
    {
        String ret_ia = null;
        for( CargoObject co : data )
        {
            if( dataStreamType == co.getDataStreamType() )
            {
                ret_ia = co.getIndexingAlias();
            }
        }
        if( null == ret_ia )
        {
            log.warn( String.format( "Could not retrieve IndexingAlias with DataStreamType %s", dataStreamType ) );
            //we'll let the client deal with null.
        }

        return ret_ia;
    }

    /**
     * This method sets the IndexingAlias on a DataStream
     * The method assumes that there are only 1 of each DataStreamType 
     * 
     * @param indexingAlias, the value to set on a specific dataStreamType  
     * @param dataStreamType, the specification of which DataStreamType 
     * to set the alias on
     * @return false if the type of DataStreamType wasnt found, otherwise true
     */
    public boolean setIndexingAlias( String indexingAlias, DataStreamType dataStreamType )
    {
        for( CargoObject co : data )
        {
            if( dataStreamType == co.getDataStreamType() )
            {
                co.setIndexingAlias( indexingAlias );
                return true;
            }
        }
        return false;
    }


    public boolean hasMetadata( DataStreamType type )
    {
        for( Entry<DataStreamType, MetaData> meta : metadata.entrySet() )
        {
            if( meta.getKey() == type )
            {
                return true;
            }
        }
        return false;
    }


    public List<MetaData> getMetaData()
    {
        List<MetaData> retval = new ArrayList<MetaData>();
        for( Entry<DataStreamType, MetaData> meta : metadata.entrySet() )
        {
            retval.add( meta.getValue() );
        }

        if( retval == null )
        {
            log.warn( "Could not retrieve MetaData elements from CargoContainer" );
        }
        return retval;
    }


    public MetaData getMetaData( DataStreamType mdst )
    {
        MetaData retval = null;
        for( Entry<DataStreamType, MetaData> meta : metadata.entrySet() )
        {
            if( meta.getKey() == mdst )
            {
                retval = meta.getValue();
            }
        }

        if( retval == null )
        {
            log.info( String.format( "No metadata with type %s in CargoContainer", mdst.getName() ) );
        }
        return retval;

    }


    public DublinCore getDublinCoreMetaData()
    {
        DublinCore retval = null;
        for( Entry<DataStreamType, MetaData> meta : metadata.entrySet() )
        {
            if( meta.getKey() == DataStreamType.DublinCoreData )
            {
                retval = (DublinCore) meta.getValue();
            }
        }
        if( retval == null )
        {
            log.warn( "No DublinCore element found in CargoContainer" );
        }
        return retval;
    }

 
}

