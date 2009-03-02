/**
 * \file Retrieve.java
 * \brief The Retrieve class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IRepositoryRetrieve;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import java.net.MalformedURLException;
import java.io.IOException;
import java.rmi.RemoteException;

import org.exolab.castor.xml.ValidationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Unmarshaller;

import dk.dbc.opensearch.xsd.Datastream;
// import dk.dbc.opensearch.xsd.DatastreamVersion;


import java.io.ByteArrayInputStream;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import dk.dbc.opensearch.xsd.DigitalObject;
/**
 * 
 */
public class Retrieve extends FedoraHandle implements IRepositoryRetrieve{
  /**
   * 
   */

    private PluginType pluginType = PluginType.RETRIEVE;

    public Retrieve() throws ServiceException, MalformedURLException, IOException 
    {
        super();
    }

    public CargoContainer getCargoContainer( String fedoraPid ) throws PluginException// RemoteException, MarshalException, ValidationException
    {
        
        // generate fedora url

        Unmarshaller unmarshaller = new Unmarshaller();

        byte[] foxml;
		try {
			foxml = super.fem.export( fedoraPid, "info:fedora/fedora-system:FOXML-1.1", "archive" );
		} catch (RemoteException re) {
			throw new PluginException( "The fedora base could not complete request export", re );
		}
        
        ByteArrayInputStream bis = new ByteArrayInputStream( foxml );
        InputSource iSource = new InputSource( bis );
        DigitalObject dot = null;
        try {
			 dot = (DigitalObject) unmarshaller.unmarshal( iSource );
		} catch (MarshalException me) {
			throw new PluginException( "The digital object could no be unmarshalled", me );
		} catch (ValidationException ve) {
			throw new PluginException( "The digital object failed to validate", ve );
		}
            
		try {
			CargoContainer cargo = FedoraTools.constructCargoContainerFromDOT(dot);
		} catch (ParserConfigurationException pce) {
			throw new PluginException( "Could not initialize the xml parser", pce );
		} catch (SAXException saxe) {
			throw new PluginException( "Could not parse the digital object xml", saxe );
		} catch (IOException ioe) {
			throw new PluginException( "Could not read the digital object into the CargoContainer", ioe );
		}
        return null;
    }

    
    /**
     *
     */
    public PluginType getTaskName()
    {
        return pluginType;
    }
}
