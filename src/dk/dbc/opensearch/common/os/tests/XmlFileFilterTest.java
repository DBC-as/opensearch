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
    static String acceptedName = "test.xml";
    static String dir = ".shouldnotbeaccepted";
    static File acceptedFile = null;
    static File dummy = null;

    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before public void SetUp() {
        dummy = new File( dir );
        dummy.mkdir();
        acceptedFile = new File( acceptedName );
        xff = new XmlFileFilter();
    }

    /**
     * After each test the dummy directory is removed
     */
    @After public void TearDown() {
        try{
            dummy.delete();
            acceptedFile.delete();
        }catch( Exception e ){}
    }

    /**
     * Files with .xml suffix should be accepted
     */
    @Test public void testXmlNameAccepted() {

        assertTrue( xff.accept( acceptedFile, acceptedName ) );
    }

    /**
     * Files without .xml suffix should not be accepted
     */
    @Test public void testNoXmlSuffixNotAccepted(){
        assertFalse( xff.accept( acceptedFile, dir ) );
    }

    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        assertFalse( xff.accept( dummy, dir ) );
    }

    /**
     * if dir- or filename is null, java.io.File must throw
     */
    @Test(expected = NullPointerException.class) 
    public void testNullValueForFilenameShouldFail(){
        assertFalse( xff.accept( new File( "idontexist" ), null ) );
    }

}