package dk.dbc.opensearch.common.config.tests;


import dk.dbc.opensearch.common.config.FileSystemConfig;

import org.apache.log4j.Logger;
import org.junit.Test;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;



public class FileSystemConfigTest
{
    Logger logger = Logger.getLogger( FileSystemConfigTest.class );


    @Test
    public void testTrunkPath() throws SecurityException, NoSuchFieldException, IllegalArgumentException, IllegalAccessException
    {
        String trunk = FileSystemConfig.getFileSystemTrunkPath();
        System.out.println( trunk );

        //boolean endsWith = trunk.endsWith( "opensearch/trunk/" ); 
        //assertTrue( endsWith );

    }
}