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
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.ThrownInfo;
import dk.dbc.opensearch.common.fedora.IObjectRepository;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.Iterator;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import java.lang.reflect.InvocationTargetException;

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
import mockit.Mocked;

/**
 * class for testing the PluginResolver
 */
public class PluginResolverTest 
{   
    PluginResolver PR;

    static String staticString = "staticString";
    static TestPlugin mockPlugin = createMock( TestPlugin.class );    
    @Mocked IObjectRepository repository;
     
    
    /**
     * The class to mock the PluginLoader
     */
    // @MockClass(realClass = PluginLoader.class)
    // public static class ReplacePluginLoader
    // {        
    //     @Mock public static IPluggable getPlugin( String className, String script, IObjectRepository repository )
    //     {
    //         return (IPluggable)mockPlugin;
    //     } 
    // }
    
    
    @Before public void setUp() throws Exception 
    {
        // Mockit.setUpMocks( ReplacePluginLoader.class );
        //Mockit.setUpMocks( ReplacePluginFinder.class );
    } 
    
    
    @After public void tearDown() 
    {
        Mockit.tearDownMocks();
    }
    

    /**
     * tests the construction of the PluginResolver
     */
    @Ignore @Test 
    public void pluginResolverConstructorTest() throws NullPointerException, FileNotFoundException, ParserConfigurationException, IOException, ConfigurationException
    {
        PR = new PluginResolver( repository );
        PluginResolver PR2 = new PluginResolver( repository );
    }

    
    /**
     * tests the getPlugin method, not a lot to test... 
     */
    @Ignore @Test public void getPluginTest() throws NullPointerException, IOException, FileNotFoundException, ParserConfigurationException, InstantiationException, IllegalAccessException, ClassNotFoundException, ConfigurationException, InvocationTargetException, PluginException 
    {
        PR = new PluginResolver( repository );

        IPluggable test = PR.getPlugin( "task", "script" );
        
        assertTrue( test.getClass() == mockPlugin.getClass() );
    } 
    
}