package dk.dbc.opensearch.common.statistics.tests;


/*
*GNU, General Public License Version 3. If any software components linked 
*together in this library have legal conflicts with distribution under GNU 3 it 
*will apply to the original license type.
*
*Software distributed under the License is distributed on an "AS IS" basis,
*WITHOUT WARRANTY OF ANY KIND, either express or implied. See the License
*for the specific language governing rights and limitations under the
*License.
*
*Around this software library an Open Source Community is established. Please 
*leave back code based upon our software back to this community in accordance to 
*the concept behind GNU. 
*
*You should have received a copy of the GNU Lesser General Public
*License along with this library; if not, write to the Free Software
*Foundation, Inc., 51 Franklin Street, Fifth Floor, Boston, MA  02110-1301  USA
***** END LICENSE BLOCK ***** */

import dk.dbc.opensearch.common.statistics.Estimate;

import com.mockrunner.jdbc.BasicJDBCTestCaseAdapter;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.mock.jdbc.MockResultSet;
import org.apache.commons.configuration.ConfigurationException;

import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;


public class EstimateTest extends BasicJDBCTestCaseAdapter 
{    
    MockConnection connection;
    StatementResultSetHandler statementHandler;
    Estimate estimate;
    MockResultSet result;

    
    protected void setUp() throws Exception 
    {
        super.setUp();
        connection = getJDBCMockObjectFactory().getMockConnection();
        statementHandler = connection.getStatementResultSetHandler();
        result = statementHandler.createResultSet();
        estimate = new Estimate();
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
