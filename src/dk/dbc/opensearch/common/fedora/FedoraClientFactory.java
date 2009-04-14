/**
 * \file FedoraClientFactory.java
 * \brief The FedoraClientFactory class
 * \package tools
 */
package dk.dbc.opensearch.common.fedora;


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


import dk.dbc.opensearch.common.config.FedoraConfig;

import fedora.client.FedoraClient;

import java.net.MalformedURLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief The factory spawns fedoraClients
 */
public class FedoraClientFactory 
{    
    Logger log = Logger.getLogger( FedoraClientFactory.class ); 
    
    private static String host = "";
    private static String port = "";
    private static String fedoraUrl = "";
    private static String user = "";
    private static String passphrase = "";
    
    
    /**
     * Builds And return a FedoraClient.   
     *
     * @throws ConfigurationException error reading configuration file
     * @throws MalformedURLException error obtaining fedora configuration
     */
    public FedoraClient getFedoraClient()throws ConfigurationException, MalformedURLException 
    {         
        log.debug( "Obtain config paramaters for configuring fedora connection");
        
        host       = FedoraConfig.getHost();
        port       = FedoraConfig.getPort();
        user       = FedoraConfig.getUser();
        passphrase = FedoraConfig.getPassPhrase();
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );
        
        log.debug( "Constructing FedoraClient" );
        
        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );

        log.debug( "Constructed FedoraClient" );
        return client;
    }
}