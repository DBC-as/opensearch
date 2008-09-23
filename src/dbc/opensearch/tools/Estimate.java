package dbc.opensearch.tools;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;
import org.apache.commons.configuration.ConfigurationException;

/**
 * \brief The Estimate class handles all communication to the statisticDB table
 */
public class Estimate extends DBConnection {

    /**
     *  database Connection
     */
    private Connection con;

    /**
     * Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     * Constructor
     */
    public Estimate() throws ConfigurationException {
        log.debug( "Estimate Constructor" );
    }

    /**
     * getestimate retrieves estimate from statisticDB table.
     * @return the processtime estimate.
     * If the return value == 0l, no estimate is made, caller must
     * check this, but then an exception should have been thrown
     */

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public float getEstimate( String mimeType, long length ) throws SQLException, NoSuchElementException, ClassNotFoundException {

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
            log.debug( String.format( "statisticDB queried with \"%s\"", sqlQuery ) );
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
        log.debug( String.format( "rows = %s", rowCount ) );

        if( rowCount != 1 ){
            throw new SQLException( String.format( "Count if rows is different from 1. RowCount==%s", rowCount) );
        }
        rs.first();

        long p = rs.getLong( "processtime" );
        long d = rs.getLong( "dataamount" );
        
        log.info( String.format( "processtime=%s dataamount=%s average time for mimetype %s = %s", p, d, mimeType, average_time ) );

        if ( d != 0l && p != 0l ){
            average_time = ( ( (float)p / d ) * length );
            log.debug( String.format( "\nprocesstime=%s\ndataamount=%s\np/d=%s\naverage time for mimetype %s = %s", p, d, p/d, mimeType, average_time ) );
        }
        return average_time;
    }

    /**
     * updateEstimate updates the entry in statisticDB that matches the given mimetype, with the length and time.
     * @param mimeType is the mimetype of the processed object 
     * @param length is the length in bytes of the processed object 
     * @param time is time in millisecs that it took to proces the object
     */
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
            " WHERE mimetype = '"+mimeType+"'";
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
            throw new SQLException( sqe );
        }        
     }
}