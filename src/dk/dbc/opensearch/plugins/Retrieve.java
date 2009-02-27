/**
 * \file Retrieve.java
 * \brief The Retrieve class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IRepositoryRetrieve;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

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

    public CargoContainer getCargoContainer( String fedoraPid ) throws RemoteException, MarshalException, ValidationException
    {
        
        
        
        // generate fedora url

        Unmarshaller unmarshaller = new Unmarshaller();

        byte[] foxml = super.fem.export( fedoraPid, "info:fedora/fedora-system:FOXML-1.1", "archive" );
        
        ByteArrayInputStream bis = new ByteArrayInputStream( foxml );
        InputSource iSource = new InputSource( bis );

        DigitalObject dot = (DigitalObject) unmarshaller.unmarshal( iSource );
            
            
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
