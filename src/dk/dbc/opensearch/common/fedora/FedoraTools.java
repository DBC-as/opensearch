package dk.dbc.opensearch.common.fedora;

import java.io.IOException;
import java.io.StringWriter;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import org.apache.axis.encoding.Base64;
import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import dk.dbc.opensearch.common.types.DataStreamNames;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.xsd.ObjectProperties;
import dk.dbc.opensearch.xsd.Property;
import dk.dbc.opensearch.xsd.PropertyType;
import dk.dbc.opensearch.xsd.ContentDigest;
import dk.dbc.opensearch.xsd.Datastream;
import dk.dbc.opensearch.xsd.DatastreamVersion;
import dk.dbc.opensearch.xsd.DatastreamVersionTypeChoice;
import dk.dbc.opensearch.xsd.DigitalObject;
import dk.dbc.opensearch.xsd.types.DatastreamTypeCONTROL_GROUPType;
import dk.dbc.opensearch.xsd.types.DigitalObjectTypeVERSIONType;
import dk.dbc.opensearch.xsd.types.PropertyTypeNAMEType;
import dk.dbc.opensearch.xsd.types.StateType;
import dk.dbc.opensearch.xsd.types.ContentDigestTypeTYPEType;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerConfigurationException;


import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;

public class FedoraTools {

    static Logger log = Logger.getLogger("FedoraHandler");

    public static byte[] constructFoxml(CargoContainer cargo, String nextPid, String label) throws IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException 
{
        Date now = new Date(System.currentTimeMillis());
        return constructFoxml(cargo, nextPid, label, now);
    }

    public static byte[] constructFoxml(CargoContainer cargo, String nextPid,
                                        String label, Date now) throws IOException, MarshalException, ValidationException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException 
    {
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

        DigitalObject dot = new DigitalObject();
        dot.setObjectProperties(op);
        dot.setVERSION(DigitalObjectTypeVERSIONType.VALUE_0);
        dot.setPID( nextPid ); //

        int cargo_count = cargo.getItemsCount();
        Datastream[] dsArray = new Datastream[ cargo_count+1 ];


        // Constructing adm stream
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();

        Document admStream = builder.newDocument();
        Element root = admStream.createElement( "admin-stream" );
        Node streams = admStream.createElement( "streams" );


        for(int i = 0; i < cargo_count; i++)
            {
                CargoObject c = cargo.getData().get( i );

                Element stream = admStream.createElement( "stream" );
                stream.setAttribute( "id", c.getDataStreamName().getName() );
                stream.setAttribute( "lang", c.getLang() );
                stream.setAttribute( "format", c.getFormat() );
                stream.setAttribute( "mimetype", c.getMimeType() );
                stream.setAttribute( "submitter", c.getSubmitter() );
                stream.setAttribute( "index", Integer.toString( i ) );

                streams.appendChild( (Node) stream );

                dsArray[i] = constructDatastream( c, dateFormat, timeNow );
            }

        root.appendChild( (Node) streams );

        Source source = new DOMSource((Node) root );
        StringWriter stringWriter = new StringWriter();
        Result result = new StreamResult(stringWriter); 
        TransformerFactory transformerFactory = TransformerFactory.newInstance();
        Transformer transformer = transformerFactory.newTransformer();
        transformer.transform(source, result);
        String admStreamString = stringWriter.getBuffer().toString();
        //System.out.println( admStreamString );
        //ByteArrayOutputStream bStream = new ByteArrayOutputStream();
        //ObjectOutputStream oStream = new ObjectOutputStream( bStream );
        //oStream.writeObject ( admStream );
        byte[] byteAdmArray = admStreamString.getBytes();
        // System.out.println( byteAdmArray );
        //System.out.println( byteAdmArray.length +" " +admStreamString.length() );
        CargoObject co = new CargoObject( DataStreamNames.AdminData, "text/xml", "da", "dbc", "admin", byteAdmArray );
        //System.out.println( co.getBytes() );


        dsArray[ cargo_count ] = constructDatastream( co, dateFormat, timeNow );
        //System.out.println( dsArray.length );

        log.debug( String.format( "length of datastream array=%s", dsArray.length ) );

        dot.setDatastream( dsArray );
        /**
         * Debug!!!!
         */
       //  System.out.println("dsArray size: "+ dsArray.length );
//         CargoContainer testCC = new CargoContainer( dot );
//         System.out.println( "testCC.getItemsCount() :"+testCC.getItemsCount() );
 
        /**
         * Debug!!!! 
         *
         */

        java.io.ByteArrayOutputStream out = new java.io.ByteArrayOutputStream();
        java.io.OutputStreamWriter outW = new java.io.OutputStreamWriter(out);
        Marshaller m = new Marshaller(outW); // IOException
        m.marshal(dot); // throws MarshallException, ValidationException
        log.debug( String.format( "Marshalled DigitalObject=%s", out.toString() ) );
        byte[] ret = out.toByteArray();

        log.debug( String.format( "length of return array=%s", ret.length ) );
        return ret;
    }

    /**
     * constructDatastream creates a Datastream object on the basis of the
     * information and data found in the Pair of CargoObjectInfo and List<Byte>.
     *
     * @return A datastream suitable for ingestion into the DigitalObject
     */
    private static Datastream constructDatastream(CargoObject co, SimpleDateFormat dateFormat, String timeNow) throws java.text.ParseException, IOException
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


        //dataStreamElement.setID( co.getFormat() );
        //dataStreamElement.setID( co.getDataStreamName( "test" ) );
        dataStreamElement.setID( co.getDataStreamName().getName() );
        /**
         * \todo: State type defaults to active. Client should interact with
         * datastream after this method if it wants something else
         */
        dataStreamElement.setSTATE( StateType.A );
        dataStreamElement.setVERSIONABLE( versionable );

        // datastreamVersionElement
        String itemId_version = co.getDataStreamName().getName() + ".0";

        DatastreamVersion dataStreamVersionElement = new DatastreamVersion();

        dataStreamVersionElement.setCREATED(dateFormat.parse( timeNow ) );

        dataStreamVersionElement.setID(itemId_version);

        DatastreamVersionTypeChoice dVersTypeChoice = new DatastreamVersionTypeChoice();

        ContentDigest binaryContent = new ContentDigest();

        dVersTypeChoice.setBinaryContent(ba);


        dataStreamVersionElement.setDatastreamVersionTypeChoice(dVersTypeChoice);

        String mimeLabel = String.format("%s [%s]", co.getFormat(), co.getMimeType());
        dataStreamVersionElement.setLABEL(mimeLabel);
        String mimeFormatted = String.format("%s", co.getMimeType());
        dataStreamVersionElement.setMIMETYPE( mimeFormatted );

        long lengthFormatted = (long) srcLen;

        dataStreamVersionElement.setSIZE( lengthFormatted );

        //binaryContent.setDIGEST( );//Base64.encode( ba ) );
        //binaryContent.setTYPE( ContentDigestTypeTYPEType.DISABLED );


        //dataStreamVersionElement.setContentDigest( binaryContent );
        DatastreamVersion[] dsvArray = new DatastreamVersion[] { dataStreamVersionElement };
        dataStreamElement.setDatastreamVersion( dsvArray );

        log.debug( String.format( "Datastream element is valid=%s", dataStreamElement.isValid() ) );

        return dataStreamElement;
    }

}