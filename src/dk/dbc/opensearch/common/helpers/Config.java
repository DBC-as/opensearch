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
}
