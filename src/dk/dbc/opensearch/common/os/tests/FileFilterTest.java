/**
 * \file FileFilterTest.java
 * \brief The FileFilterTest class
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


import dk.dbc.opensearch.common.os.FileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;


/**
 *
 */
public class FileFilterTest 
{
    FileFilter ff;
    static String dir = ".shouldnotbeaccepted";
    static String testString = "test"; 
    static File dummy = null;
    static File dummyChild = null;

    
    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before 
    public void SetUp() 
    {
        dummy = new File( dir );
        dummy.mkdir();
        dummyChild = new File( dummy, testString );
        dummyChild.mkdir();
        ff = new FileFilter();
    }

    
    /**
     * After each test the dummy directory is removed
     */
    @After 
    public void TearDown() 
    {
        try
        {
            dummyChild.delete();
            dummy.delete();
        }
        catch( Exception e ) 
        {
        	// do nothing!
        }
    }

    
    /**
     * Files or dirs beginning with a '.' should not be accepted
     */
    @Test 
    public void testDotFileOrDirNotAccepted() 
    {
        assertFalse( ff.accept( dummy, dir ) );
    }

    
    /**
     * Files not beginning with a '.' should be accepted
     */
    @Test 
    public void testNonDotFileOrDirAccepted()
    {
        assertTrue( ff.accept( dummy, "arbitraryname" ) );
    }

    
    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        assertFalse( ff.accept( dummy, dummyChild.getName() ) );
    }

    /**
     * if dir- or filename is null, java.io.File must throw
     */
    @Test(expected = NullPointerException.class) 
    public void testNullValueForFilenameShouldFail()
    {
        assertFalse( ff.accept( new File( "idontexist" ), null ) );
    }
}