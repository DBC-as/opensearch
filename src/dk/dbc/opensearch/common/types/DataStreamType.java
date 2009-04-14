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

import org.apache.log4j.Logger;

public enum DataStreamType 
{	
	OriginalData ( "originalData", "original data" ),
    DublinCoreData( "dublinCoreData", "dublin core data" ),
    AdminData( "adminData", "Administration" ),      
	IndexableData( "indexableData", "data prepared for indexing" );
	
	static Logger log = Logger.getLogger( DataStreamType.class );
	
	String name;
	String description;
	
	
	DataStreamType( String name, String description ) 
	{		
		this.name = name;
		this.description = description;
	}
	
	
	public String getName()
	{
		return this.name;
	}
	
	
	public String getDescription()
	{
		return this.description;
	}


	public static boolean validDataStreamNameType( String nametype )
    {
        DataStreamType DSN = DataStreamType.getDataStreamNameFrom( nametype );
        log.debug( "checking dataStreamName" );
        
        if( DSN == null )
        	return false;
        
        return true;
    }
	
	
    /**
     * @param mime
     * @return
     */
    public static DataStreamType getDataStreamNameFrom( String name )
    {
        DataStreamType DSN = null;
        for (DataStreamType dsn : DataStreamType.values() )
        {
            if( name.equals( dsn.getName() ) )
            {
                DSN = dsn;
            }
        }
        
        return DSN;
    }   
}