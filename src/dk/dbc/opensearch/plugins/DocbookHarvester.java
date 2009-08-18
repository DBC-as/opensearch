/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.StreamHandler;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import org.apache.log4j.Logger;


/**
 * Description of the task that is to be performed by
 * DocbookHarvester
 *
 */
public class DocbookHarvester implements IHarvestable
{
    Logger log = Logger.getLogger( DocbookHarvester.class );

    
    private String submitter;
    private String format;
    private String path;

    private PluginType pluginType = PluginType.HARVEST;

    
    /**
     * \todo: Implement this method
     *
     * @param data
     * @param xml
     * @return
     * @throws PluginException
     */
    public CargoContainer getCargoContainer( InputStream data, InputStream xml) throws PluginException
    {
        throw new PluginException( "Not implemented yet!" );
    }


    public CargoContainer getCargoContainer( DatadockJob job ) throws PluginException
    {
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
        
        /** \todo: hardcoded values for mimetype, langugage and data type */
        String mimetype = "text/xml";
        String lang = "da";
        DataStreamType dataStreamName = DataStreamType.OriginalData;
        InputStream data;

        long id;
        try 
        {
            data = FileHandler.readFile( this.path );
        } 
        catch (FileNotFoundException fnfe) 
        {
            throw new PluginException( String.format( "The file %s could not be found or read", this.path ), fnfe );
        }

        byte[] bdata;
        try 
        {
            bdata = StreamHandler.bytesFromInputStream( data, 0 );
        } 
        catch (IOException ioe) 
        {
            throw new PluginException( "Could not construct byte[] from InputStream", ioe );
        }

        try
        {
            id = cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, IndexingAlias.Article, bdata );
            log.info( String.format( "Added data to the cargocontainer. Id = %s", id ) );
        } 
        catch (IOException ioe) 
        {
            throw new PluginException( "Could not construct CargoContainer", ioe );
        }

        if( ! cargo.hasCargo( dataStreamName ) ) 
        {
            throw new PluginException( new IllegalStateException( String.format( "Failed to construct CargoContainer with data in it (id = %s)", id ) ) );
        }
        
        return cargo;
    }

    
    public PluginType getPluginType()
    {
        return pluginType;
    }
}











