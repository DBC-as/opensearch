/**
 * 
 */
package dk.dbc.opensearch.common.helpers;


/**
 * @author mro
 *
 */
public class FedoraConfig extends Config
{
	/* *************
	 * FEDORA HOST *
	 * *************/
	private String getHost()
	{
		String ret = config.getString( "fedora.host" );
		return ret;
	}
	
	
	public static String getFedoraHost() 
	{
		FedoraConfig fc = new FedoraConfig();		
		return fc.getHost();
	}
	
	
	/* *************
	 * FEDORA PORT *
	 * *************/
	private String getPort()
	{
		String ret = config.getString( "fedora.port" );
		return ret;
	}
	
	
	public static String getFedoraPort()
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getPort();
	}
	
	
	/* *************
	 * FEDORA USER *
	 * *************/
	private String getUser()
	{
		String ret = config.getString( "fedora.user" );
		return ret;
	}
	
	
	public static String getFedoraUser()
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getUser();
	}
	
	
	/* *******************
	 * FEDORA PASSPHRASE *
	 * ******************/
	private String getPassPhrase()
	{
		String ret = config.getString( "fedora.passphrase" );
		return ret;
	}
	
	
	public static String getFedoraPassPhrase()
	{
		FedoraConfig fc = new FedoraConfig();
		return fc.getPassPhrase();
	}
}
