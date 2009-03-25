package dk.dbc.opensearch.common.config;


import java.net.URL;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;


/**
 * @author mro
 * 
 * Super class for config classes. The sole purpose of this class is to  
 * provide access to the configuration file ../config/config.xml. This 
 * file is parsed and made available to sub classes through a non static
 * object.
 * 
 * The intended use is for sub classes to provide access to configuration 
 * settings via static methods. This is done via:
 * 
 *     1) a private non static method, and 
 *     2) a public static method using a sub class object. Throws a 
 *        ConfigurationException.
 * 
 * Sub classes shall implement a constructor to handle the exception 
 * thrown in case the config file cannot be read (ConfigurationException).
 */
public class Config 
{
	Logger log = Logger.getLogger( Config.class );
	
	
	URL cfgURL = getClass().getResource( "/config.xml" );
	static XMLConfiguration config;
    

	/**
	 * Essential method providing access to the solution's config file.
	 * 
	 * @throws ConfigurationException
	 */
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
