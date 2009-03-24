/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class PidManagerConfig extends Config
{
	public PidManagerConfig() throws ConfigurationException 
	{
		super();
	}


	private String getPidManagerNumberOfPidsToRetrieve()
	{
		String ret = config.getString( "pidmanager.num-of-pids-to-retrieve" );
		return ret;
	}
	
	
	public static String getNumberOfPidsToRetrieve() throws ConfigurationException 
	{
		PidManagerConfig pmc = new PidManagerConfig();
		return pmc.getPidManagerNumberOfPidsToRetrieve();
	} 
}
