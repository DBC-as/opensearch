/** \brief UnitTest for PluginLoader */
package dk.dbc.opensearch.common.pluginframework.tests;


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

import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginClassLoader;
import dk.dbc.opensearch.common.os.FileHandler;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

import java.io.File;
import java.lang.reflect.Method;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

/**
 *
 */
public class PluginLoaderTest
{
    ClassLoader pcl;
    PluginLoader pl;
    Boolean noException;
    Boolean illegalArgument;
    IPluggable testIPlug;
    String testClassString;
    String invalidClassString;

    /**
     *
     */
    @Before public void SetUp()throws Exception 
    {
        testClassString = "dk.dbc.opensearch.common.pluginframework.tests.TestPlugin";
        invalidClassString = "dk.dbc.opensearch.common.pluginframework.tests.NotExisting";
        noException = true;
        illegalArgument = false;

        pcl = new PluginClassLoader();
    }

    
    /**
     *
     */
    @After public void TearDown() 
    {
        pl = null;
    }

    
    /**
     *
     */
    @Test public void constructorTest() 
    {
        try
        {
            pl = new PluginLoader( pcl );
        }
        catch( Exception e)
        {
            noException = false;
        }
     
        assertTrue( noException );
    }

    
    /**
     * Tests the loadPlugin method by giving the class string
     * to the test class TestPlugin
     */
    @Test public void getPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException 
    {
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ testClassString };

        pl = new PluginLoader( pcl );
        method = pl.getClass().getDeclaredMethod( "getPlugin", argClasses );
        method.setAccessible( true );
        testIPlug = ( IPluggable ) method.invoke( pl, args );

        assertTrue( testIPlug.getClass().getName().equals( testClassString ) );
    }

    
    /**
     * Tests that the PluginLoader.loadPlugin throws an IllegalArgumentException
     * when given a not-existing class name. The exception is wrapped in an
     * InvocationTargetException
     */
    @Test public void invalidClassNameTest() throws NoSuchMethodException, IllegalAccessException
    {
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ invalidClassString };
        try
        {
            pl = new PluginLoader( pcl );
            method = pl.getClass().getDeclaredMethod( "getPlugin", argClasses );
            method.setAccessible( true );
            testIPlug = ( IPluggable ) method.invoke( pl, args );
        }
        catch( InvocationTargetException ite)
        {
            illegalArgument = ( ite.getCause().getClass() == ClassNotFoundException.class ); 
        }
        //needs to do it this way...
        assertTrue( illegalArgument );
    }
}
