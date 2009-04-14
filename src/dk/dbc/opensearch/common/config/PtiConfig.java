/**
 * 
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


/**
 * @author mro
 * 
 * Sub class of Config providing access to pti settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class PtiConfig extends Config
{
	public PtiConfig() throws ConfigurationException
	{
		super();
	}


	/* MAIN POLL TIME */
	private int getPtiMainPollTime()
	{
		int ret = config.getInt( "pti.main-poll-time" );
		return ret;
	}
	
	
	public static int getMainPollTime() throws ConfigurationException 
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiMainPollTime();
	} 
	
	
	/* REJECTED-SLEEP-TIME */
	private int getPtiRejectedSleepTime()
	{
		int ret = config.getInt( "pti.rejected-sleep-time" );
		return ret;
	}
	
	
	public static int getRejectedSleepTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiRejectedSleepTime();
	}
	
	
	/* SHUTDOWN-POLL-TIME */
	private int getPtiShutdownPollTime()
	{
		int ret = config.getInt( "pti.shutdown-poll-time" );
		return ret;
	}
	
	
	public static int getShutdownPollTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiShutdownPollTime();
	}
	
	
	/* QUEUE SIZE */
	private int getPtiQueueSize()
	{
		int ret = config.getInt( "pti.queuesize" );
		return ret;
	}
	
	
	public static int getQueueSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiQueueSize();
	}
	
	
	/* CORE POOL SIZE */
	private int getPtiCorePoolSize()
	{
		int ret = config.getInt( "pti.corepoolsize" );
		return ret;
	}
	
	
	public static int getCorePoolSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiCorePoolSize();
	}
	
	
	/* MAX POOL SIZE */
	private int getPtiMaxPoolSize()
	{
		int ret = config.getInt( "pti.maxpoolsize" );
		return ret;
	}
	
	
	public static int getMaxPoolSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiMaxPoolSize();
	}
	
	
	/* KEEP ALIVE TIME */
	private int getPtiKeepAliveTime()
	{
		int ret = config.getInt( "pti.keepalivetime" );
		return ret;
	}
	
	
	public static int getKeepAliveTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiKeepAliveTime();
	}
	
	
	/* PATH */
	private String getPtiPath()
	{
		String ret = config.getString( "pti.path" );
		return ret;
	}
	
	
	public static String getPath() throws ConfigurationException
	{		
		PtiConfig pc = new PtiConfig();
		return pc.getPtiPath();
	}
}
