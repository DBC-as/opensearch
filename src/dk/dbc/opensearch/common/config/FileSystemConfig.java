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
 * Sub class of Config providing access to file system settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class FileSystemConfig extends Config
{
	public FileSystemConfig() throws ConfigurationException 
	{
		super();
	}


	/* Ensuring a '/' at the end of path */
	private String sanitize( String path )
	{
		if( path.endsWith( "/" ) )
			return path;
		else
			return path + "/";
	}
	
	
	/* TRUNK */
	private String getFileSystemTrunkPath()
	{
		String ret = config.getString( "filesystem.trunk" );
		return sanitize( ret );
	}
	
	
	public static String getTrunkPath() throws ConfigurationException 
	{
		FileSystemConfig f = new FileSystemConfig();
		return f.getFileSystemTrunkPath();
	} 
	
	
	/* CONFIG.XML PATH */
	public static String getConfigPath() throws ConfigurationException
	{
		FileSystemConfig f = new FileSystemConfig();
		String ret = f.getFileSystemTrunkPath();
		
		return ret + "config/"; 
	}
	
	
	/* PLUGINS */
	private String getFileSystemPluginsPath()
	{
		String ret = config.getString( "filesystem.plugins" );
		return sanitize( ret );
	}
	
	
	public static String getPluginsPath() throws ConfigurationException
	{		
		FileSystemConfig fc = new FileSystemConfig();		
		return fc.getFileSystemPluginsPath();
	}
}
