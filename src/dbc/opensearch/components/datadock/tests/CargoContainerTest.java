package dbc.opensearch.components.datadock.tests;
/** \brief UnitTest for CargoContainerT **/

import static org.junit.Assert.*;
import org.junit.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import dbc.opensearch.components.datadock.*;

/**
 * 
 */
public class CargoContainerTest {

    CargoContainer cargo;
	

    @Before public void SetUp(){
        InputStream data = new ByteArrayInputStream( new byte[1] );
        try{
            cargo = new CargoContainer( data, "", "", "" );
        } catch ( IOException ioe ){
            System.out.println( ioe.toString() );
        }
    }

    /**
     * 
     */
    @Test public void testStreamSizeInContainer() {
        int expectedLength = 1;
        assertTrue( expectedLength == cargo.getStreamLength() );
    }

    @Test(expected = NullPointerException.class) 
    public void testStreamCannotBeEmpty()throws IOException{
        InputStream is = new ByteArrayInputStream( new byte[0] );
        CargoContainer co = null;
        co = new CargoContainer( is, "", "", "" );
    }

    /** \todo: need real users and possibly a constructor-check instead of this */
    /** \todo: and this only really makes sense as a static method */
    @Test public void testAllowedSubmitter() {
        assertTrue( cargo.checkSubmitter( "stm" ) );
    }

    /** \todo: need real users and possibly a constructor-check instead of this */
    /** \todo: and this only really makes sense as a static method */
    @Test( expected = IllegalArgumentException.class )
    public void testDisallowedSubmitter() {
        cargo.checkSubmitter( "findes_ikke" );
    }

}