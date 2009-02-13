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
	Pair< CargoObjectInfo, List< Byte > > pair;

    
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
	CargoObject( String mimetype, String language, String submitter, String format, InputStream data ) throws IOException
    {
    	CargoMimeType cmt = CargoMimeType.getMimeFrom( mimetype );    	
        CargoObjectInfo coi = new CargoObjectInfo( cmt, language, submitter, format );   
    
        List< Byte > list = readStream( data ); 
        
        pair = new Pair<CargoObjectInfo, List< Byte > >( coi, list );
    }
    

    /**
     * Helper method that reads all the bytes from the submitted
     * InputStream into a List<Byte> datatype
     * 
     * @param is the InputStream containing the bytestream
     * @returns a List<Byte> containing the bytearray
     * @throws IOException if the stream could not be read or was
     * closed during reading
     */
    private List<Byte> readStream( InputStream is ) throws IOException
    {
        ArrayList<Byte> al = new ArrayList<Byte>();
    	
        while( is.available() > 0 )
        {
            Byte dataByte = new Byte( (byte)is.read() );
            al.add( dataByte ); 
        }
        
        return al;
    }

    
    /**
     * \todo: is this method needed at all?
     * @return
     */
    public Pair< CargoObjectInfo, List< Byte > > getPair()
    {
    	return pair;
    }
    
    
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
    	return pair.getSecond().size();
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

	
	/**
	 * This methods converts the internal representation into a native type
	 * 
	 * @return an array of bytes containing the contents of the underlying data
	 *         container
	 */
	public byte[] getBytes() 
	{
		int bsize = pair.getSecond().size();

		Byte[] bData = pair.getSecond().toArray(new Byte[bsize]);
		byte[] b_data = new byte[bsize];
		for (int i = 0; i < bData.length; i++) {
			b_data[i] = bData[i];
		}

		return b_data;
	}
	}