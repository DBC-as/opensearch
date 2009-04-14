/**
 * \file FileHarvestTest.java
 * \brief The FileHarvestTest class
 * \package tests;
 */
package dk.dbc.opensearch.components.harvest.tests;


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


//import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.config.HarvesterConfig;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.components.harvest.FileHarvest;

import javax.xml.stream.*;

import java.io.File;
import java.io.IOException;
import java.io.FileOutputStream;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;



/** \brief UnitTest for FileHarvest */
public class FileHarvestTest {

    FileHarvest fileHarvest;
    File harvestdir;

    XMLOutputFactory factory;
    XMLStreamWriter writer;
    @Before public void SetUp() throws Exception 
    { 
        factory = XMLOutputFactory.newInstance();       


    }

    
    @After public void TearDown() { }

    
   @Ignore
    @Test
    public void testCheckFormat() throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
    	String pollTestPath = HarvesterConfig.getFolder();
    	File pollTestFile = new File( pollTestPath );
    	FileHarvest fh = new FileHarvest( pollTestFile );
    }
    
    
    /**
     * @throws IOException 
     * @throws SAXException 
     * @throws ParserConfigurationException 
     * @throws ConfigurationException 
     * 
     */
@Ignore
    @Test(expected = IllegalArgumentException.class) 
        public void testConstructorException() throws ParserConfigurationException, SAXException, IOException, ConfigurationException
    {
        harvestdir = new File( "test" );
            fileHarvest = new FileHarvest( harvestdir );
    }
    
@Ignore    
    @Test 
        public void testConstructor() throws IOException, IllegalArgumentException, ParserConfigurationException, SAXException, ConfigurationException, XMLStreamException
    {        
        //
        //testdir = File.createTempFile("opensearch-unittest","" );
        //String testdirName = testdir.getAbsolutePath();
        //testdir.delete();
        //testdir = new File( testdirName );
        //testdir.mkdir();
        //testdir.deleteOnExit();
        //
        // File testdir1 = new File( testdir + "/test.dir/" );
//         testdir1.mkdir();
//         testdir1.deleteOnExit();

//         File testFile1 = new File( testdir + "/testfile" );
//         testFile1.createNewFile();
//         testFile1.deleteOnExit();

//         File testdir2 = new File( testdir + "/test.dir/test.dir2" );
//         testdir2.mkdir();
//         testdir2.deleteOnExit();

//         File testdir3 = new File( testdir + "/test.dir/test.dir3/" );
//         testdir3.mkdir();
//         testdir3.deleteOnExit();
        
//         File testFile2 = new File( testdir + "/test.dir/test.dir2/testfile2" );
//         testFile2.createNewFile();
//         testFile2.deleteOnExit();
        
//         File testFile3 = new File( testdir + "/test.dir/test.dir2/testfile3" );
//         testFile3.createNewFile();
//         testFile3.deleteOnExit();

//         File testFile4 = new File( testdir + "/test.dir/test.dir3/testfile4" );
//         testFile4.createNewFile();
//         testFile4.deleteOnExit();
        
//         File testdir4 = new File( testdir + "/test.dir/test.dir3/testdir4" );
//         testdir4.mkdir();
//         testdir4.deleteOnExit();

        harvestdir = new File( "harvesttestdir" );
        harvestdir.mkdir();
        
        File sub1 = new File( harvestdir, "sub1" );
        sub1.mkdir();
        //File sub2 = new File( harvestdir, "sub2" );
        //sub2.mkdir();

        File format1 = new File( sub1, "format1" );
        format1.mkdir();
        File sub1format1File = new File( format1, "sub1format1File" );
        //        sub1format1File.mkdir();
        //Document testDoc = db.parse( sub1format1File );
        //Element root = testDoc.createElement( "test" );

        writer = factory.createXMLStreamWriter( new FileOutputStream( sub1format1File ), "UTF-8" );
        writer.writeStartDocument("UTF-8", "1.0"); //(encoding, version)
        writer.writeStartElement("Text");
        writer.writeCharacters( "testdata" );
        writer.writeEndElement();
        writer.writeEndDocument();



        fileHarvest = new FileHarvest( harvestdir );
        fileHarvest.start();
        Vector<DatadockJob> result1 = fileHarvest.getJobs();
        assertTrue( result1.size() == 0 );
        
        Vector<DatadockJob> result2 = fileHarvest.getJobs();
        System.out.println( result2.size() );
       
        System.out.println( String.format( "length of file: %s", sub1format1File.length() ) );
        String[] files = format1.list();
        for( int i = 0; i < files.length; i++ )
        {
            System.out.println( String.format( "File %s : %s" , i, files[i] ) );
        }
        System.out.println( String.format( "length of file: %s", sub1format1File.length() ) );
        //assertTrue( result2.size() == 1 );

        format1.delete();
        sub1.delete();
        harvestdir.delete();
        
    }
}
