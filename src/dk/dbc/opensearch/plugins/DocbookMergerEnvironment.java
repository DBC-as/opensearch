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

/**
 * \file
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class DocbookMergerEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( DocbookMergerEnvironment.class );

    public DocbookMergerEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
    }
    /**
     * main method of the plugin. Takes the original data and dc data from a 
     * Cargocontainer, merge them and overwrites the orignal data in the 
     * cargo container.
     * @param cargo the cargocontainer containing the data to be merged
     * @return a CargoContainer with the merged (original and dc of the 
     * cargocontainer given as parameter) data in the original data stream 
     */
    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {
        byte[] dc = cargo.getCargoObject( DataStreamType.DublinCoreData ).getBytes();

        CargoObject orig = cargo.getCargoObject( DataStreamType.OriginalData );

        byte[] orig_bytes = orig.getBytes();
        ByteArrayInputStream is = new ByteArrayInputStream( orig_bytes );
        Document doc = null;
        try
        {
            doc = XMLUtils.documentFromInputStream( is );
        }
        catch( ParserConfigurationException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not create XML Document from OriginalData in CargoContainer: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }


        Element origRoot = doc.getDocumentElement();

        ByteArrayInputStream dc_is = new ByteArrayInputStream( dc );

        String new_original_data = null;

        try
        {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document tingDoc = builder.newDocument();

            log.debug( "Creating top level element for new xml" );
            Element tingElement = tingDoc.createElementNS( "http://www.dbc.dk/ting/", "ting:container" );

            tingDoc.appendChild( tingElement );

            Document dCDoc = XMLUtils.documentFromInputStream( dc_is );
            Element dcRoot = dCDoc.getDocumentElement();

            log.debug( "Adding DublinCore metadata to new xml" );
            tingDoc.adoptNode( dcRoot );

            log.debug( "Adding OriginalData to new xml" );
            tingDoc.adoptNode( origRoot );

            new_original_data = XMLUtils.xmlToString( tingDoc );

        }
        catch( ParserConfigurationException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( SAXException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( TransformerException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Failed to merge original data and metadata in new xml document: %s", ex.getMessage() );
            log.error( error, ex );
            throw new PluginException( error, ex );
        }

        log.trace( String.format( "Original xml: %s", new String( orig.getBytes() ) ) );
        log.trace( String.format( "New xml: %s", new_original_data ) );

        log.debug( String.format( "Removing data with id %s", orig.getId() ) );
        if( !cargo.remove( orig.getId() ) )
        {
            log.warn( String.format( "Could not remove data with id %s", orig.getId() ) );
        }

        try
        {
            cargo.add( orig.getDataStreamType(),
                    orig.getFormat(),
                    orig.getSubmitter(),
                    orig.getLang(),
                    orig.getMimeType(),
                    new_original_data.getBytes() );
        }
        catch( IOException ioe )
        {
            String error = String.format( "Could not replace original data in CargoContainer", ioe.getMessage() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        log.trace( String.format( "New xml data: %s", new String( cargo.getCargoObject( orig.getDataStreamType() ).getBytes() ) ) );
        return cargo;

    }

}