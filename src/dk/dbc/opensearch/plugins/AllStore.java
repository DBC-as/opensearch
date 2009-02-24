/**
 * \file AllStore.java
 * \brief The AllStore class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.pluginframework.IRepositoryStore;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;
import java.text.ParseException;

import javax.xml.rpc.ServiceException;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;


/**
 *
 */
public class AllStore extends FedoraHandle implements IRepositoryStore
{
    Logger log = Logger.getLogger( AllStore.class );

    private PluginType pluginType = PluginType.STORE;
    private CargoContainer cargo;


    public AllStore() throws ServiceException
    {
        super();
    }    
    
    
    public float storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException
    {
        this.cargo = cargo;
        return this.storeCargo( job );
    }


    private float storeCargo( DatadockJob job ) throws ServiceException, MarshalException, ValidationException, IOException, ParseException, IllegalStateException
    {
        byte[] foxml = FedoraTools.constructFoxml( this.cargo, job.getPID(), job.getFormat() );

        String logm = String.format( "%s inserted", job.getFormat() );
        String pid = super.fem.ingest( foxml, job.getFormat(), logm);
        
        log.info( String.format( "Submitted data, returning pid %s", pid ) );

        return Float.parseFloat( pid );
    }

    
    public PluginType getTaskName()
    {
    	return pluginType;
    }
}

