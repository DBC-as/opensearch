/**
 * \file DatadockManagerTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock.tests;


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

/** \brief UnitTest for DatadockManager **/

import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.components.datadock.DatadockManager;
import dk.dbc.opensearch.components.datadock.DatadockPool;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.IHarvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import org.xml.sax.SAXException;


/**
 * Unittest for the DatadockManager
 */
public class DatadockManagerTest 
{
    IHarvester mockHarvester;
    DatadockPool mockDatadockPool;
    Vector<DatadockJob> mockJobs;
    DatadockJob mockDatadockJob;
    Vector< CompletedTask > mockFinJobs;

    
    @Before 
    public void Setup()
    {
        mockHarvester = createMock( FileHarvest.class );
        mockDatadockPool = createMock( DatadockPool.class );
        mockDatadockJob = createMock( DatadockJob.class );
        mockFinJobs = createMock( Vector.class );        
    }

    
    @After 
    public void tearDown()
    {
        reset( mockHarvester);
        reset( mockDatadockPool );
        reset( mockDatadockJob );
        
        reset( mockFinJobs );
    }

   
    @Test 
    public void testConstructor() throws ConfigurationException 
    {
        mockHarvester.start();
        replay( mockHarvester );
        DatadockManager datadockmanager = new DatadockManager( mockDatadockPool, mockHarvester );
        verify( mockHarvester );
    }
    
   
    @Test 
    public void testUpdate() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, ConfigurationException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException                                          
    {        
        Vector<DatadockJob> jobs = new Vector<DatadockJob>();
        jobs.add( mockDatadockJob );

        URI testURI = new URI( "testURI" );

        mockHarvester.start();
        expect( mockHarvester.getJobs() ).andReturn( jobs );

        mockDatadockPool.submit( mockDatadockJob );
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
                
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockDatadockJob );
        
        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
        
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockDatadockJob );
    }

    
    @Test 
    public void testUpdate_reject() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException 
    {        
        Vector< DatadockJob > jobs = new Vector< DatadockJob >();
        jobs.add( mockDatadockJob );

        URI testURI = new URI( "testURI" );
        
        mockHarvester.start();
        expect( mockHarvester.getJobs() ).andReturn( jobs );
        
        mockDatadockPool.submit( mockDatadockJob );
        expectLastCall().andThrow( new RejectedExecutionException() );
        //mockDatadockPool.submit( mockDatadockJob );

        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockDatadockJob );
        
        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
                       
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockDatadockJob );
    }
    
    
    @Test 
    public void testShutdown() throws InterruptedException, ConfigurationException
    {
        mockHarvester.start();
        mockHarvester.shutdown();
        mockDatadockPool.shutdown();

        replay( mockHarvester );
        replay( mockDatadockPool );
        DatadockManager datadockmanager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockmanager.shutdown();
    
        verify( mockDatadockPool );
        verify( mockHarvester );        
     }
}
