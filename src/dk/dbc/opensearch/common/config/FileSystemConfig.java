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
	
	
	/* ****************************
	 * FILESYSTEM CONFIG.XML PATH *
	 *****************************/
	public static String getConfigPath()
	{
		FileSystemConfig f = new FileSystemConfig();
		String ret = f.getTrunkPath();
		
		return ret + "config/"; 
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
		String ret = config.getString( "filesystem.plugins" );
		return sanitize( ret );
	}
	
	
	public static String getFileSystemPluginsPath()
	{		
		FileSystemConfig fc = new FileSystemConfig();		
		return fc.getPluginsPath();
	}
	
	
	/* *********************
	 * FILESYSTEM DATADOCK *
	 * *********************/
	private String getDatadock()
	{
		String ret = config.getString( "filesystem.datadock" );
		return ret;
	}
	
	/**
	 * @return Path to the config/datadock_jobs.xml file
	 */
	public static String getFileSystemDatadockPath() 
	{
		FileSystemConfig fc = new FileSystemConfig();
		return fc.getDatadock();
	} 
	
	
	/* ****************
	 * FILESYSTEM PTI *
	 * ***************/
	private String getPti()
	{
		String ret = config.getString( "filesystem.pti" );
		return ret;
	}
	
	/**
	 * @return Path to the config/pti_jobs.xml file
	 */	
	public static String getFileSystemPtiPath()
	{		
		FileSystemConfig fc = new FileSystemConfig();
		return fc.getPti();
	}
	
	
	/* ****************
	 * FILESYSTEM CPM *
	 * ***************/
	private String getCpm()
	{
		String ret = config.getString( "filesystem.cpm" );
		return ret;
	}
	
	/**
	 * @return Path to the config/pti_jobs.xml file
	 */	
	public static String getFileSystemCpmPath()
	{		
		FileSystemConfig fc = new FileSystemConfig();
		return fc.getCpm();
	}
}
