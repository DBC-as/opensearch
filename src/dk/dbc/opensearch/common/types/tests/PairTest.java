/** \brief UnitTest for Pair */
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


import java.util.HashMap;
import dk.dbc.opensearch.common.types.Pair;

import org.junit.*;
import static org.junit.Assert.*;


/**
 * 
 */
public class PairTest {

    Pair<String, String> p;

    /**
     *
     */
    @Before public void SetUp() {
        p = new Pair<String, String>( "a", "b");
    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test 
    public void testTypeConsistency() {
        assertEquals( p.getFirst(), "a" );
        assertEquals( p.getSecond(), "b" );
    }

    @Test 
    public void testHashCode(){
        Pair<String, String> p2 = 
            new Pair<String, String>( "a", "b" );

        assertEquals( p.hashCode(), p2.hashCode() );

    }
    /**
     * happy path
     */

    @Test public void testEquals(){
        Pair<String, String> p3 = 
            new Pair<String, String>( "a", "b" );

        assertTrue( p3.equals( p ) );
    }
    /**
     * two non-equal pair
     */

    @Test public void testEqualsDifferent()
    {
        Pair<String, String> p3 = 
            new Pair<String, String>( "a", "a" );

        assertFalse( p3.equals( p ) ); 
    
    }
    /**
     * Calling equal with a non-pair
     */
   @Test public void testEqualsInvalid()
    {
        String test = "test";

        assertFalse( p.equals( test ) ); 
    
    }

    @Test public void testPairInHashMaps(){
        HashMap< Pair< String, String >, String > hm =
            new HashMap< Pair< String, String >, String >();

        hm.put( new Pair<String, String>( "a", "b" ), "c" );
        hm.put( new Pair<String, String>( "d", "e" ), "f" );

        assertNotNull( hm.get( new Pair<String, String>( "a", "b" ) ) );
    }
}