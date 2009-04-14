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


import dk.dbc.opensearch.common.types.CPMAlias;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.xml.sax.SAXException;


public class CPMAliasTest 
{
    /**
      * This test is not a strict unittest because we are dependant on a file on the disc.
      */
    CPMAlias cpmAlias;
    @Before
    public void setUp() throws Exception
    {
       	cpmAlias = new CPMAlias();
    }

    
    @After
    public void tearDown() throws Exception
    {
        cpmAlias = null;
    }

    
	@Test
	public void cpmIsValidAliasTest() throws ParserConfigurationException, SAXException, IOException
	{
            boolean valid = cpmAlias.isValidAlias( "article" );
            assertTrue( valid );
    }
	
	
	@Test
	public void cpmIsValidAliasFailTest() throws ParserConfigurationException, SAXException, IOException
	{
		boolean inValid = cpmAlias.isValidAlias( "fejlmester" );
		assertFalse( inValid );
	}
}
