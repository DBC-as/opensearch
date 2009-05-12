/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package common.types
 */
package dk.dbc.opensearch.common.types;

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

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

//import dk.dbc.opensearch.common.types.IndexingAlias;

import org.apache.log4j.Logger;

/**
 * \ingroup common.types
 * \brief CargoContainer is a data structure used throughout
 *  OpenSearch for carrying information submitted for
 *  indexing. CargoContainer retains data in a private data structure
 *  consisting of CargoObject objects. All verification and work with
 *  theses objects are done through the CargoObject class.
 */
public class CargoContainer
{

    Logger log = Logger.getLogger( CargoContainer.class );

    /** The internal representation of the data contained in the CargoContainer*/
    private ArrayList< CargoObject > data;

    /**
     * Constructor initializes internal representation of data, i.e.,
     * ArrayList of CargoObjects
     */
    public CargoContainer()
    {
        data = new ArrayList< CargoObject >();
        log.debug( String.format( "Constructing new CargoContainer" ) );
    }

    /**
     * Add CargoObject to internal data representation.
     *
     * @param format
     * @param submitter
     * @param language
     * @param mimetype
     * @param data
     * @throws IOException
     * @deprecated use add() with IndexingAlias specified
     */
    @Deprecated
    public void add( DataStreamType dataStreamName, 
                     String format, 
                     String submitter, 
                     String language, 
                     String mimetype, 
                     byte[] data ) 
        throws IOException, NullPointerException
    {
        log.warn( String.format( "Use of deprecated method" ) );
        add( dataStreamName, 
                        format,
                        submitter, 
                        language, 
                        mimetype, 
                        null,
                        data );
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
     * @param indexingAlias
     *            specifies which alias should be used when indexing the
     *            datastream
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
                    IndexingAlias alias, 
                    byte[] data ) 
        throws IOException
    {


        if( dataStreamName == null ) 
        {
            log.fatal( "dataStreamName cannot be null" );
            throw new NullPointerException( "dataStreamName cannot be null" );
        }
        if( ( mimetype == null ) || ( mimetype .equals( "" ) ) ) 
        {
            log.fatal( "mimetype must be specified" );
            throw new NullPointerException( "mimetype must be specified" );
        }
        if( ( language == null ) || ( language .equals( "" ) ) ) 
        {
            log.fatal( "language must be specified" );
            throw new NullPointerException( "language must be specified" );
        }
        if( ( submitter == null ) || ( submitter .equals( "" ) ) ) 
        {
            log.fatal( "submitter must be specified" );
            throw new NullPointerException( "submitter must be specified" );
        }
        if( ( format == null ) || ( format.equals( "" ) ) ) 
        {
            log.fatal( "format must be specified" );
            throw new NullPointerException( "format must be specified" );
        }
        if( alias == null ) {
            log.fatal( "alias must be specified" );
            throw new NullPointerException( "alias must be specified" );
        }
        if( ( data == null ) || ( data.length <= 0 ) ) 
        {
            log.fatal( "data must be present " );
            throw new NullPointerException( "data must be present " );
        }
        

        CargoObject co = new CargoObject( dataStreamName, 
                                          mimetype, 
                                          language, 
                                          submitter, 
                                          format,
                                          alias,
                                          data );

        this.data.add( co );
        log.debug( String.format( "cargoObject with id '%s' added to container", 
                                  co.getId() ) );
        log.debug( String.format( "number of CargoObjects: %s", 
                                  getCargoObjectCount() ) );

        return co.getId();
    }


    /**
     * Given an id, this method returns true if a CargoObject with the
     * id was found, false otherwise
     *
     * @param id the id to match in the CargoObjects
     *
     * @return true iff a CargoObject matched the id, false otherwise
     */
    public boolean hasCargo( long id ){
        for( CargoObject co : data )
        {
            if( co.getId() == id ) {
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
    public boolean hasCargo( DataStreamType type ){
        for( CargoObject co : data )
        {
            if( co.getDataStreamName() == type ) {
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
    public CargoObject getCargoObject( long id ) {
        CargoObject ret_co = null;
        for( CargoObject co : data )
        {
            if( id == co.getId() )
            {
                ret_co = co;
            }
        }
        return ret_co;
    }

    /**
     * Based on the DataStreamType, the first CargoObject matching the type is
     * returned. This method should only be used, if you know that there is
     * exactly one CargoObject with the type in the CargoContainer. If there are
     * more, or if you are unsure, please use the getCargoObjects() method
     * instead. Use the getCargoObjectCount() method to find out how many
     * CargoObjects matching a specific DataStreamType that resides in the
     * CargoContainer.
     *
     * @param type The DataStreamType to find the CargoObject from
     *
     * @return The first CargoObject that matches the DataStreamType
     */
    public CargoObject getCargoObject( DataStreamType type ) {
        CargoObject ret_co = null;
        for( CargoObject co : data )
        {
            if( type == co.getDataStreamName() )
            {
                ret_co = co;
            }
        }
        if( null == ret_co ) {
            log.error( String.format( "Could not retrieve CargoObject with DataStreamType %s", type ) );
            throw new NullPointerException( String.format( "Could not retrieve CargoObject with DataStreamType %s", type ) );
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
    public int getCargoObjectCount( DataStreamType type ) {
        int count = 0;
        for( CargoObject co : data )
        {
            if( type == co.getDataStreamName() )
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
    public int getCargoObjectCount() {
        return data.size();
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
    public List<CargoObject> getCargoObjects(DataStreamType type) {
        List<CargoObject> ret_list = new ArrayList<CargoObject>();
        for( CargoObject co : data ){
            if( type == co.getDataStreamName() ) {
                ret_list.add( co );
            }
        }

        // the returned list must contain the same number of
        // CargoObjects that getCargoObjectCount(DataStreamType type)
        // does
        assert( getCargoObjectCount( type ) == ret_list.size() );

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
    public List<CargoObject> getCargoObjects() {
        return data;
    }

    /**
     * Given an id of a CargoObject, this method returns the DataStreamType
     * which the CargoObject was registered with
     *
     * @param id the id to match the CargoObject with
     * @return the DataStreamType of the CargoObject with the specified id
     */
    public DataStreamType getDataStreamType( long id ) {
        // TODO Auto-generated method stub
        return null;
    }

    /**
     * Given an id of a CargoObject, this method returns the IndexingAlias
     * for the data in the CargoObject
     *
     * @param id the id to match the CargoObject with
     * @return the alias that is used to index the data in the CargoObject with
     */
    public IndexingAlias getIndexingAlias( long id ) {
        IndexingAlias ret_ia = null;
        log.debug( String.format( "id to test for = %s", id ) );
        for( CargoObject co : data ){
            log.debug( String.format( "co.getId() = %s", co.getId() ) );
            if( id == co.getId() )
            {
                ret_ia = co.getIndexingAlias();
            }
        }

        return ret_ia;
    }

    /**
     * Given a DataStreamType of a CargoObject, this method returns the IndexingAlias
     * for the data in the CargoObject
     *
     * @param dataStreamType the DataStreamType to match the CargoObject with
     * @return the alias that is used to index the data in the CargoObject with
     */
    public IndexingAlias getIndexingAlias( DataStreamType dataStreamType ) {
        IndexingAlias ret_ia = null;
        for( CargoObject co : data ){
            if( dataStreamType == co.getDataStreamName() )
            {
                ret_ia = co.getIndexingAlias();
            }
        }

        return ret_ia;
    }


}
