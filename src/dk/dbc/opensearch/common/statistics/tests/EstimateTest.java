package dk.dbc.opensearch.tools.tests;

import dk.dbc.opensearch.tools.Estimate;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import com.mockrunner.mock.jdbc.MockStatement;

import org.apache.commons.configuration.ConfigurationException;
import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


public class EstimateTest extends BasicJDBCTestCaseAdapter {
    
    MockConnection connection;
    StatementResultSetHandler statementHandler;
    Estimate estimate;
    MockResultSet result;

    protected void setUp() throws Exception {
        super.setUp();
        connection = getJDBCMockObjectFactory().getMockConnection();
        statementHandler = connection.getStatementResultSetHandler();
        result = statementHandler.createResultSet();
        estimate = new Estimate();
    }
    

    
    public void testValidgetEstimate() throws ConfigurationException, ClassNotFoundException, SQLException {
    
        int processtime = 25;
        int dataamount = 10;
        long length = 5;
        String mimetype = "text/xml";

        float average_time = ((float)processtime / dataamount) * length;

        result.addColumn("processtime", new Integer[] { processtime });
        result.addColumn("dataamount", new Integer[] { dataamount });
        statementHandler.prepareGlobalResultSet( result );
        
        float returnval = estimate.getEstimate( mimetype, length );
        assertTrue( average_time == returnval );

        String sql_query = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();

    }
    public void testInvalidgetEstimate() throws ConfigurationException, ClassNotFoundException, SQLException {
        
        String mimetype = "notype";
        try{
            float returnval = estimate.getEstimate( mimetype, 3 );
            fail("Should have gotten NoSuchElementException - returned zero rows updated from statisticsDB table");
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }
        
        String sql_query = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();                
    }

    public void testValidupdateEstimate() throws ConfigurationException, ClassNotFoundException, SQLException {
        
        int processtime = 25;
        long length = 5;
        String mimetype = "text/xml";
        statementHandler.prepareGlobalUpdateCount( 1 ); 

        estimate.updateEstimate( mimetype, length, processtime );

        String sql_query = String.format( "UPDATE statisticDB "+
                                          "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                          "WHERE mimetype = '%s'", processtime, length, mimetype);
        verifySQLStatementExecuted( sql_query );       
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();                       
    }
    
    public void testInvalidupdateEstimate() throws ConfigurationException, ClassNotFoundException, SQLException {

        int processtime = 25;
        long length = 5;
        String mimetype = "notype";
        statementHandler.prepareGlobalUpdateCount( 0 ); 
        
        try{
            estimate.updateEstimate( mimetype, length, processtime );
            fail("Should have gotten NoSuchElementException - returned zero rows updated from statisticsDB table");
        }
        catch(NoSuchElementException nse){
            // Expected - intentional
        }
        
        String sql_query = String.format( "UPDATE statisticDB "+
                                          "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                          "WHERE mimetype = '%s'", processtime, length, mimetype);
        verifySQLStatementExecuted( sql_query );       
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();                          
    }
}
