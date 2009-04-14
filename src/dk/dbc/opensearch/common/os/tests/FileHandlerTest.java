/**
 * \file FileHandlerTest.java
 * \brief The FileHandlerTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.os.tests;


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


import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;


import java.io.InputStream;
//import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.util.Vector;

import dk.dbc.opensearch.common.os.FileHandler;
//import dk.dbc.opensearch.common.os.FileFilter;
//import dk.dbc.opensearch.common.os.XmlFileFilter;


/** \brief UnitTest for FileHandler **/
//import static org.junit.Assert.*;
//import org.junit.*;

/**
 *
 */
public class FileHandlerTest 
{
    File testdir;
    File file1;
    File file2;
    File file3;
    File file4;
    File file5;
    File file6;
    File file7;

    FilenameFilter mockFileNameFilter1;
    FilenameFilter mockFileNameFilter2;

    FilenameFilter[] fnf1 = new FilenameFilter[1];
    FilenameFilter[] fnf2 = new FilenameFilter[2];


    String testdirName = "";
    Vector<String> testCase;
    /**
     *
     */

    @Before public void setUp(){

    }

    @After public void tearDown(){}

    private void setUpGetFileList() throws IOException {

        /**
         * Making the follwing directory structure to facilitate the test of the filehandler:
         *
         * test-dir
         * |--opensearch-unittest[int]
         *    |--test.xml
         *    |--test.java
         *    |--.test.xml
         *    `--descenddir
         *       |--test.xml
         *       |--test.java
         *       |--.test.xml
         *
         * Where test-dir is the directory retrieved from the java file getTmpFile method.
         */

        File tmp = File.createTempFile("opensearch-unittest","" );
        testdirName = tmp.getAbsolutePath();
        tmp.delete();
        testdir = new File( testdirName );
        testdir.mkdir();
        testdir.deleteOnExit();
        file1 = new File( testdir+"/test.xml");
        file1.createNewFile();
        file1.deleteOnExit();
        file2 = new File( testdir+"/test.java");
        file2.createNewFile();
        file2.deleteOnExit();
        file3 = new File( testdir+"/.test.xml");
        file3.createNewFile();
        file3.deleteOnExit();
        file4 = new File( testdir+"/descend_dir");
        file4.mkdir();
        file4.deleteOnExit();
        file5 = new File( testdir+"/descend_dir/test.xml");
        file5.createNewFile();
        file5.deleteOnExit();
        file6 = new File( testdir+"/descend_dir/test.java");
        file6.createNewFile();
        file6.deleteOnExit();
        file7 = new File( testdir+"/descend_dir/.test.xml");
        file7.createNewFile();
        file7.deleteOnExit();

        mockFileNameFilter1 = createMock( FilenameFilter.class );
        mockFileNameFilter2 = createMock( FilenameFilter.class );

        fnf1[0] = mockFileNameFilter1;
        fnf2[0] = mockFileNameFilter1;
        fnf2[1] = mockFileNameFilter2;

        testCase = new Vector();
    }

    public void tearDownGetFileList() throws IOException {}


    @Test 
    public void testGetFileListNotDirectory() throws IOException 
    {
        setUpGetFileList();
        
        try
        {
            testCase = FileHandler.getFileList( file1.getAbsolutePath(), fnf1, false );
            fail("Should have gotten IllegalArgumentException - Supplied no direcotry for method");
        }
        catch(IllegalArgumentException iae){
            // Expected - intentional
        }
    }
    
    
    @Test 
    public void testGetFileListCase1() throws IOException 
    {
        //case1:descend false, 1 filter
        setUpGetFileList();

        expect( mockFileNameFilter1.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, ".test.xml" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( testdir, "descend_dir" ) ).andReturn( false );

        replay( mockFileNameFilter1 );
        testCase = FileHandler.getFileList( testdirName, fnf1, false );
        verify( mockFileNameFilter1 );

        if( testCase.indexOf( file1.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }
        if( testCase.indexOf( file2.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file2.getAbsolutePath() ) );
        }
    }


