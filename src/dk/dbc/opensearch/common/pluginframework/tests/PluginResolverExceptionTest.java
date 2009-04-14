/** \brief UnitTest for PluginResolverException */
package dk.dbc.opensearch.common.pluginframework.tests;


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

import static org.junit.Assert.*;
import org.junit.*;
import mockit.Mockit;
import static org.easymock.classextension.EasyMock.*;

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import java.util.Vector;

/**
 * Class to test the PluginResolverException
 */
public class PluginResolverExceptionTest {

    PluginResolverException pre;
    String message = "message";
    Vector<ThrownInfo> exceptionVector = new Vector();

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
     *
     */
    @Test public void pluginResolverExceptionTwoArgsConstructorTest() {
        ThrownInfo testInfo = new ThrownInfo( new NullPointerException( "test" ), message );
        exceptionVector.add( testInfo );
        pre = new PluginResolverException( exceptionVector, message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == exceptionVector );
    }
    /**
     *
     */
    @Test public void pluginResolverExceptionOneArgConstructorTest() {

        pre = new PluginResolverException( message );
        assertTrue( pre.getMessage().equals( message) );
        assertTrue( pre.getExceptionVector() == null );
    }
}