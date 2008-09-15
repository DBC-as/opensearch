package dbc.opensearch.components.processqueue;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;
/**
 * \brief Dequeue is the part of the processqueue that retrieves items
 * from the ProcessQueue, which in turn is implemented in a database.
 */
public class Dequeue {

    /**
     * Databasedriver
     */
    private static String driver = "";

    /**
     * Url to database
     */
    private static String url = "";

    /**
     * UserID to log into database
     */
    private static String userID = "";

    /**
     * password to log into database
     */
    private static String passwd = "";

    /**
     *  database Connection
     */
    private Connection con;
    /**
     *  Popped_queueid holds elements queueid after pop
     */
    private int popped_queueid = 0;

    /**
     *  Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     * The Dequeue constructor reads configuration parameters
     */
    public Dequeue() throws ConfigurationException { 

        log.debug( "Dequeue Constructor" );

        log.debug( "Obtain config paramaters");
        
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            log.fatal( "ConfigurationException: " + cex.getMessage() );
            throw new ConfigurationException( cex.getMessage() );
        }

        driver = config.getString( "database.driver" );
        url    = config.getString( "database.url" );
        userID = config.getString( "database.userID" );
        passwd = config.getString( "database.passwd" );
        
        log.debug( "driver: "+driver );
        log.debug( "url:    "+url );
        log.debug( "userID: "+userID );
    }

    /**
     * pops the top-most element from the Dequeue, returning the fedorahandle as a String
     * @returns fedoraHandle: a String containing the unique handle
     * for the resource in the object repository
     */
    public String pop() throws ClassNotFoundException, SQLException, NoSuchElementException {
        log.debug( "Dequeue.pop() called" );

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
        popped_queueid = cs.getInt(2);
        log.info( "Handle obtained by pop: "+ handle );
        log.debug( "popped_queueid: "+popped_queueid );

        // Close database connection
        cs.close();
        con.close();

        return handle;
    }


    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     */
    public void commit() throws ClassNotFoundException, SQLException, IllegalCallException {
        log.debug( "Dequeue.update() called" );
        
        // Throw exception if commit is not called right after pop (no popped queueelement to commit)
        if (popped_queueid == 0 ){
            log.warn( "No pop to commit." );
            throw new IllegalCallException( "No pop to commit." ); 
        }
        
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

        // remove element from queue ie. delete row from processqueue table
        try{
            stmt = con.createStatement();
            stmt.executeUpdate("DELETE FROM processqueue WHERE queueid = "+popped_queueid);
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
     * rolls back the pop. This restores the element in the queue and
     * the element is in concideration the next time a pop i done.
     */    
    public void rollback() throws ClassNotFoundException, SQLException, IllegalCallException{
        log.debug( "Dequeue.rollback() called" );

        // Throw exception if rollback is not called right after pop (no popped queueelement to rollback)
        if (popped_queueid == 0 ){
            log.warn( "No pop to rollback." );
            throw new IllegalCallException( "No pop to rollback." ); 
        }
        
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

        // update processqueue set processing = 'N' where queueid =3;

        // restore element in queue ie. update queueid in row from processqueue table
        try{
            stmt = con.createStatement();
            stmt.executeUpdate("UPDATE processqueue SET processing = 'N' WHERE queueid = "+popped_queueid);
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        
        // Close database connection
        stmt.close();
        con.close();

    }
    
    private static Connection establishConnection() throws ClassNotFoundException, SQLException {

        Connection con = null;

        try {
            Class.forName(driver);

        } 
        catch(ClassNotFoundException ce) {
            log.fatal( "ClassNotFoundException: " + ce.getMessage() );
            throw new ClassNotFoundException( ce.getMessage() );
        }

        try {
            con = DriverManager.getConnection(url, userID, passwd);
        } 
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        log.debug( "Got connection." );

        return con;
    }
}
