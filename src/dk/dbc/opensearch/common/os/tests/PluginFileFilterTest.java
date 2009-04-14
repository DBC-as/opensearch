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

import dk.dbc.opensearch.common.os.PluginFileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PluginFileFilterTest {

    /**
     *
     */
    PluginFileFilter pff;
    static File pluginDir = new File( "pluginDir" );
    static File pluginFile;
    static File otherFile;
    static File dir2; 
    

    @Before public void SetUp() 
    {
        pff = new PluginFileFilter();
        pluginDir.mkdir();
        pluginFile = new File( pluginDir, "test.plugin"); 
        otherFile = new File( pluginDir, "notplugin" );
    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pluginDir.delete();
    }

    /**
     * 
     */
    @Test public void testAcceptsPluginFiles()
    {
        assertTrue( pff.accept( pluginDir, pluginFile.getName() ) );
    }

@Test public void testRejectsNonPluginFiles()
    {
        assertFalse( pff.accept( pluginDir, otherFile.getName() ) );
    }

    @Test public void testRejectsDirectories()
    {
        dir2 = new File( pluginDir, "testdir" );
        dir2.mkdir();
        //System.out.println( (new File( pluginDir, dir2.getName() )).isDirectory() );
        assertFalse( pff.accept( pluginDir, dir2.getName() ) );
        dir2.delete();
    }

    @Test( expected = NullPointerException.class ) 
    public void testNPException1()
    {
        otherFile = null;
        pff.accept( otherFile, pluginFile.getName() );
    }
    @Test( expected = NullPointerException.class )  
        public void testNPException2()
    {
        String hat = null;
        pff.accept( pluginDir, hat );
    }
    
}