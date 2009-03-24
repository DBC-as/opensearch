/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class DatadockConfig extends Config
{
	public DatadockConfig() throws ConfigurationException 
	{
		super();
	}


	/* *************************
	 * DATADOCK MAIN-POLL-TIME *
	 * *************************/
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
	
	
	/* ******************************
	 * DATADOCK REJECTED-SLEEP-TIME *
	 * ******************************/
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
	
	
	/* *****************************
	 * DATADOCK SHUTDOWN-POLL-TIME *
	 * *****************************/
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
	
	
	/* ********************
	 * DATADOCK QUEUESIZE *
	 * ********************/
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
	
	
	/* ***********************
	 * DATADOCK COREPOOLSIZE *
	 * ***********************/
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
	
	
	/* **********************
	 * DATADOCK MAXPOOLSIZE *
	 * **********************/
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
	
	
	/* ************************
	 * DATADOCK KEEPALIVETIME *
	 * ***********************/
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
	
	
	/* *******************
	 * DATADOCK JOBLIMIT *
	 * *******************/
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
	
	
	/* ***************
	 * DATADOCK PATH *
	 * ***************/
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
