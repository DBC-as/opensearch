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

package dk.dbc.opensearch.components.harvest ;

import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.OracleDBConnection;

import java.util.ArrayList;
import java.util.Iterator;

import java.sql.Blob;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import org.apache.commons.configuration.ConfigurationException;
/**
 *
 */
public class ESHarvest implements IHarvest
{
    private IDBConnection oracleInstance;
    private Connection conn;
    Logger log = Logger.getLogger( ESHarvest.class );

    /**
     *
     */
    public ESHarvest()
    {

    }

    /**
     *    Why do we want to call start on a Harvester?
     *    Shouldn't it not just start when it is created? (i.e. the constructor is called)
     */
    public void start()
    {
        /** \todo: When the ES-harvester starts, it must check whether ther are
            any posts in the ES-base with the status "inProgress". If there are,
            it is safe to assume that the posts can be given to the DataDock again
            without causing any error or faults. This must be done by setting the
            posts to "queued".
        */

        //create the DBconnection
        try
        {
            oracleInstance = new OracleDBConnection();
            conn = oracleInstance.getConnection();
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
        System.out.println( "ESHarvest started" );

        // Cleaning the ES-base, i.e. setting all "inProcess" to "queued":
        cleanupESBase();

    }

    /**
     *
     */
    public void shutdown()
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

        //close the DBconnection
        try
        {
            conn.close();
            System.out.println( "ESHarvest shutdown" );
        }
        catch( SQLException sqle )
        {
            sqle.printStackTrace();
        }
    }

    /**
     *
     */
    public ArrayList<IJob> getJobs( int maxAmount )
    {

        System.out.println( String.format( "The dummy harvester was requested for %s jobs", maxAmount ) );
        ArrayList<IJob> theJobList = new ArrayList();
        ResultSet rs = null;
        try{
            Statement stmt = conn.createStatement();
            int taken = 0;
            ArrayList<Integer> takenList = new ArrayList();

            // \todo: Single query to retrieve all available queued packages _and_
            //        their supplementalId3 - must be veriefied
            // get queued targetreference, lbnr and referencedata (supplementalId3):
            rs = stmt.executeQuery( "SELECT suppliedrecords.targetreference, suppliedrecords.lbnr, suppliedrecords.supplementalid3 FROM  taskpackagerecordstructure, suppliedrecords WHERE suppliedrecords.targetreference IN (SELECT targetreference FROM updatepackages  WHERE databasename = 'test' )  AND taskpackagerecordstructure.recordstatus = 2 AND taskpackagerecordstructure.targetreference = suppliedrecords.targetreference AND taskpackagerecordstructure.lbnr = suppliedrecords.lbnr ORDER BY suppliedrecords.targetreference, suppliedrecords.lbnr" );
            // \todo: databasename ('test' in above) should come from config-file-thingy.


            while( rs.next() && taken < maxAmount )
            {
                int targetRef        = rs.getInt( 1 );    // suppliedrecords.targetreference
                int lbnr             = rs.getInt( 2 );    // suppliedrecords.lbnr
                String referenceData = rs.getString( 3 ); // suppliedrecords.supplementalId3

                // Locking the row for update:
                Statement stmt2 = conn.createStatement();
                int res1 = stmt2.executeUpdate("SELECT recordstatus FROM taskpackagerecordstructure WHERE targetreference = " + targetRef + " AND lbnr = " + lbnr + " AND recordstatus = 2 FOR UPDATE OF recordstatus");

                // Testing all went well:
                if (res1 != 1) {
                    // Something went wrong - we did not lock a single row for update
                    System.out.println( "Error: Result from select for update was " + res1 + ". Not 1" );
                    // \todo: Throw an exception or just go to next row in rs?
                    continue;
                }

                // Updating recordstatus in row:
                int res2 = stmt2.executeUpdate("UPDATE taskpackagerecordstructure SET recordstatus = 3 WHERE targetreference = " + targetRef + " AND lbnr = " + lbnr + " AND recordstatus = 2");

                if (res2 != 1) {
                    // Something went wrong - we did not update a single row
                    System.out.println( "Error: Result from update was " + res2 + ". Not 1" );
                    // \todo: Throw an exception or just go to next row in rs?
                    continue;
                }

                // Committing the update:
                // \todo: Is this inefficient?
                stmt2.close();
                conn.commit();

                Identifier id = new Identifier( targetRef, lbnr );
                Job theJob = new Job( id, referenceData.getBytes() );
                theJobList.add( theJob );
                taken++;

            }

        }
        catch( SQLException sqle )
        {
            sqle.printStackTrace();
        }

        return theJobList;
    }



