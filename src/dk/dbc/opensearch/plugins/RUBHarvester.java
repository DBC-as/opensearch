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

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;

import java.io.FilenameFilter;
import dk.dbc.opensearch.common.os.PdfFileFilter;
import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;

import java.util.Vector;

import org.apache.log4j.Logger;

/**
 * Plugin class for harvesting RUB data.
 */
public class RUBHarvester implements IHarvestable{
    
    Logger log = Logger.getLogger( RUBHarvester.class );

    private String submitter;
    private String format;
    private String path;

    /**
     * The init method for the RUBHarvester plugin.
     * 
     * @param job The jobinstance describing the specific job
     */
    public void init( DatadockJob job){        
        log.debug( "init( datadockJob ) called" );
        path = job.getUri().getPath();
        submitter = job.getSubmitter();
        format = job.getFormat();
        log.debug( String.format( "values: uri='%s', submitter='%s', format='%s'", path, submitter, format ) );
    }

    /**
     * The getCargoContainer returns a cargoContainer with the data
     * described in the datadockJob given in the init method.
     *  
     * @return the CargoContainer
     * @throws IOException if the data cannot be read
     */
    public CargoContainer getCargoContainer() throws IOException 
    { 
        log.debug( "getCargoContainer() called" );

        CargoContainer cargoContainer = new CargoContainer();

        String mimetype = "application/pdf";
        String lang = "EN"; // can we test the pdf doc for this?
        
        // read and add pdf to cargoContainer
        FilenameFilter[] pdfFilter = { new PdfFileFilter() };
        String pdfFilePath = FileHandler.getFileList( path, pdfFilter, false ).remove(0);
        log.debug( String.format( "pdf filepath='%s'", pdfFilePath ) );
        InputStream pdfData = FileHandler.readFile( pdfFilePath );
        cargoContainer.add( DataStreamNames.OriginalData, format, submitter, lang, mimetype, pdfData );
        
        // read and add xml to cargoContainer
        FilenameFilter[] xmlFilter = { new XmlFileFilter() };
        String xmlFilePath = FileHandler.getFileList( path, xmlFilter, false ).remove(0);
        log.debug( String.format( "xml filepath='%s'", xmlFilePath ) );
        InputStream xmlData = FileHandler.readFile( xmlFilePath ); 
        cargoContainer.add( DataStreamNames.DublinCoreData, format, submitter, lang, mimetype, xmlData );

        return cargoContainer; 
    }
}
