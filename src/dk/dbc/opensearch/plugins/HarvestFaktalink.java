package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginID;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;

public class HarvestFaktalink implements IHarvestable
{
    Logger log = Logger.getLogger( "HarvestFaktalink" );

    private PluginID id;
    private InputStream data;
    private String submitter;
    private String format;

    
    public void init( PluginID pluginId, InputStream data )
    {
        this.id = pluginId;
        this.data = data;

        this.submitter = id.getPluginSubmitter();
        this.format = id.getPluginFormat();
    }

    public PluginID getPluginID()
    {
        return id;
    }

    public String getTaskName()
    {
        return id.getPluginTask();
    }


    public CargoContainer getCargoContainer() throws IllegalArgumentException, NullPointerException, IOException
    {
        return createCargoContainerFromFile();
    }


    private CargoContainer createCargoContainerFromFile() throws IllegalArgumentException, NullPointerException, IOException
    {
        String mimetype = "text/xml";
        String lang = "DA";

        CargoContainer cc = new CargoContainer();
        cc.add( format, submitter, lang, mimetype, data);
        
        return cc;
    }
}