    public byte[] getData( IIdentifier jobId ) throws UnknownIdentifierException
    {
        System.out.println( String.format( "ESHarvest.getData( identifier %s ) ", jobId ) );
        //get the data associated with the identifier from the record field
        Blob data = null;
        ResultSet rs = null;
        byte[] returnData = null;
        Identifier theJobId = (Identifier)jobId;
        try
        {
            Statement stmt = conn.createStatement();
            String queryString = String.format( "SELECT record FROM suppliedrecords WHERE targetreference = %s AND lbnr = %s" ,theJobId.getTargetRef() , theJobId.getLbNr() );
            System.out.println( queryString );
            rs = stmt.executeQuery( queryString );
            if( rs == null || ! rs.next() )
            {
                throw new UnknownIdentifierException( String.format( "the Identifier %s is unknown in the base", jobId.toString() ) );
            }
            else
            {
                data= rs.getBlob( "RECORD" );
                long blobLength = data.length();
                if( blobLength > 0 )
                {
                    returnData = data.getBytes( 1l, (int)blobLength );
                }
                else
                {
                    System.out.println( String.format( "No data associated with id %s", theJobId.toString() ) );
                }
            }
        }
        catch( SQLException sqle )
        {
            sqle.printStackTrace();
        }
        return returnData;
    }

    /**
     * The setstatus only accepts failure and success right now. retry will come later
     */
    public void setStatus( IIdentifier jobId, JobStatus status ) throws UnknownIdentifierException, InvalidStatusChangeException
    {
        System.out.println( String.format( "Dummy harvester was requested to set status %s on data identified by the identifier %s", status, jobId ) );
        //check if the status associated with the identifier has previously been set
        //if not set it to what the parameter says
        //if success (1) -> xxxx: invalid
        //if failure (4) -> success: ok
        //if failure (4) -> failure: invalid
        String updateString;
        Identifier theJobId;
        theJobId = (Identifier)jobId;
        try
        {
            Statement stmt = conn.createStatement();
            String fetchStatusString = String.format( "SELECT recordstatus FROM taskpackagerecordstructure WHERE targetreference = %s AND lbnr = %s FOR UPDATE OF recordstatus", theJobId.getTargetRef() , theJobId.getLbNr() );
            ResultSet rs = stmt.executeQuery( fetchStatusString );
            if( rs == null )
            {
                throw new UnknownIdentifierException( String.format( "recordstatus requested for unknown identifier: %s ", jobId.toString() ) );
            }
            else
            {
                if( rs.next() )
                {
                    int recordStatus = rs.getInt( "recordstatus");
                    //check if its set already
                    if( recordStatus == 1 || ( recordStatus == 4 && status == JobStatus.FAILURE ) )
                    {

                        throw new InvalidStatusChangeException( String.format( "the status is already set to success for identifier: %s", jobId.toString() ) );
                    }
                    else
                    {
                        switch( status )
                        {
                        case SUCCESS:
                            updateString = String.format( "UPDATE taskpackagerecordstructure SET recordstatus = 1 WHERE targetreference = %s AND lbnr = %s ", theJobId.getTargetRef(), theJobId.getLbNr()  );
                            break;
                        case FAILURE:
                            updateString = String.format( "UPDATE taskpackagerecordstructure SET recordstatus = 4 WHERE targetreference = %s AND lbnr = %s ", theJobId.getTargetRef(), theJobId.getLbNr()  );
                            break;
                        case RETRY:
                            throw new InvalidStatusChangeException( "RETRY not implemented yet" );
                        default:
                            throw new InvalidStatusChangeException( "Unknown status" );
                        }
                        System.out.println( String.format( "Updating with: %s", updateString ) );
                        int updateResult = stmt.executeUpdate( updateString );
                        if( updateResult != 1 )
                        {
                            log.warn( String.format( "unknown status update atempt on identifier targetref: %s lbnr :%s ", theJobId.getTargetRef(), theJobId.getLbNr() ) );
                        }
                    }
                }
                else
                {
                    throw new InvalidStatusChangeException( String.format( "recordstatus requested for unknown identifier: %s", jobId.toString() ) );
                }

            }
        }catch( SQLException sqle ){
            sqle.printStackTrace();
        }


    }


    private void cleanupESBase( )
    {

        System.out.println("Cleaning up ES-base");

        try
        {
            Statement stmt = conn.createStatement();
            // Locking the rows:
            int res1 = stmt.executeUpdate( "SELECT recordstatus FROM taskpackagerecordstructure WHERE recordstatus = 3 FOR UPDATE OF recordstatus" );
            System.out.println("Select for update: " + res1);
            if (res1 > 0) {
                int res2 = stmt.executeUpdate( "UPDATE taskpackagerecordstructure SET recordstatus = 2 WHERE recordstatus = 3" );
                System.out.println("Update: " + res2);
                stmt.close();
                conn.commit();
            } else {
                // no rows for update - just close down the statement:
                stmt.close();
            }
        }
        catch( SQLException sqle )
        {
            sqle.printStackTrace();
        }
    }

}