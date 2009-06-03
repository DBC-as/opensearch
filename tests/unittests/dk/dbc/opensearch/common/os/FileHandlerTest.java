/**
 * \file FileHandlerTest.java
 * \brief The FileHandlerTest class
 * \package dk.dbc.opensearch.common.os;
 */

package dk.dbc.opensearch.common.os;

/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


import dk.dbc.opensearch.common.os.FileHandler;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;

import java.io.InputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.BufferedWriter;
import java.io.BufferedReader;
import java.io.InputStreamReader;

import java.io.IOException;
import java.util.Vector;


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


    @Before 
    public void setUp() { }


    @After 
    public void tearDown(){}


    private void setUpGetFileList() throws IOException 
    {
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
