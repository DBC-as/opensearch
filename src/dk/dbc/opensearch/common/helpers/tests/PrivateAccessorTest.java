/** \brief UnitTest for PrivateAccessor **/

package dk.dbc.opensearch.common.helpers.tests;


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

import dk.dbc.opensearch.common.helpers.PrivateAccessor;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Class that tests the helper PrivateAccessor.
 * It is not possible to generate the IllegalAccessException in the 
 * getPrivateField and invokePrivateMethod methods and likewise with 
 * the InvocationTargetException in the invokePrivateMethod method. 
 * So those lines will remain untested
 */
public class PrivateAccessorTest {

    /**
     * 
     */
    class TestClass
    {
        private String privateString = "private Field";

        private String privateMethod( String testArg )
        {
            // System.out.println( "hello" );
            return privateString;
        }
   } 

    TestClass tc = new TestClass();
    
    @Test public void testGetPrivateField() 
{
    String match = (String)PrivateAccessor.getPrivateField( tc, "privateString" );
    assertEquals( match, "private Field" );
}

    @Test(expected = IllegalArgumentException.class) 
public void testGetPrivateFieldNotExisting() 
    {
     String match = (String)PrivateAccessor.getPrivateField( tc, "notExistingFiledName" );
    }

    @Test public void testInvokePrivateMethod() 
    {
        String match = (String)PrivateAccessor.invokePrivateMethod( tc, "privateMethod", "testarg" );
        assertEquals( match, "private Field" );
    }
  
    @Test(expected=IllegalArgumentException.class)  
        public void testInvokePrivateMethodNotExisting() 
    {
         String match = (String)PrivateAccessor.invokePrivateMethod( tc, "nonExistingMethod", "testarg" );
    }
}