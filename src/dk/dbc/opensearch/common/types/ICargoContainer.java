package dk.dbc.opensearch.common.types;


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

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.IOException;

import java.util.List;

/**
 * @file   ICargoContainer.java
 * @author Steen Manniche <stm@dbc.dk>
 * @date   Wed Apr  1 18:24:59 2009
 * 
 * @brief The CargoContainer interface defines the interactions that
 * can be made with a CargoContainer. The CargoContainer holds zero or
 * more CargoObjects and the methods defined in the CargoContainer
 * aims to provide simple and uniform access to the data contained in
 * the CargoContainer.
 * 
 * 
 */

public interface ICargoContainer
{


    /** 
     * Adds a 'datastream' to the CargoContainer; a datastream is any
     * kind of data which, for the duration of the CargoContainer
     * object to which it is attached will be treated as binary
     * data. To ensure (and guarantee) that the program does not
     * meddle with the data, it is added as a byte[] and returned as a
     * byte[]. No attempts are made to interpret the contained data at
     * any times.
     *      
     * The returned id uniquely identifies the data and makes it
     * available as a CargoObject structure that encapsulates the
     * information given to this method through the getCargoObject()
     * and getCargoObjects().
     * 
     * 
     * @param dataStreamName 
     * @param format 
     * @param submitter 
     * @param language 
     * @param mimetype 
     * @param data 
     * 
     * @return a unique id identifying the submitted data
     */
    public int add( DataStreamType dataStreamName, 
                     String format, 
                     String submitter, 
                     String language, 
                     String mimetype, 
                     byte[] data ) 
        throws IOException;


    /** 
     * Gets a specific CargoObject based on the id that was returned
     * from the add method. Please note, that if the id does not map
     * to a CargoObject, the method returns null.
     * 
     * @param id The id returned from the add method
     * 
     * @return CargoObject or a null CargoObject if id isn't found
     */    
    public CargoObject getCargoObject( int id );


    /** 
     * Based on the DataStreamType, the first CargoObject matching the
     * type is returned. This method should only be used, if you know
     * that there is exactly one CargoObject with the type in the
     * CargoContainer. If there are more, or if you are unsure, please
     * use the getCargoObjects() method instead. Use the
     * getCargoObjectCount() method to find out how many CargoObjects
     * matching a specific DataStreamType that resides in the
     * CargoContainer.
     * 
     * @param type The DataStreamType to find the CargoObject from
     * 
     * @return The first CargoObject that matches the DataStreamType
     */
    public CargoObject getCargoObject( DataStreamType type);


    /** 
     * Returns a List of CargoObjects that matches the
     * DataStreamType. If you know that there are only one CargoObject
     * matching the DataStreamType, use getCargoObject() instead. If
     * no CargoObjects match the DataStreamType, this method returns
     * null.
     * 
     * @param type The DataStreamType to find the CargoObject from
     * 
     * @return a List of CargoObjects or a null List if none were
     * found
     */
    public List<CargoObject> getCargoObjects( DataStreamType type );


    /** 
     * Returns a List of all the CargoObjects that are contained in
     * the CargoContainer. If no CargoObjects are found, a null List
     * object is returned
     * 
     * @return a List of all CargoObjects from the CargoContainer or a
     * null List object if none are found
     */
    public List<CargoObject> getCargoObjects();


    /** 
     * Get the count of all CargoObjects that have type as their
     * DataStreamType.
     * 
     * @param type The DataStreamType to match in the CargoObjects
     * 
     * @return the count of CargoObjects matching the type
     */
    public int getCargoObjectCount( DataStreamType type );


    /** 
     * Get the total count of CargoObjects in the CargoContainer
     * 
     * @return the count of CargoObjects matching the type
     */
    public int getCargoObjectCount();


    
}