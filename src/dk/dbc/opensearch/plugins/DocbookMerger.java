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

import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.commons.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 *
 */
public class DocbookMerger implements IPluggable
{

    private static Logger log = Logger.getLogger( DocbookMerger.class );
    private PluginType pluginType = PluginType.PROCESS;

    private NamespaceContext nsc;

    public DocbookMerger( IObjectRepository repository ) throws PluginException
    {
        log.debug( "Entered DocbookMerger()" );

        nsc = new OpensearchNamespaceContext();
    }


    /**
     * 'main' plugin method.
     *
     * The purpose of this plugin is to merge Originaldata contained in the
     * CargoContainer with DublinCore data, also contained in the CargoContainer.
     * 
     * @param cargo the input CargoContainer to be processed by the plugin
     * @return the modified CargoContainer
     * @throws PluginException with a nested exception explaining the error
     */
    @Override
    public CargoContainer runPlugin( IPluginEnvironment ienv, CargoContainer cargo ) throws PluginException
    {
	throw new PluginException( "This method is not meant to be called!" );
    }


    // Non-standard funktion - used by a hack in the PTIThread
    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {
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

        byte [] dcByteArray = cargo.getCargoObject( DataStreamType.DublinCoreData ).getBytes();

        ByteArrayInputStream dc_is = new ByteArrayInputStream( dcByteArray );

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
        //   log.debug( String.format( "Adding annotated data to CargoContainer with alias '%s', overwriting original data", orig.getIndexingAlias() ) );

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

        //  cargo.setIndexingAlias( orig.getIndexingAlias(), orig.getDataStreamType() );

        log.trace( String.format( "New xml data: %s", new String( cargo.getCargoObject( orig.getDataStreamType() ).getBytes() ) ) );
        return cargo;
    }


    @Override
    public PluginType getPluginType()
    {
        return this.pluginType;
    }


    @Override
    public IPluginEnvironment createEnvironment( IObjectRepository repository, Map< String, String > args ) throws PluginException
    {
    	return new DocbookMergerEnvironment( repository, args );
    }

}
