/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package common.types
 */
package dk.dbc.opensearch.common.types;

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

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


// import dk.dbc.opensearch.xsd.DigitalObject;
// import dk.dbc.opensearch.xsd.Datastream;
// import dk.dbc.opensearch.xsd.DatastreamVersion;
// import dk.dbc.opensearch.xsd.DatastreamVersionTypeChoice;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;


/**
 * \ingroup common.types
 * \brief CargoContainer is a data structure used throughout OpenSearch for carrying information
 *  submitted for indexing. CargoContainer retains data in a private data structure consisting of 
 *  CargoObject objects. All verification and work with theses objects are done through the 
 *  CargoObject class.  
 */
public class CargoContainer 
{
	Logger log = Logger.getLogger( CargoContainer.class );

	
    /** The internal representation of the data contained in the CC*/
    private ArrayList< CargoObject > data;
    private IndexingAlias indexingAlias = null;
    /** The path to the file on disk where it is harvested from, only for debugging purposes */
    /** \todo: WTF?*/
    private String filePath = null;
    
    /**
     * Constructor initializes internal representation of data, i.e., ArrayList of CargoObjects
     */
    public CargoContainer()
    {
    	this.data = new ArrayList< CargoObject >();
    }
  
    /**
     * Add CargoObject to internal data representation.
     * 
     * @param format
     * @param submitter
     * @param language
     * @param mimetype
     * @param data
     * @throws IOException
     */
    public void add( DataStreamType dataStreamName, String format, String submitter, String language, String mimetype, byte[] data ) throws IOException
    {
    	CargoObject co = new CargoObject( dataStreamName, mimetype, language, submitter, format, data );

        log.debug( String.format("length of data in the corgobobject just created: %s ",co.getContentLength() ) );

    	this.data.add( co );    	
        log.debug("cargoObject added to container");
        log.debug( String.format( "number of CargoObjects: %s", this.getItemsCount() ) );
    }


    /**
     * Getter for internal data, the validity of which is guaranteed by the Constructor.
     * 
     * @return ArrayList of CargoObjects.
     */
    public ArrayList< CargoObject > getData()
    {
    	return data;
    }


    public CargoObject getFirstCargoObject( DataStreamType dsn )
    {
        CargoObject rco = null;
        for( CargoObject co : data )
            if( co.getDataStreamName() == dsn )
            {
                rco = co;        
            }    
        return rco;
    }
        
    
    /**
     * Getter for size, i.e., No. of CargoObjects in internal data representation.
     * 
     * @return
     */
    public int getItemsCount()
    {
    	return data.size();
    }

    /**
     * setter for the indexingAlias
     */
    public void setIndexingAlias( IndexingAlias indexingAlias )
    {
        this.indexingAlias = indexingAlias;
    }

    /**
     * getter for the indexing alias
     */
    public IndexingAlias getIndexingAlias()
    {
        return indexingAlias;
    } 
    
    /**
     * setter for the filePath
     */
    public void setFilePath( String filePath )
    {
        this.filePath = filePath;
    }

    /**
     * getter for the filePath
     */
    public String getFilePath()
    {
        return filePath;
    }
}
