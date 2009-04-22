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
 * Sub class of Config providing access to datadock settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class DatadockConfig extends Config
{
	public DatadockConfig() throws ConfigurationException 
	{
		super();
	}


	/* MAIN POLL TIME */
	private int getDatadockMainPollTime()
	{
		int ret = config.getInt( "datadock.main-poll-time" );
		return ret;
	}
	
	
	public static int getMainPollTime() throws ConfigurationException 
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockMainPollTime();
	}
	
	
	/* REJECTED SLEEP TIME */
	private int getDatadockRejectedSleepTime()
	{
		int ret = config.getInt( "datadock.rejected-sleep-time" );
		return ret;
	}
	
	
	public static int getRejectedSleepTime() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockRejectedSleepTime();
	}
	
	
	/* SHUTDOWN POLL TIME */
	private int getDatadockShutdownPollTime()
	{
		int ret = config.getInt( "datadock.shutdown-poll-time" );
		return ret;
	}
	
	
	public static int getShutdownPollTime() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockShutdownPollTime();
	}
	
	
	/* QUEUE SIZE */
	private int getDatadockQueueSize()
	{
		int ret = config.getInt( "datadock.queuesize" );
		return ret;
	}
	
	
	public static int getQueueSize() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockQueueSize();
	}
	
	
	/* CORE POOL SIZE */
	private int getDatadockCorePoolSize()
	{
		int ret = config.getInt( "datadock.corepoolsize" );
		return ret;
	}
	
	
	public static int getCorePoolSize() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockCorePoolSize();
	}
	
	
	/* MAX POOL SIZE */
	private int getDatadockMaxPoolSize()
	{
		int ret = config.getInt( "datadock.maxpoolsize" );
		return ret;
	}
	
	
	public static int getMaxPoolSize() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockMaxPoolSize();
	}
	
	
	/* KEEP ALIVE TIME */
	private int getDatadockKeepAliveTime()
	{
		int ret = config.getInt( "datadock.keepalivetime" );
		return ret;
	}
	
	
	public static int getKeepAliveTime() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockKeepAliveTime();
	}
	
	
	/* PATH */
	private String getDatadockPath()
	{
		String ret = config.getString( "datadock.path" );
		return ret;
	}
	
	/**
	 * @return Path to the config/datadock_jobs.xml file
	 * @throws ConfigurationException 
	 */
	public static String getPath() throws ConfigurationException 
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockPath();
	} 
}
