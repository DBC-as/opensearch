package dk.dbc.opensearch.common.pluginframework;


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

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import java.io.FileNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.util.ArrayList;
import java.util.Vector;


/**
 * The pluginframework is accessed through this interface. 
 * The framework can find plugins and load them runtime and it can 
 * validate whether there exists pluigns to solve a number of tasks. 
 * It cannot garantie that the plugins will still be there when 
 * called ( someone could delete them from there folders ) the 
 * IPluginResolver can also force an update on the knowledge of 
 * available plugins through the clearPluginRegistration method, 
 * if someone changed them while the system is running.   
 */
public interface IPluginResolver
{
    /**
     * Finds and loads a plugin that can solve a specific task on an object 
     * matching the format and submitter through the PluginID object for the
     * @param pluginID, the PluginID object for the wanted plugin
     * @throws FileNotFoundException when the wanted plugin cannot be found or 
     * there are no plugin registration files to be found. 
     * @throws InstantiationException when the plugin cannot be instantiated
     * \todo: Should we throw different exceptions for the 2 cases 
     * FileNotFoundException covers?  
     * @throws IllegalAccessException 
     */
    IPluggable getPlugin ( String submitter, String format, String task ) throws FileNotFoundException, InstantiationException, ClassNotFoundException, IllegalAccessException, PluginResolverException;

    
    /**
     * validates whether plugins for the tasks specified for the submitter and 
     * format exists.
     * @param submitter, the submitter to search for plugins for
     * @param format, the format to search for plugins for
     * @param taskList the tasks to find plugins for
     * @return Vector<String> with the names of tasks that could not be validated. 
     * If the Vector == null, plugins were found for all the tasks 
     * @throws PluginResolverException, when there are exceptions from the 
     * framework concerning the registrations of plugins
     */
    Vector<String> validateArgs( String submitter, String format, ArrayList< String > taskList )throws PluginResolverException;
    
    
    /**
     * clears the registrations of plugins and forces an update next time 
     * plugin information is needed
     */    
    void clearPluginRegistration();
}