package dbc.opensearch.tools.tests;
//package dbc.opensearch.tools.tests;
import dbc.opensearch.tools.Processqueue;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.mockrunner.mock.jdbc.MockStatement;

import com.mockrunner.jdbc.CallableStatementResultSetHandler;

import static org.easymock.classextension.EasyMock.*;
//import org.easymock.classextension.MockClassControl;
//import org.easymock.MockControl;

import java.sql.CallableStatement;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.configuration.ConfigurationException;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Triple;

public class ProcessqueueTest extends BasicJDBCTestCaseAdapter {

    MockConnection connection;
    StatementResultSetHandler statementHandler;
    MockResultSet result;
    
    Processqueue processqueue;

    private CallableStatementResultSetHandler callableStatementHandler;

    protected void setUp() throws Exception {

        super.setUp();
        connection = getJDBCMockObjectFactory().getMockConnection();
        callableStatementHandler = connection.getCallableStatementResultSetHandler();   
        statementHandler = connection.getStatementResultSetHandler();
        result = statementHandler.createResultSet();
        processqueue = new Processqueue();
    }

    //    private void TestEstablishConneciton(){}

    public  void testPopFromEmptyProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {

        statementHandler.prepareGlobalResultSet( result );

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


    /////////////////////////
    public void testPopFromProcessqueueWithActiveElements() throws ConfigurationException, ClassNotFoundException, SQLException {
        
        String fedorahandle = "test_handle_1";
        int queueid = 1;
        String processing = "N";
        String itemid = "item_1";


        //        CallableStatement cs = null;
        CallableStatement mockCallableStatement = createMock( CallableStatement.class );
        
        expect( mockCallableStatement.getString( 1 ) ).andReturn( fedorahandle ).times(2);
        expect( mockCallableStatement.getInt( 2 ) ).andReturn( queueid ) ;
        expect( mockCallableStatement.getString( 4 ) ).andReturn( itemid );
        replay( mockCallableStatement );

        Triple<String, Integer, String> triple = processqueue.getValues( mockCallableStatement );
      
        assertEquals(fedorahandle , Tuple.get1(triple) );
        assertEquals(queueid , (int )Tuple.get2(triple) );
        assertEquals(itemid , Tuple.get3(triple) );
        
        verifyAllStatementsClosed();
    }


    public void testPopFromProcessqueueWithNoActiveElements() throws ConfigurationException, ClassNotFoundException, SQLException {
        try{
            Triple<String, Integer, String> triple = processqueue.pop();
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }
        verifyNotCommitted();
        verifyCallableStatementClosed("call proc_prod");
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
    

    public void testPushToProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {

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
