package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;

import org.apache.log4j.Logger;


public class FaktalinkHarvester implements IHarvestable
{
    Logger log = Logger.getLogger( FaktalinkHarvester.class );

    private CargoContainer cargo;
    private String submitter;
    private String format;
    private String path;
    private URI uri;

    
    @Override
	public void init( URI uri, String submitter, String format )
    {
    	this.uri = uri;
    	this.path = this.uri.getPath();
		this.submitter = submitter;
		this.format = format;
	}
    

    /**
     * 
     */
    public CargoContainer getCargoContainer() throws IOException
    {
        return createCargoContainerFromFile();
    }


    /**
     * 
     * @return
     * @throws IOException
     */
    private CargoContainer createCargoContainerFromFile() throws IOException 
    {
        String mimetype = "text/xml";
        String lang = "DA";
        InputStream data = new FileInputStream( this.path );
        
        cargo.add( this.format, this.submitter, lang, mimetype, data );
        
        return cargo;
    }
}











