/** \brief UnitTest for StreamHandler */

package dk.dbc.opensearch.common.os.tests;

import dk.dbc.opensearch.common.os.StreamHandler;

import java.io.InputStream;
import java.io.IOException;
//import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class StreamHandlerTest {

    /**
     *
     */

    String testString = "hat";
    InputStream input;
    byte[] matchData;
    byte[] dataOut;
    
    @Before public void SetUp() throws Exception
    {
        matchData = testString.getBytes( "UTF-8" );
        input = new ByteArrayInputStream( testString.getBytes( "UTF-8" ) );
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test public void testChunkSizeCondTrue() throws IOException
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


    @Test public void testChunkSizeCondFalse() throws IOException
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