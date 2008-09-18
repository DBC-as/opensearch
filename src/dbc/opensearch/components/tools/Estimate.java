package dbc.opensearch.components.tools;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;

import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import java.sql.Connection;
import java.sql.CallableStatement;
import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


public class Estimate{


    /**
     * Variables to hold configuration parameters
     */
    private static String driver = "";
    private static String url = "";
    private static String userID = "";
    private static String passwd = "";

    /**
     *  database Connection
     */
    private Connection con;

    /**
     * Log
     */

    private static final Logger log = Logger.getRootLogger();

    public Estimate() throws ConfigurationException {
        log.debug( "Estimate Constructor" );

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
     * call estimates processtime, stores the data and queues
     * the handle.
     * @return the processtime estimate.
     * If the return value == 0l, no estimate is made, caller must
     * check this, but then an exceprion should have been thrown
     */

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public float getEstimate( String mimeType, long length ) throws SQLException, NoSuchElementException, ClassNotFoundException{

        log.info( String.format( "in estimate(). Length=%s",length ) );

        float average_time = 0f;
        ResultSet rs = null;

        // 20: open database connection
        log.debug( "Establishing connection to statisticDB" );

        Connection con = null;
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }

        // 25: create statement
        Statement stmt = null;

        String sqlQuery = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimeType );

        log.info( String.format( "SQL Query == %s", sqlQuery ) );

        try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        try{
            rs = stmt.executeQuery ( sqlQuery );
            log.info( String.format( "statisticDB queried with \"%s\"", sqlQuery ) );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        if( rs == null ){
            throw new NoSuchElementException( String.format( "We didnt get anything from the database, the mimetype \"%s\"is unknown.", mimeType ) );
        }
        rs.last();
        int rowCount = rs.getRow();
        log.info( String.format( "rows = %s", rowCount ) );

        if( rowCount != 1 ){
            throw new SQLException( String.format( "Count if rows is different from 1. RowCount==%s", rowCount) );
        }
        rs.first();

        long p = rs.getLong( "processtime" );
        long d = rs.getLong( "dataamount" );
        average_time = ( ( (float)p / d ) * length );
        log.info( String.format( "\nprocesstime=%s\ndataamount=%s\np/d=%s\naverage time for mimetype %s = %s", p, d, p/d, mimeType, average_time ) );

        return average_time;


    }

    public void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException{
        log.debug( String.format( "Entering UpdateEstimate(mimeType = %s, length = %s, time = %s)", mimeType, length, time ) );

        log.debug( "Entering UpdateEstimate" );
        
        // open database connection
        log.debug( "Establishing connection to statisticDB" );

        Connection con = null;
        try{
            con = establishConnection();
        }
        catch(ClassNotFoundException ce){
            throw new ClassNotFoundException( ce.getMessage() );
        }
        catch(SQLException sqe){
            throw new SQLException( sqe.getMessage() );
        }
        // create query
        String sqlQuery = "UPDATE statisticDB"+
            " SET processtime = processtime+"+time+
            " SET dataamount = dataamount+"+length+
            " WHERE mimetype = "+mimeType;
        log.debug( String.format( "SQL Query == %s", sqlQuery ) );
        
        // Write new estimate values to database
        Statement stmt = null;
        try{
            
            stmt = con.createStatement();
            stmt.executeUpdate( sqlQuery );
            log.debug( "New estimate written to database" );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        
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