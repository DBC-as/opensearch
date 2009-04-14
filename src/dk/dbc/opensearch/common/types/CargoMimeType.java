/**
 * \file CargoMimeType.java
 * \brief The CargoMimetype enum
 * \package datadock
 */
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
 * \ingroup datadock
 * \brief Enum to control the possible values of mimetypes that we can
 * handle. This is a subset of the official mimetypes as listed in
 * /etc/mime.types.
 */
public enum CargoMimeType
{
	/** represents known mimetypes. All handler registrations must use
     * mimetypes defined here. Mimetypes from /etc/mime.types
     */
    TEXT_XML( "text/xml", "XML Document"),
    APPLICATION_PDF( "application/pdf", "PDF Document" );

	static Logger log = Logger.getLogger( CargoMimeType.class );
	
    private final String mimetype;
    private final String description;


    CargoMimeType( String mimetype, String description )
    {
        this.mimetype    = mimetype;
        this.description = description;
    }


    /**
     * Returns The description of the mimetype.
     *
     * @returns The description of the mimetype.
     */
    public String getDescription()
    {
        return this.description;
    }

    
    /**
     * use instanceOfCargoMimeType.getMimeType() to get the (official)
     * name of the mimetype
     *
     * @returns The mimetype
     */
    public String getMimeType()
    {
        return this.mimetype;
    }
    
    
    public static boolean validMimetype( String mimetype )
    {
        CargoMimeType CMT = CargoMimeType.getMimeFrom( mimetype );
        log.debug( "checking mimetype" );
        
        if( CMT == null )
        	return false;
        
        return true;
    }


    /**
     * @param mime
     * @return
     */
    public static CargoMimeType getMimeFrom( String mime )
    {
        CargoMimeType CMT = null;
        for (CargoMimeType cmt : CargoMimeType.values() )
        {
            if( mime.equals( cmt.getMimeType() ) )
            {
                CMT = cmt;
            }
        }
        
        return CMT;
    }
}