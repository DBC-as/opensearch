package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

public class HarvestFaktalink implements IHarvestable
{
	Logger log = Logger.getLogger( "HarvestFaktalink" );
	String xmlFilePath = "xml/faktalink_kanon.xml";
	
	private String id;
	private String path;
	private String submitter;
	private String format;
	
	
	public HarvestFaktalink( String pluginId, String path, String submitter, String format )
	{	
		this.id = pluginId;
		this.path = path;
		this.submitter = submitter;
		this.format = format;
	}
	
	
	public String getPluginID()
	{
		return id;
	}
	
	
    public String getTaskName()
    {
        return "harvest";
    }


    public CargoContainer getCargoContainer() throws IllegalArgumentException, NullPointerException, IOException
    {
    	return createCargoContainerFromFile();
    }

    
    private CargoContainer createCargoContainerFromFile() throws IllegalArgumentException, NullPointerException, IOException
    {
    	String mimetype = "";
    	String lang = "";
    	CargoContainer cc = null;
    	
    	File file = new File( path );
    	InputStream data = new FileInputStream( file );
    		
    	cc = new CargoContainer( data, mimetype, lang, submitter, format );
    	return cc;
    }
}











