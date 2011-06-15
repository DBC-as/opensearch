/**
 * 
 */
package dk.dbc.opensearch.config;

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
 * Sub class of Config providing access to pti settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class PtiConfig extends Config
{
	public PtiConfig() throws ConfigurationException
	{
		super();
	}


	/* MAIN POLL TIME */
	private int getPtiMainPollTime()
	{
		int ret = config.getInt( "pti.main-poll-time" );
		return ret;
	}
	
	
	public static int getMainPollTime() throws ConfigurationException 
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiMainPollTime();
	} 
	
	
	/* REJECTED-SLEEP-TIME */
	private int getPtiRejectedSleepTime()
	{
		int ret = config.getInt( "pti.rejected-sleep-time" );
		return ret;
	}
	
	
	public static int getRejectedSleepTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiRejectedSleepTime();
	}
	
	
	/* SHUTDOWN-POLL-TIME */
	private int getPtiShutdownPollTime()
	{
		int ret = config.getInt( "pti.shutdown-poll-time" );
		return ret;
	}
	
	
	public static int getShutdownPollTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiShutdownPollTime();
	}
	
	
	/* QUEUE SIZE */
	private int getPtiQueueSize()
	{
		int ret = config.getInt( "pti.queuesize" );
		return ret;
	}
	
	
	public static int getQueueSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiQueueSize();
	}
	
	
	/* CORE POOL SIZE */
	private int getPtiCorePoolSize()
	{
		int ret = config.getInt( "pti.corepoolsize" );
		return ret;
	}
	
	
	public static int getCorePoolSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiCorePoolSize();
	}
	
	
	/* MAX POOL SIZE */
	private int getPtiMaxPoolSize()
	{
		int ret = config.getInt( "pti.maxpoolsize" );
		return ret;
	}
	
	
	public static int getMaxPoolSize() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiMaxPoolSize();
	}
	
	
	/* KEEP ALIVE TIME */
	private int getPtiKeepAliveTime()
	{
		int ret = config.getInt( "pti.keepalivetime" );
		return ret;
	}
	
	
	public static int getKeepAliveTime() throws ConfigurationException
	{
		PtiConfig pc = new PtiConfig();
		return pc.getPtiKeepAliveTime();
	}
	
	
	/* PATH */
	private String getPtiPath()
	{
		String ret = config.getString( "pti.path" );
		return ret;
	}
	
	
	public static String getPath() throws ConfigurationException
	{		
		PtiConfig pc = new PtiConfig();
		return pc.getPtiPath();
	}
}
