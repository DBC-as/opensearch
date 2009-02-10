/**
 * \file PluginResolverTest.java
 * \brief UnitTest for thre PluginResolver
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginID;
import dk.dbc.opensearch.common.pluginframework.PluginFinder;
import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;

import java.io.FileNotFoundException;

import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

/**
 * class for testing the PluginResolver
 */
public class PluginResolverTest {

    PluginResolver PR = null;
    PluginID mockPluginID;

    static String staticString = "staticString";
    static TestPlugin mockPlugin = createMock( TestPlugin.class );    

    /**
     * The class to mock the PluginFinder
     */
    public static class ReplacePluginFinder{
        public ReplacePluginFinder( DocumentBuilder docB, String path ){
            //System.out.print(" hep finder \n");
        }
        public String getPluginClassName( int key ){
            return staticString;
        }
    }
    
    /**
     * The class to mock the PluginLoader
     */
    public static class ReplacePluginLoader{
        public ReplacePluginLoader( ClassLoader pcl ){
            //System.out.print(" hep loader \n");
           
        }
        public IPluggable getPlugin( String className ){
            return (IPluggable)mockPlugin;
        } 
    }

    @Before public void setUp() {
        
        mockPluginID = createMock( PluginID.class );

        Mockit.redefineMethods( PluginLoader.class, ReplacePluginLoader.class );
        Mockit.redefineMethods( PluginFinder.class, ReplacePluginFinder.class );
        

    } 
    
    @After public void tearDown() {
        Mockit.restoreAllOriginalDefinitions();
    
        reset( mockPluginID );
        PR = null;
    }

    /**
     * tests the construction of the PluginResolver
     */

    @Test public void constructorTest() throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException{

        PR = new PluginResolver();
    }
    /**
     * tests the getPlugin method, not a lot to test... 
     */
    @Ignore
    @Test public void getPluginTest() throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        expect( mockPluginID.getPluginID() ).andReturn( 2 );

        replay( mockPluginID );

        PR = new PluginResolver();
        IPluggable test = PR.getPlugin( mockPluginID );
        //        assertTrue( PR.getPlugin( mockPluginID ).getClass() == TestPlugin.class );
        
        verify( mockPluginID );
    } 
}