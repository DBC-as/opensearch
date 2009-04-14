/**
 *
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
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer.
 */
public abstract class FedoraHandle
{
    protected FedoraAPIM fem;
    protected FedoraAPIA fea;

    Logger log = Logger.getLogger( FedoraHandle.class );

    /**
     * The constructor handles the initiation of the connection with the
     * fedora base
     *
     * @throws ServiceException
     * @throws ConfigurationException 
     */
    public FedoraHandle() throws ServiceException, java.net.MalformedURLException, java.io.IOException, ConfigurationException
    {
        String fedora_base_url;

        String host = FedoraConfig.getHost();
        String port = FedoraConfig.getPort();
        String user = FedoraConfig.getUser();
        String pass = FedoraConfig.getPassPhrase();

        fedora_base_url  = String.format( "http://%s:%s/fedora", host, port );

        log.debug( String.format( "connecting to fedora base using %s, user=%s, pass=%s", fedora_base_url, user, pass ) );

        FedoraClient fc = new FedoraClient( fedora_base_url, user, pass );
        fea = fc.getAPIA();
        fem = fc.getAPIM();

    }
}
