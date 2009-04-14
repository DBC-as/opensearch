/** \brief UnitTest for PluginFileFilter */
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

import dk.dbc.opensearch.common.os.PdfFileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PdfFileFilterTest {

    /**
     *
     */
    PdfFileFilter pff;
    static File pdfDir = new File( "pdfDir" );
    static File pdfFile;
    static File otherFile;
    static File dir2; 
    

    @Before public void SetUp() 
    {
        pff = new PdfFileFilter();
        pdfDir.mkdir();
        pdfFile = new File( pdfDir, "test.pdf"); 
        otherFile = new File( pdfDir, "notplugin" );
    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pdfDir.delete();
    }

    /**
     * 
     */
    @Test public void testAcceptsPdfFiles()
    {
        assertTrue( pff.accept( pdfDir, pdfFile.getName() ) );
    }

@Test public void testRejectsNonPdfFiles()
    {
        assertFalse( pff.accept( pdfDir, otherFile.getName() ) );
    }

    @Test public void testRejectsDirectories()
    {
        dir2 = new File( pdfDir, "testdir" );
        dir2.mkdir();
        //System.out.println( (new File( pluginDir, dir2.getName() )).isDirectory() );
        assertFalse( pff.accept( pdfDir, dir2.getName() ) );
        dir2.delete();
    }

    @Test( expected = NullPointerException.class ) 
    public void testNPException1()
    {
        otherFile = null;
        pff.accept( otherFile, pdfFile.getName() );
    }
    @Test( expected = NullPointerException.class )  
        public void testNPException2()
    {
        String hat = null;
        pff.accept( pdfDir, hat );
    }
    
}