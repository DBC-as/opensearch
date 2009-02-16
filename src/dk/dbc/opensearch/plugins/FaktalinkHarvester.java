package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;

import java.io.FileInputStream;
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

    
    @Override
	public void init( DatadockJob job )
    {
    	this.path = job.getUri().getPath();
		this.submitter = job.getSubmitter();
		this.format = job.getFormat();
	}
    

    public CargoContainer getCargoContainer() throws IOException
    {
        return createCargoContainerFromFile();
    }


    /**
     * 
     * @return the CargoContainer from 
     * @throws IOException if the data cannot be read
     */
    private CargoContainer createCargoContainerFromFile() throws IOException 
    {
    	/** \todo: hardcoded values for mimetype, langugage and data type */
        String mimetype = "text/xml";
        String lang = "DA";
        DataStreamNames dataStreamName = DataStreamNames.OriginalData;
        InputStream data = new FileInputStream( this.path );
        
        cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, data );
        
        return cargo;
    }
}











