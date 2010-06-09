/** \brief UnitTest for CargoObject */

package dk.dbc.opensearch.common.types;

/**
 *
 * This file is part of opensearch.
 * Copyright © 2009, Dansk Bibliotekscenter a/s,
 * Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 * opensearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opensearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */

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

    CargoObject co;

    String format;
    String language;
    String mimetype;
    String submitter;
    byte[] data;
    DataStreamType dst;
    String teststring;

    @Before
    public void SetUp() throws UnsupportedEncodingException, IOException
    {

        dst = DataStreamType.getDataStreamTypeFrom( "originalData" );
        format = "forfatterweb";
        language = "DA";
        mimetype = "text/xml";
        submitter = "dbc";
        teststring = "æøå";
        data = teststring.getBytes( "UTF-8" );
        co = new CargoObject( dst, 
                              mimetype, 
                              language, 
                              submitter,
                              format,
                              data );
    }

    /**
     * happy path + testing the getId()
     */
    @Test
    public void testConstructor() throws IOException
    {
        long id = co.getId();
        assertNotNull( id );
    }


    /**
     * testing the language getter
     */
    @Test public void testGetLang() throws IOException
    {
        assertEquals( co.getLang(), language );
    }


    /**
     * Testing that the internal representation equals the data inserted
     */
    @Test public void testGetByteArrayLength() throws IOException
    {
        co = new CargoObject( dst, mimetype, language, submitter, format, data );
        assertTrue( data.length == co.getContentLength() );
    }
}

