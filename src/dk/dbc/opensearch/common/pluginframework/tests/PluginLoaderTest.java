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

    ClassLoader pcl;
    PluginLoader pl;
    Boolean noException;
    Boolean illegalArgument;
    IPluggable testIPlug;
    String testClassString;
    String invalidClassString;

    /**
     *
     */
    @Before public void SetUp()throws Exception {

        testClassString = "dk.dbc.opensearch.common.pluginframework.tests.TestPlugin";
        invalidClassString = "dk.dbc.opensearch.common.pluginframework.tests.NotExisting";
        noException = true;
        illegalArgument = false;

        pcl = new PluginClassLoader();
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
            pl = new PluginLoader( pcl );
        }catch( Exception e){
            noException = false;
        }
        assertTrue( noException );
    }

    /**
     * Tests the loadPlugin method by giving the class string
     * to the test class TestPlugin
     */
    @Test public void loadPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException {

        try{
            pl = new PluginLoader( pcl );
            testIPlug = pl.loadPlugin( testClassString );
        }catch( Exception e){
            noException = false;
        }
        assertTrue( noException );
        assertTrue( testIPlug.getClass().getName() == testClassString );

    }

    /**
     * Tests that the PluginLoader.loadPlugin throws an IllegalArgumentException
     * when given a noexisting class name.
     */
    @Test public void invalidClassNameTest(){

        try{
            pl = new PluginLoader( pcl );
            testIPlug = pl.loadPlugin( invalidClassString );
        }catch( Exception e){
            if( e.getClass() == IllegalArgumentException.class ){
                illegalArgument = e.getMessage().equals( String.format( " class %s not found! ", invalidClassString ) );
            }
        }
        assertTrue( illegalArgument );
    }
}