/*
 *
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s,
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * \file ESHarvest.java
 * \brief
 */

package dk.dbc.opensearch.components.harvest ;

import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.OracleDBConnection;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import java.util.logging.Level;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.log4j.Logger;

import org.apache.commons.configuration.ConfigurationException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * The ES-base implementation of the Harvester-backend. The ESHarvester delivers jobs 
 * to a frontend, i.e. the DataDock, delivers data through {@link getData} and maintains the state of 
 * the jobs in the ES-base through {@link setStatus}.
 */
public class ESHarvest implements IHarvest
{
    private IDBConnection oracleInstance;
    private Connection conn;
    Logger log = Logger.getLogger( ESHarvest.class );

    /**
     *   Creates a new ES-Harvester 
     */
    public ESHarvest()
    {

    }

    /**
     *  Starts the ES-Harvester. When the ES-harvester is started, it will look into the ES-base
     *  and find jobs currently in progress {@link cleanupESBase} and set them to queued.
     *  Please notice, that as a consequence of the above, only one ES-Harvester is allowed to
     *  run on an ES-base.
     */
    public void start() throws HarvesterIOException
    {
	// \todo: Why do we want to call start on a Harvester? Shouldn't it not just start when it is created? (i.e. the constructor is called)

	log.info( "Starting the ES-Harvester" );

        // create the DBconnection
        try
	    {
		oracleInstance = new OracleDBConnection();
		conn = oracleInstance.getConnection();
	    }
        catch( ConfigurationException ce )
	    {
		log.fatal( "Error while trying to connect to Oracle ES-base: " , ce );
		throw new HarvesterIOException( "Error while trying to connect to Oracle ES-base", ce );
	    }
        catch( SQLException sqle )
	    {
		log.fatal( "Error while trying to connect to Oracle ES-base: " , sqle );
		throw new HarvesterIOException( "Error while trying to connect to Oracle ES-base", sqle );
	    }
        catch( ClassNotFoundException cnfe )
	    {
		log.fatal( "Error while trying to connect to Oracle ES-base: " , cnfe );
		throw new HarvesterIOException( "Error while trying to connect to Oracle ES-base", cnfe );
	    }

        log.debug( "ESHarvest started" );

        // Cleaning the ES-base, i.e. setting all "inProcess" to "queued":
        cleanupESBase();

    }


    /**
     *  Shuts down the ES-base connection.
     */
    public void shutdown() throws HarvesterIOException
    {
        /**
           \Note: It could be discussed wheter the cleanup-method mentioned in the
           * the above start()-method also should be called from here.
           * The argument for doing this, is that in case the DataDock gracefully crashes,
           * then the ES-base could be updated, and if data-deliverers looks at the ES-base
           * then they will see their posts as (rightfully) queued and not (wrongfully)
           * inProgress. Of course if the DataDock does not die gracefully, then the
           * shutdown-method may not be run.
           */

	log.info( "ESHarvest shutdown" );

        //close the DBconnection
        try
	    {
		conn.close();
	    }
        catch( SQLException sqle )
	    {
		log.fatal( "Error when closing the Oracle connection" , sqle );
		throw new HarvesterIOException( "Error when closing the Oracle connection", sqle );
	    }
    }


