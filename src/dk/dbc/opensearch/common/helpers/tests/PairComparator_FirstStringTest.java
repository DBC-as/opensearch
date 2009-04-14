/** \brief UnitTest for PairComparator_FirstString */
package dk.dbc.opensearch.common.helpers.tests;


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

import dk.dbc.opensearch.common.helpers.PairComparator_FirstString;
import dk.dbc.opensearch.common.types.Pair;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PairComparator_FirstStringTest {

    PairComparator_FirstString pcfs;
    Pair<String, Integer> small;
    Pair<String, Integer> large;
    /**
     *
     */
    @Before public void SetUp() 
    {
        pcfs = new PairComparator_FirstString();
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test public void testConstructor() 
    {
        assertTrue( pcfs != null );
    }

    @Test public void testcompareLargerThan()
    {
        small = new Pair<String, Integer>( "a", 1 );
        large = new Pair<String, Integer>( "b", 1 );
        
        assertTrue( pcfs.compare( large, small ) > 0 );
    }
    
    @Test public void testcompareSmallerThan()
    {
            small = new Pair<String, Integer>( "a", 1 );
            large = new Pair<String, Integer>( "b", 1 );
            
            assertTrue( pcfs.compare( small, large ) < 0 );
    }

    @Test public void testcompareEquals()
    {
            small = new Pair<String, Integer>( "a", 1 );

            assertTrue( pcfs.compare( small, small ) == 0 );
    }
}