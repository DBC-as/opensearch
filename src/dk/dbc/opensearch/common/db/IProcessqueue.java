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
import dk.dbc.opensearch.common.types.Pair;


public interface IProcessqueue{
    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException;
    public Vector< Pair< String, Integer > > popAll() throws SQLException;
    public Vector<Pair<String, Integer>> pop( int maxSize ) throws SQLException;
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException;
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException;
    public int deActivate() throws ClassNotFoundException, SQLException;
    public void notDocked( String path ) throws ClassNotFoundException, SQLException;
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException;
}