    /**
     *  Retrieve a list of jobs from the ESHarvester.
     */
    public ArrayList<IJob> getJobs( int maxAmount ) throws HarvesterIOException
    {

	log.info( String.format( "The ES-Harvester was requested for %s jobs", maxAmount ) );
        ArrayList<IJob> theJobList = new ArrayList<IJob>();
	try 
	    {
		Statement stmt = conn.createStatement();
		stmt.setMaxRows( maxAmount );
		ArrayList<Integer> takenList = new ArrayList<Integer>();
		
		// \todo: Single query to retrieve all available queued packages _and_
		//        their supplementalId3 - must be veriefied
		// get queued targetreference, lbnr and referencedata (supplementalId3):
		String queryStr = new String( "SELECT suppliedrecords.targetreference, suppliedrecords.lbnr, suppliedrecords.supplementalId3 " + 
					      "FROM taskpackagerecordstructure, suppliedrecords " + 
					      "WHERE suppliedrecords.targetreference " + 
					      "IN (SELECT targetreference FROM updatepackages  WHERE databasename = 'test' ) " +
					      "AND taskpackagerecordstructure.recordstatus = 2 " + 
					      "AND taskpackagerecordstructure.targetreference = suppliedrecords.targetreference " + 
					      "AND taskpackagerecordstructure.lbnr = suppliedrecords.lbnr " + 
					      "ORDER BY suppliedrecords.targetreference, suppliedrecords.lbnr" );
		log.debug( queryStr );
		ResultSet rs = stmt.executeQuery( queryStr );
		// \todo: databasename ('test' in above) should come from config-file-thingy.
		
		
		while( rs.next() )
		    {
			
			int targetRef        = rs.getInt( 1 );    // suppliedrecords.targetreference
			int lbnr             = rs.getInt( 2 );    // suppliedrecords.lbnr
			String referenceData = rs.getString( 3 ); // suppliedrecords.supplementalId3
			
			// Update Recordstatus
			updateRecordStatus( targetRef, lbnr );
			
			ESIdentifier id = new ESIdentifier( targetRef, lbnr );
			Document doc = null;
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			
			boolean DocOK = true; // The Doc structure have no problems
			try
			    {
				DocumentBuilder builder = factory.newDocumentBuilder();
				doc = builder.parse( new InputSource( new ByteArrayInputStream( referenceData.getBytes() ) ) );
			    }
			catch( ParserConfigurationException pce )
			    {
				log.error( String.format( "Caught error while trying to instantiate documentbuilder '%s'", pce ) );
				DocOK = false;
			    }
			catch( SAXException se )
			    {
				log.error( String.format( "Could not parse data: '%s'", se ) );
				DocOK = false;
			    }
			catch( IOException ioe )
			    {
				log.error( String.format( "Could not cast the bytearrayinputstream to a inputsource: '%s'", ioe ) );
				DocOK = false;
			    }

			if ( DocOK ) {
			    Job theJob = new Job( id, doc );
			    theJobList.add( theJob );
			} else {

			    try
				{
				    setStatus( id, JobStatus.FAILURE );
				} 
			    catch ( HarvesterUnknownIdentifierException huie )
				{
				    log.error( String.format( "Error when changing JobStatus (unknown identifier) TargetRef = %s lbnr = %s : ", id.getTargetRef(), id.getLbNr(), huie.getMessage() ), huie );
				}
			    catch ( HarvesterInvalidStatusChangeException hisce )
				{
				    log.error( String.format( "Error when changing JobStatus (invalid status) TargetRef = %s lbnr = %s : ", id.getTargetRef(), id.getLbNr(), hisce.getMessage() ), hisce );
				}
			}

		    }

	    }
        catch( SQLException sqle )
	    {
		log.fatal( "A Database Error occured" );
		throw new HarvesterIOException( "A database error occured" , sqle );
	    }

        return theJobList;
    }



