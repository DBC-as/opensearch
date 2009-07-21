package dk.dbc.opensearch.common.statistics;


/*   
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


import dk.dbc.opensearch.common.statistics.Estimate;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import org.apache.commons.configuration.ConfigurationException;

import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;
import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.PostgresqlDBConnection;


public class EstimateTest extends BasicJDBCTestCaseAdapter 
{    
    MockConnection connection;
    StatementResultSetHandler statementHandler;
    Estimate estimate;
    MockResultSet result;
    IDBConnection dbConnection;
    
    protected void setUp() throws Exception 
    {
        super.setUp();
        dbConnection = new PostgresqlDBConnection();
        connection = getJDBCMockObjectFactory().getMockConnection();
        statementHandler = connection.getStatementResultSetHandler();
        result = statementHandler.createResultSet();
        estimate = new Estimate( dbConnection );
    }
    
    
    public void testDataAmountIsZero() throws ConfigurationException, ClassNotFoundException, SQLException 
    {    
        int processtime = 10;
        int dataamount = 0;
        long length = 5;
        String mimetype = "text/xml";

        float average_time = 0l;

        result.addColumn("processtime", new Integer[] { processtime });
        result.addColumn("dataamount", new Integer[] { dataamount });
        statementHandler.prepareGlobalResultSet( result );
        
        float returnval = estimate.getEstimate( mimetype, length );
        assertTrue( average_time == returnval );

        String sql_query = String.format( "SELECT processtime, dataamount FROM statistics WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
    public void testProcessTimeIsZero() throws ConfigurationException, ClassNotFoundException, SQLException 
    {    
        int processtime = 0;
        int dataamount = 10;
        long length = 5;
        String mimetype = "text/xml";

        float average_time = 0l;

        result.addColumn("processtime", new Integer[] { processtime });
        result.addColumn("dataamount", new Integer[] { dataamount });
        statementHandler.prepareGlobalResultSet( result );
        
        float returnval = estimate.getEstimate( mimetype, length );
        assertTrue( average_time == returnval );

        String sql_query = String.format( "SELECT processtime, dataamount FROM statistics WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }

    public void testValidgetEstimate() throws ConfigurationException, ClassNotFoundException, SQLException 
    {    
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

        String sql_query = String.format( "SELECT processtime, dataamount FROM statistics WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
    
    
    public void testInvalidgetEstimate() throws ConfigurationException, ClassNotFoundException, SQLException 
    {        
        String mimetype = "notype";
        try
        {
            float returnval = estimate.getEstimate( mimetype, 3 );
            fail("Should have gotten NoSuchElementException - returned zero rows updated from statisticsDB table");
        }
        catch(NoSuchElementException nse)
        {
            // Expected - intentional
        }
        
        String sql_query = String.format( "SELECT processtime, dataamount FROM statistics WHERE mimetype = '%s'", mimetype );
        verifySQLStatementExecuted( sql_query );       
        verifyAllStatementsClosed();
        verifyConnectionClosed();                
    }

    
    public void testValidupdateEstimate() throws ConfigurationException, ClassNotFoundException, SQLException 
    {        
        int processtime = 25;
        long length = 5;
        String mimetype = "text/xml";
        statementHandler.prepareGlobalUpdateCount( 1 ); 

        estimate.updateEstimate( mimetype, length, processtime );

        String sql_query = String.format( "UPDATE statistics "+
                                          "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                          "WHERE mimetype = '%s'", processtime, length, mimetype);
        verifySQLStatementExecuted( sql_query );       
        verifyCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();                       
    }
    
    
    public void testInvalidupdateEstimate() throws ConfigurationException, ClassNotFoundException, SQLException 
    {
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
        
        String sql_query = String.format( "UPDATE statistics "+
                                          "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                          "WHERE mimetype = '%s'", processtime, length, mimetype);
        verifySQLStatementExecuted( sql_query );       
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();                          
    }
}
