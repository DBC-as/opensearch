/**
 * 
 */
package dk.dbc.opensearch.common.config;


import org.apache.commons.configuration.ConfigurationException;


/**
 * @author mro
 * 
 * Sub class of Config providing access to fedora settings in the 
 * configuration file. Method names should be explanatory enough.
 * 
 * See super class Config for description of methodology.
 *
 */
public class FedoraConfig extends Config
{
	public FedoraConfig() throws ConfigurationException 
	{
		super();
	}


	/* HOST */
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
	
	
	/* PORT */
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
	
	
	/* USER */
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
	
	
	/* PASS PHRASE */
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
