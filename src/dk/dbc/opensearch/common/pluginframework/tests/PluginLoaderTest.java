/** \brief UnitTest for PluginLoader */
package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginClassLoader;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;

/**
 *
 */
public class PluginLoaderTest {

    ClassLoader pcl;
    PluginLoader pl;
    PluginClassLoader mockPCL;
    Boolean noException;
    Class replaceClass;
    IPluggable mockIPluggable;
    IPluggable testIPlug;
    /*
    public final class ReplacementClass{
        public Object newInstance(){
            return mockIPluggable;
        }
    }
    */
    /**
     *
     */
    @Before public void SetUp()throws Exception {

      
        noException = true;
        mockPCL = createMock( PluginClassLoader.class );
        pcl = new PluginClassLoader();
        //  replaceClass = pcl.loadClass( "dk.dbc.opensearch.common.pluginframework.IPluggable" );
        mockIPluggable = createMock( IPluggable.class );
        //Mockit.redefineMethods( Class.class, ReplacementClass.class);
    }

    /**
     *
     */
    @After public void TearDown() {
        pl = null;
        //Mockit.restoreAllOriginalDefinitions();
    }

    /**
     *
     */
    @Test public void constructorTest() {
        try{
            pl = new PluginLoader( mockPCL );
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
        
        
      
        
        String testClassString = "dk.dbc.opensearch.common.pluginframework.IPluggable";
        try{
        pl = new PluginLoader( pcl );        
        testIPlug = pl.loadPlugin( testClassString );
        }catch( Exception e){
            noException = false;
            e.printStackTrace();
        }
        assertTrue( noException );


    }
}