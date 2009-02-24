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
	
	
	public static String getPluginsPti()
	{		
		PluginsConfig pc = new PluginsConfig();
		return pc.getPti();
	}
}
