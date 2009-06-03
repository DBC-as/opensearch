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


import dk.dbc.opensearch.common.db.Processqueue;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import java.util.ArrayList;
import java.util.Vector;
import dk.dbc.opensearch.common.types.InputPair;


/** \brief Unittest for Processqueue */
public class ProcessqueueTest extends BasicJDBCTestCaseAdapter
{
    MockConnection connection;
    StatementResultSetHandler statementHandler;
    MockResultSet result;
    Vector<InputPair<String, Integer>> resultVector;
    Processqueue processqueue;


    /**
     * Common setup
     */
    protected void setUp() throws Exception
    {
        super.setUp();

        connection = getJDBCMockObjectFactory().getMockConnection();
        statementHandler = connection.getStatementResultSetHandler();

        processqueue = new Processqueue();
        resultVector = new Vector<InputPair<String, Integer>>(); 
    }


    /**
     * setup mock resultset vector
     */
    public void setupResultset(){

        result = statementHandler.createResultSet();
        result.addColumn( "fedorahandle" );
        result.addColumn( "queueID" );
        
        Object r[] =  { "handle1", 1 };
        result.addRow( r );
        resultVector.add( new InputPair<String, Integer>( (String) r[0], (Integer) r[1] ) );
        r[0] = "handle2";
        r[1] = 2;
        result.addRow( r );
        resultVector.add( new InputPair<String, Integer>( (String) r[0], (Integer) r[1] ) );
        r[0] = "handle3";
        r[1] = 3;
        result.addRow( r );
        resultVector.add( new InputPair<String, Integer>( (String) r[0], (Integer) r[1] ) );
        r[0] = "handle4";
        r[1] = 4;
        result.addRow( r );
        resultVector.add( new InputPair<String, Integer>( (String) r[0], (Integer) r[1] ) );
        r[0] = "handle5";
        r[1] = 5;
        result.addRow( r );
        resultVector.add( new InputPair<String, Integer>( (String) r[0], (Integer) r[1] ) );
    }


