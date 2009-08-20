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

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.ConfigurationException;
/**
 *
 */
public class ESHarvest implements IHarvest
{
    private IDBConnection oracleInstance;
    private Connection conn;


    /**
     *
     */
    public ESHarvest()
    {

    }
    public void start()
    {
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


    }

    public void shutdown()
    {
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

    public IJobList getJobs( int maxAmount )
    {

        System.out.println( String.format( "The dummy harvester was requested for %s jobs", maxAmount ) );
        IJobList theJobList = new JobList();
        ResultSet rs = null;
        try{
            Statement stmt = conn.createStatement();
            int taken = 0;
            ArrayList takenList = new ArrayList();
            //get Targetreference from view UPDATEPACKAGES
            rs = stmt.executeQuery( "SELECT TARGETREFERENCE FROM UPDATEPACKAGES WHERE DATABASENAME = 'test'" );

            //for each Targetref get lbnr where recordstatus = 2
            //in table taskpackagerecordstructure
            if( rs == null )
            {
                return theJobList;
            }
            else
            {
                while( rs.next() )
                {
                    int targetRef = rs.getInt( "TARGETREFERENCE" );
                    ResultSet rs2 = null;
                    String getlbnrQueryString = String.format( "SELECT SUPPLIEDRECORDS.LBNR, SUPPLEMENTALID3 FROM TASKPACKAGERECORDSTRUCTURE, SUPPLIEDRECORDS WHERE SUPPLIEDRECORDS.TARGETREFERENCE = %s AND TASKPACKAGERECORDSTRUCTURE.LBNR = SUPPLIEDRECORDS.LBNR AND TASKPACKAGERECORDSTRUCTURE.TARGETREFERENCE = %s AND RECORDSTATUS = 2" , targetRef, targetRef );
                    System.out.println( getlbnrQueryString );
                    rs2 = stmt.executeQuery( getlbnrQueryString );
                    if( rs2 == null )
                    {
                        throw new IllegalStateException( String.format( "no lbnr for the targetreference: %s", targetRef ) );
                    }
                    else
                    {
                        while( rs2.next() && taken < maxAmount )
                        {
                            int lbnr = rs2.getInt( "LBNR" );
                            Identifier id = new Identifier();
                            id.init( targetRef, lbnr );
                            String referenceData = rs2.getString( "SUPPLEMENTALID3" );
                            Job theJob = new Job();
                            theJob.init( id, referenceData.getBytes() );
                            theJobList.add( theJob );
                            taken++;
                            takenList.add( lbnr );
                        }

                        //set recordstatus to 3 for the taken targetref and lbnr
                        Iterator takenIter = takenList.iterator();
                        String updateString = "UPDATE TASKPACKAGERECORDSTRUCTURE SET RECORDSTATUS = 3 WHERE TARGETREFERENCE = " + targetRef + " AND LBNR = " + takenIter.next();
                        while( takenIter.hasNext())
                        {
                            updateString = updateString + " OR LBNR = " + takenIter.next();
                        }
                        System.out.println( updateString );
                        stmt.executeUpdate( updateString );
                        takenList.clear();

                    }

                }

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
        System.out.println( String.format( "identifier %s called on the Dummy harvester", jobId ) );
        //get the data associated with the identifier from the record field
        return null;
    }

    public void setStatus( IIdentifier jobId, JobStatus status ) throws UnknownIdentifierException, InvalidStatusChangeException
    {
        System.out.println( String.format( "Dummy harvester was requested to set status %s on data identified by the identifier %s", status, jobId ) );
        //check if the status associated with the identifier has previously been set
        //if not set it to what the parameter says
        //if success -> xxxx: invalid
        //if failure -> success: ok
        //if failure -> failure: invalid
    }

}