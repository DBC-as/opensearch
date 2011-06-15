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

package dk.dbc.opensearch.metadata;


import dk.dbc.commons.types.Pair;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.CargoObject;
import dk.dbc.opensearch.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.log4j.Logger;
import org.xml.sax.SAXException;


/**
 * Represents metadata that describes (for third party
 * components/libraries) which data is contained in a CargoContainer
 * and how it can be reconstructed using the information contained in
 * the administration stream.
 *
 * This class is primarily intended to be used with serializing, but
 * it can be used in general to retrieve information on the
 * CargoContainer.
 */
public class AdministrationStream implements IMetaData
{
    private static final String schemaString = "<xsd:schema xmlns:xsd=\"http://www.w3.org/2001/XMLSchema\"><xsd:attribute name=\"name\" type=\"xsd:string\"/><xsd:element name=\"admin-stream\"><xsd:complexType><xsd:sequence><xsd:element name=\"streams\"><xsd:complexType><xsd:sequence><xsd:element name=\"stream\" maxOccurs=\"unbounded\"><xsd:complexType><xsd:attribute name=\"format\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"id\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"index\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"lang\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"mimetype\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"streamNameType\" type=\"xsd:string\" use=\"required\"/><xsd:attribute name=\"submitter\" type=\"xsd:string\" use=\"required\"/></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType></xsd:element></xsd:sequence></xsd:complexType></xsd:element></xsd:schema>";

    // private Map< Integer, HashMap< AdministrationStreamElement, String > > admvalues;
    private Map< Integer, AdministrationStreamObject > admvalues;
    private static final String identifier = DataStreamType.AdminData.getName();

    /**
     * The DataStreamType identifying the metadata.
     */
    public static final DataStreamType type = DataStreamType.AdminData;

    private static Logger log = Logger.getLogger( AdministrationStream.class );


    /**
     * Constructs an empty AdministrationStream object. The client can use the
     * {@link #addStream(CargoObject, String)} to add information about
     * data into the AdministrationStream.
     */
    public AdministrationStream()
    {
        // admvalues = new HashMap<Integer, HashMap<AdministrationStreamElement, String>>();
	admvalues = new HashMap<Integer, AdministrationStreamObject >();
    }

    /**
     * Constructs an AdministrationStream object from
     * adminstrationstream xml, optionally validating the input XML
     * {@link InputStream}. Not validating the input xml saves time,
     * but it is then the responsibility of the client to ensure that
     * the input xml is valid and parsable according to the
     * administration stream xml Schema.
     * @param in the administration xml
     * @param validating validate the InputStream if true  
     */
    public AdministrationStream( InputStream in, boolean validating ) throws XMLStreamException, SAXException, IOException
    {
        if ( in == null )
        {
            String error = String.format( "InputStream is null, cannot construct adminstration stream" );
            log.error( error );
            throw new IllegalStateException( error );
        }
        
        if ( validating )
        {
            String ADM_NS = "http://www.w3.org/2001/XMLSchema";
            Source schemaurl = new StreamSource( new ByteArrayInputStream( schemaString.getBytes() ) );
            SchemaFactory schemaf = SchemaFactory.newInstance( ADM_NS );
            Schema schema = schemaf.newSchema( schemaurl );
            Validator validator = schema.newValidator();

            Source inXML = new StreamSource( in );
            validator.validate( inXML );
            in.reset();
        }

        // admvalues = new HashMap<Integer, HashMap<AdministrationStreamElement, String>>();
	admvalues = new HashMap<Integer, AdministrationStreamObject >();

        XMLInputFactory infac = XMLInputFactory.newInstance();
        XMLStreamReader parser = infac.createXMLStreamReader( in );
        XMLEventReader eventReader = infac.createXMLEventReader( parser );

        XMLEvent event;

        StartElement element;
        int counter = 0;
        while( parser.hasNext() )
        {
            event = eventReader.nextEvent();
            if( XMLStreamConstants.START_ELEMENT == event.getEventType() )
            {
                element = event.asStartElement();
                if( element.getName().getLocalPart().equals( "stream" ) )
                {
		    // admvalues.put( counter, new HashMap<AdministrationStreamElement, String>() );
                    // HashMap<AdministrationStreamElement, String> stream = admvalues.get( counter++ );

                    Attribute attribute;
		    String id = "";
		    String lang = "";
		    String format = "";
		    String mimetype = "";
		    String submitter = "";
		    int index = 0;
		    DataStreamType streamNameType = null;
                    for( Iterator<Attribute> attributeIter = element.getAttributes(); attributeIter.hasNext(); )
                    {
                        attribute = attributeIter.next();
			String attrNameUpperCase = attribute.getName().getLocalPart().toUpperCase();
                        // stream.put( AdministrationStreamElement.valueOf( attribute.getName().getLocalPart().toUpperCase() ), attribute.getValue() );
			id             = attrNameUpperCase.equals( "ID" ) ? attribute.getValue() : id;
			format         = attrNameUpperCase.equals( "FORMAT" ) ? attribute.getValue() : format;
			lang           = attrNameUpperCase.equals( "LANG" ) ? attribute.getValue() : lang;
			mimetype       = attrNameUpperCase.equals( "MIMETYPE" ) ? attribute.getValue() : mimetype;
			submitter      = attrNameUpperCase.equals( "SUBMITTER" ) ? attribute.getValue() : submitter;
			index          = attrNameUpperCase.equals( "INDEX" ) ? new Integer( attribute.getValue() ) : index;
			streamNameType = attrNameUpperCase.equals( "STREAMNAMETYPE" ) ? DataStreamType.getDataStreamTypeFrom( attribute.getValue() ) : streamNameType;
                    }

		    AdministrationStreamObject ase = 
			new AdministrationStreamObject( id, lang, format, mimetype, 
							  submitter, index, streamNameType );
		    admvalues.put( counter, ase );
		    counter++;
                }
            }
        }

        assert ( admvalues.size() == counter );
       
    }

    
    /**
     * Adds information about a data object as a stream in the
     * administrationstream. {@code id} by convention is the
     * DataStreamId.
     * @param obj the CargoObject to add data about into 
     * the administration stream
     * @param id the id of the CargoObject
     */
    public boolean addStream( CargoObject obj, String id )
    {

        if( obj.getDataStreamType() == DataStreamType.DublinCoreData )
        {
            log.trace( "Object added is DC" );
        }
        
        int pos = admvalues.size();
        boolean added = false;
	
	AdministrationStreamObject map = 
	    new AdministrationStreamObject( id, obj.getLang(), obj.getFormat(), 
					    obj.getMimeType(), obj.getSubmitter(),
					    pos, obj.getDataStreamType() );
        // HashMap<AdministrationStreamElement, String> map = new HashMap<AdministrationStreamElement, String>( 6 );
        // map.put( AdministrationStreamElement.FORMAT, obj.getFormat() );
        // map.put( AdministrationStreamElement.INDEX, Integer.toString( pos ) );
        // map.put( AdministrationStreamElement.LANG, obj.getLang() );
        // map.put( AdministrationStreamElement.MIMETYPE, obj.getMimeType() );
        // map.put( AdministrationStreamElement.STREAMNAMETYPE, obj.getDataStreamType().getName() );
        // map.put( AdministrationStreamElement.SUBMITTER, obj.getSubmitter() );
        // map.put( AdministrationStreamElement.ID, id );
        admvalues.put( new Integer( pos ), map );
        if( pos + 1 == admvalues.size() )
        {
            added = true;
        }
        return added;
    }


