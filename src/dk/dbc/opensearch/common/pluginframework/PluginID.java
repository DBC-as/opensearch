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

/**
 * The PluginId type handles information about plugins.
 *
 */

/**
 * PluginID
 */
public class PluginID 
{
    private String submitter;
    private String format;
    private String task;

    /**
     * @param submitter Information on the submitter of the material. The submitter should be known by the system through other means than registering with a plugin
     * @param format Information on the format of the submitted material. 
     * @param task Information on the task that the plugin handles. This information is matched a String in the plugins 
     */
    public PluginID( String submitter, String format, String task ) 
    {
        this.submitter = submitter;
        this.format = format;
        this.task = task;
    }

    
    /**
     * getPluginID returns a hashvalue based on the submitter, format
     * and task that the PluginID object is constructed with. Please
     * note that the hashvalue is dependant on the (in this method
     * embedded) position of the informations.
     * 
     * @returns an integer defining the hash value of the plugin.
     */
    public int getPluginID()
    {
        String hashSubject = submitter + format + task;
        return hashSubject.hashCode();
    }

    
    /**
     * @returns the value of the submitter associated with the
     * pluginid
     */
    public String getPluginSubmitter()
    {
        return submitter;
    }
    
    
    /**
     * @returns the value of the format associated with the
     * pluginid
     */
    public String getPluginFormat()
    {
        return format;
    }
    
    
    /**
     * @returns the value of the task associated with the
     * pluginid
     */
    public String getPluginTask()
    {
        return task;
    }
}
