/**
 * \file CargoObject.java
 * \brief The CargoObject class
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
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;

/**
 * \ingroup common.types
 * 
 * \brief CargoObject is a data structure used throughout OpenSearch,
 * which basically consists of a pair (common.types.Pair) of
 * CargoObjectInfo and a byte[]. This class is the access point
 * (through the CargoObjectInfo object) for information about the
 * input stream stored in the byte[]. It is used a
 * complex type by the CargoContainer class.
 */
public class CargoObject
{
    /**
     * Internal data structure for the CargoObject class.
     */
    Pair< CargoObjectInfo, byte[] > pair;
    Logger log = Logger.getLogger( CargoObject.class );

    /**
     * Constructor for the CargoObject class. Here an object of the
     * type CargoMimeType is constructed, which in turn is used in the
     * construction of a CargoObjectInfoObject. Also, the InputStream
     * is read into a byte[] holding the actual data of the
     * object. The two are stored in a pair
     * (dk.dbc.opensearch.common.types.Pair).
     *
     * @param mimetype
     * @param language
     * @param submitter
     * @param format
     * @param data
     * @throws IOException
     */
    public CargoObject( DataStreamType dataStreamName, String mimetype, String language, String submitter, String format, byte[] data ) throws IOException
    {
        CargoMimeType cmt = CargoMimeType.getMimeFrom( mimetype );
        CargoObjectInfo coi = new CargoObjectInfo( dataStreamName, cmt, language, submitter, format );
        log.debug( String.format( "length of data: %s", data.length ) );
        pair = new Pair<CargoObjectInfo, byte[] >( coi, data );
    }

    /**
     * gets the name of the datastream (\see:
     * dk.dbc.opensearch.common.types.DataStreamNames)
     * 
     * @return the enum value of the name of the Datastream
     */
    public DataStreamType getDataStreamName()
    {
        return this.pair.getFirst().getDataStreamName();
    }
    

    /*  public String getDataStreamName( String name )
        {
        return this.pair.getFirst().getDataStreamNameFrom( name );
        }
    */
    

    /**
     * Checks if the language of the submitted data is allowed in a
     * CargoObject
     * 
     * @param language the language to be checked
     * @return True if language is allowed, False otherwise
     */
    public boolean checkLanguage( String language )
    {
        return pair.getFirst().checkLanguage( language );
    }


    /**
     * Checks if the mimetype of the submitted data is allowed in a
     * CargoObject. The string should contain a mimetype conforming 
     * to the RFC 822 ( http://www.faqs.org/rfcs/rfc822.html)
     * 
     * @param mimetype the mimetype to be checked
     * @return True if mimetype is allowed, False otherwise
     */
    public boolean validMimetype( String mimetype )
    {
        return pair.getFirst().validMimetype( mimetype );
    }


    /**
     * Checks if the submitter of the submitted data is allowed in a
     * CargoObject
     * 
     * @param submitter the submitter to be checked
     * @return True if mimetype is allowed, False otherwise
     */
    public boolean checkSubmitter( String name ) throws IllegalArgumentException
    {
        return pair.getFirst().checkSubmitter( name );
    }


    /**
     * Gets the size of the underlying byte array.
     *
     * @return the size of the byte[]
     */
    public int getContentLength()
    {
        return pair.getSecond().length;
    }


    public String getLang()
    {
        return pair.getFirst().getLanguage();
    }


    /**
     * Gets the format (type of material) of the CargoObject
     * 
     * @return the format as a String
     */
    public String getFormat()
    {
        return pair.getFirst().getFormat();
    }

    /*
    public String getLanguage()
    {
        return pair.getFirst().getLanguage();
    }
    */

    /**
     * Returns the mimetype of the data associated with the underlying
     * CargoObjectInfo
     *
     * @returns the mimetype of the data as a String
     */
    public String getMimeType()
    {
        return pair.getFirst().getMimeType();
    }


    /**
     * Returns the name of the submitter of the data associated with
     * the underlying CargoObjectInfo
     *
     * @returns the submitter as a String
     */
    public String getSubmitter()
    {
        return pair.getFirst().getSubmitter();
    }


    /**
     * Returns this CargoObject CargoObjectInfo's timestamp
     *
     * @returns the timestamp of the underlying CargoObjectInfo
     */
    public long getTimestamp()
    {
        return pair.getFirst().getTimestamp();
    }


    /**
     * Returns the underlying data in the CargoObject as a byte[]
     * 
     * @return a byte[] containing the data of the CargoObject
     */
    public byte[] getBytes()
    {
        return pair.getSecond();
    }

    /**
     * Handle with care
     * Overwrites the existing data with new data given in the byte[]
     */
    public void updateByteArray( byte[] data )
    {
        Pair<CargoObjectInfo, byte[]> new_pair = new Pair<CargoObjectInfo, byte[]>( pair.getFirst(), data );
        pair = new_pair;
    }
    
    /**
     * Returns the length of the underlying byte[]
     * 
     * @return length of the byte[]
     * 
     * \todo: this is a duplicate method. Please refactor one of us out.
     */
    public int getByteArrayLength(){
        return pair.getSecond().length;
    }

}