/**
 * \file IEstimate.java
 * \brief The IEstimate class
 * \package statistics;
 */

package dk.dbc.opensearch.common.statistics;


import java.lang.ClassNotFoundException;
import java.lang.NullPointerException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;

/**
 * The IEstimate Interface handles all communication to the statistics table
 */
public interface IEstimate
{
    /**
     * \brief getEstimate retrieves estimate from statistics table.
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
    float getEstimate( String mimeType, long length )throws SQLException, NoSuchElementException, ClassNotFoundException, NullPointerException;
    
    
    /**
     * updateEstimate updates the entry in statistics that matches the given mimetype, with the length and time.
     *
     * @param mimeType is the mimetype of the processed object 
     * @param length is the length in bytes of the processed object 
     * @param time is time in millisecs that it took to proces the object
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException;
}



