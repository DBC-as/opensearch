package dbc.opensearch.tools;

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

/**
 * \brief The Processqueue class handles all communication to the processqueue
 */
public class Processqueue extends DBConnection {

    Logger log = Logger.getLogger("Processqueue");

    private Connection con;
    
    /**
     * Constructor does nothing except throws exception
     * @throws ConfigurationException if ...?
     * \todo: this kinda indicates that the class methods should be static, doesn't it?
     */
    public Processqueue()throws ConfigurationException, ClassNotFoundException {
        log.debug( "Processqueue Constructor" );
    } 

    /**
     * Push a fedorahandle to the processqueue
     * @params fedorahandle: a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     * @params itemID the id of the datastream that carries the actual data to be indexed
     * @throws ClassNotFoundException if the databasedriver could not be loaded in Processqueue
     * @throws SQLException if the request for a push could not be carried out in the DB layer
     */
    public void push( String fedorahandle, String itemID ) throws ClassNotFoundException, SQLException {
        log.debug( "Processqueue.pop() called" );
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        Connection con;        
        //        try{
            con = establishConnection();
        // }
        // catch(ClassNotFoundException ce){
        //     throw new ClassNotFoundException( ce.getMessage() );
        // }
        // catch(SQLException sqe){
        //     throw new SQLException( sqe.getMessage() );
        // }

        /** \todo: reset counter if queue is empty */
        
        Statement stmt = null;
        try{
            stmt = con.createStatement();
            String sql_query = (  String.format( "INSERT INTO processqueue(queueid, fedorahandle, itemID, processing) VALUES(processqueue_seq.nextval ,'%s','%s','N')", fedorahandle, itemID ) );
            
            stmt.executeUpdate( sql_query );
            log.debug( String.format( "Written query %s to database", sql_query ) );
        // }
        // catch(SQLException sqe) {
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        }finally{

            log.debug( String.format( "Close database connection" ) );
            stmt.close();
            con.close();
        }
    }

    /**
     * pops the top-most element from the Processqueue, returning the fedorahandle as a String
     * @returns A pair cointaning the fedorahandle (a String containing the unique handle), and queueid a number identifying the fedorahandle in the queue. Used later for commit or rollback.
     * for the resource in the object repository
     * @throws ClassNotFoundException if the databasedriver could not be loaded in Processqueue
     * @throws SQLException if the request for a push could not be carried out in the DB layer
     * @throws NoSuchElementException if the queue was empty
     */
    public Triple<String, Integer, String>  pop() throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Processqueue.pop() called" );

        log.debug( "establish databaseconnection" );
        //        try{
        con = establishConnection();
        // }
        // catch(ClassNotFoundException ce){
        //     throw new ClassNotFoundException( ce.getMessage() );
        // }
        // catch(SQLException sqe){
        //     throw new SQLException( sqe.getMessage() );
        // }

        // preparing call of stored procedure
        CallableStatement cs=null;
        ResultSet rs=null;
        
        //        try{
        cs = con.prepareCall("{call proc_prod(?,?,?,?)}");
        cs.registerOutParameter(1, java.sql.Types.VARCHAR);
        cs.registerOutParameter(2, java.sql.Types.INTEGER);
        cs.registerOutParameter(3, java.sql.Types.VARCHAR);
        cs.registerOutParameter(4, java.sql.Types.VARCHAR);
        
            // execute procedure
        rs = cs.executeQuery();
        // }
        // catch ( SQLException sqe ){            
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        // }
        
        if ( cs.getString(1) == null ) { // Queue is empty
            log.info( "Processqueue is empty" );
            throw new NoSuchElementException("No elements on processqueue");
        } 

        log.debug( String.format( "fetch data from the database" ) );
        String handle = cs.getString( 1 );
        int popped_queueid = cs.getInt( 2 );
        String itemID = cs.getString( 4 );
        log.info( "Handle obtained by pop: "+ handle );
        log.debug( "popped_queueid: "+popped_queueid );
        log.debug( "itemID: " + itemID );

        log.debug( String.format( "Close database connection" ) );
        /** \todo: should this be in a try/finally? */
        cs.close();
        con.close();
        
