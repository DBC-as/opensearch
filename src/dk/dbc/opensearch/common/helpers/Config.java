package dk.dbc.opensearch.common.helpers;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class Config 
{	
	URL cfgURL = getClass().getResource( "/config.xml" );
	static XMLConfiguration config;
    
	protected Config()
	{
		try 
		{
			config = new XMLConfiguration( cfgURL );
		} 
		catch (ConfigurationException e) 
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/**************************
	 * config.xml -> database *
	 **************************/	
	public static int getDatabasePollTime()
	{
		System.out.println("polltime");
		return Database.pollTime;
	}
	
	
	private static class Database 
	{
		static int pollTime = config.getInt( "datadock.main-poll-time" );
		static int queueSize = config.getInt( "datadock.queuesize" );
		static int corePoolSize = config.getInt( "datadock.corepoolsize" );
		static int maxPoolSize = config.getInt( "datadock.maxpoolsize" );
		static int keepAliveTime = config.getInt( "datadock.keepalivetime" );
		static String harvestDir = config.getString( "harvester.folder" );        
	}
}
