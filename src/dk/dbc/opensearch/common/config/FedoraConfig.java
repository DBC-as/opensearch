/**
 * 
 */
package dk.dbc.opensearch.common.config;


/**
 * @author mro
 *
 */
public class FedoraConfig extends Config
{
	/* *************
	 * FEDORA HOST *
	 * *************/
	private String getFedoraHost()
	{
		String ret = config.getString( "fedora.host" );
		return ret;
	}
	
	
	public static String getHost() 
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
	
	
	public static String getPort()
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
	
	
	public static String getUser()
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
	
	
	public static String getPassPhrase()
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getFedoraPassPhrase();
	}
}
