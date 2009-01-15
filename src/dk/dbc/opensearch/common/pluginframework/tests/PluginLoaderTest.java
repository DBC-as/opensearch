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
public class PluginLoaderTest {

    FileHandler fh;
    ClassLoader pcl;
    PluginLoader pl;
    PluginClassLoader mockPCL;
    Boolean noException;
    //Class replaceClass;
    IPluggable mockIPluggable;
    IPluggable testIPlug;
    String testClassString;
    File mockFile;    

    public final class ReplaceClass{
        public Object newInstance(){
            return mockIPluggable;
        }
    }
    public class ReplaceFileHandler{
        public File getFile( String name ){
            return mockFile;
        }
    }
    
    /**
     *
     */
    @Before public void SetUp()throws Exception {

        testClassString = "dk.dbc.opensearch.common.pluginframework.IPluggable";
        noException = true;

        mockPCL = createMock( PluginClassLoader.class );
        mockFile = createMock( File.class );
        //mockIPluggable = createMock( IPluggable.class );
       
        Mockit.redefineMethods( FileHandler.class, ReplaceFileHandler.class );
        fh = new FileHandler();
        //Is it nessecary to use a classloader to get a Class object?
        //pcl = new PluginClassLoader();
        //Mockit.redefineMethods( Class.class, ReplaceClass.class );
        //replaceClass = pcl.loadClass( testClassString );    
    }

    /**
     *
     */
    @After public void TearDown() {
        pl = null;
        Mockit.restoreAllOriginalDefinitions();
    }

    /**
     *
     */
    @Test public void constructorTest() {
        try{
            pl = new PluginLoader( pcl, fh );
        }catch( Exception e){
            noException = false;
        }
        assertTrue( noException );
    }
    /**
     *
     */
    @Ignore
    @Test public void loadPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        
        try{
            pl = new PluginLoader( pcl, fh );        
        testIPlug = pl.loadPlugin( testClassString );
        }catch( Exception e){
            noException = false;
            e.printStackTrace();
        }
        assertTrue( noException );


    }
}