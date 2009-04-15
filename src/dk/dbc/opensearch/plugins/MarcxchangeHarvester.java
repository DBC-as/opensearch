package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.StreamHandler;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


public class MarcxchangeHarvester implements IHarvestable
{
    Logger log = Logger.getLogger( MarcxchangeHarvester.class );

    //private CargoContainer cargo;
    private String submitter;
    private String format;
    private String path;

    private PluginType pluginType = PluginType.HARVEST;

    public CargoContainer getCargoContainer( DatadockJob job ) throws PluginException
    {
        //cargo = new CargoContainer();
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
    private CargoContainer createCargoContainerFromFile() throws PluginException
    {
        CargoContainer cargo = new CargoContainer();
        cargo.setIndexingAlias( IndexingAlias.Danmarcxchange );
        cargo.setFilePath( path );
        /** \todo: hardcoded values for mimetype, langugage and data type */
        String mimetype = "text/xml";
        String lang = "da";
        DataStreamType dataStreamName = DataStreamType.OriginalData;
        InputStream data;
        try {
            data = FileHandler.readFile( path );
            log.debug( String.format( "File: %s has been read",path ) );
        } catch (FileNotFoundException fnfe) {
            throw new PluginException( String.format( "The file %s could not be found or read", this.path ), fnfe );
        }

        byte[] bdata;
        try {
            bdata = StreamHandler.bytesFromInputStream( data, 0 );
            log.debug(String.format("the data read has size: %s", bdata.length));
        } catch (IOException ioe) {
            throw new PluginException( "Could not construct byte[] from InputStream", ioe );
        }

        try {
            cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, bdata );
        } catch (IOException ioe) {
            throw new PluginException( "Could not construct CargoContainer", ioe );
        }catch(Exception e){
            log.error( String.format( "Exception of type: %s cast with message: %s", e.getClass(), e.getMessage() ) );
        }
        log.debug(String.format("num of objects in cargo: %s", cargo.getItemsCount()) );
        return cargo;
    }

    public PluginType getTaskName()
    {
        return pluginType;
    }

}