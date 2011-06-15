package dk.dbc.commons.os;

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

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayOutputStream;

/**
 * StreamHandler is an appendix to FileHandler and provides methods for 
 * handling Streams within the opensearch project
 */
public class StreamHandler 
{
    /**
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