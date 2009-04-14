/**
 * \file FileHandlerStaticCallTest.java
 * \brief The FileHandlerStaticCallTest class
 * \package tests;
 */

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


/** \brief UnitTest for FileHandlerStaticCall **/
import java.io.File;
import static org.junit.Assert.*;
import org.junit.*;

import junit.framework.TestCase;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.FileHandlerStaticCall;
import mockit.Mockit;
import static org.easymock.classextension.EasyMock.*;

/**
 * 
 */
public class FileHandlerStaticCallTest extends TestCase {
    
    
    
//    static File mockFile = createMock( File.class );
//
//    String teststring = "røvbanan !";
//
//
//    public static class MockFileHandler{
//
//        public static File getFile( String path ){
//            
//            return mockFile;
//        }
//    }
//    
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//        Mockit.redefineMethods( FileHandler.class, MockFileHandler.class );
//    }
//    
//
//    
//
//    /**
//     * 
//     */
	@Ignore
	@Test public void teststatictest() {
//        expect( mockFile.getAbsolutePath() ).andReturn( teststring );
//        replay( mockFile );
//    
//        FileHandlerStaticCall fhm = new FileHandlerStaticCall();
//        String returnstr = fhm.testStatic();
//        assertEquals( returnstr, teststring);
//        verify( mockFile ); 
    }
}