    public void testPush() throws ClassNotFoundException, SQLException
    {

        String mockFedorahandle = "mockhandle";
        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','N' )", mockFedorahandle ) );
        
        processqueue.push( mockFedorahandle );

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testPushExceptionThrown() throws ClassNotFoundException
    {
        String mockFedorahandle = "mockhandle";

        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','N' )", mockFedorahandle ) );

        statementHandler.prepareThrowsSQLException( sql_query, new SQLException( "test exception" ));

        try{
            processqueue.push( mockFedorahandle );
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testPopAll() throws SQLException
    {

        String sql_query = "SELECT * from get_all_posts()";
        setupResultset();
        statementHandler.prepareGlobalResultSet( result );

        Vector<InputPair<String, Integer>> r = new Vector<InputPair<String, Integer>>();
        r = processqueue.popAll();
        
        assertEquals( r, resultVector );

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testPopAllExceptionThrown()
    {

        String sql_query = "SELECT * from get_all_posts()";
        
        statementHandler.prepareThrowsSQLException( sql_query, new SQLException( "test exception" ));
        Vector<InputPair<String, Integer>> r = new Vector<InputPair<String, Integer>>();
        
        try{
            r = processqueue.popAll();
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();        
    }


    public void testPopLessThanMaxSize() throws SQLException
    {
        setupResultset();
        int maxSize = 5;
        String sql_query = String.format( "SELECT * from get_posts( %s )", maxSize );

        statementHandler.prepareGlobalResultSet( result );

        Vector<InputPair<String, Integer>> r = new Vector<InputPair<String, Integer>>();
        r = processqueue.pop( maxSize );
        
        assertEquals( r, resultVector );

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
    

    public void testPopMoreOrEquealToMaxSize() throws SQLException
    {
        setupResultset();
        int maxSize = 15;
        String sql_query = String.format( "SELECT * from get_posts( %s )", maxSize );

        statementHandler.prepareGlobalResultSet( result );

        Vector<InputPair<String, Integer>> r = new Vector<InputPair<String, Integer>>();
        r = processqueue.pop( maxSize );
        
        assertEquals( r, resultVector );

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    
    public void testPopExceptionThrown()
    {
        int maxSize = 15;
        String sql_query = String.format( "SELECT * from get_posts( %s )", maxSize );
        
        statementHandler.prepareThrowsSQLException( sql_query, new SQLException( "test exception" ));
        Vector<InputPair<String, Integer>> r = new Vector<InputPair<String, Integer>>();
        
        try{
            r = processqueue.pop( maxSize );
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testCommitWithValidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException
    {
        statementHandler.prepareGlobalUpdateCount( 1 );
        int testQueueID = 10;

        processqueue.commit( testQueueID );
        String sql_query = String.format( "DELETE FROM processqueue WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testCommitWithInvalidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException
    {
        statementHandler.prepareGlobalUpdateCount( 0 );
        int testQueueID = 10;

        try
        {
            processqueue.commit( testQueueID );
            fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
        }
        catch(NoSuchElementException nse){ /* intentional */ }

        String sql_query = String.format( "DELETE FROM processqueue WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testRollbackWithValidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException
    {
        statementHandler.prepareGlobalUpdateCount( 1 );
        int testQueueID = 10;
        processqueue.rollback( testQueueID );

        String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();

    }


    public void testRollbackWithInvalidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException
    {
        statementHandler.prepareGlobalUpdateCount( 0 );
        int testQueueID = 10;

        try
        {
            processqueue.rollback( testQueueID );
            fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
        }
        catch(NoSuchElementException nse){ /* intentional */ }

        String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testDeActivate()throws ClassNotFoundException, SQLException
    {
        statementHandler.prepareGlobalUpdateCount( 2 );

        processqueue.deActivate();

        String sql_query = "UPDATE processqueue SET processing = 'N' WHERE processing = 'Y'";

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testDeActivateExceptionThrown()throws ClassNotFoundException
    {
        String sql_query = "UPDATE processqueue SET processing = 'N' WHERE processing = 'Y'";
     
        statementHandler.prepareThrowsSQLException( sql_query, new SQLException( "test exception" ));

        try{
            processqueue.deActivate();
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testNotDocked() throws ClassNotFoundException, SQLException
    {

        String mockPath = "mockpath";

        String sql_query = (  String.format( "INSERT INTO notdocked( path ) "+
                                             "VALUES( %s )", mockPath ) );

        processqueue.notDocked( mockPath );

        verifySQLStatementExecuted( sql_query );
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }


    public void testNotDockedExceptionThrown() throws ClassNotFoundException
    {
        String mockPath = "mockpath";

        String sql_query = (  String.format( "INSERT INTO notdocked( path ) "+
                                             "VALUES( %s )", mockPath ) );

        statementHandler.prepareThrowsSQLException( sql_query, new SQLException( "test exception" ));
        
        try{
            processqueue.notDocked( mockPath );
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testNotIndexed() throws ClassNotFoundException, SQLException
    {
        int queueID = 1;
        String sql_query1 = String.format( "SELECT * FROM processqueue WHERE queueID = %s", queueID );
        
        result = statementHandler.createResultSet();
        result.addColumn( "fedorahandle" );
        result.addColumn( "queueID" );
        
        Object r[] =  { "handle1", 1 };
        result.addRow( r );
        
        statementHandler.prepareGlobalResultSet( result );

        String sql_query2 = String.format( "INSERT INTO notindexed( queueid, fedorahandle ) VALUES( %s, '%s' )", r[1], r[0] );
        
        String sql_query3 = String.format( "DELETE FROM processqueue WHERE queueID = %s", queueID );
        
        statementHandler.prepareGlobalUpdateCount( 1 );

        processqueue.notIndexed( queueID );

        verifySQLStatementExecuted( sql_query1 );
        verifySQLStatementExecuted( sql_query2 );
        verifySQLStatementExecuted( sql_query3 );
        verifyCommitted();
        verifyAllResultSetsClosed();
        verifyConnectionClosed();
    }


    public void testNotIndexedThrowsExceptionQuery1() throws ClassNotFoundException, SQLException
    {
        int queueID = 1;
        String sql_query1 = String.format( "SELECT * FROM processqueue WHERE queueID = %s", queueID );
        Object r[] =  { "handle1", 1 };

        statementHandler.prepareThrowsSQLException( sql_query1, new SQLException( "test exception" ));

        String sql_query2 = String.format( "INSERT INTO notindexed( queueid, fedorahandle ) VALUES( %s, '%s' )", r[1], r[0] );
        
        String sql_query3 = String.format( "DELETE FROM processqueue WHERE queueID = %s", queueID );
        
        statementHandler.prepareGlobalUpdateCount( 1 );


        try{        
            processqueue.notIndexed( queueID );
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifySQLStatementNotExecuted( sql_query2 );
        verifySQLStatementNotExecuted( sql_query3 );
        verifyNotCommitted();
        verifyAllResultSetsClosed();
        verifyConnectionClosed();
    }

    public void testNotIndexedThrowsExceptionQuery2() throws ClassNotFoundException, SQLException
    {
        int queueID = 1;
        String sql_query1 = String.format( "SELECT * FROM processqueue WHERE queueID = %s", queueID );
                
        result = statementHandler.createResultSet();
        result.addColumn( "fedorahandle" );
        result.addColumn( "queueID" );
        
        Object r[] =  { "handle1", 1 };
        result.addRow( r );
        
        statementHandler.prepareGlobalResultSet( result );

        String sql_query2 = String.format( "INSERT INTO notindexed( queueid, fedorahandle ) VALUES( %s, '%s' )", r[1], r[0] );
        
        String sql_query3 = String.format( "DELETE FROM processqueue WHERE queueID = %s", queueID );

        statementHandler.prepareThrowsSQLException( sql_query2, new SQLException( "test exception" ));        

        try{        
            processqueue.notIndexed( queueID );
            fail("Should have thrown SQLException");
        }
        catch( SQLException sqlex ){ /* intentional */ }

        verifySQLStatementExecuted( sql_query1 );
        verifySQLStatementNotExecuted( sql_query3 );
        verifyNotCommitted();
        verifyAllResultSetsClosed();
        verifyConnectionClosed();
    }
}
