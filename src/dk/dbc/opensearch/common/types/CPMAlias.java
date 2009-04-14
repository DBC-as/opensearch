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


import dk.dbc.opensearch.common.config.CompassConfig;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CPMAlias
{
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    Document cpmDocument;
    String xsemFile;
    NodeList cpmNodeList;

    
    public CPMAlias() throws ParserConfigurationException, SAXException, IOException, ConfigurationException
    {    	
    	docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        xsemFile = CompassConfig.getXSEMPath();
        cpmDocument = docBuilder.parse( xsemFile );
        Element xmlRoot = cpmDocument.getDocumentElement();
        cpmNodeList = xmlRoot.getElementsByTagName( "xml-object" );
    }
    

    public boolean isValidAlias( String alias ) throws ParserConfigurationException, SAXException, IOException
    {
        
        for( int i = 0; i < cpmNodeList.getLength(); i++ )
        {
            Element aliasNode = (Element)cpmNodeList.item( i );
            String aliasValue = aliasNode.getAttribute( "alias" );
            if ( aliasValue.equals( alias ) )
            {
                return true;
            }
        }

        return false;
    }
}
