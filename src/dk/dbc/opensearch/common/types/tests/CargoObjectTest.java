/** \brief UnitTest for CargoObject */

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

