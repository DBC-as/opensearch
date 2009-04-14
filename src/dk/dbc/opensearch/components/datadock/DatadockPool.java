/**
 * \file DatadockPool.java
 * \brief The DatadockPool class
 * \package datadock;
 */
package dk.dbc.opensearch.components.datadock;


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


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.RejectedExecutionException;
import java.util.concurrent.ThreadPoolExecutor;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * \ingroup datadock
 *
 * \brief The datadockPool manages the datadock threads and provides methods
 * to add and check running jobs
 */
public class DatadockPool
{
    static Logger log = Logger.getLogger( DatadockPool.class );
    
    
    private Vector< FutureTask > jobs;
    private final ThreadPoolExecutor threadpool;
    private Estimate estimate;
    private Processqueue processqueue;
    private int shutDownPollTime;
    private HashMap< Pair< String, String >, ArrayList< String > > jobMap;
    private PIDManager PIDmanager;

    private int i = 0;

    XMLConfiguration config = null;

    
    /**
     * Constructs the the datadockPool instance
     *
     * @param threadpool The threadpool to submit jobs to
     * @param estimate the estimation database handler
     * @param processqueue the processqueue handler
     * @param fedoraHandler the fedora repository handler
     */
    public DatadockPool( ThreadPoolExecutor threadpool, Estimate estimate, Processqueue processqueue, PIDManager PIDmanager, HashMap< Pair< String, String >, ArrayList< String > > jobMap )throws ConfigurationException
    {
        log.debug( "Constructor( threadpool, estimat, processqueue, PIDmanager, jobMap ) called" );

        this.threadpool = threadpool;
        this.estimate = estimate;
        this.processqueue = processqueue;
        this.PIDmanager = PIDmanager;
        
        this.jobMap = jobMap;
        log.debug(String.format( "jobMap:%s", jobMap.toString() ));

        jobs = new Vector< FutureTask >();

        shutDownPollTime = DatadockConfig.getShutdownPollTime();
    }

    
    /**
     * submits a job to the threadpool for execution by a datadockThread.
     *
     * @param DatadockJob The job to start.
     *
     * @throws RejectedExecutionException Thrown if the threadpools jobqueue is full.
     * @throws ParserConfigurationException 
     * @throws PluginResolverException 
     * @throws NullPointerException 
     * @throws SAXException 
     */
    public void submit( DatadockJob datadockJob ) throws RejectedExecutionException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, ServiceException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        log.debug( String.format( "submit( path='%s', submitter='%s', format='%s' )",
                                  datadockJob.getUri().getRawPath(), datadockJob.getSubmitter(), datadockJob.getFormat() ) );

        // Get fedoraPID for job and adding it to the datadockJob.
        datadockJob.setPID( PIDmanager.getNextPID( datadockJob.getSubmitter() ) );

        log.debug( String.format( "counter = %s", ++i  ) );

        FutureTask future = getTask( datadockJob );
        
        threadpool.submit( future );
        //log.debug(String.format("Future is null is: %s ", future == null));        
        jobs.add( future );

    }

    
    public FutureTask getTask( DatadockJob datadockJob )throws ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        return new FutureTask( new DatadockThread( datadockJob, estimate, processqueue, jobMap ) );
    }


    /**
     * Checks the jobs submitted for execution, and returns a vector containing 
     * the jobs that are not running anymore
     *
     * if a Job throws an exception it is written to the log and the
     * datadock continues.
     *
     * @throws InterruptedException if the job.get() call is interrupted (by kill or otherwise).
     */
    public Vector< CompletedTask > checkJobs() throws InterruptedException 
    {
        log.debug( "DatadockPool method checkJobs called" );
    
        Vector< CompletedTask > finishedJobs = new Vector< CompletedTask >();
        for( FutureTask job : jobs )        
        {
            if( job.isDone() )
            {
                Float f = -1f;
                
                try
                {
                    log.debug( "DatadockPool checking job" );                    
                    f = (Float)job.get();
                }
                catch( ExecutionException ee )
                {                    
                    log.fatal( "DatadockPool Exception caught from job" );
                 
                    // getting exception from thread
                    Throwable cause = ee.getCause();
                    
                    log.error( String.format( "Exception Caught: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
                    StackTraceElement[] trace = cause.getStackTrace();
                    for( int i = 0; i < trace.length; i++ )
                    {
                    	log.error( trace[i].toString() );
                    }
                }
                
                log.debug( "DatadockPool adding to finished jobs" );
                finishedJobs.add( new CompletedTask( job, f ) );
            }
        }
        
        for( CompletedTask finishedJob : finishedJobs )
        {
            log.debug( String.format( "Removing Job Vector< FutureTask > jobs size: %s", jobs.size() ) );
            jobs.remove( finishedJob.getFuture() );
        }
        
        return finishedJobs;
    }

    
    /**
     * Shuts down the datadockPool. it waits for all current jobs to
     * finish before exiting.
     *
     * @throws InterruptedException if the checkJobs or sleep call is interrupted (by kill or otherwise).
     */
    public void shutdown() throws InterruptedException 
    {
        log.debug( "shutdown() called" );
    
        boolean activeJobs = true;
        while( activeJobs )
        {
            activeJobs = false;
            for( FutureTask job : jobs )
            {
                if( ! job.isDone() )
                {
                    activeJobs = true;
                }
            }
        }
    }
}