    public boolean addStream( IMetaData obj, String id )
    {
        boolean added = false;
        if( obj.getType() == DataStreamType.AdminData )
        {
            log.warn( "Refusing to add adminstream data information to admin stream" );
            added = false;
        }
        return added;
    }


    /**
     * Returns a {@link List} containing an {@link Pair}
     * containing an {@link Integer} denoting the index position of
     * the data element and another {@link Pair} containing a
     * {@link CargoObject} and its identifier (which by convention is
     * the DataStreamId).
     */
    public List< Pair< Integer, Pair< String, CargoObject > > > getStreams() throws IOException
    {
        List< Pair< Integer, Pair< String, CargoObject > > > retlist = new ArrayList< Pair< Integer, Pair< String,CargoObject > > >( admvalues.size() );
        CargoContainer cargo = new CargoContainer();
        // for( Entry< Integer, HashMap< AdministrationStreamElement, String> > set : admvalues.entrySet() )
	for( Entry< Integer, AdministrationStreamObject > set : admvalues.entrySet() )
        {
	    AdministrationStreamObject tmp = set.getValue();
	    cargo.add( tmp.getStreamNameType(),
		       tmp.getFormat(),
		       tmp.getSubmitter(),
		       tmp.getLang(),
		       tmp.getMimetype(),
		       // set.getValue().get( AdministrationStreamElement.STREAMNAMETYPE ) ),
		       // set.getValue().get( AdministrationStreamElement.FORMAT ),
		       // set.getValue().get( AdministrationStreamElement.SUBMITTER ),
		       // set.getValue().get( AdministrationStreamElement.LANG ),
		       // set.getValue().get( AdministrationStreamElement.MIMETYPE ),
		       "ihatefakedata".getBytes() );
        
            //String is the datastreamId and CargoObject holds all metadata on the stream, sans the data itself
            Pair< String, CargoObject > indexvalue = 
		new Pair< String, CargoObject >(
						// set.getValue().get( AdministrationStreamElement.ID ),
						tmp.getId(), 
						// cargo.getCargoObject( DataStreamType.getDataStreamTypeFrom( set.getValue().get( AdministrationStreamElement.STREAMNAMETYPE ) ) ) );
						cargo.getCargoObject( tmp.getStreamNameType() ) );
            retlist.add( new Pair<Integer, Pair<String,CargoObject>>( set.getKey(), indexvalue ) );
        }

        return retlist;
    }


