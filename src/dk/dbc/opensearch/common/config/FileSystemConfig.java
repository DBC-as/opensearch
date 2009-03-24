/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class FileSystemConfig extends Config
{
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
	
	
	public static String getTrunkPath() 
	{
		FileSystemConfig f = new FileSystemConfig();
		return f.getFileSystemTrunkPath();
	} 
	
	
	/* ****************************
	 * FILESYSTEM CONFIG.XML PATH *
	 *****************************/
	public static String getConfigPath()
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
	
	
	public static String getPluginsPath()
	{		
		FileSystemConfig fc = new FileSystemConfig();		
		return fc.getFileSystemPluginsPath();
	}
}
