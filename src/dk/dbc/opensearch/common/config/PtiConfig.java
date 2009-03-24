/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class PtiConfig extends Config
{
	public PtiConfig() throws ConfigurationException
	{
		super();
	}


	/* ***********************
	 * PTI
	 * *********************/
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
	
	
	/* *************************
	 * PTI REJECTED-SLEEP-TIME *
	 * *************************/
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
	
	
	/* ************************
	 * PTI SHUTDOWN-POLL-TIME *
	 * ************************/
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
	
	
	/* ***************
	 * PTI QUEUESIZE *
	 * ***************/
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
	
	
	/* ******************
	 * PTI COREPOOLSIZE *
	 * ******************/
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
	
	
	/* *****************
	 * PTI MAXPOOLSIZE *
	 * *****************/
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
	
	
	/* *******************
	 * PTI KEEPALIVETIME *
	 * *******************/
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
	
	
	/* **********
	 * PTI PATH *
	 * **********/
	private String getPtiPath()
	{
		String ret = config.getString( "pti.path" );
		return ret;
	}
	
	/**
	 * @return Path to the config/pti_jobs.xml file
	 * @throws ConfigurationException 
	 */	
	public static String getPath() throws ConfigurationException
	{		
		PtiConfig pc = new PtiConfig();
		return pc.getPtiPath();
	}
}
