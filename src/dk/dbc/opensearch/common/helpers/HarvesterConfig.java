/**
 * 
 */
package dk.dbc.opensearch.common.helpers;


/**
 * @author mro
 *
 */
public class HarvesterConfig extends Config
{
	/* ******************
	 * HARVESTER FOLDER *
	 * ******************/
	private String getFolder()
	{
		String ret = config.getString( "harvester.folder" );
		return ret;
	}
	
	
	public static String getHarvesterFolder() 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getFolder();
	} 
}
