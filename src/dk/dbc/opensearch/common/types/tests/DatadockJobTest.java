/**
 * \file DatadockJobTest.java
 * \brief The DatadockJobTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.types.tests;


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

import java.net.URI;
import dk.dbc.opensearch.common.types.DatadockJob;
import java.net.URISyntaxException;

/** \brief UnitTest for DatadockJob **/

import static org.junit.Assert.*;
import org.junit.*;

public class DatadockJobTest {

    /**
     * Testing the getters and setters of DatadockJob
     */
    
    @Test 
    public void testSettersAndGetters() 
    {        
        String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        URI testURI = null;
        
        try
        {
            testURI = new URI( "testURI" );
        }
        catch( URISyntaxException use )
        {
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        DatadockJob datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);
        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getUri().equals( testURI ) );
        
        testSubmitter = "testSubmitter2";
        testFormat = "testFormat2";        
        try{
            testURI = new URI( "testURI2" );
        }catch( URISyntaxException use ){
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }
        
        datadockJob.setUri( testURI );
        datadockJob.setSubmitter( testSubmitter );
        datadockJob.setFormat( testFormat );

        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
        assertTrue( datadockJob.getUri().equals( testURI ) );
    }

    @Test public void testConstructorWithPid()
    {
      String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        URI testURI = null;
        String testPID1 = "dbc:1";
        String testPID2 = "dbc:2";
        
        try
        {
            testURI = new URI( "testURI" );
        }
        catch( URISyntaxException use )
        {
            fail( "Catched URISyntaxException under construction of test uri."+use.getMessage() );
        }   

        DatadockJob ddj = new DatadockJob( testURI, testSubmitter, testFormat, testPID1 );

        assertEquals( ddj.getPID(), testPID1 );
        ddj.setPID( testPID2 );
        assertEquals( ddj.getPID(), testPID2 );
    }
}
