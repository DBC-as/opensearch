/**
 * \file DatadockMain.java
 * \brief The DatadockMain class
 * \package datadock;
 */
package dk.dbc.opensearch.components.datadock;

import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.helpers.JobMapCreator;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.IHarvester;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;


/**
 * The Main method of the datadock. It secures all necessary
 * resources for the program, starts the datadockManager and then
 * closes stdin and stdout thus closing connection to the console.
 * 
 * It also adds a shutdown hook to the JVM so orderly shutdown is
 * accompleshed when the process is killed.
 */
public class DatadockMain 
{
    static Logger log = Logger.getLogger( DatadockMain.class );
    
    static protected boolean shutdownRequested = false;    
    static DatadockPool datadockPool = null;
    static DatadockManager datadockManager = null;
    
    static XMLConfiguration config = null;
    static int queueSize;
    static int corePoolSize;
    static int maxPoolSize;
    static long keepAliveTime;
    static int pollTime;
    static URL cfgURL;
    static String harvestDir;
    public static HashMap< Pair< String, String >, ArrayList< String > > jobMap;
    

    public DatadockMain() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {       
        pollTime = DatadockConfig.getDatadockMainPollTime();
        queueSize = DatadockConfig.getDatadockQueueSize();
        corePoolSize = DatadockConfig.getDatadockCorePoolSize();
        maxPoolSize = DatadockConfig.getDatadockMaxPoolSize();
        keepAliveTime = DatadockConfig.getDatadockKeepAliveTime();
        harvestDir = HarvesterConfig.getHarvesterFolder();
        
        jobMap = JobMapCreator.getMap( this.getClass() );

        log.debug( String.format( "--->queueSIZE='%s'", queueSize ) );
    }
    
    
    /**
     * The shutdown hook. This method is called when the program catches the kill signal.
     */
    static public void shutdown()
    {
        shutdownRequested = true;

        try
        {
            log.info("Shutting down.");
            datadockManager.shutdown();
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
        FileHandler.getFile( System.getProperty( "daemon.pidfile" ) ).deleteOnExit();
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
     * The datadocks main method.  
     * Starts the datadock and starts the datadockManager.
     */
    static public void main(String[] args)
    {
    	System.out.println("print");
    	
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());
        
        try
        {
            DatadockMain datadockmain = new DatadockMain();
            
            log.removeAppender( "RootConsoleAppender" );
            log.addAppender(startupAppender);

            /** -------------------- setup and start the datadockmanager -------------------- **/            
            log.info("Starting the datadock");
            
            log.debug( "initializing resources" );
            
            // DB access
            Estimate estimate = new Estimate();
            Processqueue processqueue = new Processqueue();               
            
            // Fedora access
            PIDManager PIDmanager = new PIDManager();
            //            FedoraClientFactory fedoraClientFactory = new FedoraClientFactory();
            //            FedoraClient fedoraClient = fedoraClientFactory.getFedoraClient();
            //            FedoraHandler fedoraHandler = new FedoraHandler( fedoraClient );      
            // Job hashmapper
            
            log.debug( "Starting datadockPool" );
            
            // datadockpool
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( 10 );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );

            datadockPool = new DatadockPool( threadpool, estimate, processqueue, PIDmanager, jobMap );

            log.debug( "Starting harvester" );
            
            // harvester;
            File harvestDirectory = new File( harvestDir );
            IHarvester harvester = new FileHarvest( harvestDirectory );            
            
            log.debug( "Starting the manager" );
            // Starting the manager
            
            datadockManager = new DatadockManager( datadockPool, harvester );

            /** --------------- setup and startup of the datadockmanager done ---------------- **/
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
        {
            try
            {            	
                datadockManager.update();                
                Thread.currentThread().sleep( pollTime );
            }
            catch( InterruptedException ie )
            {
                log.error("InterruptedException caught in mainloop: ");
                log.error("  "+ie.getMessage() );
            }
            catch( RuntimeException re )
            {
                log.error("RuntimeException caught in mainloop: " + re);
                log.error("  " + re.getCause().getMessage() );
                throw re;
            }
            catch( Exception e )
            {
                log.error("Exception caught in mainloop: " + e);
                log.error("  " + e.getMessage() );
            }
        }
    }
    
    
    private HashMap< Pair< String, String >, List< String > > constructHashMap() throws DocumentException
    {
    	HashMap< Pair< String, String >, List< String > > task_list = new HashMap< Pair< String, String >, List< String > >();
        URL jobURL = getClass().getResource("/datadock_jobs.xml");
        
        Document doc = null;
        SAXReader saxReader = new SAXReader();
        doc = saxReader.read( jobURL );
        
        Element root = doc.getRootElement();

        
/*        for ( Iterator<Element> i = root.elementIterator(); i.hasNext(); )
        {
        	foreach job:
        		Pair< String, String > key = new Pair< String, String >( "submitter", "format");
        		List< String> values = new ArrayList< String >();
        		
        		foreach task:
        			values.append( name )
        			
       			Element element = i.next();
                element.getText();
                task_list.put(, value)
        }
*/        
        
    	return task_list;
    }
    
}
