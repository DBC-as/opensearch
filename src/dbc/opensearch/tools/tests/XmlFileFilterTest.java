package dbc.opensearch.tools.tests;
/** \brief UnitTest for FileFilter */

import static org.junit.Assert.*;
import org.junit.*;

import dbc.opensearch.tools.XmlFileFilter;

import java.io.File;
import java.io.IOException;

/**
 *
 */
public class XmlFileFilterTest {

    XmlFileFilter xff;
    static String dir = ".shouldnotbeaccepted"; 
    static File dummy = null;

    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before public void SetUp() {
        dummy = new File( dir );
        dummy.mkdir();
        xff = new XmlFileFilter();
    }

    /**
     * After each test the dummy directory is removed
     */
    @After public void TearDown() {
        try{
            (new File( dir )).delete();
        }catch( Exception e ){}
    }

    /**
     * Files with .xml suffix should be accepted
     */
    @Test public void testXmlNameAccepted() {
        assertTrue( xff.accept( dummy, "test.xml" ) );
    }

    /**
     * Files without .xml suffix should not be accepted
     */
    @Test public void testNoXmlSuffixNotAccepted(){
        assertFalse( xff.accept( dummy, "noxmlsuffix" ) );
    }

    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        new File( dummy, dir).mkdir();
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