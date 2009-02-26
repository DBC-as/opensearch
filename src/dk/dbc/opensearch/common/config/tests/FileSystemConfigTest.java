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
        System.out.println( "trunk: " + trunk );
        CharSequence cs = "opensearch/trunk";
        boolean endsWith = trunk.contains( cs ); //endsWith( "opensearch/trunk/" ); 
        assertTrue( endsWith );
    }
    
    
    @Test
    public void testGetPluginsPath()
    {
    	String plugins = FileSystemConfig.getFileSystemPluginsPath();
    	System.out.println( "plugins: " + plugins );
    	CharSequence cs = "opensearch/trunk/build/classes/dk/dbc/opensearch/plugins";
        boolean endsWith = plugins.contains( cs ); //endsWith( "opensearch/trunk/build/classes/dk/dbc/opensearch/plugins/" ); 
        assertTrue( endsWith );    	
    }
    
    
    @Test
    public void testGetDatadockPath()
    {
    	String datadock = FileSystemConfig.getFileSystemDatadockPath();
    	System.out.println( "datadock: " + datadock );
        boolean endsWith = datadock.endsWith( "opensearch/trunk/config/datadock_jobs.xml" ); 
        assertTrue( endsWith );    	
    }
    
    
    @Test
    public void testGetPtiPath()
    {
    	String pti = FileSystemConfig.getFileSystemPtiPath();
    	System.out.println( "pti: " + pti );
        boolean endsWith = pti.endsWith( "opensearch/trunk/config/pti_jobs.xml" ); 
        assertTrue( endsWith );    	
    }
}