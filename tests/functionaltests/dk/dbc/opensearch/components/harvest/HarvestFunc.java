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

import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.OracleDBConnection;
import java.sql.Connection;
import java.sql.SQLException;

import dk.dbc.opensearch.common.helpers.Log4jConfiguration;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Logger;
import org.apache.log4j.SimpleLayout;

import dk.dbc.opensearch.components.harvest.ESIdentifier;

/**
 *
 */
public class HarvestFunc
{


    static Logger log = Logger.getLogger( HarvestFunc.class );

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


    static void runTests()
    {
        try
        {
            startESHarvestTest();
            getJobsNDataTest();
        }
        catch ( HarvesterIOException hioe )
        {
            System.out.println( "An internal Harvester error occured" );
            hioe.printStackTrace();
        }
    }


    private static void startESHarvestTest() throws HarvesterIOException
    {
	String databasename = "test";
	IDBConnection oracleInstance;
	Connection conn;

	try
	    {
		oracleInstance = new OracleDBConnection();
		conn = oracleInstance.getConnection();
	    }
	catch (ConfigurationException ce )
	    {
		String errorMsg = "ConfigurationException caught when trying to create the database instance"; 
		log.fatal( errorMsg, ce );
		throw new HarvesterIOException( errorMsg, ce );
	    }
        catch( ClassNotFoundException cnfe )
	    {
		String errorMsg = "ClassNotFoundException caught when trying to create the database instance"; 
		log.fatal( errorMsg , cnfe );
		throw new HarvesterIOException( errorMsg, cnfe );
	    }
        catch( SQLException sqle )
	    {
		String errorMsg = "Error while trying to connect to Oracle ES-base: ";
		log.fatal( errorMsg , sqle );
		throw new HarvesterIOException( errorMsg, sqle );
	    }

	//        esh = new ESHarvest( oracleInstance, databasename );
	esh = new ESHarvest( conn, databasename );
        esh.start();
    }


    private static void getJobsNDataTest() throws HarvesterIOException
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
		// Notice: Empty PID
                esh.setStatusSuccess( id, "" );
            }
            else
            {
                esh.setStatusFailure( id, "This is a failure" );
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
