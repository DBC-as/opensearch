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


import dk.dbc.opensearch.common.types.Pair;

import java.lang.UnsupportedOperationException;

/**
 *  Use this class if you want a Pair class that can be sorted
 *  It sorts on the first element and only considers the second 
 *  if the two first elements are equal.
 */

public class ComparablePair< E extends Comparable< E >, V extends Comparable< V > > implements Comparable
{
    private E first;
    private V second;

    public ComparablePair( E first, V second ) 
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
    
    @Override
    public boolean equals( Object cPair )
    {
        if( cPair == null )
        {
            return false;
        }
        else if( ! ( cPair instanceof ComparablePair ) )
        {
            return false;
        }
        else if(!( first.equals( ( (ComparablePair)cPair ).getFirst() ) ) )
        {
            return false;
        }
        else if(!( second.equals( ( (ComparablePair)cPair ).getSecond() ) ) )
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
        return String.format( "ComparablePair< %s, %s >", first.toString(), second.toString() );
    }
    
    
    public int hashCode()
    {
        return first.hashCode() ^ second.hashCode();
    }

    public int compareTo( Object pair ){
        if ( ! ( pair instanceof ComparablePair ) )
        {
            throw new UnsupportedOperationException( String.format( "Type %s is not a comparable type", pair.toString() ) );
        }

        ComparablePair newpair = (ComparablePair)pair;
 
        if ( first.equals( newpair.getFirst() ) )
        {
                return (int)second.compareTo( (V)newpair.getSecond() );
        }
        return (int)first.compareTo( (E)newpair.getFirst() );
        
    }
}
