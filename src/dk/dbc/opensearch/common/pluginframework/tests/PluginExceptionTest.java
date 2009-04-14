/** \brief UnitTest for PluginException */
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

import dk.dbc.opensearch.common.pluginframework.PluginException;
import java.io.IOException;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PluginExceptionTest 
{
    PluginException pe;

    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pe = null;
    }

    /**
     * 
     */
    @Test public void testConstructorNoMsg() 
    {
        pe = new PluginException( new IOException( "test" ) );
        assertTrue( pe.getException().getClass() == IOException.class );
        assertTrue( pe.getException().getMessage() == "test" );
        assertTrue( pe.getMessage() == null ); 
    }
    
    /**
     * 
     */
    @Test public void testConstructorNoExp() 
    {
        pe = new PluginException( "test" );
        assertTrue( pe.getException() == null );
        assertTrue( pe.getMessage() == "test" ); 
    }
    /**
     * 
     */
    @Test public void testConstructor() 
    {
        pe = new PluginException( "test", new IOException( "test" ) );
        assertTrue( pe.getException().getClass() == IOException.class );
        assertTrue( pe.getException().getMessage() == "test" );
        assertTrue( pe.getMessage() == "test" ); 
    }
}