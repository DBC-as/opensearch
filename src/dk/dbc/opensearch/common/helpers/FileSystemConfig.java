/**
 * 
 */
package dk.dbc.opensearch.common.helpers;


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
	private String getTrunkPath()
	{
		String ret = config.getString( "filesystem.trunk" );
		return sanitize( ret );
	}
	
	
	public static String getFileSystemTrunkPath() 
	{
		FileSystemConfig f = new FileSystemConfig();
		return f.getTrunkPath();
	} 
	
	
	/* ********************
	 * FILESYSTEM PLUGINS *
	 * ********************/
	private String getPluginsPath()
	{
		String ret = config.getString( "filesystem.trunk" );
		return sanitize( ret );
	}
	
	
	public static String getFileSystemPluginsPath()
	{		
		FileSystemConfig f = new FileSystemConfig();
		return f.getPluginsPath();
	}
}
