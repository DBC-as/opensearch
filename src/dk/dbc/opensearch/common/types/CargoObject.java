/**
 * \file CargoObject.java
 * \brief The CargoObject class
 * \package common.types
 */
package dk.dbc.opensearch.common.types;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


/**
 * \ingroup common.types
 * \brief CargoObject is a data structure used throughout OpenSearch, which basically consists of a
 * pair (common.types.Pair) of CargoObjectInfo and List< Byte >. This class is the access point
 * (through the CargoObjectInfo object) for information about the input stream stored in the
 * List< Byte > object. It is used a complex type by the CargoContainer class.
 */
public class CargoObject
{
    /**
     * Internal data structure for the CargoObject class.
     */
    Pair< CargoObjectInfo, byte[] > pair;


    /**
     * Constructor for the CargoObject class. Here an object of the type CargoMimeType is constructed,
     * which in turn is used in the construction of a CargoObjectInfoObject. Also, the InputStream
     * is read into a List< Byte > holding the actual data of the object. The two are stored in a
     * pair (common.types.Pair).
     *
     * @param mimetype
     * @param language
     * @param submitter
     * @param format
     * @param data
     * @throws IOException
     */
    CargoObject( DataStreamNames dataStreamName, String mimetype, String language, String submitter, String format, byte[] data ) throws IOException
    {
        CargoMimeType cmt = CargoMimeType.getMimeFrom( mimetype );
        CargoObjectInfo coi = new CargoObjectInfo( dataStreamName, cmt, language, submitter, format );

        pair = new Pair<CargoObjectInfo, byte[] >( coi, data );
    }

    
    public DataStreamNames getDataStreamName()
    {
        return this.pair.getFirst().getDataStreamName();
    }
    

    /*  public String getDataStreamName( String name )
        {
        return this.pair.getFirst().getDataStreamNameFrom( name );
        }
    */
    

    public boolean checkLanguage( String language )
    {
        return pair.getFirst().checkLanguage( language );
    }


    public boolean validMimetype( String mimetype )
    {
        return pair.getFirst().validMimetype( mimetype );
    }


    public boolean checkSubmitter( String name ) throws IllegalArgumentException
    {
        return pair.getFirst().checkSubmitter( name );
    }


    /**
     * Gets the size of the underlying byte array.
     *
     * @return the size of the List<Byte>
     */
    public int getContentLength()
    {
        return pair.getSecond().length;
    }


    public String getFormat()
    {
        return pair.getFirst().getFormat();
    }


    /**
     * Returns the mimetype of the data associated with the underlying CargoObjectInfo
     *
     * @returns the mimetype of the data as a string
     */
    public String getMimeType()
    {
        return pair.getFirst().getMimeType();
    }

    /**
     * Returns the name of the submitter of the data associated with the underlying CargoObjectInfo
     *
     * @returns the submitter as a string
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


    public byte[] getBytes()
    {
        return pair.getSecond();
    }
}