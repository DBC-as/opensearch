package dk.dbc.opensearch.common.config.tests;


import dk.dbc.opensearch.common.config.FileSystemConfig;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;



public class FileSystemConfigTest
{
    Logger logger = Logger.getLogger( FileSystemConfigTest.class );

    
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
}