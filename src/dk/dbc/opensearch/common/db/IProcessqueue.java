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


package dk.dbc.opensearch.common.db;


import java.sql.SQLException;
import java.util.NoSuchElementException;

import dk.dbc.opensearch.common.types.SimplePair;
import java.util.List;

/**
 * The IProcessqueue handles communication with the processqueue
 */

public interface IProcessqueue
{
    /**
     * Pushes a fedorahandle to the processqueue
     *
     * @param fedorahandle a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */     
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException;


    /**
     * Pops all fedorahandles from the processqueue
     *
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public List< SimplePair< String, Integer > > popAll() throws SQLException;


    /**
     * Pops up to maxSize fedorahandles from the processqueue
     *
     * @param maxSize the maximum size of the returned resultset, ie. the maximum number of handles to pop
     *
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public List< SimplePair< String, Integer > > pop( int maxSize ) throws SQLException;


    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     *
     * @param queueID identifies the element to commit.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException;


    /**
     * rolls back the pop. This restores the element in the queue and
     * the element is in concideration the next time a pop i done.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if there is no element on the queue to pop
     */
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException;


    /**
     * deactivate elements on the processqueue.  It finds all elements
     * on the processqueue which are marked as active and mark them as
     * inactive, ie. they are ready for indexing.
     *
     * @return turn the number of elements which are reactivated
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public int deActivate() throws ClassNotFoundException, SQLException;


    /**
     * The notDocked method is used to store paths to the files, that we couldnt not store in the repository
     *
     * @param path The path of the problematic file
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void notDocked( String path ) throws ClassNotFoundException, SQLException;

    
    /**
     * The notIndexed method is used to store queueIDs for indexjobs, that we couldnt not be indexed/
     *
     * @param queueID The queueID of the problematic job
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException;
}
