package dk.dbc.opensearch.common.types.tests;


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
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.CargoObjectInfo;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * The getTimestamp method is not tested
 */

public class CargoObjectInfoTest
{
    CargoMimeType cmt;
    CargoObjectInfo coi;
    CargoContainer cc;
    
    String test_submitter = "DBC";
    String test_format = "test_format";
    String test_lang = "DA";

    
    @Before 
    public void SetUp() throws UnsupportedEncodingException, IOException
    {
        cmt =  CargoMimeType.TEXT_XML;
        cc = new CargoContainer();
        
        String teststring = "æøå";
        //InputStream data = new ByteArrayInputStream( teststring.getBytes( ) );
        byte[] data = teststring.getBytes( "UTF-8" );
    	DataStreamType dataStreamName = DataStreamType.OriginalData;
        cc.add( dataStreamName, test_format, test_submitter, test_lang, cmt.getMimeType(), data );    	
    }
    
    
    @Test
    public void testGetDataStreamName()
    {
    	for ( CargoObject co : cc.getData() )
    	{
    		assertEquals( DataStreamType.OriginalData, co.getDataStreamName( ) );
    	}
    }
    
    
    @Test 
    public void testCorrectnessOfgetFormat() 
    {
        for( CargoObject co : cc.getData() )
        	assertTrue( test_format.equals( co.getFormat() ) );
    	       
    }
    
    
    @Test 
    public void testCorrectnessOfgetSubmitter() 
    {    	
    	for( CargoObject co : cc.getData() )
    		assertTrue( test_submitter.equals( co.getSubmitter() ) );
    }
    
    
    @Test 
    public void testCorrectnessOfgetMimeType() 
    {
        for( CargoObject co : cc.getData() )
        	assertTrue( cmt.getMimeType().equals( co.getMimeType() ) );
    	       
    }
    
    
    @Test
    public void testCheckSubmitter()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.checkSubmitter( test_submitter ) );
    }
    
    @Test
    public void testCheckSubmitterInvalid()
    {
    	for( CargoObject co : cc.getData() )
    		assertFalse( co.checkSubmitter( "invalid" ) );
    }
    
    @Test
    public void testCheckLanguageValid()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.checkLanguage( test_lang ) );
    }
     
    @Test
    public void testCheckLanguageInvalid()
    {
    	for( CargoObject co : cc.getData() )
    		assertFalse( co.checkLanguage( "invalid" ) );
    }
    
    @Test
    public void testValidMimetype()
    {
    	for( CargoObject co : cc.getData() )
    		assertTrue( co.validMimetype( cmt.getMimeType() ) );
    }
}
