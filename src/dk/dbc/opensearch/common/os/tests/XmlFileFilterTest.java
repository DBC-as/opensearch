package dk.dbc.opensearch.common.os.tests;/** \brief UnitTest for FileFilter */

import static org.junit.Assert.*;
import org.junit.*;

import dk.dbc.opensearch.common.os.XmlFileFilter;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class XmlFileFilterTest {

    XmlFileFilter xff;
    static File xmlFile;
    static File xmlDir;
    static File otherFile;

    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before public void SetUp() {
        xmlDir = new File( "xmlDir" );
        xmlDir.mkdir();
        xmlFile = new File( xmlDir, ".xml" );
        otherFile = new File( xmlDir, "notXml" );
        xff = new XmlFileFilter();
    }

    /**
     * After each test the xmlDir directory is removed
     */
    @After public void TearDown() {
        try{
            xmlDir.delete();
        }catch( Exception e ){}
    }

    /**
     * Files with .xml suffix should be accepted
     */
    @Test public void testXmlNameAccepted() {

        assertTrue( xff.accept( xmlDir, xmlFile.getName() ) );
    }

    /**
     * Files without .xml suffix should not be accepted
     */
    @Test public void testNoXmlSuffixNotAccepted(){
        assertFalse( xff.accept( xmlDir, otherFile.getName() ) );
    }

    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        otherFile.mkdir();
        assertFalse( xff.accept( xmlDir, otherFile.getName() ) );
        otherFile.delete();
    }

    /**
     * if dir- or filename is null, java.io.File must throw
     */
    @Test(expected = NullPointerException.class) 
    public void testNullValueForFilenameShouldFail(){
        String hat = null;
        xff.accept( xmlDir, hat );
    }
    @Test(expected = NullPointerException.class) 
    public void testNullValueForDirShouldFail(){
        xmlDir = null;
        xff.accept( xmlDir, xmlFile.getName() );

    
    }
}