/** 
 * \brief UnitTest for StreamHandler 
 */

package dk.dbc.opensearch.common.os;


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


import dk.dbc.opensearch.common.os.StreamHandler;

import java.io.InputStream;
import java.io.IOException;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * 
 */
public class StreamHandlerTest 
{
    /**
     *
     */

    String testString = "hat";
    InputStream input;
    byte[] matchData;
    byte[] dataOut;
    

    @Before 
    public void SetUp() throws Exception
    {
        matchData = testString.getBytes( "UTF-8" );
        input = new ByteArrayInputStream( testString.getBytes( "UTF-8" ) );
    }


    /**
     *
     */
    @After 
    public void TearDown() { }


    /**
     * 
     */
    @Test 
    public void testChunkSizeCondTrue() throws IOException
    {
        dataOut = StreamHandler.bytesFromInputStream( input, 0 );
        boolean val = false;
        if( dataOut.length == matchData.length ){
            val = true;
            for( int i = 0; i < dataOut.length; i++ )
            {
                if( !(dataOut[i] == matchData[i]) )
                {
                    val = false;
                }
            }
        }

        assertTrue( val ); 
    }


    @Test 
    public void testChunkSizeCondFalse() throws IOException
    {
        dataOut = StreamHandler.bytesFromInputStream( input, 1 );
        boolean val = false;
        if( dataOut.length == matchData.length ){
            val = true;
            for( int i = 0; i < dataOut.length; i++ )
            {
                if (!(dataOut[i] == matchData[i]) )
                {
                    val = false;
                }
            }
        }
        assertTrue( val );
    }
}