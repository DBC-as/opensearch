/**
 * \file PdfFileFilter.java
 * \brief The PdfFileFilter class
 * \package os;
 */

package dk.dbc.opensearch.common.os;


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


import java.io.File;
import java.io.FilenameFilter;

/**
 *  
 */
public class PdfFileFilter implements FilenameFilter
{
    /**
     * This method returns true only if arguments dir+name evals to a
     * filename not starting with a '.' and the suffix is .pdf
     *
     * @param dir the path of the directory to be tested
     * @param name the dir- or filename on the path
     *
     * @returns true if path denotes a file that ends with ".pdf"
     *
     * @throws NullPointerException if the dir- or filename is null
     */
    public boolean accept(File dir, String name) throws NullPointerException
    {
        
         if( dir == null )
         {
             throw new NullPointerException( "invalid directory" );
         }
        if( ! ( new File( dir, name ) ).isDirectory() && name.endsWith( ".pdf" ) )
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
