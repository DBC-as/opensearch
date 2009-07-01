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


/**
 * \file DatadockMainTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.components.datadock.DatadockMain;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.Ignore;


/**
 * 
 */
public class DatadockMainTest 
{
    @Before 
    public void Setup() {}

    
    @After 
    public void tearDown() {}

    //@Ignore( "This method tries to actually connect to the fedora server. No good, should be mocked" )   
    @Test public void testConstructor() throws Exception 
    {
    	DatadockMain datadockmain = new DatadockMain();
    	datadockmain.init();
    	
    	// Get jobMap listing jobs to be executed using plugins 
    	//HashMap< InputPair< String, String >, ArrayList< String > > jobMap = DatadockMain.jobMap;
    	
    	// Get set of jobs specified by submitter and format
    	//Set< InputPair< String, String > > keysSet = jobMap.keySet();
    	
    	// Loop through jobs
//    	for( InputPair< String, String > pair : keysSet )
//    	{
//            String submitter = pair.getFirst().toString();
//            String format = pair.getSecond().toString();    		
//            URI uri = new URI( DatadockConfig.getPath() ); 
//            DatadockJob job = new DatadockJob( uri, submitter, format );
//    		
//            if( jobMap.containsKey( pair ) )
//    	    {	
//                // Get list of plugins
//                ArrayList< String > list = jobMap.get( pair );
//                    
//                // Validate plugins
//                PluginResolver pluginResolver = new PluginResolver();
//                Vector< String > missingPlugins = pluginResolver.validateArgs( submitter, format, list );
//                    
//                if( ! missingPlugins.isEmpty() )
//                {		
//                    System.out.println( " kill thread" );
//                    throw new Exception( "plugins not found in test" );
//                    // kill thread/throw meaningful exception/log message
//	    	    }
//                else
//                {
//                    CargoContainer cc = null;
//				
//                    for( String task : list)
//                    {	
//                        IPluggable plugin = (IPluggable)pluginResolver.getPlugin( submitter, format, task );
//                        switch ( plugin.getTaskName() )
//                        {	
//                            case HARVEST:
//                                IHarvestable harvestPlugin = (IHarvestable)plugin; 
//                                cc = harvestPlugin.getCargoContainer( job );
//                                break;
//                            case ANNOTATE:
//                                IAnnotate annotatePlugin = (IAnnotate)plugin;
//                                cc = annotatePlugin.getCargoContainer( cc );
//                                break;
//                            case STORE:
//                                IRepositoryStore repositoryStore = (IRepositoryStore)plugin;
//                                //repositoryStore.storeCargoContainer( cc, job );
//                        }
//                    }
//                }
//            }
//    	}
    }
}