    @Test 
    public void testGetFileListCase2() throws IOException 
    {
        //case1:descend false, more than 1 filter

        setUpGetFileList();
        expect( mockFileNameFilter1.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, ".test.xml" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( testdir, "descend_dir" ) ).andReturn( false );

        expect( mockFileNameFilter2.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( testdir, "test.java" ) ).andReturn( false );

        replay( mockFileNameFilter1 );
        replay( mockFileNameFilter2 );

        testCase = FileHandler.getFileList( testdirName, fnf2, false );

        verify( mockFileNameFilter1 );
        verify( mockFileNameFilter2 );

        if( testCase.indexOf( file1.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }
    }

    
    @Test 
    public void testGetFileListCase3() throws IOException 
    {
        //case3:descend true, 1 filter

        setUpGetFileList();

        expect( mockFileNameFilter1.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, ".test.xml" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( testdir, "descend_dir" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( file4, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( file4, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( file4, ".test.xml" ) ).andReturn( false );

        replay( mockFileNameFilter1 );
        testCase = FileHandler.getFileList( testdirName, fnf1, true );

        verify( mockFileNameFilter1 );

        if( testCase.indexOf( file1.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }
        if( testCase.indexOf( file2.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file2.getAbsolutePath() ) );
        }
        if( testCase.indexOf( file5.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }
        if( testCase.indexOf( file6.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file2.getAbsolutePath() ) );
        }
    }

    
    @Test 
    public void testGetFileListCase4() throws IOException 
    {
        //case4:descend true, more than 1 filter

        setUpGetFileList();

        expect( mockFileNameFilter1.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( testdir, ".test.xml" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( testdir, "descend_dir" ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( file4, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( file4, "test.java" ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( file4, ".test.xml" ) ).andReturn( false );

        expect( mockFileNameFilter2.accept( testdir, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( testdir, "test.java" ) ).andReturn( false );
        expect( mockFileNameFilter2.accept( file4, "test.xml" ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( file4, "test.java" ) ).andReturn( false );


        replay( mockFileNameFilter1 );
        replay( mockFileNameFilter2 );

        testCase = FileHandler.getFileList( testdirName, fnf2, true );

        verify( mockFileNameFilter1 );
        verify( mockFileNameFilter2 );

        if( testCase.indexOf( file1.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }
        if( testCase.indexOf( file5.getAbsolutePath() ) < 0 ){
            fail( String.format( "file='%s' should be returned", file1.getAbsolutePath() ) );
        }

    }


    @Test 
    public void testReadFile() throws IOException 
    {
        String teststr = "THIS IS A TEST";

        File tmp = File.createTempFile("opensearch-unittest","" );
        tmp.deleteOnExit();

        FileWriter  fstream = new FileWriter( tmp );
        BufferedWriter out = new BufferedWriter( fstream );
        out.write( teststr );
        //Close the output stream
        out.close();

        InputStream is = FileHandler.readFile( tmp.getAbsolutePath() );

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        while ( ( line = reader.readLine() ) != null ){
            sb.append( line );
        }
        
        is.close();

        assertEquals( sb.toString(), teststr );
    }

    
    @Test 
    public void testReadFileWithNoFile() throws IOException 
    {
        setUpGetFileList();

        try
        {
            InputStream is = FileHandler.readFile( testdir.getAbsolutePath() );
            fail("Should have gotten FileNotFoundException - Did not supply valid filename");
        }
        catch( FileNotFoundException iae )
        {
            // Expected - intentional
        }
    }

    
    @Test 
    public void testGetFile() throws IOException 
    {
        File tmp = File.createTempFile( "opensearch-unittest", "" );
        tmp.deleteOnExit();
        String tmpname = tmp.getAbsolutePath();

        File get = FileHandler.getFile( tmpname );
        assertTrue( get.exists() );
    }
}
