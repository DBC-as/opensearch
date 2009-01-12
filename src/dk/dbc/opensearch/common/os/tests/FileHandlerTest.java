/**
 * \file FileHandlerTest.java
 * \brief The FileHandlerTest class
 * \package tests;
 */

package dbc.opensearch.tools.tests;


import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;


import java.io.InputStream;
import java.io.FileInputStream;
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
import dk.dbc.opensearch.common.os.FileFilter;
import dk.dbc.opensearch.common.os.XmlFileFilter;


/** \brief UnitTest for FileHandler **/


import static org.junit.Assert.*;
import org.junit.*;

/**
 * This Unittest is not completely atomic as it is now - it depends on FileFilter and xmlFileFilter
 */
public class FileHandlerTest {

    FilenameFilter mockFileNameFilter1;
    FilenameFilter mockFileNameFilter2;
    
    FilenameFilter[] fnf1 = new FilenameFilter[1];
    FilenameFilter[] fnf2 = new FilenameFilter[2];

    String testDir = "";
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
        System.out.println( tmp.getAbsolutePath() );
        testDir = tmp.getAbsolutePath();
        tmp.delete();
        File tmpdir = new File( testDir );
        tmpdir.mkdir();
        tmpdir.deleteOnExit();
        File file1 = new File( tmpdir+"/test.xml");
        file1.createNewFile();
        file1.deleteOnExit();
        File file2 = new File( tmpdir+"/test.java");
        file2.createNewFile();
        file2.deleteOnExit();
        File file3 = new File( tmpdir+"/.test.xml");
        file3.createNewFile();
        file3.deleteOnExit();
        File file4 = new File( tmpdir+"/descend_dir");
        file4.mkdir();
        file4.deleteOnExit();
        File file5 = new File( tmpdir+"/descend_dir/test.xml");
        file5.createNewFile();
        file5.deleteOnExit();
        File file6 = new File( tmpdir+"/descend_dir/test.java");
        file6.createNewFile();
        file6.deleteOnExit();
        File file7 = new File( tmpdir+"/descend_dir/.test.xml");
        file7.createNewFile();
        file7.deleteOnExit();
        
        mockFileNameFilter1 = createMock( FilenameFilter.class );
        mockFileNameFilter2 = createMock( FilenameFilter.class );
        
        fnf1[0] = mockFileNameFilter1 ;
        fnf2[0] = mockFileNameFilter1;
        fnf2[1] = mockFileNameFilter2;

        testCase = new Vector();
    }

    public void tearDownGetFileList() throws IOException {

    }
    @Test public void testGetFileListCase1() throws IOException {
        //case1:descend false, 1 filter

        setUpGetFileList();        
        
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
                
        replay( mockFileNameFilter1 );
        testCase = FileHandler.getFileList( testDir, fnf1, false );
        verify( mockFileNameFilter1 );

        assertEquals( testDir+"/test.xml", testCase.get( 0 ) );        
        assertEquals( testDir+"/test.java", testCase.get( 1 ) );        
    }
    
    @Test public void testGetFileListCase2() throws IOException {
        //case1:descend false, more than 1 filter

        setUpGetFileList();        
        
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        
        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
                
        replay( mockFileNameFilter1 );
        replay( mockFileNameFilter2 );

        testCase = FileHandler.getFileList( testDir, fnf2, false );
                
        verify( mockFileNameFilter1 );
        verify( mockFileNameFilter2 );

        assertEquals( testDir+"/test.xml", testCase.get( 0 ) );        
    }

    @Test public void testGetFileListCase3() throws IOException {
        //case3:descend true, 1 filter
        
        setUpGetFileList();
        
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        
        replay( mockFileNameFilter1 );
        testCase = FileHandler.getFileList( testDir, fnf1, true );
       
        verify( mockFileNameFilter1 );
        
        assertEquals( testDir+"/test.xml", testCase.get( 0 ) );        
        assertEquals( testDir+"/test.java", testCase.get( 1 ) );
        assertEquals( testDir+"/descend_dir/test.xml", testCase.get( 2 ) );        
        assertEquals( testDir+"/descend_dir/test.java", testCase.get( 3 ) );                 
    }

    @Test public void testGetFileListCase4() throws IOException {
        //case4:descend true, more than 1 filter

        setUpGetFileList();        
        
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );        
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter1.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );

        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( true );
        expect( mockFileNameFilter2.accept( isA( File.class ), isA( String.class ) ) ).andReturn( false );
                
        replay( mockFileNameFilter1 );
        replay( mockFileNameFilter2 );

        testCase = FileHandler.getFileList( testDir, fnf2, true );
                
        verify( mockFileNameFilter1 );
        verify( mockFileNameFilter2 );

        assertEquals( testDir+"/test.xml", testCase.get( 0 ) );
        assertEquals( testDir+"/descend_dir/test.xml", testCase.get( 1 ) );        
    }


    @Test public void testReadFile() throws IOException {

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
    
    @Test public void testGetFile() throws IOException {
        File tmp = File.createTempFile("opensearch-unittest","" );   
        tmp.deleteOnExit();
        String tmpname = tmp.getAbsolutePath();
        
        File get = FileHandler.getFile( tmpname );
        assertTrue( get.exists() );
    }
}
