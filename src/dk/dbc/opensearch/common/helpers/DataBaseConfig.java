/**
 * 
 */
package dk.dbc.opensearch.common.helpers;


/**
 * @author mro
 *
 */
public class DataBaseConfig extends Config
{
	/* *****************
	 * DATABASE DRIVER *
	 * *****************/
	private String getDriver()
	{
		String ret = config.getString( "database.driver" );
		return ret;
	}
	
	
	public static String getDataBaseDriver() 
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getDriver();
	}
	
	
	/* **************
	 * DATABASE URL *
	 * **************/
	private String getUrl()
	{
		String ret = config.getString( "database.url" );
		return ret;
	}
	
	
	public static String getDataBaseUrl()
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getUrl();
	}
	
	
	/* *****************
	 * DATABASE USERID *
	 * *****************/
	private String getUserID()
	{
		String ret = config.getString( "database.userID" );
		return ret;
	}
	
	
	public static String getDataBaseUserID()
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getUserID();
	}
	
	
	/* *****************
	 * DATABASE PASSWD *
	 * *****************/
	private String getPassWd()
	{
		String ret = config.getString( "passwd" );
		return ret;
	}
	
	
	public static String getDataBasePassWd()
	{
		DataBaseConfig dbc = new DataBaseConfig();
		return dbc.getPassWd();
		
	}
}
