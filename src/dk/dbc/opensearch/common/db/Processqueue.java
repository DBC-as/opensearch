/**
 * \file Processqueue.java
 * \brief The Processqueue class
 * \package tools
 */
package dk.dbc.opensearch.common.db;


import dk.dbc.opensearch.common.db.DBConnection;
import dk.dbc.opensearch.common.types.Pair;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.NoSuchElementException;
import java.util.Vector;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief The Processqueue class handles all communication to the processqueue
 */
public class Processqueue
{
    Logger log = Logger.getLogger( Processqueue.class );
    DBConnection DBconnection = null;


    /**
     * Constructor
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Processqueue()throws ConfigurationException, ClassNotFoundException
    {
        log.debug( "Processqueue Constructor" );
        DBconnection = new DBConnection();
    }


    /**
     * Pushes a fedorahandle to the processqueue
     *
     * @param fedorahandle a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     * @param itemID an  itemID, identifying the dataobject, later used for indexing purposes
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException
    {
        log.debug( String.format( "Processqueue.push( '%s' ) called", fedorahandle ) );
        Connection con = DBConnection.getConnection();

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


    public Vector< Pair< String, Integer > > popAll() throws SQLException
    {
        log.debug( "Entering Processqueue.popAll()" );

        Connection con = DBConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        Vector<Pair<String, Integer>> jobs = new Vector<Pair<String, Integer>>();

        try
            {
                stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
                rs = stmt.executeQuery("SELECT * from get_all_posts()");

                while( rs.next() )
                    {
                        String fedoraHandle = rs.getString( "fedorahandle" );
                        int queueID = rs.getInt( "queueID" );

                        log.debug( String.format( "Found a new element fh='%s', queueID='%s'", fedoraHandle, queueID ) );

                        Pair<String, Integer> pair = new Pair<String, Integer>( fedoraHandle, queueID );
                        jobs.add( pair );
                    }

                con.commit();
            }
        finally
            {
                // Close database connection
                rs.close();
                stmt.close();
                con.close();
            }

        return jobs;
    }


    public Vector<Pair<String, Integer>> pop( int maxSize ) throws SQLException
    {
        log.debug( "Entering Processqueue.pop()" );

        Connection con = DBConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;

        Vector<Pair<String, Integer>> jobs = new Vector<Pair<String, Integer>>();

        try
            {
                stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
                rs = stmt.executeQuery( String.format( "SELECT * from get_posts( %s)", maxSize ) );

                while( rs.next() )
                    {
                        String fedoraHandle = rs.getString( "fedorahandle" );
                        int queueID = rs.getInt( "queueID" );

                        log.debug( String.format( "Found a new element fh='%s', queueID='%s'", fedoraHandle, queueID ) );

                        Pair<String, Integer> pair = new Pair<String, Integer>( fedoraHandle, queueID );
                        jobs.add( pair );
                    }

                con.commit();
            }
        finally
            {
                // Close database connection
                rs.close();
                stmt.close();
                con.close();
            }
        return jobs;
    }


    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     *
     * @param queueid identifies the element to commit.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException
    {
        log.debug( String.format( "Processqueue.commit( queueID='%s' ) called", queueID ) );
        Connection con = DBConnection.getConnection();

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
            {
                // Close database connection
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
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException
    {
        log.debug( String.format( "Processqueue.rollback( queueID='%s' ) called", queueID ) );
        Connection con = DBConnection.getConnection();

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
    public int deActivate() throws ClassNotFoundException, SQLException
    {
        log.debug( "Entering Processqueue.deActivate()" );
        Connection con = DBConnection.getConnection();

        String sql_query = "UPDATE processqueue SET processing = 'N' WHERE processing = 'Y'";

        Statement stmt = con.createStatement();
        int rowsUpdated = 0;
        try
            {
                rowsUpdated = stmt.executeUpdate( sql_query );
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
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException
    {
        log.debug( String.format( "Entering notIndexed( queueID = '%s')",queueID ) );
        Connection con = DBConnection.getConnection();

        String sql_query = String.format( "SELECT * FROM processqueue WHERE queueID = %s", queueID );

        ResultSet rs = null;
        Statement stmt_get = con.createStatement();
        int rowsSelected = 0;

        try{

            stmt_get = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt_get.executeQuery( sql_query );
            while( rs.next() ){
                String fedoraHandle = rs.getString( "fedorahandle" );
                int qid = rs.getInt( "queueID" );
                Statement stmt_insert = con.createStatement();
                sql_query = String.format( "INSERT INTO notindexed( queueid, fedorahandle ) VALUES( %s, '%s' )", qid, fedoraHandle );
                stmt_insert.executeUpdate( sql_query );

                commit( qid );
                stmt_insert.close();
            }
        }
        finally
            {
                // Close database connection
                con.commit();
                stmt_get.close();
                con.close();
            }

    }

}