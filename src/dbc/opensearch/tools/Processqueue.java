package dbc.opensearch.tools;

import org.apache.log4j.Logger;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.NoSuchElementException;
import org.apache.commons.configuration.ConfigurationException;

/**
 * \brief The Processqueue class handles all communication to the processqueue
 */
public class Processqueue extends DBConnection {

    /**
     *  Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     *  database Connection
     */
    private Connection con;
    
    /**
     * Constructor
     */
    public Processqueue() throws ConfigurationException {
        log.debug( "Processqueue Constructor" );
    } 

    /**
     * Push an fedorahandle to the processqueue
     * @params fedorahandle: a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     */
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException {
        log.debug( "Processqueue.pop() called" );
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        Connection con;        
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        /** \todo: reset counter if queue is empty */
        
        Statement stmt = null;
        try{
            // Write fedorahandle and queueID to database
            stmt = con.createStatement();
            
            stmt.executeUpdate( "INSERT INTO processqueue(queueid, fedorahandle, processing)" +
                                " VALUES(processqueue_seq.nextval ,'"+fedorahandle+"','N')" );
            log.debug( "Written to database" );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        // Close database connection

        stmt.close();
        con.close();
    }

    /**
     * pops the top-most element from the Processqueue, returning the fedorahandle as a String
     * @returns A pair cointaning the fedorahandle (a String containing the unique handle), and queueid a number identifying the fedorahandle in the queue. Used later for commit or rollback.
     * for the resource in the object repository
     */
    public Pair<String, Integer>  pop() throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Processqueue.pop() called" );

        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        // preparing call of stored procedure
        CallableStatement cs=null;
        ResultSet rs=null;
        
        try{
            cs = con.prepareCall("{call proc_prod(?,?,?)}");
            cs.registerOutParameter(1, java.sql.Types.VARCHAR);
            cs.registerOutParameter(2, java.sql.Types.INTEGER);
            cs.registerOutParameter(3, java.sql.Types.VARCHAR);
            
            // execute procedure
            rs = cs.executeQuery();
        }
        catch ( SQLException sqe ){            
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        
        if ( cs.getString(1) == null ) { // Queue is empty
            log.info( "Processqueue is empty" );
            throw new NoSuchElementException("No elements on processqueue");
        } 

        //fetch data
        String handle = cs.getString(1);
        int popped_queueid = cs.getInt(2);
        log.info( "Handle obtained by pop: "+ handle );
        log.debug( "popped_queueid: "+popped_queueid );

        // Close database connection
        cs.close();
        con.close();

        //        return handle;
        return Tuple.from( handle, popped_queueid );
    }

    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     */
    public void commit( int queueid ) throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Dequeue.update() called" );
                
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        Statement stmt = null;
        int rowsRemoved = 0;
        
        // remove element from queue ie. delete row from processqueue table
        try{
            stmt = con.createStatement();
            rowsRemoved = stmt.executeUpdate("DELETE FROM processqueue WHERE queueid = "+queueid);
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        if( rowsRemoved == 0 ) {
            throw new NoSuchElementException( "The queueid does not match a popped element." ); 
        }
        
        // Close database connection
        stmt.close();
        con.close();
    }

    /**
     * rolls back the pop. This restores the element in the queue and
     * the element is in concideration the next time a pop i done.
     */    
    public void rollback( int queueid ) throws ClassNotFoundException, SQLException, NoSuchElementException{
        log.debug( "Processqueue.rollback() called" );

        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        Statement stmt = null;
        int rowsRemoved = 0;
        
        // restore element in queue ie. update queueid in row from processqueue table
        try{
            stmt = con.createStatement();
            stmt.executeUpdate( "UPDATE processqueue SET processing = 'N' WHERE queueid = "+queueid );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        
        if( rowsRemoved == 0 ) {
            throw new NoSuchElementException( "The queueid does not match a popped element." ); 
        }

        // Close database connection
        stmt.close();
        con.close();

    }
    
    /**
     * getActiveprocesses queries the processqueue table and find
     * elements that are marked as processing.
     * @return an integer arrey contaning queueids matching active processing threads
     */
    public int[] getActiveProcesses()throws ClassNotFoundException, SQLException, NoSuchElementException{
        log.debug( "Processqueue.getActiveProcesses() called" );
        
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        // Query database
        Statement stmt = null;
        String SQL_query = "SELECT queueid FROM processqueue WHERE processing = 'Y' ";
        log.debug( String.format( "SQL Query == %s", SQL_query ) );

        try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        
        ResultSet rs = null;
        try{
            rs = stmt.executeQuery ( SQL_query );
            log.debug( String.format( "Processqueue queried with \"%s\"", SQL_query ) );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        int [] queueIDArray = null;
        
        if( rs != null ){ // items marked as prccessing found
            
            rs.last();
            queueIDArray = new int[ rs.getRow() ];
            rs.first();
            
            int i=0;
            while( rs.next() ){
                queueIDArray[i] = rs.getInt("queueid");
            }            
        }
        return queueIDArray;
    }
}
