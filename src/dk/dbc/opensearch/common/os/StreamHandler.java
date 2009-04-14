package dk.dbc.opensearch.common.os;


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