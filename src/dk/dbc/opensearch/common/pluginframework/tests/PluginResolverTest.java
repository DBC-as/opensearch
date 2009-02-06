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
//import junit.framework.TestCase;
import static org.easymock.classextension.EasyMock.*;
import mockit.Mockit;
//import mockit.MockClass;
//import mockit.Mock;

import java.io.FileNotFoundException;

import java.util.Vector;
import java.util.Iterator;


import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

/**
 * class for testing the PluginResolver
 */
public class PluginResolverTest {

    //DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
    //DocumentBuilder docB = null;
    //String path = "";
    PluginResolver PR;
    PluginID mockPluginID;


    static String staticString = "staticString";
    static TestPlugin mockPlugin = createMock( TestPlugin.class );    

    /**
     * The class to mock the PluginFinder
     */
    //@MockClass(realClass = PluginFinder.class)
    public static class ReplacePluginFinder{
   
        /*public ReplacePluginFinder(){
        }
        
        public ReplacePluginFinder( DocumentBuilder docB, String path ){
            System.out.print(" hep finder \n");
        }
        */
        public String getPluginClassName( int key ) throws PluginResolverException, FileNotFoundException{
            if (key == ( "testSubmitter"+"testFormat"+"throwException" ).hashCode()){
                throw new FileNotFoundException( "no plugin for testTask3" );
            }
            return "staticString";
        }
        
        public void updatePluginClassNameMap( String path ){
        }
    }
    
    /**
     * The class to mock the PluginLoader
     */
    //@MockClass(realClass = PluginLoader.class)
    public static class ReplacePluginLoader{
        
        /*public ReplacePluginLoader( ClassLoader pcl ){
            
           
          }*/
        
        public IPluggable getPlugin( String className ){
            //            System.out.println( className );
            return (IPluggable)mockPlugin;
        }
 
    }
    
    @Before public void setUp() throws Exception {
        
        mockPluginID = createMock( PluginID.class );

        //  Mockit.setUpMocks(ReplacePluginFinder.class);
        //Mockit.setUpMocks(ReplacePluginLoader.class);

        Mockit.redefineMethods( PluginLoader.class, ReplacePluginLoader.class );
        Mockit.redefineMethods( PluginFinder.class, ReplacePluginFinder.class ); 
      

    } 
    
    @After public void tearDown() {
        Mockit.restoreAllOriginalDefinitions();
    
        reset( mockPluginID );
        //    PR = null;
        }

    /**
     * tests the construction of the PluginResolver
     */

    @Test public void pluginResolverConstructorTest() throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException{

        PR = new PluginResolver();
    }
    /**
     * tests the getPlugin method, not a lot to test... 
     */
    @Test public void getPluginTest() throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException {

        expect( mockPluginID.getPluginID() ).andReturn( 2 );

        replay( mockPluginID );

        PR = new PluginResolver();

        IPluggable test = PR.getPlugin( mockPluginID );
       
        assertTrue( test.getClass() == mockPlugin.getClass() );
        
        verify( mockPluginID );
    } 
    /**
     * Tests the happy path of the validatArgs method, where an empty vector is return
     */

    @Test public void validateArgsTest() throws ParserConfigurationException ,FileNotFoundException, PluginResolverException{
        String submitter = "testSubmitter";
        String format = "testFormat";
        String task1 = "testTask1";
        String task2 = "testTask2";
        String taskException = "throwException";
        String[] testTaskList = new String[]{"testTask1","testTask2"};

        PR = new PluginResolver();

        Vector<String> noPluginForVector = PR.validateArgs( submitter, format, testTaskList );
        assertTrue( noPluginForVector.isEmpty() );
    }  

    /**
     * Tests the case where plugins cant be found for all wanted tasks. 
     * The redefinded method of the PluginFinder throws the FileNotFoundException 
     * when asked to look for the task "throwException". This but it on the vector 
     * to be returned
     */
    @Test public void validateArgsNotAllPluginsFoundTest() throws ParserConfigurationException ,FileNotFoundException, PluginResolverException{
        String submitter = "testSubmitter";
        String format = "testFormat";
        String task1 = "testTask1";
        String task2 = "testTask2";
        String taskException = "throwException";
        String[] testTaskList = new String[]{ task1, task2, taskException };

        PR = new PluginResolver();

        Vector<String> noPluginForVector = PR.validateArgs( submitter, format, testTaskList );
        Iterator iter = noPluginForVector.iterator();
        assertTrue( taskException.equals((String)iter.next() ) );
    }

    /**
     * Tests the clearPluginRegistration method...
     * There is nothing but a method call to the PluginFinder in it
     */
    @Test public void clearPluginRegistrationTest() throws ParserConfigurationException ,FileNotFoundException, PluginResolverException {
        PR = new PluginResolver();

        PR.clearPluginRegistration();
    }

}