package dbc.opensearch.components.processqueue;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;

/**
 * \brief Enqueue is the part of the processqueue that
 * accepts fedoraHandles and puts the on the queue.
 * Enqueue is implemented through a database.
 */
public class Enqueue {

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
     *  Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     * Constrcutor for the Engueue class. Creates an instance of
     * Enqueue, with a fedorahandle, pointing to the dataobject in the
     * Opensearch repository. If the fedorahandle conforms, and the
     * database information resolves, the fedorahandle is pushed to
     * the queue, and ready for processing.
     * \see dbc.opensearch.components.pti
     * @params fedorahandle: a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     */
    public Enqueue( String fedoraHandle ) throws ClassNotFoundException, ConfigurationException, SQLException {
        
        log.debug( "Enqueue Constructor" );

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
        
        /** \todo: check that the input conforms to a fedoraHandle (how?)*/

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
                                " VALUES(processqueue_seq.nextval ,'"+fedoraHandle+"','N')" );
            log.debug( "Written to database" );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        // Close database connection

        stmt.close();
        con.close();

        log.debug( "Exit Queue Constructor" );
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
