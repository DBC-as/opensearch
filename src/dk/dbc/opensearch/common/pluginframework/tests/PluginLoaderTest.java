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
import java.lang.reflect.Method;

import java.lang.InstantiationException;
import java.lang.ClassNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

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
    @Test public void getPluginTest() throws InstantiationException, ClassNotFoundException, IllegalAccessException, NoSuchMethodException, InvocationTargetException {
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ testClassString };

        pl = new PluginLoader( pcl );
        method = pl.getClass().getDeclaredMethod( "getPlugin", argClasses );
        method.setAccessible( true );
        testIPlug = ( IPluggable ) method.invoke( pl, args );

        assertTrue( testIPlug.getClass().getName().equals( testClassString ) );

    }

    /**
     * Tests that the PluginLoader.loadPlugin throws an IllegalArgumentException
     * when given a not-existing class name. The exception is wrapped in an
     * InvocationTargetException
     */
    @Test public void invalidClassNameTest() throws NoSuchMethodException, IllegalAccessException{
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ invalidClassString };
        try{
            pl = new PluginLoader( pcl );
            method = pl.getClass().getDeclaredMethod( "getPlugin", argClasses );
            method.setAccessible( true );
            testIPlug = ( IPluggable ) method.invoke( pl, args );

        }catch( InvocationTargetException ite){
            illegalArgument = ( ( ite.getCause().getClass() == IllegalArgumentException.class ) && ( ite.getCause().getMessage().equals( String.format( " class %s not found! ", invalidClassString ) ) ) );
        }
        //needs to do it this way...
        assertTrue( illegalArgument );
    }
}