/**
 * \file DatadockMainTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock.tests;

/** \brief UnitTest for DatadockManager **/

import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.pluginframework.IAnnotate;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginResolver;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.datadock.DatadockMain;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Vector;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;


/**
 * Unittest for the DatadockMain
 */
public class DatadockMainTest 
{
    @Before public void Setup() {}

    
    @After public void tearDown() {}

    @Ignore( "This method tries to actually connect to the fedora server. No good, should be mocked" )   
    @Test public void testConstructor() throws Exception 
    {
    	DatadockMain datadockmain = new DatadockMain();
    	
    	// Get jobMap listing jobs to be executed using plugins 
    	HashMap< Pair< String, String >, ArrayList< String > > jobMap = DatadockMain.jobMap;
    	
    	// Get set of jobs specified by submitter and format
    	Set< Pair< String, String > > keysSet = jobMap.keySet();
    	
    	// Loop through jobs
    	for( Pair< String, String > pair : keysSet )
    	{
            String submitter = pair.getFirst().toString();
            String format = pair.getSecond().toString();    		
            URI uri = new URI( FileSystemConfig.getFileSystemDatadock() ); 
            DatadockJob job = new DatadockJob( uri, submitter, format );
    		
            if( jobMap.containsKey( pair ) )
    	    {	
                // Get list of plugins
                ArrayList< String > list = jobMap.get( pair );
                    
                // Validate plugins
                PluginResolver pluginResolver = new PluginResolver();
                Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
                    
                if( ! missingPlugins.isEmpty() )
                {		
                    System.out.println( " kill thread" );
                    throw new Exception( "plugins not found in test" );
                    // kill thread/throw meaningful exception/log message
	    	    }
                else
                {
                    CargoContainer cc = null;
				
                    for( String task : list)
                    {	
                        IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
                        switch ( plugin.getTaskName() )
                        {	
                            case HARVEST:
                                IHarvestable harvestPlugin = (IHarvestable)plugin; 
                                cc = harvestPlugin.getCargoContainer( job );
                                break;
                            case ANNOTATE:
                                IAnnotate annotatePlugin = (IAnnotate)plugin;
                                cc = annotatePlugin.getCargoContainer( cc );
                                break;
                            case STORE:
                                IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
                                //repositoryStore.storeCargoContainer( cc, job );
                        }
                    }
                }
            }
    	}
    }
}
