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


import dk.dbc.opensearch.common.fedora.FedoraHandle;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.pluginframework.ICreateCargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;

import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Description of the task that is to be performed by
 * DocbookHarvester
 *
 */
public class DocbookHarvester implements ICreateCargoContainer
{
    Logger log = Logger.getLogger( DocbookHarvester.class );

    
    private String submitter;
    private String format;
    private byte[] data;

    private PluginType pluginType = PluginType.HARVEST;
    private final FedoraHandle fedoraHandle;

    public DocbookHarvester() throws PluginException
    {
        try
        {
            this.fedoraHandle = new FedoraHandle();
        }
        catch( ObjectRepositoryException ex )
        {
            String error = String.format( "Failed to get connection to fedora base" );
            log.error( error );
            throw new PluginException( error, ex );

        }
    }
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


    public CargoContainer getCargoContainer( DatadockJob job, byte[] data ) throws PluginException
    {
    	//this.path = job.getUri().getPath();
    	this.submitter = job.getSubmitter();
    	this.format = job.getFormat();
        this.data = data;
    	return createCargoContainerFromFile();
    }


    /**
     *
     * @return the CargoContainer from
     * @throws IOException if the data cannot be read
     */
    private CargoContainer createCargoContainerFromFile() throws PluginException
    {
        String[] pid = null;
        try
        {
            pid = fedoraHandle.getNextPID( 1,  this.submitter );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Could not get pid for %s", this.submitter );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Could not get pid for %s", this.submitter );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Could not get pid for %s", this.submitter );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not get pid for %s", this.submitter );
            log.error( error );
            throw new PluginException( error, ex );
        }
        catch( IllegalStateException ex )
        {
            String error = String.format( "Could not get pid for %s", this.submitter );
            log.error( error );
            throw new PluginException( error, ex );
        }
        if( null == pid && 1 != pid.length )
        {
            String error = String.format( "pid is empty for namespace '%s', but no exception was caught.", this.submitter );
            log.error( error );
            throw new PluginException( new IllegalStateException( error ) );
        }

        CargoContainer cargo = new CargoContainer( pid[0] );
        
        /** \todo: hardcoded values for mimetype, langugage*/
        String mimetype = "text/xml";
        String lang = "da";
        DataStreamType dataStreamName = DataStreamType.OriginalData;

        long id;

        try
        {
            id = cargo.add( dataStreamName, this.format, this.submitter, lang, mimetype, IndexingAlias.Article, data );

            log.trace( "Constructing DC datastream" );

            DublinCore dcStream = new DublinCore( pid[0] );

            cargo.addMetaData( dcStream );
        }
        catch ( IOException ioe )
        {
        	String msg = String.format( "Could not construct CargoContainer %s", ioe.getMessage() );
        	log.error( msg );
            throw new PluginException( msg, ioe );
        }
        
        return cargo;
    }

    
    public PluginType getPluginType()
    {
        return pluginType;
    }
}











