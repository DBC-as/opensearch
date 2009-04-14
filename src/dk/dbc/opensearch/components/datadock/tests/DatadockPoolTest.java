/**
 * \file DatadockPoolTest.java
 * \brief The DatadockPoolTest class
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

/** \brief UnitTest for DatadockPool **/

import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.datadock.DatadockPool;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.Throwable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.*;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;


/**
 *
 */
public class DatadockPoolTest extends TestCase
{
    /**
     * The (mock)objects we need for the most of the tests
     */
    Estimate mockEstimate;
    Processqueue mockProcessqueue;
    CargoContainer mockCargoContainer;
    FutureTask mockFutureTask;
    ThreadPoolExecutor mockThreadPoolExecutor;
    DatadockJob datadockJob;
    DatadockPool datadockPool;
    DatadockThread datadockThread;
    /**
     * After each test the mock are reset
     */

    static FutureTask mockFuture = createMock( FutureTask.class );

    @Ignore
    @MockClass( realClass = DatadockPool.class )
    public static class MockDatadockPool
    {
    	@Mock(invocations = 1)
    	public static FutureTask getTask( DatadockJob datadockjob )
    	{
    		return mockFuture;
        }
    }

    @Ignore
    @Test public void testConstructor()
    {
        //        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
    }


    @Ignore
    @Test public void testSubmit() throws IOException, ConfigurationException, ClassNotFoundException
    {
//     	Mockit.setUpMocks( MockDatadockPool.class );

//         mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
//         mockFutureTask = createMock( FutureTask.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );

//         //
//         datadockPool.submit( datadockJob );
//         //
        
//         verify( mockThreadPoolExecutor );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
//         reset( mockFutureTask );
//         reset( mockFuture );

//         Mockit.restoreAllOriginalDefinitions();
    }

    
    @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring" )
    @Test    
    public void testSubmit2() throws IOException, ConfigurationException, ClassNotFoundException
    {   
//     	mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( isA( FutureTask.class ) ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );

//         //
//         //        datadockPool.submit( datadockJob );
//         //

//         // verify( mockThreadPoolExecutor );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
    }

    
    @Ignore
    @Test 
    public void testCheckJobs_isDoneFalse() throws Exception
    {
//         Mockit.setUpMocks( MockDatadockPool.class );

//         mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
//         mockFutureTask = createMock( FutureTask.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );
        
//         expect( mockFuture.isDone() ).andReturn( false );
//         replay( mockFuture );

//         //
//         datadockPool.submit( datadockJob );
//         datadockPool.checkJobs();
//         //

//         verify( mockFuture );
//         verify( mockThreadPoolExecutor );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
//         reset( mockFutureTask );
//         reset( mockFuture );

//         Mockit.restoreAllOriginalDefinitions();
    }

    
    @Ignore
    @Test 
    public void testCheckJobs_isDoneTrue() throws Exception
    {
//         Mockit.setUpMocks( MockDatadockPool.class );

//         mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
//         mockFutureTask = createMock( FutureTask.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );
        
//         expect( mockFuture.isDone() ).andReturn( true );
//         expect( mockFuture.get() ).andReturn( 10f );
        
//         replay( mockFuture );

//         //
//         datadockPool.submit( datadockJob );
//         datadockPool.checkJobs();
//         //

//         verify( mockFuture );
//         verify( mockThreadPoolExecutor );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
//         reset( mockFutureTask );
//         reset( mockFuture );

//         Mockit.restoreAllOriginalDefinitions();
    }
    

    @Ignore
    @Test 
    public void testCheckJobs_isDoneError() throws Exception
    {
//         Mockit.setUpMocks( MockDatadockPool.class );

//         mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
//         mockFutureTask = createMock( FutureTask.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );
        
//         expect( mockFuture.isDone() ).andReturn( true );
//         expect( mockFuture.get() ).andThrow( new ExecutionException( new Throwable( "test exception" ) ) );
//         replay( mockFuture );

//         //
//         datadockPool.submit( datadockJob );
//         datadockPool.checkJobs();
//         //

//         verify( mockFuture );
//         verify( mockThreadPoolExecutor );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
//         reset( mockFutureTask );
//         reset( mockFuture );

//         Mockit.restoreAllOriginalDefinitions();
    }


    @Ignore
    @Test 
    public void testShutdown() throws Exception
    {
//         Mockit.setUpMocks( MockDatadockPool.class );

//         mockFedoraHandler = createMock( FedoraHandler.class );
//         mockEstimate = createMock( Estimate.class );
//         mockProcessqueue = createMock( Processqueue.class );
//         mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
//         mockFutureTask = createMock( FutureTask.class );

//         String testSubmitter = "testSubmitter";
//         String testFormat = "testFormat";
//         File tmpFile = File.createTempFile("opensearch-unittest","" );
//         FileWriter fstream = new FileWriter( tmpFile );
//         BufferedWriter out = new BufferedWriter(fstream);
//         out.write("Hello Java");
//         out.close();

//         tmpFile.deleteOnExit();
//         URI testURI = tmpFile.toURI();

//         datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockFedoraHandler);
//         datadockJob = new DatadockJob( testURI, testSubmitter, testFormat);

//         expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFutureTask );
//         replay( mockThreadPoolExecutor );
        
//         expect( mockFuture.isDone() ).andReturn( false );
//         expect( mockFuture.isDone() ).andReturn( true );
        
//         replay( mockFuture );

//         //
//         datadockPool.submit( datadockJob );
//         datadockPool.shutdown();
//         //

//         verify( mockThreadPoolExecutor );
//         verify( mockFuture );

//         reset( mockFedoraHandler );
//         reset( mockEstimate );
//         reset( mockProcessqueue );
//         reset( mockThreadPoolExecutor );
//         reset( mockFutureTask );
//         reset( mockFuture );

//         Mockit.restoreAllOriginalDefinitions();
    }    
}
