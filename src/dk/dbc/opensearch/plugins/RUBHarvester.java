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

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;

import java.io.InputStream;
import java.io.IOException;

import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;


/**
 * Plugin class for harvesting RUB data.
 */
public class RUBHarvester implements IPluggable
{
    private static Logger log = Logger.getLogger( RUBHarvester.class );

    private PluginType pluginType = PluginType.HARVEST;
    private IObjectRepository repository; 

    public RUBHarvester( IObjectRepository repository )
    {
        this.repository = repository;
    }

    /**
     * The getCargoContainer returns a cargoContainer with the data
     * described in the datadockJob given in the init method.
     *
     * @return the CargoContainer
     * @throws IOException if the data cannot be read
     * @throws ParserConfigurationException
     * @throws SAXException
     */
    public CargoContainer getCargoContainer( DatadockJob job, byte[] data, String alias ) throws PluginException
    {
        log.debug( "getCargoContainer() called" );

        String submitter = job.getSubmitter();
        String format = job.getFormat();
        String mimetype = "application/pdf";
        Element root = null;
        root = job.getReferenceData().getDocumentElement();

        Element info = (Element)root.getElementsByTagName( "info" ).item( 0 );
        String lang = info.getAttribute( "lang" );

        log.debug( String.format( "values: submitter='%s', format='%s', lang = %s", submitter, format, lang  ) );

        CargoContainer cargoContainer = new CargoContainer();

        try
        {
            cargoContainer.add( DataStreamType.OriginalData, format, submitter, lang, mimetype, alias, data );
        }
        catch (IOException ioe)
        {
            throw new PluginException( "Could not construct CargoContainer", ioe );
        }

        return cargoContainer;
    }


    public PluginType getPluginType()
    {
        return pluginType;
    }

    /**
     * satisfying the interface
     */

    @Override
    public CargoContainer getCargoContainer( CargoContainer cargo, Map<String, String> argsMap ) throws PluginException
    {
        return null;
    }

   
    private boolean validateArgs( Map<String, String> argsMap )
    {
        return true;
    }
}
