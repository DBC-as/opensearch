/**
 * 
 */
package dk.dbc.opensearch.common.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to database settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class DataBaseConfig extends Config
{
	public DataBaseConfig() throws ConfigurationException 
	{
		super();
	}


	/* DRIVER */
	private String getDataBaseDriver()
	{
		String ret = config.getString( "database.driver" );
		return ret;
	}
	
	
	public static String getDriver() throws ConfigurationException 
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getDataBaseDriver();
	}
	
	
	/* URL */
	private String getDataBaseUrl()
	{
		String ret = config.getString( "database.url" );
		return ret;
	}
	
	
	public static String getUrl() throws ConfigurationException
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getDataBaseUrl();
	}
	
	
	/* USERID */
	private String getDataBaseUserID()
	{
		String ret = config.getString( "database.userID" );
		return ret;
	}
	
	
	public static String getUserID() throws ConfigurationException
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getDataBaseUserID();
	}
	
	
	/* PASSWD */
	private String getDataBasePassWd()
	{
		String ret = config.getString( "database.passwd" );
		return ret;
	}
	
	
	public static String getPassWd() throws ConfigurationException
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getDataBasePassWd();		
	}
}
