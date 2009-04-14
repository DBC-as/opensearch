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


import dk.dbc.opensearch.common.types.Pair;

import java.util.Comparator;


/**
 * Helper class for the FedoraTolls class. Used to sorting arraylists
 * of Pair<String, Object>. Sorting on the first element, the String in
 * the Pair
 */
public class PairComparator_FirstString implements Comparator
{
    public int compare( Object x, Object y )
    {
        return ((Pair< String, Object >) x).getFirst().compareTo( ( (Pair<String, Object>)y).getFirst() );
    }
}