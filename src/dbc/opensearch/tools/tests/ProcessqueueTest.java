package dbc.opensearch.tools.tests;
import dbc.opensearch.tools.Processqueue;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.mockrunner.mock.jdbc.MockStatement;

import org.apache.commons.configuration.ConfigurationException;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;



public class ProcessqueueTest extends BasicJDBCTestCaseAdapter {
    
   
    private void TestEstablishConneciton(){

    }


    public  void testPopFromEmptyProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {
        
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();

        //Processqueue processqueue = new Processqueue();
        MockResultSet result = statementHandler.createResultSet();
        statementHandler.prepareGlobalResultSet( result );
        Processqueue processqueue = new Processqueue();
        
        try{
            Triple<String, Integer, String> triple = processqueue.pop();
            fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }        
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();    
    }

    //////////////////
    public void testPopFromProcessqueueWithActiveElements() throws ConfigurationException, ClassNotFoundException, SQLException {
    
//         MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
//         StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();       
//         MockResultSet result = statementHandler.createResultSet();

//         result.addColumn("queueid", new Integer[] { 1 });
//         result.addColumn("fedoraHandle", new String[] {"testfedoraHandle1" });
//         result.addColumn("itemID", new String[] {"testItem1" });
//         result.addColumn("processing", new String[] { "N" });
//         statementHandler.prepareGlobalResultSet( result );
        
//         Processqueue processqueue = new Processqueue();
//         Triple<String, Integer, String> triple = processqueue.pop();
//         System.out.println( triple.toString() );
    
    }

    private void TestPopFromProcessqueueWithNoActiveElements(){}
    //////////////////

    public void testPushToProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {
        
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();
        MockStatement mockStatement = new MockStatement( connection );
        Processqueue processqueue = new Processqueue();

        String testFedoraHandle = "testFedoraHandle";
        String testItemID = "testItemID";
        processqueue.push( testFedoraHandle, testItemID );

        String sql_query = (  String.format( "INSERT INTO processqueue(queueid, fedorahandle, itemID, processing) "+
                                             "VALUES(processqueue_seq.nextval ,'%s','%s','N')", testFedoraHandle, testItemID ) );
        verifySQLStatementExecuted( sql_query );       
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
    
    public void testCommitWithValidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException{
        
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();
        MockStatement mockStatement = new MockStatement( connection );
        Processqueue processqueue = new Processqueue();

        statementHandler.prepareGlobalUpdateCount( 1 ); 
        int testQueueID = 10;

        processqueue.commit( testQueueID );
        String sql_query = String.format( "DELETE FROM processqueue WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );       
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testCommitWithInvalidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException{
        
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();
        MockStatement mockStatement = new MockStatement( connection );
        Processqueue processqueue = new Processqueue();

        statementHandler.prepareGlobalUpdateCount( 0 ); 
        int testQueueID = 10;

        try{
            processqueue.commit( testQueueID );
            fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }
        
        String sql_query = String.format( "DELETE FROM processqueue WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );       
        
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();    
    }
    public void testRollbackWithValidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException{
                
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();
        MockStatement mockStatement = new MockStatement( connection );
        Processqueue processqueue = new Processqueue();

        statementHandler.prepareGlobalUpdateCount( 1 ); 
        int testQueueID = 10;
        processqueue.rollback( testQueueID );

        String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );       
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();

}
    public void testRollbackWithInvalidQueueID() throws ConfigurationException, ClassNotFoundException, SQLException{
        MockConnection connection = getJDBCMockObjectFactory().getMockConnection();
        StatementResultSetHandler statementHandler = connection.getStatementResultSetHandler();
        MockStatement mockStatement = new MockStatement( connection );
        Processqueue processqueue = new Processqueue();
        
        statementHandler.prepareGlobalUpdateCount( 0 ); 
        int testQueueID = 10;
        
        try{
            processqueue.rollback( testQueueID );
            fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }
        
        String sql_query = String.format( "UPDATE processqueue SET processing = 'N' WHERE queueid = %s", testQueueID );
        verifySQLStatementExecuted( sql_query );       
        
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();    
    }

    private void TestDeActivate(){}    
}
