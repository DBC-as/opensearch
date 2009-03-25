/**
 * 
 */
package dk.dbc.opensearch.common.config;


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
	
	
	/* JOB LIMIT */
	private int getDatadockJobLimit()
	{
		int ret = config.getInt( "datadock.joblimit" );
		return ret;
	}
	
	
	public static int getJobLimit() throws ConfigurationException
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockJobLimit();
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
