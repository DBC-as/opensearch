/** \brief UnitTest for OpensearchNamespaceContext */
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

import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;

import static org.junit.Assert.*;
import org.junit.*;

import org.apache.commons.lang.NotImplementedException;
/**
 * 
 */
public class OpensearchNamespaceContextTest {


    OpensearchNamespaceContext nsc;
    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * Not really doing a lot... 
     */
    @Test public void testConstructor() {
        nsc = new OpensearchNamespaceContext();
        assertTrue( nsc != null );
    }

    @Test public void testGetNamespaceURI() 
    { 
        String uri = "http://docbook.org/ns/docbook";
        nsc = new OpensearchNamespaceContext();
        assertEquals( uri, nsc.getNamespaceURI( "docbook" ) );
        assertTrue( null == nsc.getNamespaceURI( "anything else" ) );
    }

    @Test(expected=NotImplementedException.class) 
        public void testGetPrefixes() 
    {
        nsc = new OpensearchNamespaceContext();
        nsc.getPrefixes( "anything" );
    }
    @Test(expected=NotImplementedException.class)
 public void testGetPrefix() 
    {
        nsc = new OpensearchNamespaceContext();
        nsc.getPrefix( "anything" );
    }
}