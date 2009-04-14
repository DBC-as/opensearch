/**
 * \file AnnotateTest.java
 * \brief The AnnotateTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins.tests;


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


/** \brief UnitTest for Annotate **/


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.plugins.DocbookAnnotate;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 */
public class AnnotateTest {

    /**
     *
     */
    @Ignore
    @Test public void testatest() throws Exception {

          File f = new File("/home/shm/amnesty.xml");
          FileInputStream fis = FileHandler.readFile( f.getPath() ); 
          URI uri = new URI( f.getPath() );
          byte[] fb = new byte[(int)f.length()];
          fis.read( fb );

          DatadockJob ddj = new DatadockJob( uri, "dbc", "faktalink", "dbc:100" );
          CargoContainer cc = new CargoContainer();
          cc.add( DataStreamType.OriginalData, "faktalink", "dbc", "da", "text/xml", fb );

          System.out.println("Annotate Test !!!");
          DocbookAnnotate dba = new DocbookAnnotate();

          CargoContainer cc2 = dba.getCargoContainer( cc );
    }
}