        return Tuple.from( handle, popped_queueid, itemID );
    }

    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     * @params queueid the queueid to remove permanently from the queue
     * @throws ClassNotFoundException if the databasedriver could not be loaded in Processqueue
     * @throws SQLException if the request for a push could not be carried out in the DB layer
     * @throws NoSuchElementException if the queueid does not match a popped element
     */
    public void commit( int queueid ) throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Dequeue.update() called" );
                
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        //        try{
            con = establishConnection();
        // }
        // catch(ClassNotFoundException ce){
        //     throw new ClassNotFoundException( ce.getMessage() );
        // }
        // catch(SQLException sqe){
        //     throw new SQLException( sqe.getMessage() );
        // }

        Statement stmt = null;
        int rowsRemoved = 0;
        
        // remove element from queue ie. delete row from processqueue table
        //        try{
            stmt = con.createStatement();
            rowsRemoved = stmt.executeUpdate("DELETE FROM processqueue WHERE queueid = "+queueid);
        // }
        // catch(SQLException sqe) {
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        // }

        if( rowsRemoved == 0 ) {
            throw new NoSuchElementException( String.format( "The queueid '%s' does not match a popped element.", queueid ) ); 
        }
        
        log.debug( String.format( "Close database connection" ) );
        /** \todo: should this be in a try/finally? */
        stmt.close();
        con.close();
    }

    /**
     * rolls back the pop. This restores the element in the queue and
     * the element is in concideration the next time a pop i done.
     * @params queueid the id of the queued element to rollback
     * @throws ClassNotFoundException if the databasedriver could not be loaded in Processqueue
     * @throws SQLException if the request for a push could not be carried out in the DB layer
     * @throws NoSuchElementException if the queueid does not match a popped element
     */    
    public void rollback( int queueid ) throws ClassNotFoundException, SQLException, NoSuchElementException{
        log.debug( "Processqueue.rollback() called" );

        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        //        try{
            con = establishConnection();
        // }
        // catch(ClassNotFoundException ce){
        //     throw new ClassNotFoundException( ce.getMessage() );
        // }
        // catch(SQLException sqe){
        //     throw new SQLException( sqe.getMessage() );
        // }

        Statement stmt = null;
        int rowsRemoved = 0;
        
        log.debug( String.format( "restore element in queue ie. update queueid in row from processqueue table" ) );
        //        try{
            stmt = con.createStatement();
            stmt.executeUpdate( "UPDATE processqueue SET processing = 'N' WHERE queueid = "+queueid );
        // }
        // catch(SQLException sqe) {
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        // }
        
        if( rowsRemoved == 0 ) {
            throw new NoSuchElementException( String.format( "The queueid '%s' does not match a popped element.", queueid ) ); 
        }

        log.debug( String.format( "Close database connection" ) );
        /** \todo: should this be in a try/finally? */
        stmt.close();
        con.close();
    }
    
    /**
     * getActiveprocesses queries the processqueue table and find
     * elements that are marked as processing.
     * @throws ClassNotFoundException if the databasedriver could not be loaded in Processqueue
     * @throws SQLException if the request for a push could not be carried out in the DB layer
     * @throws NoSuchElementException if the queueid does not match a popped element
     * @return an integer arrey contaning queueids matching active processing threads
     */
    public int[] getActiveProcesses()throws ClassNotFoundException, SQLException, NoSuchElementException{
        log.debug( "Processqueue.getActiveProcesses() called" );
        
        // establish databaseconnection
        log.debug( "establish databaseconnection" );
        //        try{
            con = establishConnection();
        // }
        // catch(ClassNotFoundException ce){
        //     throw new ClassNotFoundException( ce.getMessage() );
        // }
        // catch(SQLException sqe){
        //     throw new SQLException( sqe.getMessage() );
        // }

        // Query database
        Statement stmt = null;
        String SQL_query = "SELECT queueid FROM processqueue WHERE processing = 'Y' ";
        log.debug( String.format( "SQL Query == %s", SQL_query ) );

        // try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        // }
        // catch(SQLException sqe) {
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        // }
        
        ResultSet rs = null;
        // try{
            rs = stmt.executeQuery ( SQL_query );
            log.debug( String.format( "Processqueue queried with \"%s\"", SQL_query ) );
        // }
        // catch(SQLException sqe) {
        //     log.fatal( "SQLException: " + sqe.getMessage() );
        //     throw new SQLException( sqe.getMessage() );
        // }
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
