package dk.dbc.opensearch.common.types.tests;
/** \brief UnitTest for CargoContainerT **/

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.types.Pair;
import fedora.common.policy.DatastreamNamespace;
import fedora.server.types.gen.Datastream;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.*;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;


/**
 *
 */
public class CargoContainerTest
{
    CargoContainer cargo;

    private String format;
    private String language;
    private String mimetype;
    private String submitter;
    //private InputStream data;
    private byte[] data;
    private DataStreamNames dsn;
    String teststring;


    @Before
        public void SetUp() throws UnsupportedEncodingException
    {
        dsn = DataStreamNames.getDataStreamNameFrom( "originalData" );
        format = "forfatterweb";
        language = "DA";
        mimetype = "text/xml";
        submitter = "DBC";

        teststring = "æøå";
        data = teststring.getBytes( "UTF-8" );
        //data = new ByteArrayInputStream( teststring.getBytes( "UTF-8" ) );
        cargo = new CargoContainer();
    }


    /**
     *
     * @throws IOException
     */
    @Test
        public void testAdd() throws IOException
    {
        CargoContainer cargo = new CargoContainer();
        assertEquals( 0, (long)cargo.getData().size() );

        cargo.add( dsn, format, submitter, language, mimetype, data );
        assertEquals( 1, (long)cargo.getData().size() );
    }


    /**
     * @throws IOException
     *
     */
    @Test public void testStreamSizeInContainer() throws IOException
    {
        cargo.add( dsn, format, submitter, language, mimetype, data );

        //UTF-8 uses two bytes per Danish letter
        int expectedLength = teststring.length() * 2;

        ArrayList< CargoObject > list = cargo.getData();
        CargoObject co = list.get( 0 );
        int contentLength = co.getContentLength();

        assertTrue( expectedLength == contentLength );
    }


    @Test(expected = NullPointerException.class)
        public void testStreamCannotBeEmpty() throws IOException
    {

        //InputStream is = new ByteArrayInputStream( new byte[0] );
        byte[] is = new byte[0];
        CargoContainer cc = new CargoContainer();
        cc.add( dsn, format, submitter, language, mimetype, is );

        CargoObject co = cc.getData().get( 0 );
        Pair< CargoObjectInfo, byte[] > pair = co.getPair();
        byte[] list = pair.getSecond();

        if( list.length == 0)
            throw new NullPointerException();
    }


    @Test public void testGetByteArrayPreservesUTF8() throws IOException, UnsupportedEncodingException
    {
        cargo.add( dsn, format, submitter, language, mimetype, data );

        ArrayList< CargoObject > aList = cargo.getData();
        byte[] listB = aList.get( 0 ).getPair().getSecond();
        byte[] sixBytes = new byte[6];
        for( int i = 0; i < listB.length; i++ )
            sixBytes[i] = listB[i];

        assertTrue( teststring.equals( new String( sixBytes, "UTF-8" ) ) );
    }


    @Test
        public void testItemsCount() throws IOException
    {
        CargoContainer cc = new CargoContainer();

        String str1 = "abc";
        // InputStream data1 = new ByteArrayInputStream( str1.getBytes() );
        byte[] data1 = str1.getBytes();

        String str2 = "abc";
        //InputStream data2 = new ByteArrayInputStream( str2.getBytes() );
        byte[] data2 = str2.getBytes();

        String str3 = "abc";
        //InputStream data3 = new ByteArrayInputStream( str3.getBytes() );
        byte[] data3 = str3.getBytes();

        String str4 = "abc";
        //InputStream data4 = new ByteArrayInputStream( str4.getBytes() );
        byte[] data4 = str4.getBytes();

        cc.add( dsn, format, submitter, language, mimetype, data1);
        cc.add( dsn, format, submitter, language, mimetype, data2);
        cc.add( dsn, format, submitter, language, mimetype, data3);
        cc.add( dsn, format, submitter, language, mimetype, data4);

        int expectedCount = 4;
        int actualCount = cc.getItemsCount();

        assertEquals( expectedCount, actualCount );
    }
}