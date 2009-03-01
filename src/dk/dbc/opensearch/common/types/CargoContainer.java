/**
 * \file CargoContainer.java
 * \brief The CargoContainer class
 * \package common.types
 */
package dk.dbc.opensearch.common.types;

import java.io.InputStream;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.log4j.Logger;


import dk.dbc.opensearch.xsd.DigitalObject;
import dk.dbc.opensearch.xsd.Datastream;
import dk.dbc.opensearch.xsd.DatastreamVersion;
import dk.dbc.opensearch.xsd.DatastreamVersionTypeChoice;

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
    
    
    /**
     * Constructor initializes internal representation of data, i.e., ArrayList of CargoObjects
     */
    public CargoContainer()
    {
    	this.data = new ArrayList< CargoObject >();
    }
    
    public CargoContainer( DigitalObject dot ) throws ParserConfigurationException, SAXException, IOException
{
        log.debug( "Constructor( DigitalObject ) called" );
        
        this.data = new ArrayList< CargoObject >();

        Datastream adminStream = null;
        Datastream[] streams = dot.getDatastream();

        for( Datastream stream : streams ){
            if ( DataStreamNames.getDataStreamNameFrom( stream.getID() ) == DataStreamNames.AdminData ){
                adminStream = stream;
            }            
        }
        DatastreamVersionTypeChoice datastreamVersionTypeChoice = adminStream.getDatastreamVersion( 0 ).getDatastreamVersionTypeChoice();
        byte[] ba = datastreamVersionTypeChoice.getBinaryContent();
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document admDoc = builder.parse( new ByteArrayInputStream( ba ) );

        Element root = admDoc.getDocumentElement();
        Element streamsElem = (Element) root.getElementsByTagName( "streams" ).item( 0 );
        NodeList streamList = streamsElem.getElementsByTagName( "stream" );

        //System.out.println( "number of <stream> elements " +streamList.getLength() );

        //in this loop we dont add the adminstream

        for( int i=0; i < streamList.getLength(); i++){
            Element streamElem = (Element) streamList.item( i );

            DataStreamNames datastreamName = DataStreamNames.getDataStreamNameFrom( streamElem.getAttribute( "id" ) ); 
            String language = streamElem.getAttribute( "lang" );
            String format = streamElem.getAttribute( "format" );
            String mimetype = streamElem.getAttribute( "mimetype" );
            String submitter = streamElem.getAttribute( "submitter");
            int index = new Integer( streamElem.getAttribute( "index" ) );

            DatastreamVersionTypeChoice tmp_datastreamVersionTypeChoice = streams[i].getDatastreamVersion( 0 ).getDatastreamVersionTypeChoice();
            byte[] barray = tmp_datastreamVersionTypeChoice.getBinaryContent();

            CargoObject co = new CargoObject( datastreamName, mimetype, language, submitter, format, barray );
            data.add( co );
        }
        /**
         * \Todo: Do we need the adminStream at all now?
         */
        //creating a CargoObject out of the adminStream and adding it to the CargoContainer
        CargoObject admco = new CargoObject( DataStreamNames.AdminData, "text/xml", "da", "dbc", "adminstream", ba );
        data.add( admco );
    }

    
    /**
     * Add CargoObject to internal data representation.
     * 
     * @param format
     * @param submitter
     * @param language
     * @param mimetype
     * @param data
     * @return TRUE if add operation finishes successfully.
     * @throws IOException
     */
    public void add( DataStreamNames dataStreamName, String format, String submitter, String language, String mimetype, byte[] data ) throws IOException
    {
    	CargoObject co = new CargoObject( dataStreamName, mimetype, language, submitter, format, data );
    	this.data.add( co );    	
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


    public CargoObject getFirstCargoObject( DataStreamNames dsn)
    {
        CargoObject rco = null;
        for( CargoObject co : data )
            if( co.getDataStreamName() == dsn )
                rco = co;        
    
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
}