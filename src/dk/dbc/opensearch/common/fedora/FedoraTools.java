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

package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.xsd.DigitalObject;
import java.io.ByteArrayOutputStream;
import java.io.OutputStreamWriter;
import org.exolab.castor.xml.Marshaller;
import java.io.IOException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import java.util.Date;
import dk.dbc.opensearch.xsd.ObjectProperties;
import dk.dbc.opensearch.xsd.Property;
import dk.dbc.opensearch.xsd.types.PropertyTypeNAMEType;
import dk.dbc.opensearch.xsd.types.DigitalObjectTypeVERSIONType;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.xsd.types.DatastreamTypeCONTROL_GROUPType;
import org.apache.log4j.Logger;
import dk.dbc.opensearch.xsd.Datastream;
import dk.dbc.opensearch.xsd.DatastreamVersion;
import dk.dbc.opensearch.xsd.types.StateType;
import java.text.SimpleDateFormat;
import java.text.ParseException;
import dk.dbc.opensearch.xsd.PropertyType;
import dk.dbc.opensearch.xsd.DatastreamVersionTypeChoice;


/**
 *
 */
public class FedoraTools {

    static Logger log = Logger.getLogger( FedoraTools.class );
    protected static final SimpleDateFormat dateFormat =
        new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS");


    /**
     * Serializes a DigitalObject into a byte[] containing the
     * serialized xml document.
     *
     * Through the serializing functionality provided by the castor
     * framework, the DigitalObject is validated before serialized. If
     * something is amiss with the object structure and castor finds
     * out in time, a ValidationException will be thrown. If castor is
     * unable, for other reasons, to serialize the object, a
     * MarshallException will be thrown.
     *
     * @param dot the DigitalObject to be serialized
     *
     * @return a byte[] containing the (xml-) serialized form of the DigitalObject
     */
    public static byte[] DigitalObjectAsByteArray( DigitalObject dot )throws IOException, MarshalException, ValidationException//, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException
    {
        log.debug( "Marshalling the digitalObject to a byte[]" );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        OutputStreamWriter outW = new OutputStreamWriter(out);
        Marshaller m = new Marshaller( outW ); // IOException
        m.marshal(dot); // throws MarshallException, ValidationException
        //log.debug( String.format( "Marshalled DigitalObject=%s", out.toString() ) );
        byte[] ret = out.toByteArray();

        log.debug( String.format( "length of marshalled byte[]=%s", ret.length ) );
        return ret;

    }

    /**
     * Initializes and returns a DigitalObject with no
     * DataStreams. This method defaults the timestamp to
     * System.currentTimeMillis
     *
     * @param state one of: Active, Inactive or Deleted
     * - Active: The object is published and available.
     * - Inactive: The object is not publicly available.
     * - Deleted: The object is deleted, and should not be available
     *            to anyone. It is still in the repository, and special
     *            administration tools should be able to resurrect it.
     * @param label A descriptive label of the Digitial Object
     * @param owner The (system) name of the owner of the Digital
     * Object. Please note that this has nothing to do with the
     * ownership of the material (although the names can and may
     * coincide).
     *
     * @return a DigitalObject with no DataStreams
     */
    static DigitalObject initDigitalObject( String state,
                                                    String label,
                                                    String owner,
                                                    String pid )
    {
        Date timestamp = new Date( System.currentTimeMillis() );
        return initDigitalObject( state, label, owner, pid, timestamp );
    }

    /**
     * Initializes and returns a DigitalObject with no
     * DataStreams.
     * @see initDigitalObject( String, String, String ) for more info
     * @param state one of Active, Inactive or Deleted
     * @param label description of the DigitalObject
     * @param owner (System) owner of the DigitalObject
     * @param timestamp overrides the default (now) timestamp
     *
     * @return a DigitalObject with no DataStreams
     */
    static DigitalObject initDigitalObject( String state,
                                                    String label,
                                                    String owner,
                                                    String pid,
                                                    Date timestamp )
    {
        //ObjectProperties holds all the Property types
        ObjectProperties op = new ObjectProperties();

        Property pState = new Property();
        pState.setNAME( PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_STATE );
        pState.setVALUE( state );

        Property pLabel = new Property();
        pLabel.setNAME( PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_LABEL );
        pLabel.setVALUE( label );

        PropertyType pOwner = new Property();
        pOwner.setNAME(PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_OWNERID);
        pOwner.setVALUE( owner );

        //SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        String timeNow = dateFormat.format( timestamp );
        Property pCreatedDate = new Property();
        pCreatedDate.setNAME( PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_MODEL_CREATEDDATE );
        pCreatedDate.setVALUE( timeNow );

        // Upon creation, the last modified date == created date
        Property pLastModifiedDate = new Property();
        pLastModifiedDate.setNAME( PropertyTypeNAMEType.INFO_FEDORA_FEDORA_SYSTEM_DEF_VIEW_LASTMODIFIEDDATE );
        pLastModifiedDate.setVALUE( timeNow );

        Property[] props = new Property[] { pState, pLabel, (Property) pOwner,
                                            pCreatedDate, pLastModifiedDate };
        op.setProperty( props );

        DigitalObject dot = new DigitalObject();
        dot.setObjectProperties( op );
        dot.setVERSION( DigitalObjectTypeVERSIONType.VALUE_0 );
        dot.setPID( pid );

        return dot;
    }

