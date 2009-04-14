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
/** \brief UnitTest for CargoMimeType */

import dk.dbc.opensearch.common.types.CargoMimeType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class CargoMimeTypeTest 
{
    @Before 
    public void SetUp() 
    {
        
    }

    
    @Test 
    public void testBasicSanityOfMimeTypeRepresentation()
    {
        assertTrue( "text/xml".equals( CargoMimeType.TEXT_XML.getMimeType() ) );
        assertTrue( "application/pdf".equals( CargoMimeType.APPLICATION_PDF.getMimeType() ) );
    }

    
    @Test 
    public void testBasicSanityOfMimeTypeDescription()
    {
        assertTrue( "XML Document".equals( CargoMimeType.TEXT_XML.getDescription() ) );
        assertTrue( "PDF Document".equals( CargoMimeType.APPLICATION_PDF.getDescription() ) );
    }

    
    @Test
    public void testValidMimetype()
    {
    	String validMimetype = "text/xml";
    	assertTrue( CargoMimeType.validMimetype( validMimetype ) );
    }
    
    
    @Test
    public void testInvalidMimetype()
    {
    	String invalidMimetype = "invalid/test";
    	assertFalse( CargoMimeType.validMimetype( invalidMimetype ) );
    }
    
    
    @Test
    public void testGetMimetypeFrom()
    {
    	String mimetype = "text/xml";
    	CargoMimeType cmt = CargoMimeType.TEXT_XML;
    	
    	assertTrue( CargoMimeType.getMimeFrom( mimetype ).equals( cmt ) );
    }
    
    
    @Test
    public void testNullGetMimetypeFrom()
    {
    	String mimetype = "null/test";
    	assertNull( CargoMimeType.getMimeFrom( mimetype ) );    	
    }
}