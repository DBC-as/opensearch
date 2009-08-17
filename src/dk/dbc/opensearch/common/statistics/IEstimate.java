/**
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


package dk.dbc.opensearch.common.statistics;


import java.sql.SQLException;
import java.util.NoSuchElementException;


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



