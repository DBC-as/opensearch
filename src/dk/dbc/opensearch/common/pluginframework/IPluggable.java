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
 * This interface represents the base point for all plugins. It
 * defines two methods that are the common basis for all plugins that
 * perform tasks in the datadock or pti.
 * 
 * The pluginID must be given to the plugin at object creation
 * time. The taskname is defined at compile time but could be the
 * subject of configuration variables in the future.
 * 
 * \todo: make the taskname a configuration value instead of setting it compile time
 * 
 * Plugins in the opensearch plugin framework are expected to work
 * primarily with CargoContainers. Plugins are defined on the basis of
 * the format they're going to work with combined with th e type of
 * task they're going to perform. E.g. for the format 'faktalink' (DBC
 * material, formatted in XML), the tasks could likely be:
 * 
 * 1) harvest: reading one or more files from a path and putting the
 *    content of the files and known metadata about them into
 *    CargoContainers.
 *    
 * 2) annotate: lookup metadata relating to the data in a
 *    CargoContainer and annotate the metadata to the
 *    CargoContainer(s).
 *    
 * 3) index: convert the CargoContainer into a lucene index
 * 
 * 4) store: format the CargoContainer into a Fedora Digital Object
 *    and store the digital object in the Fedora Digital Repository.
 * 
 * 
 */
public interface IPluggable
{
	/**
     * getTaskName returns the - at compile time given - name of the
     * task the plugin is constructed to handle. The task name is used
     * by the users of the plugin framework to assign jobs and
     * jobqueues to the plugins involved in getting a job through the
     * system.
     * 
     * @returns a string containing the task name
     */
    public PluginType getTaskName();
    /**
     *	 Empty plugin only used to define the type!
     */
}
