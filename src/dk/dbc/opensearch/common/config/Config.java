package dk.dbc.opensearch.common.config;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;


public class Config 
{	
	URL cfgURL = getClass().getResource( "/config.xml" );
	static XMLConfiguration config;
    
	public Config()
	{
		try 
		{
			config = new XMLConfiguration( cfgURL );
		} 
		catch (ConfigurationException e) 
		{
			e.printStackTrace();
		}
	}
}
