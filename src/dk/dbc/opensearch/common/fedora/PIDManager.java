/**
 * \file PIDManager.java
 * \brief The PIDManager class
 * \package fedora;
 */
package dk.dbc.opensearch.common.fedora;


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


import dk.dbc.opensearch.common.config.PidManagerConfig;

import java.util.HashMap;
import java.util.Vector;
import java.util.Arrays;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


public class PIDManager  extends FedoraHandle 
{
    static Logger log = Logger.getLogger( PIDManager.class );

    
    HashMap <String, Vector< String > > pidMap;
    NonNegativeInteger numPIDs;


    /**
     * Constructor for the PIDManager. Gets fedora connection inforamtion from configuration
     */
    public PIDManager() throws ConfigurationException, ServiceException, java.net.MalformedURLException, java.io.IOException
    {
    	super();
    	
        log.debug( "Constructor() called" );
     
        numPIDs =  new NonNegativeInteger( PidManagerConfig.getNumberOfPidsToRetrieve() );
        pidMap = new HashMap <String, Vector< String > >();
    }


    /**
     * this Method provides a new PID for a digital object to store it
     * in the repository.
     * 
     * @param prefix The prefix for the new PID
     * 
     * @returns The next PID
     */
    public String getNextPID( String prefix ) throws MalformedURLException, ServiceException, RemoteException, IOException
    {
        log.debug( String.format( "getNextPid(prefix='%s') called", prefix ) );

        Vector< String > prefixPIDs = null;
        
        if( pidMap.containsKey( prefix ) ){ // checks whether we already retrieved PIDs
            prefixPIDs = pidMap.get( prefix );
            
            if( prefixPIDs.isEmpty() ){ // checks if there are any PIDs left
                log.debug( "Used all the PIDs, retrieving new PIDs" );
                prefixPIDs = retrievePIDs( prefix );                
            }   
            pidMap.remove( prefix );
        }
        else{
            log.debug( "No PIDs for this namespace, retrieving new PIDs" );
            prefixPIDs = retrievePIDs( prefix );
        }
        
        String newPID = (String) prefixPIDs.remove( 0 );
        pidMap.put( prefix, prefixPIDs );
        
        log.debug( String.format( "returns PID='%s'", newPID ) );
        return newPID;
    }    

    
    /**
     * Method for retrieving new PIDs from the fedoraRepository
     * 
     * @param prefix The prefix for the new PID
     * 
     * @returns a vector containing new PIDs for the given namespace
     */
    private Vector< String > retrievePIDs( String prefix ) throws MalformedURLException, ServiceException, RemoteException, IOException
    {
        log.debug( String.format( "retrievePIDs(prefix='%s') called", prefix ) );
        log.debug( String.format( "Calling through super.dem.getNextPID( %s, %s): fem is %s", numPIDs, prefix, fem ) );

        Vector< String > pidlist = new Vector< String >( Arrays.asList( super.fem.getNextPID( numPIDs, prefix ) ) );

        log.debug( String.format( "Got pidlist=%s",pidlist.toString() ) );

        return pidlist;
    }
}
