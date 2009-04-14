package dk.dbc.opensearch.common.db.tests;


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


import dk.dbc.opensearch.common.db.Processqueue;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

import java.lang.ClassNotFoundException;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;

//import com.mallardsoft.tuple.Triple;
//import com.mallardsoft.tuple.Tuple;


public class ProcessqueueTest extends BasicJDBCTestCaseAdapter 
{
    MockConnection connection;
    StatementResultSetHandler statementHandler;
    MockResultSet result;    
    Processqueue processqueue;
    

    protected void setUp() throws Exception 
    {
        super.setUp();
        
        connection = getJDBCMockObjectFactory().getMockConnection();
        statementHandler = connection.getStatementResultSetHandler();
        result = statementHandler.createResultSet();
        processqueue = new Processqueue();
    }

    
//     public  void testPopFromEmptyProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {
        
//         String sql_query = "SELECT * from processqueue_pop_post()";
 
//         statementHandler.prepareGlobalResultSet( result );

//         try{
//             Triple<String, Integer, String> triple = processqueue.pop();
//             fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
//         }
//         catch(NoSuchElementException nse){
//             // Expected - intentional
//         }
//         verifySQLStatementExecuted( sql_query );
//         verifyNotCommitted();
//         verifyAllStatementsClosed();
//         verifyConnectionClosed();
//     }

    
//     public void testPopFromProcessqueueWithNoHandle() throws ConfigurationException, ClassNotFoundException, SQLException 
//     {
        
//         String fedorahandle = null;
//         int queueid = 1;
//         String itemid = "item_1";
//         String sql_query = "SELECT * from processqueue_pop_post()";

//         result.addColumn("fedorahandle", new String[] { fedorahandle });
//         result.addColumn("itemID", new String[] { itemid });
//         result.addColumn("queueID", new Integer[] { queueid });
//         statementHandler.prepareGlobalResultSet( result );

//         Triple<String, Integer, String> triple = null;
       
//         try
//         {
//             triple = processqueue.pop();
//             fail("Should have gotten NoSuchElementException - returned zero rows removed from processqueue table");
//         }
//         catch(NoSuchElementException nse){
//             // Expected - intentional
//         }
        
//         verifySQLStatementExecuted( sql_query );
//         verifyNotCommitted();
//         //assertEquals(fedorahandle , Tuple.get1(triple) );
//         //assertEquals(queueid , (int )Tuple.get2(triple) );
//         //assertEquals(itemid , Tuple.get3(triple) );
        
//         verifyAllStatementsClosed();
//     }




//     public void testPopFromProcessqueueWithActiveElements() throws ConfigurationException, ClassNotFoundException, SQLException {
        
//         String fedorahandle = "test_handle_1";
//         int queueid = 1;
//         String itemid = "item_1";
//         String sql_query = "SELECT * from processqueue_pop_post()";

//         result.addColumn("fedorahandle", new String[] { fedorahandle });
//         result.addColumn("itemID", new String[] { itemid });
//         result.addColumn("queueID", new Integer[] { queueid });
//         statementHandler.prepareGlobalResultSet( result );

//         Triple<String, Integer, String> triple = null;
//         triple = processqueue.pop();
        
//         verifySQLStatementExecuted( sql_query );
//         verifyCommitted();
//         assertEquals(fedorahandle , Tuple.get1(triple) );
//         assertEquals(queueid , (int )Tuple.get2(triple) );
//         assertEquals(itemid , Tuple.get3(triple) );
        
//         verifyAllStatementsClosed();
//     }
    // @Ignore
//     public void testPushToProcessqueue() throws ConfigurationException, ClassNotFoundException, SQLException {
        
//         String testFedoraHandle = "testFedoraHandle";
//         String testItemID = "testItemID";
//         processqueue.push( testFedoraHandle);
        
//         String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, itemID, processing ) "+
//                                              "VALUES( nextval( 'processqueue_sequence' ) ,'%s','%s','N' )", testFedoraHandle, testItemID ) );

//         verifySQLStatementExecuted( sql_query );
//         verifyCommitted();
//         verifyAllStatementsClosed();
//         verifyConnectionClosed();
//     }

    
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
        catch(NoSuchElementException nse)
        {
            // Expected - intentional
        }

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
        catch(NoSuchElementException nse)
        {
            // Expected - intentional
        }

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
        verifyNotCommitted();
        verifyAllStatementsClosed();
        verifyConnectionClosed();
    }
}
