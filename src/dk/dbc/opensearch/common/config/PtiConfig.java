/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class PtiConfig extends Config
{
	/* ***********************
	 * PTI
	 * *********************/
	private int getMainPollTime()
	{
		int ret = config.getInt( "pti.main-poll-time" );
		return ret;
	}
	
	
	public static int getPtiMainPollTime() 
	{
		PtiConfig pc = new PtiConfig();
		return pc.getMainPollTime();
	} 
	
	
	/* *************************
	 * PTI REJECTED-SLEEP-TIME *
	 * *************************/
	private int getRejectedSleepTime()
	{
		int ret = config.getInt( "pti.rejected-sleep-time" );
		return ret;
	}
	
	
	public static int getPtiRejectedSleepTime()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getRejectedSleepTime();
	}
	
	
	/* ************************
	 * PTI SHUTDOWN-POLL-TIME *
	 * ************************/
	private int getShutdownPollTime()
	{
		int ret = config.getInt( "pti.shutdown-poll-time" );
		return ret;
	}
	
	
	public static int getPtiShutdownPollTime()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getShutdownPollTime();
	}
	
	
	/* ***************
	 * PTI QUEUESIZE *
	 * ***************/
	private int getQueueSize()
	{
		int ret = config.getInt( "pti.queuesize" );
		return ret;
	}
	
	
	public static int getPtiQueueSize()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getQueueSize();
	}
	
	
	/* ******************
	 * PTI COREPOOLSIZE *
	 * ******************/
	private int getCorePoolSize()
	{
		int ret = config.getInt( "pti.corepoolsize" );
		return ret;
	}
	
	
	public static int getPtiCorePoolSize()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getCorePoolSize();
	}
	
	
	/* *****************
	 * PTI MAXPOOLSIZE *
	 * *****************/
	private int getMaxPoolSize()
	{
		int ret = config.getInt( "pti.maxpoolsize" );
		return ret;
	}
	
	
	public static int getPtiMaxPoolSize()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getMaxPoolSize();
	}
	
	
	/* *******************
	 * PTI KEEPALIVETIME *
	 * *******************/
	private int getKeepAliveTime()
	{
		int ret = config.getInt( "pti.keepalivetime" );
		return ret;
	}
	
	
	public static int getPtiKeepAliveTime()
	{
		PtiConfig pc = new PtiConfig();
		return pc.getKeepAliveTime();
	}
}
