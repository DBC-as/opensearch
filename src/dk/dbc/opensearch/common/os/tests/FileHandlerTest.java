/**
 * \file FileHandlerTest.java
 * \brief The FileHandlerTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.os.tests;
import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import dk.dbc.opensearch.tools.FileHandler;
import dk.dbc.opensearch.tools.FileFilter;
import dk.dbc.opensearch.tools.XmlFileFilter;


/** \brief UnitTest for FileHandler **/


import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class FileHandlerTest {

     /**
      * 
      */
    

    @Test public void testGetFileList() throws IOException {
       
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
        String tmpname = tmp.getAbsolutePath();
        tmp.delete();
        File tmpdir = new File( tmpname );
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

        FilenameFilter filter1 = new FileFilter();
        
        FilenameFilter filter2 = new XmlFileFilter();

        // case 1:
        Vector<String> case1 = new Vector();
        FilenameFilter[] fnf = {filter1, filter2 };
        
        case1 = FileHandler.getFileList( tmpname, fnf, false );
        
       
        // case 2:
        // case 3:
        
        
        
    }
}
