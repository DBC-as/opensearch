/**
 * \file PTIManagerConfig.java
 * \brief The PTIManagerConfig class
 * \package config;
 */

package dk.dbc.opensearch.common.config;


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

import org.apache.commons.configuration.ConfigurationException;


public class PTIManagerConfig extends Config
{

    public PTIManagerConfig() throws ConfigurationException 
    {
		super();
	}

	/* *************************************
     * PTIMANAGER QUEUE RESULTSET MAX SIZE *
     * *************************************/

    private String getPtiManagerQueueResultsetMaxSize()
    {
        String ret = config.getString( "pti.queue-resultset-maxsize" );
        return ret;
    }

    public static String getQueueResultsetMaxSize() throws ConfigurationException
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getPtiManagerQueueResultsetMaxSize();
    }

    
    /* *************************************
     * PTIMANAGER REJECTED JOB SLEEP TIME  *
     * *************************************/
    private String getPtiManagerRejectedSleepTime()
    {
        String ret = config.getString( "pti.rejected-sleep-time" );
        return ret;
    }

    public static String getRejectedSleepTime() throws ConfigurationException
    {
        PTIManagerConfig pmc = new PTIManagerConfig();
        return pmc.getPtiManagerRejectedSleepTime();
    }
}

