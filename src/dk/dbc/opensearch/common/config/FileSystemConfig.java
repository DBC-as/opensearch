/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class FileSystemConfig extends Config
{
	public FileSystemConfig() throws ConfigurationException 
	{
		super();
	}


	private String sanitize( String path )
	{
		if( path.endsWith( "/" ) )
			return path;
		else
			return path + "/";
	}
	
	
	/* ******************
	 * FILESYSTEM TRUNK *
	 * ******************/
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
	
	
	/* ****************************
	 * FILESYSTEM CONFIG.XML PATH *
	 *****************************/
	public static String getConfigPath() throws ConfigurationException
	{
		FileSystemConfig f = new FileSystemConfig();
		String ret = f.getFileSystemTrunkPath();
		
		return ret + "config/"; 
	}
	
	
	/* ********************
	 * FILESYSTEM PLUGINS *
	 * ********************/
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
