/** \brief UnitTest for PluginType */
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

import dk.dbc.opensearch.common.pluginframework.PluginType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 */
public class PluginTypeTest
{

    PluginType pt;
    boolean outcome;
    /**
     * Tests the constructions of the PluginType
     */
    @Test public void testLegalConstructions()
    {
        pt = PluginType.HARVEST;

        switch( pt )
            {
            case HARVEST: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.ANNOTATE;
        switch( pt )
            {
            case ANNOTATE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.STORE;
        switch( pt )
            {
            case STORE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.INDEX;
        switch( pt )
            {
            case INDEX: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.PROCESS;
        switch( pt )
            {
            case PROCESS: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
        pt = PluginType.RETRIEVE;
        switch( pt )
            {
            case RETRIEVE: outcome = true;
                break;
            default: outcome = false;
            }
        assertTrue( outcome );
    }
}