/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class HarvesterConfig extends Config
{
	/* ******************
	 * HARVESTER FOLDER *
	 * ******************/
	private String getHarvesterFolder()
	{
		String ret = config.getString( "harvester.folder" );
		return ret;
	}
	
	
	public static String getFolder() 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getHarvesterFolder();
	} 
}
