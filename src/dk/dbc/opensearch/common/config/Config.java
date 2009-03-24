package dk.dbc.opensearch.common.config;

import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


public class Config 
{
	Logger log = Logger.getLogger( Config.class );
	
	
	URL cfgURL = getClass().getResource( "/config.xml" );
	static XMLConfiguration config;
    
	public Config() throws ConfigurationException
	{
		try 
		{
			config = new XMLConfiguration( cfgURL );
		} 
		catch ( ConfigurationException e ) 
		{
			log.fatal( "ConfigurationException caught in class Config:" );
			log.fatal( e.getStackTrace().toString() );
			throw e;
		}
	}
}
