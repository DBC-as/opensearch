/**
 * \file Processqueue.java
 * \brief The Processqueue class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;


import dk.dbc.opensearch.common.db.IProcessqueue;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import dk.dbc.opensearch.common.types.Pair;
import java.util.Vector;

public class Processqueue implements IProcessqueue {
    public Processqueue(){}

    public void push( String fedorahandle ) throws ClassNotFoundException, SQLException{}
    public Vector< Pair< String, Integer > > popAll() throws SQLException{return null;}
    public Vector<Pair<String, Integer>> pop( int maxSize ) throws SQLException{return null;}
    public void commit( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException{}
    public void rollback( int queueID ) throws ClassNotFoundException, SQLException, NoSuchElementException{}
    public int deActivate() throws ClassNotFoundException, SQLException{return 0;}
    public void notDocked( String path ) throws ClassNotFoundException, SQLException{}
    public void notIndexed(int queueID ) throws ClassNotFoundException, SQLException{}

}
