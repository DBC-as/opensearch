/**
 * 
 */
package dk.dbc.opensearch.common.config;

import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 *
 */
public class FedoraConfig extends Config
{
	public FedoraConfig() throws ConfigurationException 
	{
		super();
	}


	/* *************
	 * FEDORA HOST *
	 * *************/
	private String getFedoraHost()
	{
		String ret = config.getString( "fedora.host" );
		return ret;
	}
	
	
	public static String getHost() throws ConfigurationException 
	{
		FedoraConfig fc = new FedoraConfig();		
		return fc.getFedoraHost();
	}
	
	
	/* *************
	 * FEDORA PORT *
	 * *************/
	private String getFedoraPort()
	{
		String ret = config.getString( "fedora.port" );
		return ret;
	}
	
	
	public static String getPort() throws ConfigurationException
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getFedoraPort();
	}
	
	
	/* *************
	 * FEDORA USER *
	 * *************/
	private String getFedoraUser()
	{
		String ret = config.getString( "fedora.user" );
		return ret;
	}
	
	
	public static String getUser() throws ConfigurationException
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getFedoraUser();
	}
	
	
	/* *******************
	 * FEDORA PASSPHRASE *
	 * ******************/
	private String getFedoraPassPhrase()
	{
		String ret = config.getString( "fedora.passphrase" );
		return ret;
	}
	
	
	public static String getPassPhrase() throws ConfigurationException
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getFedoraPassPhrase();
	}
}
