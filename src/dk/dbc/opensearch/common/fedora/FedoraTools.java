package dk.dbc.opensearch.common.fedora;

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


import dk.dbc.opensearch.common.helpers.PairComparator_FirstString;
import dk.dbc.opensearch.common.helpers.PairComparator_SecondInteger;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.xsd.Datastream;
import dk.dbc.opensearch.xsd.DatastreamVersion;
import dk.dbc.opensearch.xsd.DatastreamVersionTypeChoice;
import dk.dbc.opensearch.xsd.DigitalObject;
import dk.dbc.opensearch.xsd.ObjectProperties;
import dk.dbc.opensearch.xsd.Property;
import dk.dbc.opensearch.xsd.PropertyType;
import dk.dbc.opensearch.xsd.types.DatastreamTypeCONTROL_GROUPType;
import dk.dbc.opensearch.xsd.types.DigitalObjectTypeVERSIONType;
import dk.dbc.opensearch.xsd.types.PropertyTypeNAMEType;
import dk.dbc.opensearch.xsd.types.StateType;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;


public class FedoraTools 
{
    static Logger log = Logger.getLogger( FedoraTools.class );


    public static byte[] constructFoxml(CargoContainer cargo, String nextPid, String label) throws IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException
    {
    	log.debug( String.format( "Constructor( cargo, nextPid='%s', label='%s' ) called", nextPid, label ) );
    
        Date now = new Date(System.currentTimeMillis());
        return constructFoxml(cargo, nextPid, label, now);
    }

    
    public static byte[] constructFoxml(CargoContainer cargo, String nextPid, String label, Date now) throws IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException
    {
        log.debug( String.format( "constructFoxml( cargo, nexPid='%s', label='%s', now) called", nextPid, label ) );

        // Setting properties
        ObjectProperties op = new ObjectProperties();

        Property pState = new Property();
        pState.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_STATE);
        pState.setVALUE("Active");

