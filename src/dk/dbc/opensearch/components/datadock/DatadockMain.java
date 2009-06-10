/**
 * \file DatadockMain.java
 * \brief The DatadockMain class
 * \package datadock;
 */
package dk.dbc.opensearch.components.datadock;

/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/

import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.IHarvester;
import dk.dbc.opensearch.common.fedora.FedoraCommunication;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;

import java.io.IOException;
import java.net.URL;
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

    static int queueSize;
    static int corePoolSize;
    static int maxPoolSize;
    static long keepAliveTime;
    static int pollTime;
    static URL cfgURL;
    static String harvestDir;
    //public static HashMap< InputPair< String, String >, ArrayList< String > > jobMap;


    public DatadockMain() {}
    
    
    public void init() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
    	log.debug( "DatadockMain init called" );
    	pollTime = DatadockConfig.getMainPollTime();
        queueSize = DatadockConfig.getQueueSize();
        corePoolSize = DatadockConfig.getCorePoolSize();
        maxPoolSize = DatadockConfig.getMaxPoolSize();
        keepAliveTime = DatadockConfig.getKeepAliveTime();
        harvestDir = HarvesterConfig.getFolder();

        //jobMap = JobMapCreator.getMap( this.getClass() );
        //log.debug( String.format( "the map: %s ",jobMap.toString() ));

        log.debug( String.format( "---> queueSIZE = '%s'", queueSize ) );
    }


    // Helper method to avoid static problems in init
    @SuppressWarnings("unchecked")
	public Class getClassType()
    {
    	return this.getClass();
    }
    
    /**
     * The shutdown hook. This method is called when the program catches the kill signal.
     */
    static public void shutdown()
    {
        shutdownRequested = true;

        try
        {
            log.info( "Shutting down." );
            datadockManager.shutdown();
        }
        catch( InterruptedException e )
        {
            log.error( "Interrupted while waiting on main daemon thread to complete." );
        }

        log.info( "Exiting." );
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
        String pidFile = System.getProperty( "daemon.pidfile" );
        FileHandler.getFile( pidFile ).deleteOnExit();
        System.out.close();
        System.err.close();
    }


    /**
     * Adds the shutdownhook.
     */
    static protected void addDaemonShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread() { public void run() { shutdown(); } } );
    }


    /**
     * The datadocks main method.
     * Starts the datadock and starts the datadockManager.
     */
    static public void main(String[] args) throws Throwable
    {
    	log.debug( "DatadockMain main called" );
        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

        try
        {
            DatadockMain datadockmain = new DatadockMain();
            log.debug( "DatadockMain main called" );
            
            datadockmain.init();

            log.removeAppender( "RootConsoleAppender" );
            log.addAppender( startupAppender );

            /** -------------------- setup and start the datadockmanager -------------------- **/
            log.info( "Starting the datadock" );

            log.debug( "initializing resources" );

            // DB access
            IEstimate estimate = new Estimate();
            IProcessqueue processqueue = new Processqueue();

            // Fedora access
            PIDManager PIDmanager = new PIDManager();
            IFedoraCommunication fedoraCom = new FedoraCommunication();

            log.debug( "Starting datadockPool" );

            // datadockpool
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( 10 );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );            
            threadpool.purge();
            
            datadockPool = new DatadockPool( threadpool, (Estimate) estimate, processqueue, PIDmanager, fedoraCom );

            log.debug( "Starting harvester" );
            
            // harvester;
            IHarvester harvester = new FileHarvest();

            log.debug( "Starting the manager" );
            // Starting the manager
            datadockManager = new DatadockManager( datadockPool, harvester );

            /** --------------- setup and startup of the datadockmanager done ---------------- **/
            log.debug( "Daemonizing" );

            daemonize();
            addDaemonShutdownHook();
        }
        catch ( Throwable e )
        {
            System.out.println( "Startup failed." + e );
            log.fatal( "Startup failed.", e);
            throw e;
        }
        finally
        {
            log.removeAppender( startupAppender );
        }

        while( ! isShutdownRequested() )
        {
            try
            {
            	log.debug( "DatadockMain calling datadockManager update" );
                datadockManager.update();
                Thread.currentThread();
                Thread.sleep( pollTime );
            }
            catch( InterruptedException ie )
            {
                /**
                 * \todo: dont we want to get the trace?
                 */
                log.error( "InterruptedException caught in mainloop: "  + ie );
                log.error( "  " + ie.getMessage() );
            }
            catch( RuntimeException re )
            {
                log.error( "RuntimeException caught in mainloop: " + re );
                log.error( "  " + re.getCause().getMessage() );
                throw re;
            }
            catch( Exception e )
            {
                /**
                 * \todo: dont we want to get the trace?
                 */
                log.error( "Exception caught in mainloop: " + e.toString() );
            }
        }
    }
}
