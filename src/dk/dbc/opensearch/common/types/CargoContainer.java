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


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
    private ArrayList<CargoObject> data;
    private String dcIdentifier = null;
    private String dcTitle = null;
    private String dcCreator = null;
    private String dcType = null;
    private String dcSource = null;
    private String _001_a = null;
    private String dcRelation = null;

    /**
     * Constructor initializes internal representation of data, i.e.,
     * ArrayList of CargoObjects
     */
    public CargoContainer()
    {
        data = new ArrayList<CargoObject>();
        log.trace( String.format( "Constructing new CargoContainer" ) );
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
     * @param alias
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
                     byte[] data ) throws IOException
    {
        if( dataStreamName == null )
        {
            log.fatal( "dataStreamName cannot be null" );
            throw new IllegalArgumentException( "dataStreamName cannot be null" );
        }
        else if( (mimetype == null) || (mimetype.equals( "" )) )
        {
            log.fatal( "mimetype must be specified" );
            throw new IllegalArgumentException( "mimetype must be specified" );
        }
        else if( (language == null) || (language.equals( "" )) )
        {
            log.fatal( "language must be specified" );
            throw new IllegalArgumentException( "language must be specified" );
        }
        else if( (submitter == null) || (submitter.equals( "" )) )
        {
            log.fatal( "submitter must be specified" );
            throw new IllegalArgumentException( "submitter must be specified" );
        }
        else if( (format == null) || (format.equals( "" )) )
        {
            log.fatal( "format must be specified" );
            throw new IllegalArgumentException( "format must be specified" );
        }
        else if( alias == null )
        {
            log.fatal( "alias must be specified" );
            throw new IllegalArgumentException( "alias must be specified" );
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
                alias,
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

        /** \todo: is it okay to return null? */
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
            //we'll let the client deal with null;
            //throw new NullPointerException( String.format( "Could not retrieve CargoObject with DataStreamType %s", type ) );
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
     * Given an id of a CargoObject, this method returns the DataStreamType
     * which the CargoObject was registered with
     *
     * @param id the id to match the CargoObject with
     * @return the DataStreamType of the CargoObject with the specified id
     */
    //    public DataStreamType getDataStreamType( long id )
    //    {
    //        return null;
    //    }
    /**
     * Given an id of a CargoObject, this method returns the IndexingAlias
     * for the data in the CargoObject
     *
     * @param id the id to match the CargoObject with
     * @return the alias that is used to index the data in the CargoObject with
     */
    public IndexingAlias getIndexingAlias( long id )
    {
        IndexingAlias ret_ia = null;
        for( CargoObject co : data )
        {
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
    public IndexingAlias getIndexingAlias( DataStreamType dataStreamType )
    {
        IndexingAlias ret_ia = null;
        for( CargoObject co : data )
        {
            if( dataStreamType == co.getDataStreamType() )
            {
                ret_ia = co.getIndexingAlias();
            }
        }

        return ret_ia;
    }


    /**
     * \todo: all the following values are implementation specific for
     * dublin core and marc data, they should not be exposed in a
     * CargoContainer type. A preferrable way of handling (almost)
     * arbitrary fields would be a string dictionary restricted to
     * namespaces. Something along the lines of
     * 
     * HashMap<Pair<NamespaceContext, String>, <String>> datafields = new HashMap<Pair<NamespaceContext, String>, <String>>();
     * ...
     * cargo.addDatafield( new InputPair<NamespaceContext, String>( new FedoraNamespaceContext().FedoraNamespace.DublinCore, "identifier" ), "data subject id" );
     */

    public void setDCIdentifier( String dcIdentifier )
    {
        this.dcIdentifier = dcIdentifier;
    }


    public String getDCIdentifier() throws IllegalStateException
    {
        if( this.dcIdentifier == null || this.dcIdentifier.isEmpty() || this.dcIdentifier.equals( "" ) )
        {
            log.warn( String.format( "Identifier for CargoContainer not specified" ) );
            //if identifier is not given in the constructor, a missing identifier is a valid state.
            //throw new IllegalStateException(  );
            }

        return this.dcIdentifier;
    }


    public void setDCTitle( String dcTitle )
    {
        //\todo: clean up this code
        if( this.dcTitle == null )
        {
            if( dcTitle.contains( "[Materialevurdering]" ) )
            {
                dcTitle = dcTitle.replaceAll( "[Materialevurdering]", "" );
            }

            this.dcTitle = dcTitle;
        }
    }


    public String getDCTitle()
    {
        if( this.dcTitle != null )
        {
            return this.dcTitle;
        }
        else
        {
            return "";
        }
    }


    public void setDCCreator( String dcCreator )
    {
        if( this.dcCreator == null )
        {
            this.dcCreator = dcCreator;
        }
    }


    public String getDCCreator()
    {
        if( this.dcCreator != null )
        {
            return this.dcCreator;
        }
        else
        {
            return "";
        }
    }


    public void setDCType( String dcType )
    {
        if( this.dcType == null )
        {
            this.dcType = dcType;
        }
    }


    public String getDCType()
    {
        if( this.dcType != null )
        {
            return this.dcType;
        }
        else
        {
            return "";
        }
    }


    public void setDCSource( String dcSource )
    {
        if( this.dcSource == null )
        {
            this.dcSource = dcSource;
        }
    }


    public String getDCSource()
    {
        if( this.dcSource != null )
        {
            return this.dcSource;
        }
        else
        {
            return "";
        }
    }


    public void set_001_a( String _001_a )
    {
        if( this._001_a == null )
        {
            this._001_a = _001_a;
        }
    }


    public String get_001_a()
    {
        if( this._001_a != null )
        {
            return this._001_a;
        }
        else
        {
            return "";
        }
    }


    public void setDCRelation( String dcRelation )
    {
        if( this.dcRelation == null )
        {
            this.dcRelation = dcRelation;
        }
    }


    public String getDCRelation()
    {
        if( this.dcRelation != null )
        {
            return this.dcRelation;
        }
        else
        {
            return "";
        }
    }


}
