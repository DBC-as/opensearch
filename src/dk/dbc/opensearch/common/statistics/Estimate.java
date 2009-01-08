/**
 * \file Estimate.java
 * \brief The Estimate class
 * \package tools
 */
package dk.dbc.opensearch.tools;

import org.apache.log4j.Logger;

import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.NoSuchElementException;
import java.lang.ClassNotFoundException;
import org.apache.commons.configuration.ConfigurationException;

/**
 * \ingroup tools
 * \brief The Estimate class handles all communication to the statisticDB table
 */
public class Estimate extends DBConnection {

    Logger log = Logger.getLogger("Estimate");

    /**
     * Constructor
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Estimate() throws ConfigurationException, ClassNotFoundException {
        log.debug( "Estimate Constructor" );
    }

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    /**
     * \brief getEstimate retrieves estimate from statisticDB table.
     *
     * @param mimeType The mimeType of the element were trying to estimate processtime for
     * @param length length in bytes of the element were trying to estimate processtime for
     *
     * @return the processtime estimate.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if the mimetype is not known

     */
    public float getEstimate( String mimeType, long length ) throws SQLException, NoSuchElementException, ClassNotFoundException {
        log.debug( String.format( "estimate.getEstimate(mimeType=%s, length=%s) called", mimeType, length ) );
        Connection con;
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
           
            long p = 0l;
            long d = 0l;
            log.debug("obtained resultset");
            while(rs.next()){
                log.debug("next in rs");
                p = rs.getLong( "processtime" );
                log.debug( String.format( "got p: '%s'", p ) );
                //                log.debug("got element out of resultset");
                d = rs.getLong( "dataamount" );
                log.debug( String.format( "got d: '%s'", d ) );

                if ( d != 0l && p != 0l ){ // if either is zero
                    average_time = ( ( (float)p / d ) * length );
                }
            }
            log.debug( String.format( "processtime=%s dataamount=%s, averagetime=%s", p, d, average_time ) );
        }catch(NullPointerException npe){
            log.debug( "got nullpointer exception" );
            npe.printStackTrace();
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
     *
     * @param mimeType is the mimetype of the processed object 
     * @param length is the length in bytes of the processed object 
     * @param time is time in millisecs that it took to proces the object
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException{
        log.debug( String.format( "UpdateEstimate(mimeType = %s, length = %s, time = %s) called", mimeType, length, time ) );
        Connection con;
        con = establishConnection();
 
        Statement stmt = null;
        String sqlQuery = String.format( "UPDATE statisticDB "+
                                         "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                         "WHERE mimetype = '%s'", time, length, mimeType);
        log.debug( String.format( "query database with %s ", sqlQuery ) );

        int rowsUpdated = 0;        
        try{      
            stmt = con.createStatement();
            rowsUpdated = stmt.executeUpdate( sqlQuery );
            if( rowsUpdated == 0 ) {
                throw new NoSuchElementException( "The mimetype does not match a known mimetype, couldn't update." ); 
            }
            con.commit();
        }
        finally{
            stmt.close();
            con.close();
        }
        log.info( "estimate Updated" );
    }
}
