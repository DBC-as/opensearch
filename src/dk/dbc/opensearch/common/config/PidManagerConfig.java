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
	private String getPidManagerNumberOfPidsToRetrieve()
	{
		String ret = config.getString( "pidmanager.num-of-pids-to-retrieve" );
		return ret;
	}
	
	
	public static String getNumberOfPidsToRetrieve() 
	{
		PidManagerConfig pmc = new PidManagerConfig();
		return pmc.getPidManagerNumberOfPidsToRetrieve();
	} 
}