    /**
     * Constructing a Datastream with a default timestamp (
     * System.currentTimeMillis )
     *
     * @param co
     * @param itemID
     *
     * @return
     */
     static Datastream constructDatastream( CargoObject co,
                                            String itemID ) throws ParseException,
                                                                   IOException
    {
        Date timestamp = new Date( System.currentTimeMillis() );
        String timeNow = dateFormat.format( timestamp );

        return constructDatastream( co, timeNow, itemID );
    }

    /**
     * constructDatastream creates a Datastream object on the basis of a CargoObject
     *
     * @param co the CargoObject from which to get the data
     * @param timeNow
     * @param itemID
     *
     * @return A datastream suitable for ingestion into the DigitalObject
     */
     static Datastream constructDatastream( CargoObject co,
                                                   String timeNow,
                                                   String itemID ) throws ParseException
    {
        return constructDatastream( co, timeNow, itemID, false, false, false );
    }

    /**
     * Control Group: the approach used by the Datastream to represent or encapsulate the content as one of four types or control groups:
     *    - Internal XML Content - the content is stored as XML
     *      in-line within the digital object XML file
     *    - Managed Content - the content is stored in the repository
     *      and the digital object XML maintains an internal
     *      identifier that can be used to retrieve the content from
     *      storage
     *    - Externally Referenced Content (not yet implemented) - the
     *      content is stored outside the repository and the digital
     *      object XML maintains a URL that can be dereferenced by the
     *      repository to retrieve the content from a remote
     *      location. While the datastream content is stored outside of
     *      the Fedora repository, at runtime, when an access request
     *      for this type of datastream is made, the Fedora repository
     *      will use this URL to get the content from its remote
     *      location, and the Fedora repository will mediate access to
     *      the content. This means that behind the scenes, Fedora will
     *      grab the content and stream in out the the client
     *      requesting the content as if it were served up directly by
     *      Fedora. This is a good way to create digital objects that
     *      point to distributed content, but still have the repository
     *      in charge of serving it up.
     *    - Redirect Referenced Content (not supported)- the content
     *      is stored outside the repository and the digital object
     *      XML maintains a URL that is used to redirect the client
     *      when an access request is made. The content is not
     *      streamed through the repository. This is beneficial when
     *      you want a digital object to have a Datastream that is
     *      stored and served by some external service, and you want
     *      the repository to get out of the way when it comes time to
     *      serve the content up. A good example is when you want a
     *      Datastream to be content that is stored and served by a
     *      streaming media server. In such a case, you would want to
     *      pass control to the media server to actually stream the
     *      content to a client (e.g., video streaming), rather than
     *      have Fedora in the middle re-streaming the content out.
     */
     static Datastream constructDatastream( CargoObject co,
                                                   String timeNow,
                                                   String itemID,
                                                   boolean versionable,
                                                   boolean externalData,
                                                   boolean inlineData ) throws ParseException
    {
        int srcLen = co.getContentLength();
        byte[] ba = co.getBytes();

        log.debug( String.format( "constructing datastream from cargoobject id=%s, format=%s, submitter=%s, mimetype=%s, contentlength=%s, datastreamtype=%s, indexingalias=%s, datastream id=%s",co.getId(), co.getFormat(),co.getSubmitter(),co.getMimeType(), co.getContentLength(), co.getDataStreamName(), co.getIndexingAlias(), itemID ) );

        DatastreamTypeCONTROL_GROUPType controlGroup = null;
        if( (! externalData ) && ( ! inlineData ) && ( co.getMimeType() == "text/xml" ) )
        {
            //Managed content
            controlGroup = DatastreamTypeCONTROL_GROUPType.M;
        }
        else if( ( ! externalData ) && ( inlineData ) && ( co.getMimeType() == "text/xml" )) {
            //Inline content
            controlGroup = DatastreamTypeCONTROL_GROUPType.X;
        }
        // else if( ( externalData ) && ( ! inlineData ) ){
        //     /**
        //      * external data cannot be inline, and this is regarded as
        //      * a client error, but we assume that the client wanted
        //      * the content referenced; we log a warning and proceed
        //      */
        //     log.warn( String.format( "Both externalData and inlineData was set to true, they are mutually exclusive, and we assume that the content should be an external reference" ) );
        //     controlGroup = DatastreamTypeCONTROL_GROUPType.E;
        // }

        // datastreamElement
        Datastream dataStreamElement = new Datastream();

        dataStreamElement.setCONTROL_GROUP( controlGroup );

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

        dataStreamVersionElement.setCREATED( dateFormat.parse( timeNow ) );

        dataStreamVersionElement.setID( itemId_version );

        DatastreamVersionTypeChoice dVersTypeChoice = new DatastreamVersionTypeChoice();

        //ContentDigest binaryContent = new ContentDigest();

        dVersTypeChoice.setBinaryContent( ba );

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