/** \brief UnitTest for CargoObject */

package dk.dbc.opensearch.common.types.tests;

import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import static org.junit.Assert.*;
import org.junit.*;

/**
 * This class test the parts of the CargoObjct that is not caught in the 
 * tests of other classes. The getTimeStamp method of the CargoObject 
 * is not tested.
 */
public class CargoObjectTest {

    /**
     *
     */
    CargoObject co;

    private String format;
    private String language;
    private String mimetype;
    private String submitter;

    private byte[] data;
    private DataStreamType dst;
    String teststring;

    @Before public void SetUp() throws UnsupportedEncodingException
    {
        dst = DataStreamType.getDataStreamNameFrom( "originalData" );
        format = "forfatterweb";
        language = "DA";
        mimetype = "text/xml";
        submitter = "dbc";
        teststring = "æøå";
        data = teststring.getBytes( "UTF-8" );

        // co = new CargoObject( dst, mimetype, language, submitter, format, data );
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * happy path
     */
    @Test public void testConstructor() throws IOException
    {
        co = new CargoObject( dst, mimetype, language, submitter, format, data );
    }

    /**
     * testing the language getter
     */
    @Test public void testGetLang() throws IOException
    {
        co = new CargoObject( dst, mimetype, language, submitter, format, data );
        assertEquals( co.getLang(), language );
    }

    /**
     * Testing the getter for the length of the data og the CargoObject
     */

    @Test public void testGetByteArrayLength() throws IOException
    {
        co = new CargoObject( dst, mimetype, language, submitter, format, data );
        assertTrue( data.length == co.getByteArrayLength() );
    }
}

