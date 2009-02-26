package dk.dbc.opensearch.common.config.tests;


import dk.dbc.opensearch.common.config.Config;

import java.lang.reflect.Field;
import java.net.URL;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.assertNotNull;


public class ConfigTest
{
    Logger logger = Logger.getLogger( ConfigTest.class );


    @Test
    public void testConstructor() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
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
}