    /**
     *  Retrieves data from the ES-base associated with the jobId.
     */
    public byte[] getData( IIdentifier jobId ) throws HarvesterUnknownIdentifierException, HarvesterIOException
    {
        log.info( String.format( "ESHarvest.getData( identifier %s ) ", jobId ) );

        //get the data associated with the identifier from the record field
        Blob data = null;
        byte[] returnData = null;
        ESIdentifier theJobId = (ESIdentifier)jobId;

        try
	    {
		Statement stmt = conn.createStatement();
		String queryString = String.format( "SELECT record " + 
						    "FROM suppliedrecords " + 
						    "WHERE targetreference = %s " + 
						    "AND lbnr = %s" ,
						    theJobId.getTargetRef() , theJobId.getLbNr() );
		log.debug( queryString );
		ResultSet rs = stmt.executeQuery( queryString );
		if( ! rs.next() )
		    {
			// \todo : log
			throw new HarvesterUnknownIdentifierException( String.format( "the Identifier %s is unknown in the base", jobId.toString() ) );
		    }
		else
		    {
			data = rs.getBlob( "record" );
			long blobLength = data.length();
			if( blobLength > 0 )
			    {
				returnData = data.getBytes( 1l, (int)blobLength );
			    }
			else
			    {
				log.error( String.format( "No data associated with id %s", theJobId.toString() ) );
			    }
		    }
	    }
        catch( SQLException sqle )
	    {
		log.fatal( "A database error occured " , sqle );
		throw new HarvesterIOException( "A database error occured", sqle );
	    }
        return returnData;
    }






    /**
     *  Changes the status of a post in the ES-base. 
     *  If the status is {@link JobStatus.RETRY}, then the status of post will be changed from inProgress to queued.
     *  If the status is allready set to either Success or Failure, then an excpetion is thrown, since it
     *  is not allowed to change status on an allready finished post.
     */
    public void setStatus( IIdentifier jobId, JobStatus status ) throws HarvesterUnknownIdentifierException, HarvesterInvalidStatusChangeException, HarvesterIOException
    {
        log.info( String.format( "ESHarvester was requested to set status %s on data identified by the identifier %s", status, jobId ) );

        // check if the status associated with the identifier has previously been set to success or failure.
        // if not set it to what the parameter says
	// Otherwise it is an error - you cannot update a previuosly set status if it is success or failure.
        ESIdentifier theJobId;
        theJobId = (ESIdentifier)jobId;
        try
	    {

		Statement stmt = conn.createStatement();
		// Lock row for update
		String fetchStatusString = String.format( "SELECT recordstatus " + 
							  "FROM taskpackagerecordstructure " + 
							  "WHERE targetreference = %s " + 
							  "AND lbnr = %s " + 
							  "FOR UPDATE OF recordstatus", 
							  theJobId.getTargetRef() , theJobId.getLbNr() );
		ResultSet rs = stmt.executeQuery( fetchStatusString );
		int counter = 0;
		if ( !rs.next() ) {
		    // No more rows. If this is the first time rs.next() is called, then no rows where found
		    // in the above statement, and we should throw an exception.
		    if (counter == 0) {
			stmt.close();
			String errorMsg = String.format( "recordstatus requested for unknown identifier: %s ", jobId.toString() );
			log.error( errorMsg );
			throw new HarvesterUnknownIdentifierException( errorMsg );
		    } 
		} else {
		    ++counter;
		
		    //check if the status is set already, i.e. the post is _not_ inProgress:
		    int recordStatus = rs.getInt( "recordstatus" );
		    if( recordStatus != 3 )
			{
			    String errorMsg = String.format( "the status is already set to %s for identifier: %s", recordStatus, jobId.toString() );
			    log.error( errorMsg );
			    stmt.close();
			    throw new HarvesterInvalidStatusChangeException( errorMsg );
			}
		
		    // Set the status:
		    int new_recordStatus = 0;
		    switch( status )
			{
			case SUCCESS:
			    new_recordStatus = 1;
			    break;
			case RETRY:
			    new_recordStatus = 2;
			    break;
			case FAILURE:
			    new_recordStatus = 4;
			    break;
			default:
			    stmt.close();
			    throw new HarvesterInvalidStatusChangeException( "Unknown status" );
			}
		    String updateString = String.format( "UPDATE taskpackagerecordstructure " + 
							 "SET recordstatus = %s " + 
							 "WHERE targetreference = %s " + 
							 "AND lbnr = %s ", new_recordStatus, 
							 theJobId.getTargetRef(), theJobId.getLbNr()  );

		    log.debug( String.format( "Updating with: %s", updateString ) );

		    int updateResult = stmt.executeUpdate( updateString );
		    if( updateResult != 1 )
			{
			    log.warn( String.format( "unknown status update attempt on identifier targetref: %s lbnr :%s ", theJobId.getTargetRef(), theJobId.getLbNr() ) );
			}

		    setTaskPackageStatus( theJobId.getTargetRef() );
		
		}
	    } catch( SQLException sqle ) {
	    log.fatal( "A database error occured", sqle );
	    throw new HarvesterIOException( "A database error occured", sqle );
        }


    }



