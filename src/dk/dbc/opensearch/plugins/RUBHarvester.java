/**
 * \file RUBHarvester.java
 * \brief The RUBHarvester class
 * \package plugins;
 */

package dk.dbc.opensearch.plugins;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.pluginframework.IHarvestable;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoObject;

import java.io.FileInputStream;
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
    public CargoContainer getCargoContainer( DatadockJob job ) throws IOException, ParserConfigurationException, SAXException
    { 
    	log.debug( "init( datadockJob ) called" );
        
        path = job.getUri().getPath();
        submitter = job.getSubmitter();
        format = job.getFormat();
        log.debug( String.format( "values: uri='%s', submitter='%s', format='%s'", path, submitter, format ) );
        
        log.debug( "getCargoContainer() called" );

        CargoContainer cargoContainer = new CargoContainer();
        String mimetype = "application/pdf";

        log.debug( "read and add xml to cargoContainer" );
       
        FilenameFilter[] xmlFilter = { new XmlFileFilter() };
        String xmlFilePath = FileHandler.getFileList( path, xmlFilter, false ).remove(0);        
        log.debug( String.format( "xml filepath='%s'", xmlFilePath ) );
        File xmlFile = FileHandler.getFile( xmlFilePath );
        FileInputStream xmlData = FileHandler.readFile( xmlFilePath ); 

        byte xmlBytes[] = new byte[(int)xmlFile.length()];
        xmlData.read(xmlBytes);

        log.debug( "get language of pdf from dublin core file" );
       
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();     
        Document document = builder.parse( xmlFile );

        NodeList dcoai = document.getElementsByTagName( "oai_dc:dc" );
        // assert dcoai.getLength() == 1
        NodeList dcLanguage = ( (Element) dcoai.item( 0 ) ).getElementsByTagName( "dc:language" );
        // assert dcLanguage.getLength() == 1
        NodeList language = ( (Element) dcLanguage.item( 0 ) ).getChildNodes();
        // assert language.getLength() == 1
        String lang = language.item(0).getNodeValue();

        cargoContainer.add( DataStreamNames.DublinCoreData, format, submitter, lang, mimetype, xmlBytes );
       
        log.debug( "read and add pdf to cargoContainer" );

        FilenameFilter[] pdfFilter = { new PdfFileFilter() };
        String pdfFilePath = FileHandler.getFileList( path, pdfFilter, false ).remove(0);
        log.debug( String.format( "pdf filepath='%s'", pdfFilePath ) );
        File pdfFile = FileHandler.getFile( pdfFilePath );
        FileInputStream pdfData = FileHandler.readFile( pdfFilePath );
        
        byte pdfBytes[] = new byte[(int)pdfFile.length()];
        pdfData.read( pdfBytes );

        cargoContainer.add( DataStreamNames.OriginalData, format, submitter, lang, mimetype, pdfBytes );
        
        return cargoContainer; 
    }

	
	public PluginType getTaskName() 
	{	
		return pluginType;
	}
}
