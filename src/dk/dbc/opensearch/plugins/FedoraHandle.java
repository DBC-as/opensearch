/**
 * 
 */
package dk.dbc.opensearch.plugins;

import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.XMLConfiguration;

import info.fedora.www.definitions._1._0.api.FedoraAPIA;
import info.fedora.www.definitions._1._0.api.FedoraAPIAServiceLocator;
import info.fedora.www.definitions._1._0.api.FedoraAPIM;
import info.fedora.www.definitions._1._0.api.FedoraAPIMServiceLocator;

/**
 * FedoraStore act as the plugin communication link with the fedora base. The
 * only function of this (abstract) class is to establish the SOAP communication
 * layer.
 */
public abstract class FedoraHandle {

	private FedoraAPIMServiceLocator m_locator;
	private FedoraAPIAServiceLocator a_locator;
	
	protected FedoraAPIM fem;
	protected FedoraAPIA fea;
	/**
	 * The constructor handles the initiation of the connection with the
	 * fedora base
	 * 
	 * @throws ServiceException
	 */
	public FedoraHandle() throws ServiceException {
        /**
         * If needed by the fedora base, we can construct/get credentials here.
                   
		log.debug( "Obtain config parameters for the fedora user");
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );
        
        //host       = config.getString( "fedora.host" );
        //port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        
        passphrase = config.getString( "fedora.passphrase" );
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );
        */
        
		m_locator = new FedoraAPIMServiceLocator();
		// locator.setMaintainSession( false );
		fem = m_locator.getFedoraAPIMServiceHTTPPort();
		
		a_locator = new FedoraAPIAServiceLocator();
		fea = a_locator.getFedoraAPIAPortSOAPHTTP();
	}
}
