/**
 * \file PluginResolverException 
 * \brief
 * \package pluginframework
 */
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


import dk.dbc.opensearch.common.types.ThrownInfo;
import java.util.Vector;


/**
 * This class is a custom Exception for handling the many exceptions that can be 
 * thrown from the PluginResolvers components that not nessecarily should halt the 
 * executing. It therefore contains a Vector of <ThrownInfo> 
 * that can be examined where the PluginResolver is being called from.
 * To get the class of the Exception from the Throwable object call getClass()
 * The info from the ThrownInfo tells what object the exception is concerned 
 * with or caused by.     
 */
public class PluginResolverException extends Exception 
{
    Vector<ThrownInfo> exceptionVector;
    String message;

    
    /**
     * @param exceptionVector is the Vector containing the ThrownInfos, that each 
     * contains a Throwable and aditional information.
     * @param String, message is the general message about the Exception, stating
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( Vector<ThrownInfo> exceptionVector, String message ) 
    {
        this.exceptionVector = exceptionVector;
        this.message = message;
    }

    
    /**
     * Constructor for sending a single message when the flow of the 
     * PluginResolvers components is as expected and no Exceptions where 
     * caused, but there are values that are not computed or retrived as 
     * expected. The exceptionVector will be null when the exception is 
     * constructed this way.
     * @param String, message is the general message about the Exception, stating 
     * what the collection of Throwables are regarding. 
     */
    public PluginResolverException( String message ) 
    {
        this.exceptionVector = null;
        this.message = message;
    }

    
    /**
     * The standard method for retrieving the overall information about 
     * the Exception.  
     * @return String, the overall information about the Exception.
     */
    public String getMessage()
    {
        return message;
    }

    
    /**
     * The method for retrieving the Vector containing the Throwables and 
     * eachs paired information. The returned Vector should allways be 
     * checked for being null before used.
     * @return Vector<ThrownInfo> the Vector with the Throwables 
     * and information about them.
     */
    public Vector<ThrownInfo> getExceptionVector()
    {
        return exceptionVector;
    }
}