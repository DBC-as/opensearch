/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to harvester settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class HarvesterConfig extends Config
{
	public HarvesterConfig() throws ConfigurationException 
	{
		super();
	}


	/* FOLDER */
	private String getHarvesterFolder()
	{
		String ret = config.getString( "harvester.toharvest" );
		return ret;
	}
	
	
	public static String getFolder() throws ConfigurationException 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getHarvesterFolder();
	}
	
	
	/* DONE FOLDER */
	private String getHarvesterDoneFolder()
	{
		String ret = config.getString( "harvester.harvestdone" );
		return ret;
	}
	
	
	public static String getDoneFolder() throws ConfigurationException 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getHarvesterDoneFolder();
	}
	
	
	/* MAX TO HARVEST */
	private int getHarvesterMaxToHarvest()
	{
		int ret = config.getInt( "harvester.maxtoharvest" );
		return ret;
	}
	
	
	public static int getMaxToHarvest() throws ConfigurationException 
	{
		HarvesterConfig hc = new HarvesterConfig();
		return hc.getHarvesterMaxToHarvest();
	}
}
