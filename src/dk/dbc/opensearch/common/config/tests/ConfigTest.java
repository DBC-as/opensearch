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


import dk.dbc.opensearch.common.config.Config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.lang.reflect.Field;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mockit.Mockit;
import mockit.MockClass;
import mockit.Mock;


public class ConfigTest
{
    //Logger logger = Logger.getLogger( ConfigTest.class );

    @MockClass( realClass = XMLConfiguration.class )
        public static class MockXMLConf
        {
            @Mock public void $init( URL url ) throws ConfigurationException
            {
                throw new ConfigurationException( "meaningful message" );
            } 
        }
    /**
     * mix between unit and function test...
     */
    @After public void tearDown()
    {
        Mockit.tearDownMocks();  
    }

    @Test
    public void testConstructor() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
    	Config c = new Config();
        Field cfgURL;
        cfgURL = c.getClass().getDeclaredField( "cfgURL" );
        
        cfgURL.setAccessible( true );
        
        // Test that getResource() finds a file.
        URL url = (URL) cfgURL.get( c );
        assertNotNull( url );
        
        // Test that getResource() finds the correct file.
        boolean fileName = url.getPath().endsWith( "config.xml" );
        assertTrue( fileName );
    }


    @Test( expected = ConfigurationException.class )
        public void testConstructor2() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf.class );

        Config c = new Config();
    }
}