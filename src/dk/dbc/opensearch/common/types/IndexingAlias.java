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

/**
 * Type for telling what alias should be used to index the data
 */
public enum IndexingAlias 
{
    Article ( "article", "the docbook/ting xml alias" ),
        Danmarcxchange ( "danmarcxchange", "alias for marc posts" ),
        DC ( "dc","data from DR in Dublin Core format" );

    static Logger log = Logger.getLogger( IndexingAlias.class );

    private String name;
    private String description;

    IndexingAlias( String name, String description )
    {
        this.name = name;
        this.description = description;
    }

    /**
     * Gets the canonical name of the type. This name is used as an
     * alias in the indexing process and must be unique within this
     * Enum.
     * @return the name of the type as a String
     */
    public String getName()
    {
        return name;
    }

    /**
     * Gets the human-readable description of the type
     * @return the description of the type as a String
     */
    public String getDescription()
    {
        return description;
    }

    /**
     * @param alias The alias to validate against the available types
     * 
     * @return true if the alias is in the types, false otherwise  
     */
    public static boolean validIndexingAlias( String alias )
    {
        log.debug( String.format( "Getting indexing alias from string %s", alias ) );
        IndexingAlias IA = IndexingAlias.getIndexingAlias( alias );
        
        if( IA == null )
        {
            return false;
        }
        return true;
    }
	
	
    /**
     * @param name, the name of the wanted IndexingAlias
     * @return the IndexingAlias that matched the name given or null if none matched
     */
    public static IndexingAlias getIndexingAlias( String name )
    {
        IndexingAlias IA = null;
        for ( IndexingAlias ia : IndexingAlias.values() )
        {
            if( name.equals( ia.getName() ) )
            {
                IA = ia;
            }
        }
        
        return IA;
    }   
}