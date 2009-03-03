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


    /**
     * Gets the format (type of material) of the CargoObject
     * 
     * @return the format as a String
     */
    public String getLang()
    {
        return pair.getFirst().getLanguage();
    }


    public String getFormat()
    {
        return pair.getFirst().getFormat();
    }


    public String getLanguage()
    {
        return pair.getFirst().getLanguage();
    }


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
     * Returns the length of the underlying byte[]
     * 
     * @return length of the byte[]
     */
    public int getByteArrayLength(){
        return pair.getSecond().length;
    }

}