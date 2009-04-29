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


public interface IEstimate{
    float getEstimate( String mimeType, long length )throws SQLException, NoSuchElementException, ClassNotFoundException, NullPointerException;
    void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException;
}



