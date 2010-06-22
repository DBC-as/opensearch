/**
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
 * \file DatadockMain.java
 * \brief
 */


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DataBaseConfig;
import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.db.OracleDBPooledConnection;
import dk.dbc.opensearch.common.db.PostgresqlDBConnection;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.HarvestType;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.FlowMapCreator;
import dk.dbc.opensearch.common.pluginframework.PluginTask;
import dk.dbc.opensearch.components.harvest.ESHarvest;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.FileHarvestLight;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;
import dk.dbc.opensearch.components.harvest.IHarvest;
import java.io.IOException;

import java.sql.SQLException;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import java.util.Properties;
import java.util.Map;
import java.util.List;

import oracle.jdbc.pool.OracleDataSource;

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
    private final static Logger log = Logger.getLogger( DatadockMain.class );
    private final static ConsoleAppender startupAppender = new ConsoleAppender( new SimpleLayout() );

    private static final String logConfiguration = "log4j_datadock.xml";
    protected boolean shutdownRequested = false;
    static DatadockPool datadockPool = null;
    static DatadockManager datadockManager = null;
    static PluginResolver pluginResolver;
    static FlowMapCreator flowMapCreator = null;


    private final int queueSize;
    private final int corePoolSize;
    private final int maxPoolSize;
    private final long keepAliveTime;
    private final int pollTime;
    private final String pluginFlowXmlPath;
    private final String pluginFlowXsdPath;
    private HarvestType harvestType;
    private static HarvestType defaultHarvestType = HarvestType.FileHarvest;
    static java.util.Date startTime = null;
    private boolean terminateOnZeroSubmitted = false;

    public DatadockMain()  throws ConfigurationException
    {
        pollTime = DatadockConfig.getMainPollTime();
        queueSize = DatadockConfig.getQueueSize();
        corePoolSize = DatadockConfig.getCorePoolSize();
        maxPoolSize = DatadockConfig.getMaxPoolSize();
        keepAliveTime = DatadockConfig.getKeepAliveTime();
        pluginFlowXmlPath = DatadockConfig.getPluginFlowXmlPath();
        pluginFlowXsdPath = DatadockConfig.getPluginFlowXsdPath();
    }


    public void readServerConfiguration() throws ConfigurationException
    {
    }

    /**
     *  Initializes a
     * {@link dk.dbc.opensearch.components.harvest.IHarvest Harvester} from a
     * command line parameter or, if that fails, a default harvester type
     * specified in the class
     */
    private void initializeHarvester()
    {
        log.trace( "Trying to get harvester type from commandline" );
        this.harvestType = HarvestType.getHarvestType( System.getProperty( "harvester" ) );
        
        if( null == this.harvestType )
        {
            this.harvestType = defaultHarvestType;
        }
        log.debug( String.format( "initialized harvester with type: %s", this.harvestType ) );
    }

    /**
     * Reads command line arguments and initializes relevant variables on the
     * object
     * @param args arguments recieved from the command line
     */
    private void readCommandLineArguments( String[] args )
    {
        for( String a : args )
        {
            log.warn( String.format( "argument: '%s'", a ) );
            if( a.equals( "--shutDownOnJobsDone" ) )
            {
                this.terminateOnZeroSubmitted = true;
            }
            else
            {
                log.warn( String.format( "Unknown argument '%s', ignoring it", a ) );
            }
        }
    }

    private int runServer( int mainJobsSubmitted, DatadockMain serverInstance )
    {
        while( !isShutdownRequested() )
        {
            try
            {
                log.trace( "DatadockMain calling datadockManager update" );
                long timer = System.currentTimeMillis();
                int jobsSubmitted = datadockManager.update();
                timer = System.currentTimeMillis() - timer;
                mainJobsSubmitted += jobsSubmitted;
                if( jobsSubmitted > 0 )
                {
                    log.info( String.format( "%1$d Jobs submitted in %2$d ms - %3$f jobs/s", jobsSubmitted, timer, jobsSubmitted / (timer / 1000.0) ) );
                }
                else
                {
                    log.info( String.format( "%1$d Jobs submitted in %2$d ms - ", jobsSubmitted, timer ) );
                    if( terminateOnZeroSubmitted )
                    {
                        serverInstance.shutdown();
                    }
                    else
                    {
                        Thread.currentThread();
                        Thread.sleep( serverInstance.pollTime );
                    }
                }
            }
            catch( HarvesterIOException hioe )
            {
                String fatal = String.format( "A fatal error occured in the communication with the database: %s", hioe.getMessage() );
                log.fatal( fatal, hioe );
                serverInstance.shutdown();
            }
            catch( InterruptedException ie )
            {
                log.error( String.format( "InterruptedException caught in mainloop: %s", ie.getMessage(), ie ) );
            }
            catch( RuntimeException re )
            {
                log.error( String.format( "RuntimeException caught in mainloop: %s", re.getMessage(), re ) );
            }
            catch( Exception e )
            {
                log.error( "Exception caught in mainloop: " + e.getMessage(), e );
            }
        }
        return mainJobsSubmitted;
    }

    /**
     * The shutdown hook. This method is called when the program catches a
     * kill signal.
     */
    private void shutdown()
    {
        this.shutdownRequested = true;

        try
        {
            log.info( "Shutting down." );
            datadockManager.shutdown();
        }
        catch( InterruptedException e )
        {
            log.error( "Interrupted while waiting on main daemon thread to complete." );
        }
        catch( HarvesterIOException hioe )
        {
            log.fatal( "Some error occured while shutting down the harvester", hioe );
        }
        
        log.info( "Exiting." );
    }


    /**
     * Getter method for shutdown signal.
     */
    public boolean isShutdownRequested()
    {
        return this.shutdownRequested;
    }


    /**
     * Daemonizes the program, ie. disconnects from the console and
     * creates a pidfile.
     */
    private void daemonize()
    {
        String pidFile = System.getProperty( "daemon.pidfile" );
        FileHandler.getFile( pidFile ).deleteOnExit();
        System.out.close();
        System.err.close();
    }


    /**
     * Adds the shutdownhook.
     */
    protected void addDaemonShutdownHook()
    {
        Runtime.getRuntime().addShutdownHook( new Thread() {
                @Override
                public void run()
                {
                    shutdown();
                }
            } );
    }


    private void collectStatistics( long mainTimer, int mainJobsSubmitted )
    {
        mainTimer = System.currentTimeMillis() - mainTimer;
        if( mainJobsSubmitted > 0 )
        {
            log.info( String.format( "Total: %1$d Jobs submitted in %2$d ms - %3$f jobs/s", mainJobsSubmitted, mainTimer, mainJobsSubmitted / (mainTimer / 1000.0) ) );
        }
        else
        {
            log.info( String.format( "Total: %1$d Jobs submitted in %2$d ms - ", mainJobsSubmitted, mainTimer ) );
        }
    }

   private IHarvest selectHarvester() throws SQLException, IllegalArgumentException, ConfigurationException, SAXException, HarvesterIOException, IOException
    {
        IHarvest harvester;
        switch( this.harvestType )
        {
            case ESHarvest:
                harvester = this.selectESHarvester();
                break;
            case FileHarvest:
                log.trace( "selecting FileHarvest" );
                harvester = new FileHarvest();
                break;
            case FileHarvestLight:
                log.trace( "selecting FileHarvestLight" );
                harvester = new FileHarvestLight();
                break;
            default:
                log.warn( "no harvester explicitly selected, and default type failed. This should not happen, but I'll default to FileHarvester" );
                harvester = new FileHarvest();
        }
        return harvester;
    }

    private IHarvest selectESHarvester() throws ConfigurationException, SQLException, HarvesterIOException
    {
        String dataBaseName = DataBaseConfig.getOracleDataBaseName();
        String oracleCacheName = DataBaseConfig.getOracleCacheName();
        String oracleUrl = DataBaseConfig.getOracleUrl();
        String oracleUser = DataBaseConfig.getOracleUserID();
        String oraclePassWd = DataBaseConfig.getOraclePassWd();
        String minLimit = DataBaseConfig.getOracleMinLimit();
        String maxLimit = DataBaseConfig.getOracleMaxLimit();
        String initialLimit = DataBaseConfig.getOracleInitialLimit();
        String connectionWaitTimeout = DataBaseConfig.getOracleConnectionWaitTimeout();

        log.info( String.format( "DB Url : %s ", oracleUrl ) );
        log.info( String.format( "DB User: %s ", oracleUser ) );
        OracleDataSource ods;
        try
        {
            ods = new OracleDataSource();

            // set db-params:
            ods.setURL( oracleUrl );
            ods.setUser( oracleUser );
            ods.setPassword( oraclePassWd );

            // set db-cache-params:
            ods.setConnectionCachingEnabled( true ); // connection pool

            // set the cache name
            ods.setConnectionCacheName( oracleCacheName );

            // set cache properties:
            Properties cacheProperties = new Properties();

            cacheProperties.setProperty( "MinLimit", minLimit );
            cacheProperties.setProperty( "MaxLimit", maxLimit );
            cacheProperties.setProperty( "InitialLimit", initialLimit );
            cacheProperties.setProperty( "ConnectionWaitTimeout", connectionWaitTimeout );
            cacheProperties.setProperty( "ValidateConnection", "true" );

            ods.setConnectionCacheProperties( cacheProperties );

        }
        catch( SQLException sqle )
        {
            String errorMsg = new String( "An SQL error occured during the setup of the OracleDataSource" );
            log.fatal( errorMsg, sqle );
            throw sqle;
        }

        OracleDBPooledConnection connectionPool = new OracleDBPooledConnection( oracleCacheName, ods );

        return new ESHarvest( connectionPool, dataBaseName );

    }

    private static void configureLogger() throws ConfigurationException
    {
        Log4jConfiguration.configure( logConfiguration );
        log.trace( "DatadockMain main called" );

    }


    /**
     * The datadocks main method.
     * Starts the datadock and starts the datadockManager.
     */
    public static void main(String[] args) throws Exception
    {
        configureLogger();
        DatadockMain serverInstance = new DatadockMain();

        log.trace( "Initializing harvester" );
        serverInstance.initializeHarvester();

        log.trace( "Reading command line arguments, if any" );
        serverInstance.readCommandLineArguments( args );

        log.trace( "Checking if harvester needs cleanup" );

        try
        {
            serverInstance.readServerConfiguration();

            log.removeAppender( "RootConsoleAppender" );
            log.addAppender( startupAppender );

            log.trace( "Initializing process queue" );
            IProcessqueue processqueue = new Processqueue( new PostgresqlDBConnection() );

            log.trace( "Initializing plugin resolver" );
            pluginResolver = new PluginResolver( new FedoraObjectRepository() );


            flowMapCreator = new FlowMapCreator( serverInstance.pluginFlowXmlPath, serverInstance.pluginFlowXsdPath );
            
            Map<String, List<PluginTask>> flowMap = flowMapCreator.createMap( pluginResolver );


            log.trace( "Starting harvester" );
            // harvester;
            IHarvest harvester = serverInstance.selectHarvester();

            log.trace( "Initializing the DatadockPool" );

            LinkedBlockingQueue<Runnable> queue = new LinkedBlockingQueue<Runnable>( serverInstance.queueSize );
            ThreadPoolExecutor threadpool = new ThreadPoolExecutor( serverInstance.corePoolSize, serverInstance.maxPoolSize, serverInstance.keepAliveTime, TimeUnit.SECONDS , queue );

            datadockPool = new DatadockPool( threadpool, processqueue, harvester, flowMap );

            log.trace( "Initializing the DatadockManager" );
            datadockManager = new DatadockManager( datadockPool, harvester, flowMap );

            log.info( "Daemonizing Datadock server" );

            serverInstance.daemonize();
            serverInstance.addDaemonShutdownHook();
        }
        catch ( Exception e )
        {
            System.out.println( "Startup failed." + e.getMessage() );
            log.fatal( String.format( "Startup failed: %s", e.getMessage() ) );
        }
        finally
        {
            serverInstance.shutdown();
            log.removeAppender( startupAppender );
        }

        long mainTimer = System.currentTimeMillis();
        int mainJobsSubmitted = 0;
        mainJobsSubmitted = serverInstance.runServer( mainJobsSubmitted, serverInstance );
        serverInstance.collectStatistics( mainTimer, mainJobsSubmitted );
    }
}
