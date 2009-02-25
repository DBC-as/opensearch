package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.os.StreamHandler;

import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


public class FaktalinkHarvester implements IHarvestable
{
    Logger log = Logger.getLogger( FaktalinkHarvester.class );

    private CargoContainer cargo;
    private String submitter;
    private String format;
    private String path;

    private PluginType pluginType = PluginType.HARVEST;
    
    public CargoContainer getCargoContainer( DatadockJob job ) throws IOException
    {
        cargo = new CargoContainer();
    	this.path = job.getUri().getPath();
    	this.submitter = job.getSubmitter();
    	this.format = job.getFormat();
		
        return createCargoContainerFromFile();
    }


    /**
     * 
     * @return the CargoContainer from 
     * @throws IOException if the data cannot be read
     */
    private CargoContainer createCargoContainerFromFile() throws IOException 
    {
    	cargo = new CargoContainer();
    	
    	/** \todo: hardcoded values for mimetype, langugage and data type */
        String mimetype = "text/xml";
        String lang = "da";
        DataStreamNames dataStreamName = DataStreamNames.OriginalData;
        InputStream data = FileHandler.readFile( this.path );

        byte [] bdata = StreamHandler.bytesFromInputStream( data, 0 );
        
        cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, bdata );
        
        return cargo;
    }
    
    public PluginType getTaskName()
    {
    	return pluginType;
    }
}











