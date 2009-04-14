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


import org.apache.log4j.Logger;

import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

/**
 * PluginLoader
 */
public class PluginLoader 
{
    static Logger log = Logger.getLogger( PluginLoader.class );
    
    //String pluginPathName = FileSystemConfig.getFileSystemPluginsPath(); // "classes/dk/dbc/opensearch/plugins";
    //FileHandler fileHandler;
    //String pluginSubPathName = "build/classes/dk/dbc/opensearch/plugins/";
    ClassLoader cl;


    /**
     * 
     */
    public PluginLoader( ClassLoader cl ) 
    {
        this.cl = cl;
    }

    
    /**
     * Given a qualified class name of the plugin, this method locates the
     * plugin on the classpath and loads and returns the plugin
     * @param pluginName the class name of the wanted plugin
     * @return the loaded plugin
     * @throws InstantiationException if the classloader cant sinstantiate the desired plugin
     * @throws IllegalAccessException if the wanted plugin cant be accessed
     * @throws ClassNotFoundException if the specified class cannot found  
     */
    IPluggable getPlugin( String pluginClassName ) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {        
        Class loadedClass = null;
        //loading the class
        log.debug( String.format( "The plugin class name: %s", pluginClassName) );
       
        loadedClass = cl.loadClass( pluginClassName );
       
        IPluggable thePlugin = ( IPluggable )loadedClass.newInstance();

        return thePlugin;
    }
}
