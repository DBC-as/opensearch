/**
 * 
 */
package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.helpers.FedoraConfig;

import java.net.URL;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.axis.client.Stub;
import org.apache.axis.client.Call;

import info.fedora.www.definitions._1._0.api.FedoraAPIA;
import info.fedora.www.definitions._1._0.api.FedoraAPIAService;
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
	public FedoraHandle() throws ServiceException 
	{
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

        
        */

            String fedora_base_url;

            String host = FedoraConfig.getFedoraHost();
            String port = FedoraConfig.getFedoraPort();
            String user = FedoraConfig.getFedoraUser();
            String pass = FedoraConfig.getFedoraPassPhrase();

            fedora_base_url  = String.format( "http://%s:%s/fedora/services/management", host, port );

            m_locator = new FedoraAPIMServiceLocator();

            // log.debug( String.format( "Connecting to %s", fedora_base_url ) );

            m_locator.setFedoraAPIMServiceHTTPPortEndpointAddress( fedora_base_url );

            // locator.setMaintainSession( false );
            fem = m_locator.getFedoraAPIMServiceHTTPPort();

            // to use Basic HTTP Authentication:
            ( ( Stub ) fem )._setProperty( Call.USERNAME_PROPERTY, user );
            ( ( Stub ) fem )._setProperty( Call.PASSWORD_PROPERTY, pass );



            a_locator = new FedoraAPIAServiceLocator();
            fea = a_locator.getFedoraAPIAServiceHTTPPort();
                          //getFedoraAPIAPortSOAPHTTP();
	}
}
