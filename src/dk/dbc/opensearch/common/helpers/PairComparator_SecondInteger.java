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
 * helper class for the JobMapCreator class for use in sorting arraylists 
 * of Pair<String,Integer>
 * Is made a class on its on and not an inner class to prepare the JobMapCreator 
 * class to become static. See todo in JobMapCreator
 */
public class PairComparator_SecondInteger implements Comparator
{
    public int compare( Object x, Object y )
    {	
        if( ((Pair< String, Integer >)x).getSecond() < ((Pair< String, Integer >)y).getSecond() )
        	return -4;
        else
        	if( ((Pair<String, Integer>)x).getSecond() == ((Pair<String, Integer>)y).getSecond() )
        		return 0;
                    
        return 4;
    }

}