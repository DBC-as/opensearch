package dk.dbc.opensearch.common.types;


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
 * \brief A class for handling throwables and additional information 
 * \package types
 * This is a class for wrapping and transporting an exception that need to be 
 * handed up through the system with some additional information than the 
 * stacktrace and message.
 * This class is made to facilitate the need of the 
 * dk.dbc.opensearch.common.pluignframework.PluginResolverException class to 
 * have a containerclass for a Throwable and some additional information.
 */
public class ThrownInfo {
    Throwable theThrown;
    String info;
    
    /**
     * the public constructor that sets the fields
     * @param theThrown, the Throwable that shall be contained
     * @param info, the additional info about the Throwable
     */
    public ThrownInfo( Throwable theThrown , String info ) {
    
        this.theThrown = theThrown;
        this.info = info; 
  }
    /**
     * returns the Throwable
     * @return theThrown
     */
    public Throwable getThrowable(){
        return theThrown;
    } 
    /**
     * returns the additional information about the Throwable
     * @return info
     */
    public String getInfo(){
        return info;
    }
}