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
 * \file
 * \brief
 */


package dk.dbc.opensearch.components.harvest;


import java.util.ArrayList;
import java.util.Iterator;

//import dk.dbc.opensearch.common.db.OracleDBConnection;
import dk.dbc.opensearch.common.db.OracleDBPooledConnection;
import java.sql.SQLException;

import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import dk.dbc.opensearch.common.types.IIdentifier;
import dk.dbc.opensearch.common.types.IJob;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;



import oracle.jdbc.pool.OracleDataSource;
import java.util.Properties;

/**
 *
 */
public class HarvestFunc
{
    private static Logger log = Logger.getLogger( HarvestFunc.class );

    static ESHarvest esh;

    private static int counter = 0;

    public static void main( String[] args )
    {
	// Setting up the logging or bail out.
	try 
	    {
		Log4jConfiguration.configure( "log4j_datadock.xml" );
	    } 
	catch (ConfigurationException ce )
	    {
		System.out.println( "ConfigurationException Caught. Exiting!");
		System.exit(1);
	    }

        ConsoleAppender startupAppender = new ConsoleAppender(new SimpleLayout());

	log.info( "HarvestFunc.main started" );
        runTests();
    }

    /*
    private void resetESBase() {

	IDBConnection oracleInstance;
	Connection    conn;
	String        databasename = "test";
        try
	    {
		oracleInstance = new OracleDBConnection();
		conn = oracleInstance.getConnection();
	    }
        catch( Exception e )
	    {
		log.fatal( "An error occured when trying to connect to the ESbase", e );
		System.exit(1);
	    }
    }
    */

    static void runTests()
    {
        try
        {
            startESHarvestTest();
            getJobsNDataTest();
        }
        catch ( HarvesterIOException hioe )
        {
            System.out.println( "An internal Harvester IO error occured" );
            hioe.printStackTrace();
        }
        catch ( HarvesterInvalidStatusChangeException hisce )
        {
            System.out.println( "An internal Harvester status change exception error occured" );
            hisce.printStackTrace();
        }
	catch ( SQLException sqle )
	{
	    System.out.println( "An SQLException was thrown" );
	    sqle.printStackTrace();
	}
    }


    private static void startESHarvestTest() throws HarvesterIOException, SQLException
    {
	String databasename = "test";

	OracleDataSource ods = null;
	String cache_name = new String( "ESHARVESTER_CACHE" );

	try
	{

	    ods = new OracleDataSource();

	    // set db-params:
	    ods.setURL( "jdbc:oracle:thin:@tora1.dbc.dk:1521" );
	    ods.setUser( "damkjaer" );
	    ods.setPassword( "damkjaer" );

	    // set db-cache-params:
	    ods.setConnectionCachingEnabled( true ); // connection pool
	    ods.setConnectionCacheName( cache_name );

	    // set cache properties:
	    Properties cacheProperties = new Properties();
	    cacheProperties.setProperty( "MinLimit", "1" );
	    cacheProperties.setProperty( "MaxLimit", "1" );
	    cacheProperties.setProperty( "InitialLimit", "1" );
	    cacheProperties.setProperty( "ConnectionWaitTimeout", "5" );
	    cacheProperties.setProperty( "ValidateConnection", "true" );

	    ods.setConnectionCacheProperties( cacheProperties );

	}
	catch( SQLException sqle )
	{
	    String errorMsg = new String( "An SQL error occured during the setup of the OracleDataSource" );
	    log.fatal( errorMsg, sqle );
	    throw sqle;
	}

	OracleDBPooledConnection connectionPool;
	// try
	//	{
	//connectionPool = new OracleDBPooledConnection<OracleDataSource>( "ESHARVESTER_CACHE" );
	connectionPool = new OracleDBPooledConnection( cache_name, ods );
	    /*
	}
	catch( SQLException sqle ) 
	{
	    String errorMsg = new String( "Could not create OracleDBPooledConnection" );
	    log.fatal( errorMsg, sqle );
	    throw sqle;
	}
	    */
	esh = new ESHarvest( connectionPool, databasename );
        esh.start();
    }


    private static void getJobsNDataTest() throws HarvesterIOException, HarvesterInvalidStatusChangeException
    {
        byte[] data = null;

        ArrayList<IJob> jobL = esh.getJobs( 2 );
        System.out.println( String.format( " the joblist contained %s jobs", jobL.size() ) );
        Iterator iter = jobL.iterator();
        System.out.println( "got jobs:" );
        while( iter.hasNext() )
        {
            System.out.println("");
            IJob theJob = (IJob)iter.next();
            System.out.println( String.format( "job: %s", theJob.toString() ) );
            log.info( String.format( "job ID: %s", theJob.getIdentifier() ) );
            try
            {
                data = esh.getData( theJob.getIdentifier() );

                System.out.println(  String.format( "data gotten: %s", data.toString() ) );
            }
            catch( HarvesterUnknownIdentifierException huie )
            {
                huie.printStackTrace();
            }
            setStatusTest( theJob.getIdentifier() );
        }
    }
    
    /**
     * testing the updating of jobs setting the status to SUCCESS and then to FAILURE
     */
    private static void setStatusTest( IIdentifier id ) throws HarvesterIOException
    {
        try
        {
            if ( counter % 2 == 0 )
            {
                esh.setStatusFailure( id, "This is a failure" );
            }
            else
            {
		// Notice: Empty PID
                esh.setStatusSuccess( id, "" );
            }

            ++counter;
        }
        catch( HarvesterUnknownIdentifierException huie )
        {
            huie.printStackTrace();
        }
        catch( HarvesterInvalidStatusChangeException hisce )
        {
            System.out.println(hisce.getMessage() );
            hisce.printStackTrace();
        }
    }
}
