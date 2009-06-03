package dk.dbc.opensearch.common.config;

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


import dk.dbc.opensearch.common.config.FileSystemConfig;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.AbstractConfiguration;

import org.apache.commons.configuration.ConfigurationException;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;

import org.junit.*;
import static org.junit.Assert.*;



public class FileSystemConfigTest
{
    @MockClass( realClass = AbstractConfiguration.class )
    public static class MockXMLConf1
    {
    	@Mock public static String getString( String key )
    	{
    		return "test";
    	}
    } 
    
    @MockClass( realClass = AbstractConfiguration.class )
    public static class MockXMLConf2
    {
    	@Mock public static String getString( String key )
    	{
    		return "another test/";
        }
    }
   

    @After
    public void tearDown()
    {
        Mockit.tearDownMocks();
    }

    
    @Test
    public void testGetConfigPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
        String trunk = FileSystemConfig.getConfigPath();
    
        String str = "/config/";
        boolean endsWith = trunk.contains( str ); 
        assertTrue( endsWith );
    }
    

    @Test
    public void testGetTrunkPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
        String trunk = FileSystemConfig.getTrunkPath();
    
        CharSequence cs = "/opensearch";
        boolean endsWith = trunk.contains( cs );  
        assertTrue( endsWith );
    }
    
    
    @Test
    public void testGetPluginsPath() throws ConfigurationException
    {
    	String plugins = FileSystemConfig.getPluginsPath();
    
    	CharSequence cs = "/plugins";
        boolean endsWith = plugins.contains( cs ); 
        assertTrue( endsWith );    	
    }
    
    
    @Test
    public void testJobsXsdPath() throws ConfigurationException
    {
    	String xsd = FileSystemConfig.getJobsXsdPath();
    	
    	CharSequence cs = "config/jobs.xsd";
    	boolean endsWith = xsd.contains( cs );
    	assertTrue( endsWith );
    }

    /**
     * We mock the getString method of the XMLConfiguration class to get a string 
     * without and with "/" as the last character when it is called through
     * the public method getTrunkPath
     */
    @Test 
    public void testSanitize1() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf1.class);

        String test = FileSystemConfig.getTrunkPath(); 
        assertEquals( test, "test/" ); 
    }
    
    
    @Test public void testSanitize2() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf2.class);

        String test = FileSystemConfig.getTrunkPath(); 
        assertEquals( test, "another test/" ); 
    }
}