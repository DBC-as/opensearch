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

/**
 * \file PTIMain.java
 * \brief the Main class for the pti module
 */


package dk.dbc.opensearch.components.pti;


import dk.dbc.opensearch.compass.CompassFactory;
import dk.dbc.opensearch.compass.PhraseMap;
import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.config.PtiConfig;
import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.PostgresqlDBConnection;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;
import org.compass.core.Compass;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.IObjectRepository;


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
    static PTIManager ptiManager = null;
    static int queueSize;
    static int corePoolSize;
    static int maxPoolSize;
    static long keepAliveTime;
    static int pollTime;
    static PluginResolver pluginResolver;

    @SuppressWarnings("unchecked")
    public static void init() throws ConfigurationException
    {
        pollTime = PtiConfig.getMainPollTime();
        queueSize = PtiConfig.getQueueSize();
        corePoolSize = PtiConfig.getCorePoolSize();
        maxPoolSize = PtiConfig.getMaxPoolSize();
        keepAliveTime = PtiConfig.getKeepAliveTime();
    }


    // Helper method to avoid static problems in init
    @SuppressWarnings( "unchecked" )
    public Class getClassType()
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
    static public void main(String[] args) throws Throwable
    {
        Log4jConfiguration.configure( "log4j_pti.xml" );
        log.debug( "PTIMain main called" );

        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

        boolean terminateOnZeroNewJobs = false;
        for( String a : args )
        {
            if( a.equals("--shutDownOnJobsDone") )
            {
                terminateOnZeroNewJobs = true;
            }
        }


        try
        {
            init();

            log.removeAppender( "RootConsoleAppender" );
            log.addAppender(startupAppender);

            /** -------------------- setup and start the PTImanager -------------------- **/
            log.info("Starting the PTI");

            log.debug( "initializing resources" );

            IDBConnection dbConnection = new PostgresqlDBConnection();
            IProcessqueue processqueue = new Processqueue( dbConnection );
            IObjectRepository repository = new FedoraObjectRepository();
            PhraseMap phraseMap = PhraseMap.instance(CompassConfig.getXSEMPath(), CompassConfig.getModifiedXSEMPath());
            CompassFactory compassFactory = new CompassFactory();
            Compass compass = compassFactory.getCompass();
            pluginResolver = new PluginResolver( repository );

            log.debug( "Starting PTIPool" );
            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( queueSize );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( corePoolSize, maxPoolSize, keepAliveTime, TimeUnit.SECONDS , queue );

            PTIPool ptiPool = new PTIPool( threadpool, compass, repository, pluginResolver );

            ptiManager = new PTIManager( ptiPool, processqueue );

            /** --------------- setup and startup of the PTImanager done ---------------- **/
            log.debug( "Daemonizing" );
            daemonize();            
            addDaemonShutdownHook();

        }
        catch ( Throwable e )
        {
            System.out.println("Startup failed." + e);
            log.fatal("Startup failed.",e);
            throw e;
        }
        finally
        {
            log.removeAppender(startupAppender);
        }

        while(!isShutdownRequested()) // Mainloop
        {
            try
            {
                int numberOfNewJobs = ptiManager.update();

                if( numberOfNewJobs == 0 && terminateOnZeroNewJobs )
                {
                    shutdown();
                }
                else
                {
                    Thread.currentThread();
                    Thread.sleep( pollTime );
                }
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
                log.error("  " + e.getMessage(), e );
            }
        }
        System.exit(0);
    }
}
