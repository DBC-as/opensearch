package dk.dbc.opensearch.common.config.tests;


import dk.dbc.opensearch.common.config.FileSystemConfig;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.assertTrue;



public class FileSystemConfigTest
{
    Logger logger = Logger.getLogger( FileSystemConfigTest.class );


    @Test
    public void testGetTrunkPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        String trunk = FileSystemConfig.getFileSystemTrunkPath();
    
        CharSequence cs = "/opensearch";
        boolean endsWith = trunk.contains( cs ); //endsWith( "opensearch/trunk/" ); 
        assertTrue( endsWith );
    }
    
    
    @Test
    public void testGetPluginsPath()
    {
    	String plugins = FileSystemConfig.getFileSystemPluginsPath();
    
    	//CharSequence cs = "/build/classes/dk/dbc/opensearch/plugins";
    	CharSequence cs = "/plugins";
        boolean endsWith = plugins.contains( cs ); //endsWith( "opensearch/trunk/build/classes/dk/dbc/opensearch/plugins/" ); 
        assertTrue( endsWith );    	
    }
    
    
    @Test
    public void testGetDatadockPath()
    {
    	String datadock = FileSystemConfig.getFileSystemDatadockPath();
    
        boolean endsWith = datadock.endsWith( "/config/datadock_jobs.xml" ); 
        assertTrue( endsWith );    	
    }
    
    
    @Test
    public void testGetPtiPath()
    {
    	String pti = FileSystemConfig.getFileSystemPtiPath();
    	
        boolean endsWith = pti.endsWith( "/config/pti_jobs.xml" ); 
        assertTrue( endsWith );
    }
}