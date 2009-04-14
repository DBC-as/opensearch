
package dk.dbc.opensearch.common.types.tests;


/*
*GNU, General Public License Version 3. If any software components linked 
*together in this library have legal conflicts with distribution under GNU 3 it 
*will apply to the original license type.
*
*Software distributed under the License is distributed on an "AS IS" basis,
*WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
*for the specific language governing rights and limitations under the
*License.
*
*Around this software library an Open Source Community is established. Please 
*leave back code based upon our software back to this community in accordance to 
*the concept behind GNU. 
*
*You should have received a copy of the GNU Lesser General Public
*License along with this library; if not, write to the Free Software
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
***** END LICENSE BLOCK ***** */
/** \brief UnitTest for CargoContainerT **/

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.IndexingAlias;
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
    private DataStreamType dsn;
    String teststring;


    @Before
        public void SetUp() throws UnsupportedEncodingException
    {
        dsn = DataStreamType.getDataStreamNameFrom( "originalData" );
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

        byte[] is = new byte[0];
        CargoContainer cc = new CargoContainer();
        cc.add( dsn, format, submitter, language, mimetype, is );

        CargoObject co = cc.getData().get( 0 );
        byte[] list = co.getBytes();

        if( list.length == 0)
            throw new NullPointerException();
    }


    @Test public void testGetByteArrayPreservesUTF8() throws IOException, UnsupportedEncodingException
    {
        cargo.add( dsn, format, submitter, language, mimetype, data );

        ArrayList< CargoObject > aList = cargo.getData();
        byte[] listB = aList.get( 0 ).getBytes();
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
        byte[] data1 = str1.getBytes();

        String str2 = "abc";
        byte[] data2 = str2.getBytes();

        String str3 = "abc";
        byte[] data3 = str3.getBytes();

        String str4 = "abc";
        byte[] data4 = str4.getBytes();

        cc.add( dsn, format, submitter, language, mimetype, data1);
        cc.add( dsn, format, submitter, language, mimetype, data2);
        cc.add( dsn, format, submitter, language, mimetype, data3);
        cc.add( dsn, format, submitter, language, mimetype, data4);

        int expectedCount = 4;
        int actualCount = cc.getItemsCount();

        assertEquals( expectedCount, actualCount );
    }

    @Test public void testGetFirstCargoObject() throws IOException
    { 
        CargoContainer cc = new CargoContainer();
        CargoObject co;
        DataStreamType dst1 = DataStreamType.getDataStreamNameFrom( "originalData" );
        DataStreamType dst2 = DataStreamType.getDataStreamNameFrom( "indexableData" );
        DataStreamType dst3 = DataStreamType.getDataStreamNameFrom( "adminData" );
        String str1 = "abc";
        byte[] data1 = str1.getBytes();
        String str2 = "abc";
        byte[] data2 = str2.getBytes();

        cc.add( dst1, format, submitter, language, mimetype, data1);
        cc.add( dst2, format, submitter, language, mimetype, data2);

        co = cc.getFirstCargoObject( dst2 );
        assertTrue(data2 == co.getBytes() );

        assertTrue( cc.getFirstCargoObject( dst3 ) == null  );

    }

    @Test public void testIndexingAliasSetAndGet() //throws IOException
    {
        CargoContainer cc = new CargoContainer();
        IndexingAlias ia = IndexingAlias.getIndexingAlias( "article" );
        cc.setIndexingAlias( ia );
        assertTrue( ia == cc.getIndexingAlias() );
    }
    @Test public void testFilePathSetAndGet()
    {
        CargoContainer cc = new CargoContainer();
        String testFilePath = "testPath";
        cc.setFilePath( testFilePath );
        assertTrue( cc.getFilePath().equals( testFilePath ) );
    }
}