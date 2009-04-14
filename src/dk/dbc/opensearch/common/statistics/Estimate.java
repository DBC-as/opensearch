/**
 * \file Estimate.java
 * \brief The Estimate class
 * \package tools
 */
package dk.dbc.opensearch.common.statistics;


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


import dk.dbc.opensearch.common.db.DBConnection;

import java.lang.ClassNotFoundException;
import java.sql.Connection;
import java.sql.Statement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief The Estimate class handles all communication to the statistics table
 */
public class Estimate 
{
    Logger log = Logger.getLogger("Estimate");
    DBConnection DBconnection = null;
 
    
    /**
     * Constructor
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Estimate() throws ConfigurationException, ClassNotFoundException 
    {
        log.debug( "Estimate Constructor" );
        DBconnection = new DBConnection();
    }

    
    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    /**
     * \brief getEstimate retrieves estimate from statistics table.
     *
     * @param mimeType The mimeType of the element were trying to estimate processtime for
     * @param length length in bytes of the element were trying to estimate processtime for
     *
     * @return the processtime estimate.
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws NoSuchElementException if the mimetype is not known
     */
    public float getEstimate( String mimeType, long length ) throws SQLException, NoSuchElementException, ClassNotFoundException, NullPointerException
    {
        log.debug( String.format( "estimate.getEstimate(mimeType=%s, length=%s) called", mimeType, length ) );

        Connection con = DBConnection.getConnection();
     
        float average_time = 0f;
        ResultSet rs = null;
        Statement stmt = null;
        String sqlQuery = String.format( "SELECT processtime, dataamount FROM statistics WHERE mimetype = '%s'", mimeType );
        log.debug( String.format( "query database with %s ", sqlQuery ) );

        try
        {
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
            rs = stmt.executeQuery ( sqlQuery );
            
            if( rs == null )
            {
                throw new NoSuchElementException( String.format( "We didnt get anything from the database, the mimetype \"%s\"is unknown.", mimeType ) );
            }
           
            long p = 0l;
            long d = 0l;
            log.debug("obtained resultset");
            
            while(rs.next())
            {
                log.debug("next in rs");
                p = rs.getLong( "processtime" );
                log.debug( String.format( "got p: '%s'", p ) );
                //                log.debug("got element out of resultset");
                d = rs.getLong( "dataamount" );
                log.debug( String.format( "got d: '%s'", d ) );

                if ( d != 0l && p != 0l ) // if either is zero
                { 
                    average_time = ( ( (float)p / d ) * length );
                }
            }
            
            log.debug( String.format( "processtime=%s dataamount=%s, averagetime=%s", p, d, average_time ) );
        }
        finally
        {
            stmt.close();
            con.close();
        }
        
        log.info( String.format( "Obtained average processing time=%s",average_time) );
        
        return average_time;
    }

    
    /**
     * updateEstimate updates the entry in statistics that matches the given mimetype, with the length and time.
     *
     * @param mimeType is the mimetype of the processed object 
     * @param length is the length in bytes of the processed object 
     * @param time is time in millisecs that it took to proces the object
     *
     * @throws ClassNotFoundException if the databasedriver is not found
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     */
    public void updateEstimate( String mimeType, long length, long time ) throws SQLException, ClassNotFoundException
    {
        log.debug( String.format( "UpdateEstimate(mimeType = %s, length = %s, time = %s) called", mimeType, length, time ) );

        Connection con = DBConnection.getConnection();
 
        Statement stmt = null;
        String sqlQuery = String.format( "UPDATE statistics "+
                                         "SET processtime = processtime+%s, dataamount = dataamount+%s "+
                                         "WHERE mimetype = '%s'", time, length, mimeType);
        
        log.debug( String.format( "query database with %s ", sqlQuery ) );

        int rowsUpdated = 0;        
        try
        {      
            stmt = con.createStatement();
            rowsUpdated = stmt.executeUpdate( sqlQuery );
            if( rowsUpdated == 0 ) 
            {
                throw new NoSuchElementException( "The mimetype does not match a known mimetype, couldn't update." ); 
            }
            
            con.commit();
        }
        finally
        {
            stmt.close();
            con.close();
        }
        
        log.info( "estimate Updated" );
    }
}
