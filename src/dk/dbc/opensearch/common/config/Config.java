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


import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


/**
 * @author mro
 * 
 * Super (or base) class for the config classes. Hence: 
 * 	 
 *       DO NOT ALTER THIS CLASS IF IT CAN BE AVOIDED!!!
 *    
 * It should read one config file and make this file accessible via a 
 * constructor -- and do nothing else! That is, the sole purpose of this 
 * class is to provide access to the configuration file:
 *                   
 *                   ../config/config.xml. 
 *                    
 * This file is parsed and made available to sub classes through a non 
 * static object.
 * 
 * The intended use is for sub classes to provide access to configuration 
 * settings via static methods. This is done via:
 * 
 *     1) a private non static method, and 
 *     2) a public static method using a sub class object. Throws a 
 *        ConfigurationException.
 * 
 * Sub classes shall implement a constructor to handle the exception 
 * thrown in case the config file cannot be read (ConfigurationException).
 */
public class Config 
{
	Logger log = Logger.getLogger( Config.class );
	
	
	URL cfgURL = getClass().getResource( "/config.xml" );
	static XMLConfiguration config;
    

	/**
	 * Essential method providing access to the solution's config file.
	 * 
	 * @throws ConfigurationException
	 */
	public Config() throws ConfigurationException
	{
		try 
		{
			config = new XMLConfiguration( cfgURL );
		} 
		catch ( ConfigurationException e ) 
		{
			log.fatal( "ConfigurationException caught in class Config:" );
			log.fatal( e.getStackTrace().toString() );
			throw e;
		}
	}
}
