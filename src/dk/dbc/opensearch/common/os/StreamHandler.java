package dk.dbc.opensearch.common.os;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * StreamHandler is an appendix to FileHandler and provides methods for handling Streams within the opensearch project
 */
public class StreamHandler 
{
    /*
     * bytesFromInputStream extracts a byte array from an InputStream.
     * 
     * @param in The InputStream to extract the byte array from
     * @param chunkSize the number of bytes to read in each pass on the InputStream
     * 
     * @throws IOException if the InputStream cannot be fully converted into a byte array
     * 
     * @return the extracted bytearray containing the bytes from the stream
     * 
     */
    public static byte[] bytesFromInputStream( InputStream in, int chunkSize ) throws IOException
    {
        if( chunkSize < 1 )
        {
            chunkSize = 1024;
        }

        int bytesRead;
        byte[] result;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] b = new byte[chunkSize];

        try 
        {

            while( ( bytesRead = in.read( b, 0, chunkSize ) ) > 0 )
            {
                baos.write( b, 0, bytesRead );
            }

            result = baos.toByteArray();

        }
        finally 
        {
            baos.close();
        }
        
        return result;
    }
}