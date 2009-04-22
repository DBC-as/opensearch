/**
 * 
 */
package dk.dbc.opensearch.common.config;

/*
   
This file is part of opensearch.
Copyright © 2009, Dansk Bibliotekscenter a/s, 
Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

opensearch is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

opensearch is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


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
