/**
 * 
 */
package dk.dbc.opensearch.common.config;

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
