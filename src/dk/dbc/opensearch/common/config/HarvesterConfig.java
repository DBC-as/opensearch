/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class HarvesterConfig extends Config
{
	public HarvesterConfig() throws ConfigurationException 
	{
		super();
	}


	/* ******************
	 * HARVESTER FOLDER *
	 * ******************/
	private String getHarvesterFolder()
	{
		String ret = config.getString( "harvester.folder" );
		return ret;
	}
	
	
	public static String getFolder() throws ConfigurationException 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getHarvesterFolder();
	} 
}
