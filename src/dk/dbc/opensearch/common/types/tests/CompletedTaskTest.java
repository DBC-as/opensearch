/**
 * \file CompletedTaskTest.java
 * \brief The CompletedTaskTest class
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


/** \brief UnitTest for CompletedTask **/

import static org.junit.Assert.*;
import org.junit.*;

import dk.dbc.opensearch.common.types.CompletedTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;

import static org.easymock.classextension.EasyMock.*;

public class CompletedTaskTest {

    FutureTask mockFutureTask;


    /**
     * Testing the getters and setters of CompletedTask.
     */
    @Test public void testSettersAndGetters(){
        mockFutureTask = createMock( FutureTask.class );

        //FutureTask testFuture = new FutureTask( new FutureTest( 10f ) );
        float testResult = 10f;
        
        CompletedTask completedTask = new CompletedTask<Float>( mockFutureTask, testResult );
 
        Float result = (Float) completedTask.getResult();
        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( result , testResult, 0f );

        testResult = 30f;
        completedTask = new CompletedTask( mockFutureTask, testResult );
        
        completedTask.setFuture( mockFutureTask );
        completedTask.setResult( testResult );

        result = (Float) completedTask.getResult();
        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( result, testResult, 0f );
    }
}
