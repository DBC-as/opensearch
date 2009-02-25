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
	private int getMainPollTime()
	{
		int ret = config.getInt( "datadock.main-poll-time" );
		return ret;
	}
	
	
	public static int getDatadockMainPollTime() 
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getMainPollTime();
	}
	
	
	/* ******************************
	 * DATADOCK REJECTED-SLEEP-TIME *
	 * ******************************/
	private int getRejectedSleepTime()
	{
		int ret = config.getInt( "datadock.rejected-sleep-time" );
		return ret;
	}
	
	
	public static int getDatadockRejectedSleepTime()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getRejectedSleepTime();
	}
	
	
	/* *****************************
	 * DATADOCK SHUTDOWN-POLL-TIME *
	 * *****************************/
	private int getShutdownPollTime()
	{
		int ret = config.getInt( "datadock.shutdown-poll-time" );
		return ret;
	}
	
	
	public static int getDatadockShutdownPollTime()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getShutdownPollTime();
	}
	
	
	/* ********************
	 * DATADOCK QUEUESIZE *
	 * ********************/
	private int getQueueSize()
	{
		int ret = config.getInt( "datadock.queuesize" );
		return ret;
	}
	
	
	public static int getDatadockQueueSize()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getQueueSize();
	}
	
	
	/* ***********************
	 * DATADOCK COREPOOLSIZE *
	 * ***********************/
	private int getCorePoolSize()
	{
		int ret = config.getInt( "datadock.corepoolsize" );
		return ret;
	}
	
	
	public static int getDatadockCorePoolSize()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getCorePoolSize();
	}
	
	
	/* **********************
	 * DATADOCK MAXPOOLSIZE *
	 * **********************/
	private int getMaxPoolSize()
	{
		int ret = config.getInt( "datadock.maxpoolsize" );
		return ret;
	}
	
	
	public static int getDatadockMaxPoolSize()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getMaxPoolSize();
	}
	
	
	/* ************************
	 * DATADOCK KEEPALIVETIME *
	 * ***********************/
	private int getKeepAliveTime()
	{
		int ret = config.getInt( "datadock.keepalivetime" );
		return ret;
	}
	
	
	public static int getDatadockKeepAliveTime()
	{
		DatadockConfig ddc = new DatadockConfig();
		return ddc.getKeepAliveTime();
	}
}
