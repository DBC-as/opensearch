/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class PidManagerConfig extends Config
{
	private String getNumberOfPidsToRetrieve()
	{
		String ret = config.getString( "pidmanager.num-of-pids-to-retrieve" );
		return ret;
	}
	
	
	public static String getPidManagerNumberOfPidsToRetrieve() 
	{
		PidManagerConfig pmc = new PidManagerConfig();
		return pmc.getNumberOfPidsToRetrieve();
	} 
}