    /**
     * Updates the field taskpackagerecordstructure.recordstatus in ES to be 
     * inProgress (value: 3) for the taskpackagerecordstructure with targetRef and lbnr.
     */
    private void updateRecordStatus( int targetRef, int lbnr ) throws SQLException
    {

	log.debug( String.format("Updating recordstatus for targetRef=%s and lbnr%s", targetRef, lbnr) );

	// Locking the row for update:
	Statement stmt = conn.createStatement();
	int res1 = stmt.executeUpdate("SELECT recordstatus " + 
				      "FROM taskpackagerecordstructure " + 
				      "WHERE targetreference = " + targetRef + 
				      " AND lbnr = " + lbnr + 
				      " AND recordstatus = 2 " + 
				      "FOR UPDATE OF recordstatus");

	// Testing all went well:
	if (res1 != 1) {
	    // Something went wrong - we did not lock a single row for update
	    log.error( "Error: Result from select for update was " + res1 + ". Not 1." );
	    return;
	}

	// Updating recordstatus in row:
	int res2 = stmt.executeUpdate("UPDATE taskpackagerecordstructure " + 
				      "SET recordstatus = 3 " + 
				      "WHERE targetreference = " + targetRef + 
				      " AND lbnr = " + lbnr + 
				      " AND recordstatus = 2");

	if (res2 != 1) {
	    // Something went wrong - we did not update a single row
	    log.error( "Error: Result from update was " + res2 + ". Not 1" );
	    // \todo: Throw an exception or just go to next row in rs?
	    return;
	}

	// Committing the update:
	// \todo: Is this inefficient?
	stmt.close();
	conn.commit();
    }

    /**
     *
     * Finds all posts in ES-base with recordstatus 3, and changes them to recordstatus 2.
     *
     */
    private void cleanupESBase( ) throws HarvesterIOException
    {
	// \todo: Make sure only posts belonging to the correct databasename is changed

        log.info( "Cleaning up ES-base" );

        try
	    {
		Statement stmt = conn.createStatement();
		// Locking the rows:
		int res1 = stmt.executeUpdate( "SELECT recordstatus " + 
					       "FROM taskpackagerecordstructure " + 
					       "WHERE recordstatus = 3 " + 
					       "FOR UPDATE OF recordstatus" );
		log.debug("Select for update: " + res1);
		if (res1 > 0) {
		    int res2 = stmt.executeUpdate( "UPDATE taskpackagerecordstructure " + 
						   "SET recordstatus = 2 " + 
						   "WHERE recordstatus = 3" );
		    log.debug("Update: " + res2);
		    stmt.close();
		    conn.commit();
		} else {
		    // no rows for update - just close down the statement:
		    stmt.close();
		}
	    }
        catch( SQLException sqle )
	    {
		String errorMsg = new String( "An SQL error occured while cleaning up the ES-base" );
		log.fatal( errorMsg, sqle );
		throw new HarvesterIOException( errorMsg, sqle );
	    }
    }

