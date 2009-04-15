/** \brief UnitTest for PluginFileFilter */
package dk.dbc.opensearch.common.os.tests;

import dk.dbc.opensearch.common.os.PluginFileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PluginFileFilterTest {

    /**
     *
     */
    PluginFileFilter pff;
    static File pluginDir = new File( "pluginDir" );
    static File pluginFile;
    static File otherFile;
    static File dir2; 
    

    @Before public void SetUp() 
    {
        pff = new PluginFileFilter();
        pluginDir.mkdir();
        pluginFile = new File( pluginDir, "test.plugin"); 
        otherFile = new File( pluginDir, "notplugin" );
    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pluginDir.delete();
    }

    /**
     * 
     */
    @Test public void testAcceptsPluginFiles()
    {
        assertTrue( pff.accept( pluginDir, pluginFile.getName() ) );
    }

@Test public void testRejectsNonPluginFiles()
    {
        assertFalse( pff.accept( pluginDir, otherFile.getName() ) );
    }

    @Test public void testRejectsDirectories()
    {
        dir2 = new File( pluginDir, "testdir" );
        dir2.mkdir();
        //System.out.println( (new File( pluginDir, dir2.getName() )).isDirectory() );
        assertFalse( pff.accept( pluginDir, dir2.getName() ) );
        dir2.delete();
    }

    @Test( expected = NullPointerException.class ) 
    public void testNPException1()
    {
        otherFile = null;
        pff.accept( otherFile, pluginFile.getName() );
    }
    @Test( expected = NullPointerException.class )  
        public void testNPException2()
    {
        String hat = null;
        pff.accept( pluginDir, hat );
    }
    
}