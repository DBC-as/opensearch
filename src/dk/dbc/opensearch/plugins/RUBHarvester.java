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

import dk.dbc.opensearch.common.xml.XMLUtils;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.pluginframework.ICreateCargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.os.PdfFileFilter;
import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.File;
import java.io.FilenameFilter;
import java.io.InputStream;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


/**
 * Plugin class for harvesting RUB data.
 */
public class RUBHarvester implements ICreateCargoContainer
{
    private static Logger log = Logger.getLogger( RUBHarvester.class );


    private PluginType pluginType = PluginType.HARVEST;

    private String submitter;
    private String format;
    private byte[] referenceData;
    private byte[] data;
    private String lang;

    /**
     * \todo: Implement this method
     *
     * @param data
     * @param xml
     * @return
     * @throws PluginException
     */
    public CargoContainer getCargoContainer( InputStream data, InputStream xml ) throws PluginException
    {
        throw new PluginException( "Not implemented yet!" );
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
    public CargoContainer getCargoContainer( DatadockJob job, byte[] data ) throws PluginException
    {
        log.debug( "getCargoContainer() called" );

        this.data = data;
        submitter = job.getSubmitter();
        format = job.getFormat();
        this.referenceData = job.getReferenceData();
        String mimetype = "application/pdf";
        Element root = null;
        try
        {
            root = XMLUtils.getDocumentElement( referenceData );
        }
        catch( Exception e )
        {
            throw new PluginException( "Tried to get root element of the referenceData", e );
        }

        Element info = (Element)root.getElementsByTagName( "info" ).item( 0 );
        lang = info.getAttribute( lang );

        log.debug( String.format( "values: submitter='%s', format='%s', lang = %s", submitter, format, lang  ) );

        CargoContainer cargoContainer = new CargoContainer();

        try
        {
            cargoContainer.add( DataStreamType.OriginalData, format, submitter, lang, mimetype, IndexingAlias.Article, data );
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
}
