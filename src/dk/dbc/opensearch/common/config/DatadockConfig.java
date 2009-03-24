/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class DatadockConfig extends Config
{
	/* *************************
	 * DATADOCK MAIN-POLL-TIME *
	 * *************************/
	private int getDatadockMainPollTime()
	{
		int ret = config.getInt( "datadock.main-poll-time" );
		return ret;
	}
	
	
	public static int getMainPollTime() 
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
	
	
	public static int getRejectedSleepTime()
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
	
	
	public static int getShutdownPollTime()
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
	
	
	public static int getQueueSize()
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
	
	
	public static int getCorePoolSize()
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
	
	
	public static int getMaxPoolSize()
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
	
	
	public static int getKeepAliveTime()
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
	
	
	public static int getJobLimit()
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
	 */
	public static String getPath() 
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getDatadockPath();
	} 
}
