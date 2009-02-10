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

    
    Pair< CargoObjectInfo, List< Byte > > getData()
    {
    	return pair;
    }
    
    
    // \todo: this is a dummy method. to be deleted
    public int getContentLength()
    {
    	return -1;
    }
}