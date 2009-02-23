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
	private String getTrunkPath()
	{
		String ret = config.getString( "filesystem.trunk" );
		if ( ret.endsWith( "/" ) )
			return ret;
		else
			return ret + "/";
	}
	
	
	public static String getFileSystemTrunkPath() 
	{
		FileSystemConfig f = new FileSystemConfig();
		return f.getTrunkPath();
	} 
}
