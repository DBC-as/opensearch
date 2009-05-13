package dk.dbc.opensearch.common.config.tests;

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


import dk.dbc.opensearch.common.config.Config;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;

import java.lang.reflect.Field;
import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;

import org.junit.*;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import mockit.Mockit;
import mockit.MockClass;
import mockit.Mock;


public class ConfigTest
{
    //Logger logger = Logger.getLogger( ConfigTest.class );

    @MockClass( realClass = XMLConfiguration.class )
    public static class MockXMLConf
    {
        @Mock public void $init( URL url ) throws ConfigurationException
        {
            throw new ConfigurationException( "meaningful message" );
        } 
    }
    

    /**
     * mix between unit and function test...
     */
    @After public void tearDown()
    {
        Mockit.tearDownMocks();  
    }


    @Test
    public void testConstructor() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException, ConfigurationException
    {
    	Config c = new Config();
        Field cfgURL;
        cfgURL = c.getClass().getDeclaredField( "cfgURL" );
        
        cfgURL.setAccessible( true );
        
        // Test that getResource() finds a file.
        URL url = (URL) cfgURL.get( c );
        assertNotNull( url );
        
        // Test that getResource() finds the correct file.
        boolean fileName = url.getPath().endsWith( "config.xml" );
        assertTrue( fileName );
    }


    @Test( expected = ConfigurationException.class )
        public void testConstructor2() throws ConfigurationException
    {
        Mockit.setUpMocks( MockXMLConf.class );

        Config c = new Config();
    }
}