package dk.dbc.opensearch.common.config.tests;


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


import dk.dbc.opensearch.common.config.FileSystemConfig;
//import dk.dbc.opensearch.common.config.Config;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.AbstractConfiguration;

import org.apache.commons.configuration.ConfigurationException;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;

import org.junit.*;
import static org.junit.Assert.*;



public class FileSystemConfigTest
{
    @MockClass( realClass = AbstractConfiguration.class )
        public static class MockXMLConf1
        {
            @Mock public static String getString( String key )
            {
                return "test";
            }
        } 
    
    @MockClass( realClass = AbstractConfiguration.class )
        public static class MockXMLConf2
        {
            @Mock public static String getString( String key )
            {
                return "another test/";
            }
        }
   
@After
    public void tearDown()
    {
        Mockit.tearDownMocks();
    }

    @Test
    public void testGetConfigPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
        String trunk = FileSystemConfig.getConfigPath();
    
        String str = "/config/";
        boolean endsWith = trunk.contains( str ); 
        assertTrue( endsWith );
    }
    

    @Test
    public void testGetTrunkPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
        String trunk = FileSystemConfig.getTrunkPath();
    
        CharSequence cs = "/opensearch";
        boolean endsWith = trunk.contains( cs );  
        assertTrue( endsWith );
    }
    
    
    @Test
    public void testGetPluginsPath() throws ConfigurationException
    {
    	String plugins = FileSystemConfig.getPluginsPath();
    
    	CharSequence cs = "/plugins";
        boolean endsWith = plugins.contains( cs ); 
        assertTrue( endsWith );    	
    }

    /**
     * We mock the getString method of the XMLConfiguration class to get a string 
     * without and with "/" as the last character when it is called through
     * the public method getTrunkPath
     */
@Test public void testSanitize1() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf1.class);

        String test = FileSystemConfig.getTrunkPath(); 
        assertEquals( test, "test/" ); 
    }
    
    @Test public void testSanitize2() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf2.class);

        String test = FileSystemConfig.getTrunkPath(); 
        assertEquals( test, "another test/" ); 
    }
}