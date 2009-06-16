/**
 * \file PTIPool.java
 * \brief The PTIPool class
 * \package pti;
 */
package dk.dbc.opensearch.plugins;

/**   
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


public class DCHarvester implements IHarvestable
{
    Logger log = Logger.getLogger( DCHarvester.class );

    private String submitter;
    private String format;
    private String path;

    private PluginType pluginType = PluginType.HARVEST;


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
        //cargo.setFilePath( path );
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
            cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, IndexingAlias.DC, bdata );
        } catch (IOException ioe) {
            throw new PluginException( "Could not construct CargoContainer", ioe );
        }catch(Exception e){
            log.error( String.format( "Exception of type: %s cast with message: %s", e.getClass(), e.getMessage() ) );
        }
        log.debug(String.format("num of objects in cargo: %s", cargo.getCargoObjectCount()) );
        return cargo;
    }

    public PluginType getTaskName()
    {
        return pluginType;
    }

}