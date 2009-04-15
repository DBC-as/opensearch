/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to pid manager settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class PidManagerConfig extends Config
{
	public PidManagerConfig() throws ConfigurationException 
	{
		super();
	}


	/* NUMBER OF PIDS TO RETRIEVE */
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
