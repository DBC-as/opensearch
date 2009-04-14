/**
 * \file Harvester.java
 * \brief The Harvester class
 * \package harvest;
 */
package dk.dbc.opensearch.components.harvest;


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


import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;


/**
 * Harvester interface. Harvester is the interface for the datadock
 * harvester service. The harvester is an eventdriven component - and
 * three methods need to be implemented. After construction of the
 * harvester - the start method is called - so all startup logic
 * should be placed here. When the datadock is up and running, it will
 * call the getJobs method at intervals until the shutdown method is
 * called.
 */
public interface IHarvester
{
    /**
     * The start method. Called by the datadock just after
     * construction of the instance.
     */
    void start();
    
    
    /**
     * The shutdown method. Called by the datadock when closing down
     * the harvester.
     */
    void shutdown();
    
    
    /**
     * The getJobs method. Called consecutively by the datadock when
     * it is up and running.
     * 
     * @return getJobs Returns a vector of DatadockJobs - representing
     * the new jobs registered since the last call to this method.
     * @throws IOException 
     * @throws FileNotFoundException 
     * @throws ConfigurationException 
     */
    Vector< DatadockJob > getJobs() throws FileNotFoundException, IOException, ConfigurationException;
}
