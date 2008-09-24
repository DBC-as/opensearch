package dbc.opensearch.tools;

import dbc.opensearch.components.datadock.CargoContainer;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.stream.XMLOutputFactory;

import org.apache.axis.encoding.Base64;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;

import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.dom4j.QName;

import java.text.CharacterIterator;
import java.text.StringCharacterIterator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;

import java.net.MalformedURLException;
import java.net.UnknownHostException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;

/**
 * The FedoraHandler class handles connections and communication with
 * the fedora repository.
 */
public class FedoraHandler implements Constants{

    private static String host = "";
    private static String port = "";
    private static String fedoraUrl = "";
    private static String user = "";
    private static String passphrase = "";

    Logger log = Logger.getLogger("FedoraHandler");

    // The fedora api
    FedoraAPIA apia;
    FedoraAPIM apim;

    /**
     * \brief The constructor for the FedoraHandler connects to the fedora
     * base and initializes the FedoraClient. FedoraClient is used to
     * get the Fedora API objects.
     * FedoraHandler
     */
    public FedoraHandler() throws ConfigurationException, MalformedURLException, UnknownHostException, ServiceException, IOException {
        log.debug( "Fedorahandler constructor");

        log.debug( "Obtain config paramaters for configuring fedora connection");
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );
        
        host       = config.getString( "fedora.host" );
        port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        passphrase = config.getString( "fedora.passphrase" );
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";

        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );

        log.debug( "Constructing FedoraClient");
        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );
        apia = client.getAPIA();
        apim = client.getAPIM();
        log.debug( "Constructed FedoraClient");
    }

    /**
     * \brief Submits the datastream to fedora repository 
     * \todo: what are these parameters?
     * @param cargo the cargocontainer with the data 
     * @param pidNS 
     * @param itemId
     * @param label
     */
    public String submitDatastream( CargoContainer cargo, String label )throws RemoteException, XMLStreamException, IOException, IllegalStateException {
        log.debug( String.format( "submitDatastream(cargo, %s) called", label ) );
        
        DatastreamDef dDef = null;
        String pid         = null;
        String nextPid     = null;
        String itemId      = null;
        byte[] foxml       = null;

        /** \todo: We need a pid-manager for getting lists of available pids for a given ns */
        log.debug( String.format( "Getting next pid for namespace %s", cargo.getSubmitter() ) );
        String pids[] = apim.getNextPID( new NonNegativeInteger( "1" ), cargo.getSubmitter() );
        nextPid = pids[0];

        log.debug( String.format( "Getting itemId for datastream" ) );
        itemId = cargo.getFormat();

        log.debug( String.format( "Constructing foxml with pid=%s, itemId=%s and label=%s", pid, itemId, label ) );
        foxml = constructFoxml( cargo, nextPid, itemId, label );
        log.debug( "FOXML constructed, ready for ingesting" );

        pid = apim.ingest( foxml, FOXML1_1.uri, "Ingesting "+label );

        if( !pid.equals( nextPid ) ){
            log.fatal( String.format( "we expected pid=%s, but got pid=%s", nextPid, pid ) );
            throw new IllegalStateException( String.format( "expected pid=%s, but got pid=%s", nextPid, pid ) );
        }

        log.info( String.format( "Submitted data, returning pid %s", pid ) );
        return pid;
    }
    
    /**
     * \brief constructs a foxml stream from the parameters
     * \todo: what are these parameters?
     * @param cargo the cargocontainer with the data 
     * @param pidNS 
     * @param itemId
     * @param label
     * @returns a byte array contaning the foxml string
     */
    private byte[] constructFoxml( CargoContainer cargo, String nextPid, String itemId, String label ) throws IOException, XMLStreamException {
        log.debug( String.format( "constructFoxml(cargo, %s, %s, %s) called", nextPid, itemId, label ) );
    
        log.debug( "Starting constructing xml" );

        String itemId_version = itemId+".0";
        Document document = DocumentHelper.createDocument();
        // Generate root element
        QName root_qn = QName.get( "digitalObject", "foxml", "info:fedora/fedora-system:def/foxml#" );
        Element root = document.addElement( root_qn );
        root.addAttribute( "xmlns:xsi",          "http://www.w3.org/2001/XMLSchema-instance" );
        root.addAttribute( "xsi:scheamLocation", "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd");
        root.addAttribute( "PID",                nextPid );
        root.addAttribute( "VERSION",            "1.1");

        // Generate objectProperties node
        Element objproperties = root.addElement( "foxml:objectProperties" );

        // Generate property nodes

        // State
        Element property = objproperties.addElement( "foxml:property" );
        property.addAttribute( "NAME", "info:fedora/fedora-system:def/model#state" );
        property.addAttribute( "VALUE", "Active" );

        // label
        property = objproperties.addElement( "foxml:property" );
        property.addAttribute( "NAME", "info:fedora/fedora-system:def/model#label" );
        property.addAttribute( "VALUE", label );

        // OwnerID - fedoraUser
        property = objproperties.addElement( "foxml:property" );
        property.addAttribute( "NAME", "info:fedora/fedora-system:def/model#ownerId" );
        property.addAttribute( "VALUE", user );

        // createdDate
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.S");
        String timeNow = dateFormat.format( new Date( System.currentTimeMillis() ) );

        property = objproperties.addElement( "foxml:property" );
        property.addAttribute( "NAME", "info:fedora/fedora-system:def/model#createdDate" );
        property.addAttribute( "VALUE", timeNow );

        // lastModifiedDate
        property = objproperties.addElement( "foxml:property" );
        property.addAttribute( "NAME", "info:fedora/fedora-system:def/view#lastModifiedDate" );
        property.addAttribute( "VALUE", timeNow );


        // datastreamElement
        /** \todo: CONTROL_GROUP should be configurable in some way */
        /** \todo: VERSIONABLE should be configurable in some way */
        property = root.addElement( "foxml:datastream" );
        property.addAttribute( "CONTROL_GROUP", "M" );
        property.addAttribute( "ID", itemId );
        property.addAttribute( "STATE", "A" ); 
        property.addAttribute( "VERSIONABLE", "false" );
        
        // datastreamVersionElement
        property = property.addElement( "foxml:datastreamVersion" );
        property.addAttribute( "CREATED", String.format( "%s", timeNow ) );
        property.addAttribute( "ID", itemId_version );
        /** \todo: bug 7873 for the label field: http://bugs.dbc.dk/show_bug.cgi?id=7873 */
        property.addAttribute( "LABEL", "bug 7873" );
        property.addAttribute( "MIMETYPE", cargo.getMimeType() );
        property.addAttribute( "SIZE", String.format( "%s", cargo.getStreamLength() ) );
        property = property.addElement( "foxml:binaryContent" );
        property.addText( Base64.encode( cargo.getDataBytes() ) );

        log.debug( "Finished constructing xml" );

        /** \todo: This constructing business would be a lot better with xml serialization. Mainly in terms of 1:characters types, 2: boilerplate statements, 3: portability/managability and 4: type safety */
       
        return document.asXML().getBytes( "UTF-8" );
    }

    /**
     * \brief creates a cargocontainer by getting a dataobject from the repository, identified by the parameters.
     * \todo: what are these parameters?
     * @param pid 
     * @param itemID
     * @returns The cargocontainer constructed
     */    
    public CargoContainer getDatastream( java.util.regex.Pattern pid, java.util.regex.Pattern itemID ) throws NotImplementedException{
        throw new NotImplementedException( "RegEx matching on pids not yet implemented" );
    }
    
    /**
     * \brief creates a cargocontainer by getting a dataobject from the repository, identified by the parameters.
     * \todo: what are these parameters?
     * @param pid 
     * @param itemId
     * @returns The cargocontainer constructed
     */    
    public CargoContainer getDatastream( String pid, String itemId ) throws IOException, NoSuchElementException, RemoteException, IllegalStateException{
        log.debug( String.format( "getDatastream( pid=%s, itemId=%s ) called", pid, itemId ) );

        String pidNS = pid.substring( 0, pid.indexOf( ":" ));
        
        /** \todo: very hardcoded value */
        String itemId_version = itemId+".0";
        
        CargoContainer cargo = null;
        DatastreamDef[] datastreams = null;
        MIMETypedStream ds = null;

        log.debug( String.format( "Retrieving datastream information for PID %s", pid ) );
        
        datastreams = this.apia.listDatastreams( pid, null );
        
        log.debug( String.format( "Iterating datastreams" ) );
        
        for ( DatastreamDef def : datastreams ){
            log.debug( String.format( "Got DatastreamDef with id=%s", def.getID() ) );
            
            if( def.getID().equals( itemId ) ){
                
                ds = apia.getDatastreamDissemination( itemId_version, def.getID(), null );
 
                log.debug( String.format( "Making a bytearray of the datastream" ) );
                byte[] datastr = ds.getStream();

                log.debug( String.format( "Preparing the datastream for the CargoContainer" ) );
                InputStream inputStream = new ByteArrayInputStream( datastr );

                log.debug( String.format( "DataStream ID      =%s", itemId_version ) );
                log.debug( String.format( "DataStream Label   =%s", def.getLabel() ) );
                log.debug( String.format( "DataStream MIMEType=%s", def.getMIMEType() ) );

                // dc:format holds mimetype as well
                /** \todo: need to get language dc:language */
                String language = "";

                cargo = new CargoContainer( inputStream,
                                            def.getMIMEType(),
                                            language,
                                            pidNS,
                                            itemId );
            }
        }
        if( cargo == null ){
            throw new IllegalStateException( String.format( "no cargocontainer with data matching the itemId '%s' in pid '%s' ", itemId, pid ) );
        }

        log.debug( String.format( "Successfully retrieved datastream. CargoContainer has length %s", cargo.getStreamLength() ) );
        log.debug( String.format( "CargoContainer.mimetype =     %s", cargo.getMimeType() ) );
        log.debug( String.format( "CargoContainer.submitter=     %s", cargo.getSubmitter() ) );
        log.debug( String.format( "CargoContainer.streamlength = %s", cargo.getStreamLength() ) );
        
        log.info( "Successfully retrieved datastream." );
        return cargo;
    } 

    /** \todo: what is this? */
    private void addDatastreamToObject( CargoContainer cargo, String pid, String itemId, String label, char management, char state ){
        /**
         * For future reference (mostly because the Fedora API is unclear on this):
         * addDatastream resides in fedora.server.management.Management.java
         * String pid is the combination namespace:identifier
         * String dsId is the ItemID of the datastream. Can be null and will in this case be autogenerated
         * String[] altIDs is an array of alternative ids. Leaving this as null is not a problem.
         * String label is the humanreadable label for the datastream
         * boolean versionable true if fedora should version the data, false if it should overwrite
         * String MIMEType Just that. Required
         * String formatURI specify the data format through an uri instead of a mimetype
         * String dsLocation specifies the location of the datastream. eg. through an url
         * String controlGroup "X", "M", "R" or "E"
         * String state Initial state of the datastream A, I or D (active, inactive or deleted)
         * String checksumType
         * String checksum
         * String logMessage
         *
         */

        // apim.addDatastream(pid,
        //                    itemId,
        //                    null,
        //                    label,
        //                    false,
        //                    cargo.getMimeType(),
        //                    null,
        //                    cargo.getData(),
        //                    management,
        //                    state,
        //                    null,
        //                    null,
        //                    "Adding Datastream labelled"+label);

    }
}

