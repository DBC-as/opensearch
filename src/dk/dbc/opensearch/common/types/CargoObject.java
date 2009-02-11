package dk.dbc.opensearch.common.types;


import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


public class CargoObject
{
	Pair< CargoObjectInfo, List< Byte > > pair;

    
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

    
    public Pair< CargoObjectInfo, List< Byte > > getPair()
    {
    	return pair;
    }
    
    
    public boolean checkLanguage(String language )
    {
        return pair.getFirst().checkLanguage( language );
    }

    
    public boolean validMimetype( String mimetype )
    {
    	return pair.getFirst().validMimetype( mimetype );
    }
    
    
    public boolean checkSubmitter( String name ) throws IllegalArgumentException
    {
        return this.checkSubmitter( name );
    }
    
    
    public int getContentLength()
    {
    	return pair.getSecond().size();
    }
    
    
    public String getFormat( CargoObjectInfo key )
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
}