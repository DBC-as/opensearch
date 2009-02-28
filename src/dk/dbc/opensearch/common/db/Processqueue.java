/**
 * \file Processqueue.java
 * \brief The Processqueue class
 * \package tools
 */
package dk.dbc.opensearch.common.db;

import org.apache.log4j.Logger;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;

import java.util.Vector;

import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.db.DBConnection;
/**
 * \ingroup tools
 * \brief The Processqueue class handles all communication to the processqueue
 */
public class Processqueue {

    

    Logger log = Logger.getLogger("Processqueue");
    DBConnection DBconnection = null;
    /**
     * Constructor
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Processqueue()throws ConfigurationException, ClassNotFoundException {
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
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException {

        String itemID = "noVal";

        log.debug( String.format( "Processqueue.push( '%s', '%s' ) called", fedorahandle, itemID ) );
        Connection con = DBConnection.getConnection();

        log.debug( String.format( "push fedorahandle=%s, itemID=%s to queue", fedorahandle, itemID ) );
        Statement stmt = null;
        stmt = con.createStatement();
        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, itemID, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','%s','N' )", fedorahandle, itemID ) );
        log.debug( String.format( "query database with %s ", sql_query ) );

        try{
            stmt.executeUpdate( sql_query );
            con.commit();
        }
        finally{
            // Close database connection
            stmt.close();
            con.close();
        }
        log.info( String.format( "fedorahandle=%s, itemID=%s pushed to queue", fedorahandle, itemID ) );
    }

    /**
     * pops the top-most element from the Processqueue, returning the fedorahandle as a String
     *
     * @returns A triple cointaning the fedorahandle (a String
     * containing the unique handle), the itemID (a String identifing
     * the databoject, used for indexing),and queueid a number
     * identifying the fedorahandle in the queue. Used later for
     * commit or rollback.  for the resource in the object repository
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    public Triple<String, Integer, String>  pop() throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Entering Processqueue.pop()" );
        
        Connection con = DBConnection.getConnection();
        Triple returntriple = null;
        Statement stmt = null;
        ResultSet rs = null;

        try{

            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt.executeQuery("SELECT * from processqueue_pop_post()");
            boolean success = rs.next();
            if(! success || rs.getString( "fedorahandle" ) == null ){
                throw new NoSuchElementException("No elements on processqueue");
            }

            String fedorahandle = rs.getString( "fedorahandle" );
            String itemID = rs.getString( "itemID" );
            int queueID = rs.getInt( "queueID" );
            returntriple = Tuple.from( fedorahandle, queueID, itemID );
            log.debug( String.format( "Retrieved fedorahandle='%s', queueID='%s', itemID='%s'", fedorahandle, queueID, itemID ) );
            con.commit();            
        }
        finally{
            // Close database connection
            rs.close();
            stmt.close();
            con.close();
        }
        return returntriple;
    }

    public Vector<Pair<String, Integer>> popAll() throws SQLException{
        log.debug( "Entering Processqueue.popAll()" );
        
        Connection con = DBConnection.getConnection();
        Statement stmt = null;
        ResultSet rs = null;
        
        Vector<Pair<String, Integer>> jobs = new Vector<Pair<String, Integer>>();

        try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY );
            rs = stmt.executeQuery("SELECT * from get_all_posts()");
            
            while( rs.next() ){
                String fedoraHandle = rs.getString( "fedorahandle" );
                String itemID = rs.getString( "itemID" );
                int queueID = rs.getInt( "queueID" );

                log.debug( String.format( "Found a new element fh='%s', queueID='%s'", fedoraHandle, queueID ) );
                
                Pair<String, Integer> pair = new Pair( fedoraHandle, queueID );
                jobs.add( pair );
            }
            con.commit();
        }
        finally{
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
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( String.format( "Processqueue.commit( queueID='%s' ) called", queueID ) );
        Connection con = DBConnection.getConnection();

        Statement stmt = null;
        int rowsRemoved = 0;

        String sql_query = String.format( "DELETE FROM processqueue WHERE queueID = %s", queueID );
        log.debug( String.format( "query database with %s ", sql_query ) );

        // remove element from queue ie. delete row from processqueue table
        try{
            stmt = con.createStatement();
            rowsRemoved = stmt.executeUpdate( sql_query );

            if( rowsRemoved == 0 ) {
                throw new NoSuchElementException( "The queueID does not match a popped element." );
            }
            con.commit();
        }
        finally{
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
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException{
        log.debug( String.format( "Processqueue.rollback( queueID='%s' ) called", queueID ) );
            Connection con = DBConnection.getConnection();

            Statement stmt = null;
            int rowsRemoved = 0;
            String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueID = %s", queueID );
            log.debug( String.format( "query database with %s ", sql_query ) );

            // restore element in queue ie. update queueID in row from processqueue table
            try{
                stmt = con.createStatement();
                rowsRemoved = stmt.executeUpdate( sql_query );

                if( rowsRemoved == 0 ) {
                    throw new NoSuchElementException( "The queueID does not match a popped element." );
                }
                con.commit();
            }
            finally{
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
        public int deActivate() throws ClassNotFoundException, SQLException {
            log.debug( "Entering Processqueue.deActivate()" );
            Connection con = DBConnection.getConnection();
            
            String sql_query = "UPDATE processqueue SET processing = 'N' WHERE processing = 'Y'";
            
            Statement stmt = stmt = con.createStatement();
            int rowsUpdated = 0;
            try{
                rowsUpdated = stmt.executeUpdate( sql_query );
            }
            finally{
                // Close database connection
                stmt.close();
                con.close();
            }
            log.info(String.format("Updated %S processes from active to not active" , rowsUpdated ));
            return rowsUpdated;
    }
    
    
}