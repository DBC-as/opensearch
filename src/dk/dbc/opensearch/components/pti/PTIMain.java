/**
 * \file PTIMain.java
 * \brief The PTIMain class
 * \package pti;
 */
package dk.dbc.opensearch.components.pti;


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


import dk.dbc.opensearch.common.compass.CompassFactory;
import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.Pair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.compass.core.Compass;
import org.xml.sax.SAXException;


/**
 * The Main method of the PTI. It secures all necessary
 * resources for the program, starts the PTIManager and then
 * closes stdin and stdout thus closing connection to the console.
 *
 * It also adds a shutdown hook to the JVM so orderly shutdown is
 * accompleshed when the process is killed.
 */
public class PTIMain
{
    static Logger log = Logger.getLogger("PTIMain");
    static protected boolean shutdownRequested = false;
    static PTIPool ptiPool = null;
    static PTIManager ptiManager = null;

    static int queueSize;
    static int corePoolSize;
    static int maxPoolSize;
    static long keepAliveTime;
    static int pollTime;
    
    static HashMap< Pair< String, String >, ArrayList< String > > jobMap;

    
    @SuppressWarnings("unchecked")
	public static void init() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
    	PTIMain pti = new PTIMain();
    	Class classType = pti.getClassType();
    	jobMap = JobMapCreator.getMap( classType );

        pollTime = PtiConfig.getMainPollTime();
        queueSize = PtiConfig.getQueueSize();
        corePoolSize = PtiConfig.getCorePoolSize();
        maxPoolSize = PtiConfig.getMaxPoolSize();
        keepAliveTime = PtiConfig.getKeepAliveTime();

    }


    // Helper method to avoid static problems in init
    @SuppressWarnings("unchecked")
	private Class getClassType()
    {
    	return this.getClass();
    }

    
    /**
     * The shutdown hook. This method is called when the program catch
     * the kill signal.
     */
    static public void shutdown()
    {
        shutdownRequested = true;

        try
        {
            log.info("Shutting down.");
            ptiManager.shutdown();
        }
        catch(InterruptedException e)
        {
            log.error("Interrupted while waiting on main daemon thread to complete.");
        }

        log.info("Exiting.");
    }


    /**
     * Getter method for shutdown signal.
     */
    static public boolean isShutdownRequested()
    {
        return shutdownRequested;
    }


    /**
     * Daemonizes the program, ie. disconnects from the console and
     * creates a pidfile.
     */
    static public void daemonize()
    {
        FileHandler.getFile( System.getProperty("daemon.pidfile") ).deleteOnExit();
        System.out.close();
        System.err.close();
    }


    /**
     * Adds the shutdownhook.
     */
    static protected void addDaemonShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread() { public void run() { shutdown(); }});
    }

    
    /**
     * The PTIs main method.
     * Starts the PTI and starts the PTIManager.
     */
    static public void main(String[] args)
    {
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

        try
        {
            init();
            
            log.removeAppender( "RootConsoleAppender" );
            log.addAppender(startupAppender);

            /** -------------------- setup and start the PTImanager -------------------- **/
            log.info("Starting the PTI");

            log.debug( "initializing resources" );

            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();

            CompassFactory compassFactory = new CompassFactory();
            Compass compass = compassFactory.getCompass();

            log.debug( "Starting PTIPool" );
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( queueSize );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );
            PTIPool ptiPool = new PTIPool( threadpool, estimate, compass, jobMap );

            ptiManager = new PTIManager( ptiPool, processqueue );

            /** --------------- setup and startup of the PTImanager done ---------------- **/
            log.debug( "Daemonizing" );

            daemonize();
            addDaemonShutdownHook();

        }
        catch (Throwable e)
        {
            System.out.println("Startup failed." + e);
            log.fatal("Startup failed.",e);
        }
        finally
        {
            log.removeAppender(startupAppender);
        }

        while(!isShutdownRequested())
        {// Mainloop
        	try
            {
                ptiManager.update();
                Thread.currentThread();
				Thread.sleep( pollTime );
            }
        	catch(InterruptedException ie)
            {
                log.error("InterruptedException caught in mainloop: ");
                log.error("  "+ie.getMessage() );
            }
        	catch(RuntimeException re)
            {
                log.error("RuntimeException caught in mainloop: " + re);
                log.error("\n" + re.getCause().getMessage() );
                log.error("\n" + re.getCause().getStackTrace() );
                throw re;
            }
        	catch(Exception e)
            {
                log.error("Exception caught in mainloop: " + e);
                log.error("  " + e.getMessage() );
            }
        }
    }
}
