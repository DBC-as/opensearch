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

package dk.dbc.opensearch.common.db;


import dk.dbc.opensearch.common.types.SimplePair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief The Processqueue class handles all communication to the processqueue table
 */
public class Processqueue implements IProcessqueue
{
    Logger log = Logger.getLogger( Processqueue.class );
    IDBConnection dbConnection = null;

    /**
     * Constructor
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Processqueue( IDBConnection dbConnection )throws ConfigurationException, ClassNotFoundException
    {
        log.debug( "Processqueue Constructor" );        
        this.dbConnection = dbConnection;
    }


    /**
     * Pushes a fedorahandle to the processqueue
     *
     * @param fedorahandle a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException
    {
        log.debug( String.format( "Processqueue.push( '%s' ) called", fedorahandle ) );
        Connection con = dbConnection.getConnection();
        log.debug( String.format( "push fedorahandle=%s to queue", fedorahandle ) );
        Statement stmt = null;
        stmt = con.createStatement();
        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','N' )", fedorahandle ) );
        log.debug( String.format( "query database with %s ", sql_query ) );

        try
        {
            stmt.executeUpdate( sql_query );
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }
        log.info( String.format( "fedorahandle=%s pushed to queue", fedorahandle ) );
    }


    /**
     * Pops all fedorahandles from the processqueue
     *
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public List<SimplePair<String, Integer>> popAll() throws SQLException
    {
        log.debug( "Entering Processqueue.popAll()" );

        Connection con = dbConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        List<SimplePair<String, Integer>> jobs = new ArrayList<SimplePair<String, Integer>>();

        try
        {
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt.executeQuery("SELECT * from get_all_posts()");

            while( rs.next() )
            {
                String fedoraHandle = rs.getString( "fedorahandle" );
                int queueID = rs.getInt( "queueID" );

                log.debug( String.format( "Found a new element fh='%s', queueID='%s'", fedoraHandle, queueID ) );

                SimplePair<String, Integer> pair = new SimplePair<String, Integer>( fedoraHandle, queueID );
                jobs.add( pair );
            }

            rs.close();
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }

        return jobs;
    }


    /**
     * Pops up to maxSize fedorahandles from the processqueue
     *
     * @param maxSize the maximum size of the returned resultset, ie. the maximum number of handles to pop
     *
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public List<SimplePair<String, Integer>> pop( int maxSize ) throws SQLException
    {
        log.debug( "Entering Processqueue.pop()" );

        Connection con = dbConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        List<SimplePair<String, Integer>> jobs = new ArrayList<SimplePair<String, Integer>>();

        try
        {
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt.executeQuery( String.format( "SELECT * from get_posts( %s )", maxSize ) );

            while( rs.next() )
            {
                String fedoraHandle = rs.getString( "fedorahandle" );
                int queueID = rs.getInt( "queueID" );

                log.debug( String.format( "Found a new element fh='%s', queueID='%s'", fedoraHandle, queueID ) );

                SimplePair<String, Integer> pair = new SimplePair<String, Integer>( fedoraHandle, queueID );
                jobs.add( pair );
            }

            rs.close();
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }
        return jobs;
    }


    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     *
     * @param queueID identifies the element to commit.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    @Override
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException
    {
        log.debug( String.format( "Processqueue.commit( queueID='%s' ) called", queueID ) );
        Connection con = dbConnection.getConnection();

        Statement stmt = null;
        int rowsRemoved = 0;

        String sql_query = String.format( "DELETE FROM processqueue WHERE queueID = %s", queueID );
        log.debug( String.format( "query database with %s ", sql_query ) );

        // remove element from queue ie. delete row from processqueue table
        try
        {
            stmt = con.createStatement();
            rowsRemoved = stmt.executeUpdate( sql_query );

            if( rowsRemoved == 0 )
            {
                throw new NoSuchElementException( "The queueID does not match a popped element." );
            }

            con.commit();
        }
        finally
        {// Close database connection
            stmt.close();
            con.close();
        }
        log.info( String.format( "Element with queueID=%s is commited",queueID ) );
    }


    /**
     * rolls back the pop. This restores the element in the queue and
     * the element is in concideration the next time a pop i done.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    @Override
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException
    {
        log.debug( String.format( "Processqueue.rollback( queueID='%s' ) called", queueID ) );
        Connection con = dbConnection.getConnection();

        Statement stmt = null;
        int rowsRemoved = 0;
        String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueID = %s", queueID );
        log.debug( String.format( "query database with %s ", sql_query ) );

        // restore element in queue ie. update queueID in row from processqueue table
        try
        {
            stmt = con.createStatement();
            rowsRemoved = stmt.executeUpdate( sql_query );

            if( rowsRemoved == 0 )
            {
                throw new NoSuchElementException( "The queueID does not match a popped element." );
            }
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }
        log.info( String.format( "Element with queueID=%s is rolled back",queueID ) );
    }


    /**
     * deactivate elements on the processqueue.  It finds all elements
     * on the processqueue which are marked as active and mark them as
     * inactive, ie. they are ready for indexing.
     *
     * @return turn the number of elements which are reactivated
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public int deActivate() throws ClassNotFoundException, SQLException
    {
        log.debug( "Entering Processqueue.deActivate()" );
        Connection con = dbConnection.getConnection();

        String sql_query = "UPDATE processqueue SET processing = 'N' WHERE processing = 'Y'";

        Statement stmt = con.createStatement();
        int rowsUpdated = 0;
        try
        {
            rowsUpdated = stmt.executeUpdate( sql_query );
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }
        log.info(String.format("Updated %S processes from active to not active" , rowsUpdated ));

        return rowsUpdated;
    }


    /**
     * The notDocked method is used to store paths to the files, that we couldnt not store in the repository
     *
     * @param path The path of the problematic file
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public void notDocked( String path ) throws ClassNotFoundException, SQLException
    {
        log.debug( String.format( "Entering notDocked( path = '%s')",path ) );
        Connection con = dbConnection.getConnection();

        //log.debug( String.format( "push fedorahandle=%s to queue", fedorahandle ) );
        Statement stmt = con.createStatement();

        String sql_query = (  String.format( "INSERT INTO notdocked( path ) "+
                                             "VALUES( %s )", path ) );

        try
        {
            stmt.executeUpdate( sql_query );
            con.commit();
        }
        finally
        {
            // Close database connection
            stmt.close();
            con.close();
        }
    }


    /**
     * The notIndexed method is used to store queueIDs for indexjobs, that we couldnt not be indexed/
     *
     * @param queueID The queueID of the problematic job
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    @Override
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException
    {
        log.debug( String.format( "Entering notIndexed( queueID = '%s')",queueID ) );
        Connection con = dbConnection.getConnection();

        String sql_query = String.format( "SELECT * FROM processqueue WHERE queueID = %s", queueID );

        ResultSet rs = null;
        Statement stmt_get = con.createStatement();
        Statement stmt_insert = con.createStatement();
        int rowsSelected = 0;

        try{
            stmt_get = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt_get.executeQuery( sql_query );
            try{
                while( rs.next() ){
                    String fedoraHandle = rs.getString( "fedorahandle" );
                    int qid = rs.getInt( "queueID" );
                    sql_query = String.format( "INSERT INTO notindexed( queueid, fedorahandle ) VALUES( %s, '%s' )", qid, fedoraHandle );
                    stmt_insert.executeUpdate( sql_query );
                    commit( qid );
                }
                con.commit();
            }
            finally
            {
                rs.close();
            }
        }
        finally
        {// Close database connection
            stmt_insert.close();
            stmt_get.close();
            con.close();
        }
    }
}