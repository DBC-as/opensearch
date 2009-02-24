/**
 * 
 */
package dk.dbc.opensearch.common.helpers;


/**
 * @author mro
 *
 */
public class PluginsConfig extends Config
{
	/* ******************
	 * PLUGINS DATADOCK *
	 * ******************/
	private String getDatadock()
	{
		String ret = config.getString( "plugins.datadock" );
		return ret;
	}
	
	/**
	 * @return Path to the config/datadock_jobs.xml file
	 */
	public static String getPluginsDatadock() 
	{
		PluginsConfig pc = new PluginsConfig();
		return pc.getDatadock();
	} 
	
	
	/* *************
	 * PLUGINS PTI *
	 * *************/
	private String getPti()
	{
		String ret = config.getString( "plugins.pti" );
		return ret;
	}
	
	/**
	 * @return Path to the config/pti_jobs.xml file
	 */	
	public static String getPluginsPti()
	{		
		PluginsConfig pc = new PluginsConfig();
		return pc.getPti();
	}
}
