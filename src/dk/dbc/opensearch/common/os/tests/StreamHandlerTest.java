/** \brief UnitTest for StreamHandler */

package dk.dbc.opensearch.common.os.tests;


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