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
    Logger log = Logger.getLogger("Estimate");

    /**
     * Constructor
     */
    public Estimate() throws ConfigurationException, ClassNotFoundException {
        log.debug( "Estimate Constructor" );
    }

    /**
     * \brief getEstimate retrieves estimate from statisticDB table.
     * @param mimeType The mimeType of the element were trying to estimate processtime for
     * @param length length in bytes of the element were trying to estimate processtime for
     * @return the processtime estimate.
     */

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public float getEstimate( String mimeType, long length ) throws SQLException, NoSuchElementException, ClassNotFoundException {
        log.debug( String.format( "estimate.getEstimate(mimeType=%s, length=%s) called", mimeType, length ) );

        con = establishConnection();

        float average_time = 0f;
        ResultSet rs = null;
        Statement stmt = null;
        String sqlQuery = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimeType );
        log.debug( String.format( "query database with %s ", sqlQuery ) );

        try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
            rs = stmt.executeQuery ( sqlQuery );
            
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
            if ( d != 0l && p != 0l ){ // if either is zero
                average_time = ( ( (float)p / d ) * length );
            }
            
            log.debug( String.format( "processtime=%s dataamount=%s, averagetime=%s", p, d, average_time ) );
        }
        finally{
            stmt.close();
            con.close();
        }
        log.info( String.format( "Obtained average processing time=%s",average_time) );
        return average_time;
    }

    /**
     * updateEstimate updates the entry in statisticDB that matches the given mimetype, with the length and time.
     * @param mimeType is the mimetype of the processed object 
     * @param length is the length in bytes of the processed object 
     * @param time is time in millisecs that it took to proces the object
     */
    public void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException{
        log.debug( String.format( "UpdateEstimate(mimeType = %s, length = %s, time = %s) called", mimeType, length, time ) );

        con = establishConnection();
 
        Statement stmt = null;
        String sqlQuery = String.format( "UPDATE statisticDB "+
                                         "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                         "WHERE mimetype = '%s'", time, length, mimeType);
        log.debug( String.format( "query database with %s ", sqlQuery ) );
        
        try{      
            stmt = con.createStatement();
            stmt.executeUpdate( sqlQuery );
        }
        finally{
            stmt.close();
            con.close();
        }
        log.info( "estimate Updated" );
    }
}