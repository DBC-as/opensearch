/**
 * \file PluginResolverTest.java
 * \brief UnitTest for thre PluginResolver
 * \package tests
 */

package dk.dbc.opensearch.common.pluginframework;

/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginID;
import dk.dbc.opensearch.common.pluginframework.PluginLoader;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import static org.junit.Assert.*;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

import mockit.Mockit;
import mockit.Mock;
import mockit.MockClass;


/**
 * class for testing the PluginResolver
 */
public class PluginResolverTest 
{   
    PluginResolver PR;

    static String staticString = "staticString";
    static TestPlugin mockPlugin = createMock( TestPlugin.class );    

    
    /**
     * The class to mock the PluginFinder
     */
    /*@MockClass( realClass = PluginFinder.class )
    public static class ReplacePluginFinder
    {   
      
        @Mock 
        public static String getPluginClassName( int key ) throws PluginResolverException, FileNotFoundException
        {
        	if (key == ( "throwException" ).hashCode())
            {
                throw new FileNotFoundException( "no plugin for testTask3" );
            }
            
            return "staticString";
        }
        
        @Mock 
        public void buildPluginClassNameMap( String path ) {}
        }*/
    
    
    /**
     * The class to mock the PluginLoader
     */
    @MockClass(realClass = PluginLoader.class)
    public static class ReplacePluginLoader
    {        
        @Mock public static IPluggable getPlugin( String className )
        {
            return (IPluggable)mockPlugin;
        } 
    }
    
    
    @Before public void setUp() throws Exception 
    {
        Mockit.setUpMocks( ReplacePluginLoader.class );
        //Mockit.setUpMocks( ReplacePluginFinder.class );
    } 
    
    
    @After public void tearDown() 
    {
        Mockit.tearDownMocks();
    }
    

    /**
     * tests the construction of the PluginResolver
     */
    @Test 
    public void pluginResolverConstructorTest() throws NullPointerException, FileNotFoundException, PluginResolverException, ParserConfigurationException, IOException, ConfigurationException
    {
        PR = new PluginResolver();
        PluginResolver PR2 = new PluginResolver();
    }

    
    /**
     * tests the getPlugin method, not a lot to test... 
     */
    //@Ignore
    @Test public void getPluginTest() throws NullPointerException, IOException, FileNotFoundException, PluginResolverException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException 
    {
        PR = new PluginResolver();

        IPluggable test = PR.getPlugin( "task" );
        
        assertTrue( test.getClass() == mockPlugin.getClass() );
    } 
    
    
    /**
     * Tests the happy path of the validatArgs method, where an empty vector is returned
     */
    @Ignore( "validateArgs is deprecated" )
    @Test 
    public void validateArgsTest() throws ParserConfigurationException, FileNotFoundException, PluginResolverException, IOException, ConfigurationException
    {
        /*String submitter = "testSubmitter";
        String format = "testFormat";
        String task1 = "testTask1";
        String task2 = "testTask2";
        String taskException = "throwException";
        ArrayList< String > testTaskList = new ArrayList< String >();
        testTaskList.add( "testTask1 ");
        testTaskList.add( "testTask2 ");

        PR = new PluginResolver();

        Vector<String> noPluginForVector = PR.validateArgs( submitter, format, testTaskList );
        assertTrue( noPluginForVector.isEmpty() );*/
    }  
    

    /**
     * Tests the case where plugins cant be found for all wanted tasks. 
     * The redefinded method of the PluginFinder throws the FileNotFoundException 
     * when asked to look for the task "throwException". This put it on the vector 
     * to be returned
     */
    @Ignore( "validateArgs is deprecated" )
    @Test
    public void validateArgsNotAllPluginsFoundTest() throws ParserConfigurationException, FileNotFoundException, PluginResolverException, IOException, ConfigurationException
    {
        /*String submitter = "testSubmitter";
        String format = "testFormat";
        String task1 = "testTask10";
        String task2 = "testTask20";
        String taskException = "throwException";
        ArrayList< String > testTaskList = new ArrayList< String >();
        testTaskList.add( task1 );
        testTaskList.add( task2 );
        testTaskList.add( taskException );

        PR = new PluginResolver();

        Vector< String > noPluginForVector = PR.validateArgs( submitter, format, testTaskList );
        Iterator< String > iter = noPluginForVector.iterator();
     
        assertTrue( taskException.equals( (String)iter.next() ) );*/
    }
    

    /**
     * Tests the clearPluginRegistration method...
     * There is nothing but a method call to the PluginFinder in it
     */
    @Test
    @Ignore
    public void clearPluginRegistrationTest() throws ParserConfigurationException, FileNotFoundException, PluginResolverException, IOException, ConfigurationException
    {
        PR = new PluginResolver();
        //PR.clearPluginRegistration();
    }
}