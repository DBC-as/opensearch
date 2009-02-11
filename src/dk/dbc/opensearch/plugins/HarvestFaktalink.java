package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.IPluggableGeneralPurpose;
import dk.dbc.opensearch.common.pluginframework.PluginID;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


public class HarvestFaktalink implements IPluggableGeneralPurpose
{
    Logger log = Logger.getLogger( "HarvestFaktalink" );

    private CargoContainer cargo;
    private PluginID id;
    private InputStream data;
    private String submitter;
    private String format;

    
    @Override
	public void init( CargoContainer cargo ) 
    {
		this.cargo = cargo;		
	}
    
    
//    public void init( PluginID pluginId, InputStream data )
//    {
//        this.id = pluginId;
//        this.data = data;
//
//        this.submitter = id.getPluginSubmitter();
//        this.format = id.getPluginFormat();
//    }

    
    public PluginID getPluginID()
    {
        return id;
    }

    
    public String getTaskName()
    {
        return id.getPluginTask();
    }


    public CargoContainer getCargoContainer() throws IOException
    {
        return createCargoContainerFromFile();
    }


    private CargoContainer createCargoContainerFromFile() throws IOException 
    {
        String mimetype = "text/xml";
        String lang = "DA";

        //CargoContainer cc = new CargoContainer();
        //cc.add( format, submitter, lang, mimetype, data);
        cargo.add( format, submitter, lang, mimetype, data );
        //return cc;
        return cargo;
    }
}











