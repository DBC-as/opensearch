/** \brief UnitTest for PluginLoader */
package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginClassLoader;
import dk.dbc.opensearch.common.os.FileHandler;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

import java.io.File;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;

/**
 *
 */
public class PluginLoaderTest{


    //FileHandler fh;
    ClassLoader pcl;
    PluginLoader pl;
    //    PluginClassLoader mockPCL;
    Boolean noException;
    //static Class mockitClass = null;
    IPluggable testIPlug;
    String testClassString;
    static File mockFile;

    /**
     *  class that implements the IPluggable interface
     *
    public class ReplacePlug implements IPluggable{
        public ReplacePlug(){}
        public void init(){}
        public String getPluginTask(){
            return "testTask";
        }
        public String getPluginSubmitter(){
            return "testSubmitter";
        }
        public String getPluginFormat(){
            return "testFormat";
        } 
        
    }
    //ReplacePlug RPPlug;
    */
    /**
     * class that replaces the functionality of the FileHandler class
     *
     */
    public static class ReplaceFileHandler{
        public static File getFile( String path ){
            return mockFile;
        }
    }
    /**
     *
     */
    @Before public void SetUp()throws Exception {

        testClassString = "dk.dbc.opensearch.common.pluginframework.tests.TestPlugin";
        noException = true;
        //mockFile = createMock( File.class );
        //  mockPCL = createMock( PluginClassLoader.class );
        //mockIPluggable = createMock( IPluggable.class );
        //RPPlug = new RePlacePlug;
        //    Mockit.redefineMethods( Class.class, ReplaceClass.class );
        //Mockit.redefineMethods( FileHandler.class, ReplaceFileHandler.class );

        pcl = new PluginClassLoader();
    }
    /**
     *
     */
    @After public void TearDown() {
        pl = null;
        //  reset( mockPCL );
        //Mockit.restoreAllOriginalDefinitions();
    }

    /**
     *
     */
    @Test public void constructorTest() {
        try{
            pl = new PluginLoader( pcl );
        }catch( Exception e){
            noException = false;
        }
        assertTrue( noException );
    }
    /**
     *
     */

    @Test public void loadPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException {

        // expect( mockPCL.loadClass( isA( String.class ) ) ).andReturn( dk.dbc.opensearch.common.pluginframework.tests.PluginLoaderTest$ReplacePlug );

        //replay( mockPCL );
        try{
            pl = new PluginLoader( pcl );
            testIPlug = pl.loadPlugin( testClassString );
        }catch( Exception e){
            noException = false;
            e.printStackTrace();
        }
        assertTrue( noException );

        //verify ( mockPCL );

    }
}