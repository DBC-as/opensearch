/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to file system settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class FileSystemConfig extends Config
{
	public FileSystemConfig() throws ConfigurationException 
	{
		super();
	}


	/* Ensuring a '/' at the end of path */
	private String sanitize( String path )
	{
		if( path.endsWith( "/" ) )
			return path;
		else
			return path + "/";
	}
	
	
	/* TRUNK */
	private String getFileSystemTrunkPath()
	{
		String ret = config.getString( "filesystem.trunk" );
		return sanitize( ret );
	}
	
	
	public static String getTrunkPath() throws ConfigurationException 
	{
		FileSystemConfig f = new FileSystemConfig();
		return f.getFileSystemTrunkPath();
	} 
	
	
	/* CONFIG.XML PATH */
	public static String getConfigPath() throws ConfigurationException
	{
		FileSystemConfig f = new FileSystemConfig();
		String ret = f.getFileSystemTrunkPath();
		
		return ret + "config/"; 
	}
	
	
	/* PLUGINS */
	private String getFileSystemPluginsPath()
	{
		String ret = config.getString( "filesystem.plugins" );
		return sanitize( ret );
	}
	
	
	public static String getPluginsPath() throws ConfigurationException
	{		
		FileSystemConfig fc = new FileSystemConfig();		
		return fc.getFileSystemPluginsPath();
	}
}
