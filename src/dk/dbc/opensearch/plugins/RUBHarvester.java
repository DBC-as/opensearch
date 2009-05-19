/**
 * \file RUBHarvester.java
 * \brief The RUBHarvester class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;

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


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoObject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.io.File;
import java.io.FilenameFilter;
import dk.dbc.opensearch.common.os.PdfFileFilter;
import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;

import java.util.Vector;

import org.apache.log4j.Logger;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;



/**
 * Plugin class for harvesting RUB data.
 */
public class RUBHarvester implements IHarvestable{
    
    Logger log = Logger.getLogger( RUBHarvester.class );

    private PluginType pluginType = PluginType.HARVEST;
    
    private String submitter;
    private String format;
    private String path;


    /**
     * The getCargoContainer returns a cargoContainer with the data
     * described in the datadockJob given in the init method.
     *  
     * @return the CargoContainer
     * @throws IOException if the data cannot be read
     * @throws ParserConfigurationException 
     * @throws SAXException 
     */
    public CargoContainer getCargoContainer( DatadockJob job ) throws PluginException//IOException, ParserConfigurationException, SAXException
    { 
    	log.debug( "init( datadockJob ) called" );
        
        path = job.getUri().getPath();
        submitter = job.getSubmitter();
        format = job.getFormat();
        log.debug( String.format( "values: uri='%s', submitter='%s', format='%s'", path, submitter, format ) );
        
        log.debug( "getCargoContainer() called" );

        CargoContainer cargoContainer = new CargoContainer();
        //cargoContainer.setFilePath( path );
        String mimetype = "text/xml";

        log.debug( "read and add xml to cargoContainer" );
       
        FilenameFilter[] xmlFilter = { new XmlFileFilter() };
        String xmlFilePath = FileHandler.getFileList( path, xmlFilter, false ).remove(0);        
        log.debug( String.format( "xml filepath='%s'", xmlFilePath ) );
        File xmlFile = FileHandler.getFile( xmlFilePath );
        FileInputStream xmlData = null;
		try {
			xmlData = FileHandler.readFile( xmlFilePath );
		} catch (FileNotFoundException fnfe) {
			throw new PluginException( String.format( "The file %s could not be found or read", this.path ), fnfe );
		} 

        byte xmlBytes[] = new byte[(int)xmlFile.length()];
        try {
			xmlData.read(xmlBytes);
		} catch (IOException ioe) {
			throw new PluginException( "Could not read InputStream into byte[]", ioe );
		}

        log.debug( "get language of pdf from dublin core file" );
       
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
		try {
			builder = factory.newDocumentBuilder();
		} catch (ParserConfigurationException pce) {
			throw new PluginException( "Could not construct a DocumentBuilder", pce);
		}     
        Document document;
		try {
			document = builder.parse( xmlFile );
		} catch (SAXException saxe) {
			throw new PluginException( String.format( "The xml file %s could not be parsed as xml", xmlFilePath ), saxe );
		} catch (IOException ioe) {
			throw new PluginException( String.format( "The file %s could not be read", xmlFilePath ), ioe );
		}

        NodeList dcoai = document.getElementsByTagName( "oai_dc:dc" );
        // assert dcoai.getLength() == 1
        NodeList dcLanguage = ( (Element) dcoai.item( 0 ) ).getElementsByTagName( "dc:language" );
        // assert dcLanguage.getLength() == 1
        NodeList language = ( (Element) dcLanguage.item( 0 ) ).getChildNodes();
        // assert language.getLength() == 1
        String lang = language.item(0).getNodeValue();

        try {
			cargoContainer.add( DataStreamType.DublinCoreData, format, submitter, lang, mimetype, IndexingAlias.None, xmlBytes );
		} catch (IOException ioe) {
			throw new PluginException( "Could not construct CargoContainer", ioe );
		}
       
        log.debug( "read and add pdf to cargoContainer" );

        mimetype = "application/pdf";
        FilenameFilter[] pdfFilter = { new PdfFileFilter() };
        String pdfFilePath = FileHandler.getFileList( path, pdfFilter, false ).remove(0);
        log.debug( String.format( "pdf filepath='%s'", pdfFilePath ) );
        File pdfFile = FileHandler.getFile( pdfFilePath );
        FileInputStream pdfData;
		try {
			pdfData = FileHandler.readFile( pdfFilePath );
		} catch (FileNotFoundException fnfe) {
			throw new PluginException( String.format( "The file %s could not be found or read", pdfFilePath ), fnfe );
		}
        
        byte pdfBytes[] = new byte[(int)pdfFile.length()];
        try {
			pdfData.read( pdfBytes );
		} catch (IOException ioe) {
			throw new PluginException( "Could not read InputStream into byte[]", ioe );
		}

        try {
			cargoContainer.add( DataStreamType.OriginalData, format, submitter, lang, mimetype, IndexingAlias.Article, pdfBytes );
		} catch (IOException ioe) {
			throw new PluginException( "Could not construct CargoContainer", ioe );
		}
        
        return cargoContainer; 
    }

	
	public PluginType getTaskName() 
	{	
		return pluginType;
	}
}