        Property pLabel = new Property();
        pLabel.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_LABEL);
        pLabel.setVALUE(label);

        PropertyType pOwner = new Property();
        pOwner.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_OWNERID);
        /** \todo: set correct value for owner of the Digital Object*/
        pOwner.setVALUE( "user" );


        // createdDate
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        String timeNow = dateFormat.format(now);
        Property pCreatedDate = new Property();
        pCreatedDate.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_CREATEDDATE);
        pCreatedDate.setVALUE(timeNow);

        // lastModifiedDate
        Property pLastModifiedDate = new Property();
        pLastModifiedDate.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_VIEW_LASTMODIFIEDDATE);
        pLastModifiedDate.setVALUE(timeNow);

        Property[] props = new Property[] { (Property) pState, (Property) pLabel, (Property) pOwner,
                                            (Property) pCreatedDate, (Property) pLastModifiedDate };
        op.setProperty(props);

        log.debug( "Properties set, constructing the DigitalObject" );
        DigitalObject dot = new DigitalObject();
        dot.setObjectProperties(op);
        dot.setVERSION(DigitalObjectTypeVERSIONType.VALUE_0);
        dot.setPID( nextPid ); 

        int cargo_count = cargo.getCargoObjectCount();
        log.debug( String.format( "Number of CargoObjects in Container", cargo_count ) );

        
        log.debug( "Constructing adminstream" );
        PairComparator_FirstString firstComp = new PairComparator_FirstString();
        PairComparator_SecondInteger secondComp = new PairComparator_SecondInteger();

        // Constructing list with datastream indexes and id
        ArrayList< Pair < String, Integer > > lst = new  ArrayList< Pair < String, Integer > >();
        for(int i = 0; i < cargo_count; i++){
            CargoObject c = cargo.getCargoObjects().get( i );
            lst.add( new InputPair< String, Integer >( c.getDataStreamName().getName(), i ) );
        }
        
        Collections.sort( lst, firstComp);

        // Add a number to the id according to the number of datastreams with this datastreamname
        int j = 0;
        DataStreamType dsn = null;
        ArrayList< Pair < String, Integer > > lst2 = new  ArrayList< Pair < String, Integer > >();
        for( Pair< String, Integer > p : lst){
            if( dsn != DataStreamType.getDataStreamNameFrom( (String) p.getFirst() ) ){
                j = 0;
            }
            else{
                j += 1;
            }
            dsn = DataStreamType.getDataStreamNameFrom( (String) p.getFirst() );
            lst2.add( new InputPair< String, Integer >( p.getFirst() + "." + j , p.getSecond() ) );
        }
        
        lst2.add( new InputPair< String, Integer >( DataStreamType.AdminData.getName(), lst2.size() ) );
        Collections.sort( lst2, secondComp);
        
        // Constructing adm stream
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document admStream = builder.newDocument();
        Element root = admStream.createElement( "admin-stream" );
        
        Element indexingaliasElem = admStream.createElement( "indexingalias" );
        indexingaliasElem.setAttribute( "name", cargo.getIndexingAlias( DataStreamType.OriginalData ).getName() );
        root.appendChild( (Node)indexingaliasElem );
        //Element filePathElem = admStream.createElement( "filepath" );
        //filePathElem.setAttribute( "name", cargo.getFilePath() );
        //root.appendChild( (Node)filePathElem );
        
        Node streams = admStream.createElement( "streams" );
        
        for(int i = 0; i < cargo_count; i++)
        {
        	CargoObject c = cargo.getCargoObjects().get( i );

        	Element stream = admStream.createElement( "stream" );
        	stream.setAttribute( "id", lst2.get( i ).getFirst() );
        	stream.setAttribute( "lang", c.getLang() );
        	stream.setAttribute( "format", c.getFormat() );
        	stream.setAttribute( "mimetype", c.getMimeType() );
        	stream.setAttribute( "submitter", c.getSubmitter() );
        	stream.setAttribute( "index", Integer.toString( lst2.get( i ).getSecond() ) );
        	stream.setAttribute( "streamNameType" ,c.getDataStreamName().getName() );
        	streams.appendChild( (Node) stream );
        }
        
        root.appendChild( (Node) streams );
        
        // Transform document to xml string
        Source source = new DOMSource((Node) root );
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter); 
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
        String admStreamString = stringWriter.getBuffer().toString();
        log.debug( String.format( "Constructed Administration stream for the CargoContainer=%s", admStreamString ) );

        // add the adminstream to the cargoContainer
        byte[] byteAdmArray = admStreamString.getBytes();
        cargo.add( DataStreamType.AdminData, "admin", "dbc", "da", "text/xml", IndexingAlias.None, byteAdmArray );
        //cargo.add( DataStreamType.AdminData, "admin", "dbc", "da", "text/xml", byteAdmArray );

        log.debug( "Constructing foxml byte[] from cargoContainer" );
        cargo_count = cargo.getCargoObjectCount();//.getItemsCount();

        log.debug( String.format( "Length of CargoContainer including administration stream=%s", cargo_count ) );
        Datastream[] dsArray = new Datastream[ cargo_count ];
        for(int i = 0; i < cargo_count; i++)
        {
            CargoObject c = cargo.getCargoObjects().get( i );
            dsArray[i] = constructDatastream( c, dateFormat, timeNow, lst2.get( i ).getFirst() );
        }

        log.debug( "Successfully contructed datastreams for each CargoObject in the CargoContainer" );

        log.debug( String.format( "Successfully contructed datastreams from the CargoContainer. length of datastream[]='%s'", dsArray.length ) );

        // add the streams to the digital object
        dot.setDatastream( dsArray );
  
        log.debug( "Marshalling the digitalObject to a byte[]" );
        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.OutputStreamWriter outW = new java.io.OutputStreamWriter(out);
        Marshaller m = new Marshaller(outW); // IOException
        m.marshal(dot); // throws MarshallException, ValidationException
        //        log.debug( String.format( "Marshalled DigitalObject=%s", out.toString() ) );
        byte[] ret = out.toByteArray();

        log.debug( String.format( "length of marshalled byte[]=%s", ret.length ) );
        return ret;
    }

    
    /**
     * constructDatastream creates a Datastream object on the basis of the
     * information and data found in the Pair of CargoObjectInfo and List<Byte>.
     *
     * @return A datastream suitable for ingestion into the DigitalObject
     */
    private static Datastream constructDatastream(CargoObject co, SimpleDateFormat dateFormat, String timeNow, String itemID ) throws java.text.ParseException, IOException
    {
        int srcLen = co.getContentLength();
        byte[] ba = co.getBytes();
       
        log.debug( String.format( "contructing datastream from cargoobject format=%s, submitter=%s, mimetype=%s, contentlength=%s",co.getFormat(),co.getSubmitter(),co.getMimeType(), co.getContentLength() ) );

        /** \todo: VERSIONABLE should be configurable in some way */
        boolean versionable = false;

        /**
         * \todo: if the datastream is external, dsLocation should be
         * configurable
         */

        /** \todo: We always use Managed as control group... This should change/be refactored */
        DatastreamTypeCONTROL_GROUPType controlGroup = null;
        controlGroup = DatastreamTypeCONTROL_GROUPType.M;

        // datastreamElement
        Datastream dataStreamElement = new Datastream();

        /** \todo: CONTROL_GROUP should be configurable in some way */
        dataStreamElement.setCONTROL_GROUP( controlGroup );


        System.out.println("itemID "+itemID);
        dataStreamElement.setID( itemID );
        /**
         * \todo: State type defaults to active. Client should interact with
         * datastream after this method if it wants something else
         */
        dataStreamElement.setSTATE( StateType.A );
        dataStreamElement.setVERSIONABLE( versionable );

        // datastreamVersionElement
        String itemId_version = itemID+".0";

        DatastreamVersion dataStreamVersionElement = new DatastreamVersion();

        dataStreamVersionElement.setCREATED(dateFormat.parse( timeNow ) );

        dataStreamVersionElement.setID(itemId_version);

        DatastreamVersionTypeChoice dVersTypeChoice = new DatastreamVersionTypeChoice();

        //ContentDigest binaryContent = new ContentDigest();

        dVersTypeChoice.setBinaryContent(ba);

        dataStreamVersionElement.setDatastreamVersionTypeChoice(dVersTypeChoice);

        String mimeLabel = String.format("%s [%s]", co.getFormat(), co.getMimeType());
        dataStreamVersionElement.setLABEL(mimeLabel);
        String mimeFormatted = String.format("%s", co.getMimeType());
        dataStreamVersionElement.setMIMETYPE( mimeFormatted );

        long lengthFormatted = (long) srcLen;

        dataStreamVersionElement.setSIZE( lengthFormatted );
        
        DatastreamVersion[] dsvArray = new DatastreamVersion[] { dataStreamVersionElement };
        dataStreamElement.setDatastreamVersion( dsvArray );

        log.debug( String.format( "Datastream element is valid=%s", dataStreamElement.isValid() ) );

        return dataStreamElement;
    }
}