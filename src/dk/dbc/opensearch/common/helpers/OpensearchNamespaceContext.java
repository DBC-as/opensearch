/**
 * \file OpensearchNamespaceContext.java
 * \brief The OpensearchNamespaceContext class
 * \package helpers;
 */

package dk.dbc.opensearch.common.helpers;


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


import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;

import org.apache.commons.lang.NotImplementedException;

/**
 * Namespace context for opensearch. It is only getNamespaceURI which
 * is implemented correctly, and this method is used in Xpath
 * evaluation.
 */
public class OpensearchNamespaceContext implements NamespaceContext
{
    public OpensearchNamespaceContext(){}

    /**
     * @param prefix a String giving the prefix of the namespace for which to search 
     * @return the uri of the namespace that has the given prefix
     */
    public String getNamespaceURI( String prefix ){
        String uri = null;
        if ( prefix.equals( "docbook" ) ){
            uri = "http://docbook.org/ns/docbook";
        }
        return uri;
    }

    
    public Iterator< String > getPrefixes( String val ) {
        throw new NotImplementedException( "getPrefixes( String val) has not yet been implemented" );
        //return ( Iterator< String > ) null;
    }

    public String getPrefix( String uri ){
        throw new NotImplementedException( "getPrefix( String uri ) has not yet been implemented" );
        //return new String();
    }

}