    /**
     * Gets the number of streams contained in the administrationstream.
     */
    public int getCount()
    {
        return admvalues.size();
    }


    /**
     * Gets the number of streams contained in the
     * administrationstream that has the {@link DataStreamType} {@code
     * dst}.
     */
    public int getCount( DataStreamType dst )
    {
        int counter = 0;
	for( Entry< Integer, AdministrationStreamObject > set : admvalues.entrySet() )
	{
	    // \todo: Skal laves om til at benytte DST i stedet for strengrepræsentation af DST:
	    AdministrationStreamObject ase = set.getValue();
	    if ( ase.getStreamNameType() == dst ) 
	    {
		counter++;
	    }
	}

        // for( Entry< Integer, HashMap<AdministrationStreamElement, String > > set : admvalues.entrySet() )
        // {
        //     for( Entry< AdministrationStreamElement, String > streamelem : set.getValue().entrySet() )
        //     {
        //         if( streamelem.getKey() == AdministrationStreamElement.STREAMNAMETYPE )
        //         {
        //             if( streamelem.getValue().toLowerCase().equals( dst.getName().toLowerCase() ) )
        //             {
        //                 counter++;
        //             }
        //         }
        //     }
        // }

        return counter;
    }


    /**
    <admin-stream>
    <indexingalias name="danmarcxchange"/>
    <streams>
    <stream format="katalog" id="originalData.0" index="0" lang="da" mimetype="text/xml"
    streamNameType="originalData" submitter="710100"/>
    <stream format="dc" id="dublinCoreData.0" index="1" lang="da" mimetype="text/xml"
    streamNameType="dublinCoreData" submitter="dbc"/>
    </streams>
    </admin-stream>
     */
    @Override
    public void serialize( OutputStream out, String identifier ) throws XMLStreamException
    {

        // Create an output factory
        XMLOutputFactory xmlof = XMLOutputFactory.newInstance();
        XMLStreamWriter xmlw;
        try
        {
            xmlw = xmlof.createXMLStreamWriter( out );
            // Set namespace prefix defaulting for all created writers

            xmlw.writeStartDocument();
            xmlw.writeStartElement( "admin-stream" );
     
            xmlw.writeStartElement( "streams" );
            // for( Entry< Integer, HashMap<AdministrationStreamElement, String > > set : admvalues.entrySet() )
	    for( Entry< Integer, AdministrationStreamObject > set : admvalues.entrySet() )
            {
                xmlw.writeStartElement( "stream" );
                // for( Entry< AdministrationStreamElement, String > streamelem : set.getValue().entrySet() )
                // {
                //     xmlw.writeAttribute( streamelem.getKey().localName(), streamelem.getValue() );
                // }
		AdministrationStreamObject ase = set.getValue();
		xmlw.writeAttribute( "format", ase.getFormat() );
		xmlw.writeAttribute( "id", ase.getId() );
		xmlw.writeAttribute( "index", Integer.toString( ase.getIndex() ) );
		xmlw.writeAttribute( "lang", ase.getLang() );
		xmlw.writeAttribute( "mimetype", ase.getMimetype() );
		xmlw.writeAttribute( "streamNameType", ase.getStreamNameType().getName() );
		xmlw.writeAttribute( "submitter", ase.getSubmitter() );

                xmlw.writeEndElement();//closes "stream" element
            }

            xmlw.writeEndElement();//closes "streams" element
            xmlw.writeEndElement();//closes "admin-stream" element
            xmlw.writeEndDocument();//closes document
            xmlw.flush();
        }
        catch( XMLStreamException ex )
        {
            String error = "Could not write to stream writer";
            log.error( error );
            throw ex;
        }
    }


    /**
     * This implementation returns the identifier as identifier
     */    
    public String getIdentifier()
    {
        return identifier;
    }

    
    /**
     *
     * @return the type of this metadata element
     */
    @Override
    public DataStreamType getType()
    {
        return type;
    }

    /**
     *  Private class containing an Object with common data
     *  used in the AdministrationStream.
     */
    private final class AdministrationStreamObject 
    {
	private final String id;
	private final String lang;
	private final String format;
	private final String mimetype;
	private final String submitter;
	private final int index;
	private final DataStreamType streamNameType;

	AdministrationStreamObject( String id, String lang, String format, String mimetype, String submitter, int index, DataStreamType streamNameType )
	{
	    this.id = id;
	    this.lang = lang;
	    this.format = format;
	    this.mimetype = mimetype;
	    this.submitter = submitter;
	    this.index = index;
	    this.streamNameType = streamNameType;
	}

	public String getId() { return this.id; }
	public String getLang() { return this.lang; }
	public String getFormat() { return this.format; }
	public String getMimetype() { return this.mimetype; }
	public String getSubmitter() { return this.submitter; }
	public int getIndex() { return this.index; }
	public DataStreamType getStreamNameType() { return this.streamNameType; }

}



}
