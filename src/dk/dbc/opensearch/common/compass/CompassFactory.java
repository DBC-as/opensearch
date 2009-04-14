/**
 * \file CompassFactory.java
 * \brief The CompassFactory class
 * \package tools
 */
package dk.dbc.opensearch.common.compass;


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


import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.os.FileHandler;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.config.CompassConfiguration;


/**
 * \ingroup tools
 * \brief Compass factory. The role of this class is to build and hold one
 * Compass, and spawn references to it.
 */
public class CompassFactory
{
	Logger log = Logger.getLogger( CompassFactory.class );
	
	
	private static Compass compass = null;    

    
    /**
     * The getCompass method returns a reference to the Compass. If
     * none exist a new one is build and returned
     *
     * @return the Compass
     */
    public Compass getCompass() throws ConfigurationException
    {
        log.debug("Entering CompassFactory.getCompass");

        if( compass == null )
        {
            buildCompass();
        }
        
        return compass;
    }
    
    
    /**
     * builds the Compass with appropriate mapping and configuration files
     */
    private void buildCompass() throws ConfigurationException
    {
        log.debug("Entering CompassFactory.buildCompass");
        log.debug( "Setting up the Compass object" ); 

        log.debug( "Obtaining configuration parameters." );
        CompassConfiguration conf = new CompassConfiguration();

        String cfg  = CompassConfig.getConfigPath();
        String xsem = CompassConfig.getXSEMPath();
        log.debug( String.format( "Compass configuration = %s", cfg ) );
        log.debug( String.format( "XSEM mappings file    = %s", xsem ) );
       
        log.debug( "Building Compass." );
        conf.configure( FileHandler.getFile( cfg ) );
        conf.addFile( xsem );
        compass = conf.buildCompass();
    }        
}