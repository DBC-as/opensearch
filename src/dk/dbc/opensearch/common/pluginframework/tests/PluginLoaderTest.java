/** \brief UnitTest for PluginLoader */
package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginClassLoader;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;

/**
 *
 */
public class PluginLoaderTest {

    PluginLoader pl;
    PluginClassLoader mockPCL;
    Boolean noException;
    Class mockClass;
    IPluggable mockIPluggable;
    IPluggable testIPlug;
    /**
     *
     */
    @Before public void SetUp() {

        System.out.print(Class.class);
        noException = true;
        mockPCL = createMock( PluginClassLoader.class );
        mockClass = createMock( Class.class );
        mockIPluggable = createMock( IPluggable.class );
    }

    /**
     *
     */
    @After public void TearDown() {
        pl = null;
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
    @Test public void loadPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException {
        
        
        expect( mockPCL.loadClass( isA( String.class ) ) ).andReturn( mockClass );
        expect( mockClass.newInstance() ).andReturn( mockIPluggable );
        try{
        pl = new PluginLoader( mockPCL );        
        testIPlug = pl.loadPlugin( "testString" );
        }catch( Exception e){
            noException = false;
        }
        assertTrue( noException );
    }
}