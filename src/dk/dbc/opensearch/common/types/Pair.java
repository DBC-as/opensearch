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
 * InputPair
 * 
 * If You would like to have sorting done on the Pair type, please use
 * dk.dbc.opensearch.common.types.ComparablePair type instead
 */
public class Pair< E, V >// implements Comparator
{
    /**
     *
     */

    private E first;
    private V second;

    public Pair( E first, V second ) 
    {
        this.first = first;
        this.second = second;
    }

    
    public E getFirst()
    {
        return first;
    }

    
    public V getSecond()
    {
        return second;
    }
    
    
    public boolean equals( Object obj )
    {
        if(!( obj instanceof Pair ) )
        {
            return false;
        }
        else if(!( first.equals( ( (Pair)obj ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (Pair)obj ).getSecond() ) ) )
        {
            return false;
        }
        else
        {
            return true;
        }
    }
    
    
    public String toString()
    {
        return String.format( "Pair< %s, %s >", first.toString(), second.toString() );
    }
    
    
    public int hashCode()
    {
        return first.hashCode() + second.hashCode();
    }

}
