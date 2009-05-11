/**
 * \file IProcessqueue.java
 * \brief The IProcessqueue class
 * \package db;
 */

package dk.dbc.opensearch.common.db;


import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import java.util.Vector;

import dk.dbc.opensearch.common.types.InputPair;

/**
 * The IProcessqueue handles communication with the processqueue
 */

public interface IProcessqueue{
    /**
     * Pushes a fedorahandle to the processqueue
     *
     * @param fedorahandle a fedorahandle, i.e. a pointer to a document in the opensearch repository.
     * @param itemID an  itemID, identifying the dataobject, later used for indexing purposes
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
    public Vector< InputPair< String, Integer > > popAll() throws SQLException;
    
    /**
     * Pops up to maxSize fedorahandles from the processqueue
     *
     * @param maxSize the maximum size of the returned resultset, ie. the maximum number of handles to pop
     *
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public Vector< InputPair< String, Integer > > pop( int maxSize ) throws SQLException;

    /**
     * commits the pop to the queue. This operation removes the
     * element from the queue for good, and rollback is not possible
     *
     * @param queueid identifies the element to commit.
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
     * @param The path of the problematic file
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void notDocked( String path ) throws ClassNotFoundException, SQLException;

    /**
     * The notIndexed method is used to store queueIDs for indexjobs, that we couldnt not be indexed/
     *
     * @param The queueID of the problematic job
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException;
}
