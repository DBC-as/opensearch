/** \brief UnitTest for PluginID **/
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

import dk.dbc.opensearch.common.pluginframework.PluginID;

/**
 * 
 */
public class PluginIDTest {

    /**
     * Tests that the id values generated on the basis of the
     * information given to the plugin constructor are identical given
     * identical parameters.
     */
    @Test public void testUniqueHashValue() {
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin2 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( plugin1.getPluginID() , plugin2.getPluginID() );
    }
    @Test public void testDistinctHashValues(){
        PluginID plugin1 = new PluginID( "a", "b", "c" );
        PluginID plugin3 = new PluginID( "c", "b", "a" );
        Assert.assertTrue(  plugin1.getPluginID() != ( plugin3.getPluginID() ) );
    }
    @Test public void testRetrievalOfFields(){

        PluginID plugin1 = new PluginID( "a", "b", "c" );

        Assert.assertEquals( "a", plugin1.getPluginSubmitter() );
        Assert.assertEquals( "b", plugin1.getPluginFormat() );
        Assert.assertEquals( "c", plugin1.getPluginTask() );
    }

}