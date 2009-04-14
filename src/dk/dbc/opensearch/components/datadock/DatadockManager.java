/**
 * \file DatadockManager.java
 * \brief The DatadockManager class
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


import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.components.harvest.IHarvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * \brief the DataDockManager manages the startup, running and
 * closedown of the associated harvester and threadpool
 */
public class DatadockManager
{
    static Logger log = Logger.getLogger( DatadockManager.class );

    
    private DatadockPool pool= null;
    private IHarvester harvester = null;
    XMLConfiguration config = null;    
    Vector< DatadockJob > registeredJobs = null;
    
    
    /**
     * Constructs the the DatadockManager instance.
     */
    public DatadockManager( DatadockPool pool, IHarvester harvester ) throws ConfigurationException
    {
        log.debug( "Constructor( pool, harvester ) called" );

        this.pool = pool;
        
        this.harvester = harvester;
        harvester.start();

        registeredJobs = new Vector< DatadockJob >(); 
    }

    
    public void update() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, ServiceException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        log.debug( "DatadockManager update called" );
      
        // Check if there are any registered jobs ready for docking
        // if not... new jobs are requested from the harvester
        if( registeredJobs.size() == 0 )
        {
            log.debug( "no more jobs. requesting new jobs from the harvester" );
            registeredJobs = harvester.getJobs();
        }
      
        log.debug( "DatadockManager.update: Size of registeredJobs: " + registeredJobs.size() );
        
        for( int i = 0; i < registeredJobs.size(); i++ )
        {
        	DatadockJob job = registeredJobs.get( 0 );
        
            // execute jobs
        	try
        	{
        		pool.submit( job );
        		registeredJobs.remove( 0 );
        		log.debug( String.format( "submitted job: '%s'", job.getUri().getRawPath() ) );
	        }
        	catch( RejectedExecutionException re )
        	{
        		log.debug( String.format( "job: '%s' rejected, trying again", job.getUri().getRawPath() ) );	           	
        	}
        }
        
        //checking jobs
        Vector<CompletedTask> finishedJobs = pool.checkJobs();
    }
    
    
    public void shutdown() throws InterruptedException
    {
        log.debug( "Shutting down the pool" );
        pool.shutdown();        
        log.debug( "The pool is down" );        
        
        log.debug( "Stopping harvester" );        
        harvester.shutdown();
        log.debug( "The harvester is stopped" );
    }
}