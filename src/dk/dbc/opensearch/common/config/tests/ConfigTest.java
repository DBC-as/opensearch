package dk.dbc.opensearch.common.config.tests;


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