    /**
     *  Changes the status on a taskpackage if all assoicated posts are finished.
     */
    private void setTaskPackageStatus( int targetref ) throws HarvesterInvalidStatusChangeException, SQLException
    {

	log.debug( String.format( "setTaskPackageStatus with targetRef %s", targetref ) );

	Statement stmt = conn.createStatement();

	// Check if status on TP needs to be updated.
	// This happens if the post was the last in the TP to get a status of Success or Failure.
	String noofrecsQuery = String.format( "SELECT noofrecs, noofrecs_treated " + 
					      "FROM taskspecificupdate " + 
					      "WHERE targetreference = %s", 
					      targetref );

	log.debug( noofrecsQuery );
	ResultSet rs1 = stmt.executeQuery( noofrecsQuery );
	while ( rs1.next() ) {
	    int noofrecs = rs1.getInt( 1 );
	    int noofrecs_treated = rs1.getInt( 2 );
	    log.debug( String.format( "NoOfRecords: %s   NoOfRecordsTreated: %s",
				      noofrecs, noofrecs_treated ) );
	    if ( noofrecs < noofrecs_treated ) {

		// This is an error. There were more treated records than actual records. 
		// This _must_ never happen.
		throw new HarvesterInvalidStatusChangeException( String.format( "Error: There were more treated records than actual records in taskpackage %s. This should never ever happen.", targetref ) );
	    } else if ( noofrecs == noofrecs_treated ) {

		// find the number of success and failures on the taskpackage:
		String failure_success_query = String.format( "SELECT scount, fcount  " + 
							      "FROM " +
							      "(SELECT count( recordstatus ) scount " + 
							      "FROM taskpackagerecordstructure " + 
							      "WHERE targetreference = %s " + 
							      "AND recordstatus = 1) a , " + 
							      "(SELECT count( recordstatus ) fcount " +
							      "FROM taskpackagerecordstructure " + 
							      "WHERE targetreference = %s " + 
							      "AND recordstatus = 4) b",
							      targetref,
							      targetref );
		log.debug( failure_success_query );
		ResultSet failure_success_rs = stmt.executeQuery( failure_success_query );
		int counter2 = 0;
		int success_count = 0;
		int failure_count = 0;
		while ( failure_success_rs.next() ) {
		    success_count = failure_success_rs.getInt( 1 );
		    failure_count = failure_success_rs.getInt( 2 );
			    
		    ++counter2;
		}
		if (counter2 != 1 ) {
		    // \todo: 
		    // either zero or more than one row retrieved - this should not happen.
		    // Throw an exception?
		}

		// \todo: Should we test for to many updates?
		int update_status = 0;
		// update the TaskSpecificUpdate:
		if ( success_count == noofrecs ) {
		    // All was posts was succesfully handled:
		    update_status = 1;
		} else if ( failure_count == noofrecs ) {
		    // All was posts was handled with failure:
		    update_status = 3;
		} else {
		    // Posts were mixed with both success and failure:
		    update_status = 2;
		}
			
		String update_taskpackage_status_query = String.format( "SELECT updatestatus " + 
									"FROM taskspecificupdate " + 
									"WHERE targetreference = %s " + 
									"FOR UPDATE OF updatestatus", 
									targetref );
		log.debug( update_taskpackage_status_query );
		ResultSet update_taskpackage_status_rs = stmt.executeQuery( update_taskpackage_status_query );
		while ( update_taskpackage_status_rs.next() ) {
		    int current_update_status = update_taskpackage_status_rs.getInt( 1 );
		    if ( current_update_status != 0 ) {
			// This should never happen.
		        String errorMsg = String.format( "The status for the taskpackage with targetRef %s was allready set to %s", targetref, current_update_status );
			log.error( errorMsg );
			stmt.close();
			throw new HarvesterInvalidStatusChangeException( errorMsg );
		    } 

		    String update_taskpackage_status = String.format( "UPDATE taskspecificupdate " + 
								      "SET updatestatus = %s " + 
								      "WHERE targetreference = %s",
								      update_status,
								      targetref );
		    log.debug( update_taskpackage_status );
		    int res = stmt.executeUpdate( update_taskpackage_status );
		    log.debug( String.format( "%s rows updated" , res ) );

		    conn.commit();
		}

	    }
	}
	stmt.close();

    }

}
