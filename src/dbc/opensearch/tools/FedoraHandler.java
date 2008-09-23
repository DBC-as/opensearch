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
    public String submitDatastream( CargoContainer cargo, String pidNS, String itemId, String label )throws RemoteException, XMLStreamException, IOException {
        log.debug( String.format( "submitDatastream(cargo, pidNS=%s, itemId=%s, label=%s) called", pidNS, itemId, label ) );
        
        DatastreamDef dDef = null;
        String pid         = null;
        String namespace   = null;
        byte[] foxml       = null;

        foxml = constructFoxml( cargo, pidNS, itemId, label );
        log.debug( "FOXML constructed, ready for ingesting" );

        pid = apim.ingest( foxml, FOXML1_1.uri, "Ingesting "+label );

        log.info( String.format( "Submitted data, recieved pid %s", pid ) );
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
    private byte[] constructFoxml( CargoContainer cargo, String pidNS, String itemId, String label ) throws IOException, XMLStreamException {
        log.debug( String.format( "constructFoxml(cargo, pidNS=%s, itemId=%s, label=%s) called", pidNS, itemId, label ) );
    
        log.debug( "Starting constructing xml" );
        Document document = DocumentHelper.createDocument();
        // Generate root element
        QName root_qn = QName.get( "digitalObject", "foxml", "info:fedora/fedora-system:def/foxml#" );
        Element root = document.addElement( root_qn );
        root.addAttribute( "xmlns:xsi",          "http://www.w3.org/2001/XMLSchema-instance" );
        root.addAttribute( "xsi:scheamLocation", "info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd");
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
        /** todo: ryd op i flg linjer */
        property = root.addElement( "foxml:datastream" );
        property.addAttribute( "CONTROL_GROUP", "M" ); //this shold be programmable -- bestemt dynamisk gives af objektet
        property.addAttribute( "ID", cargo.getSubmitter() );
        property.addAttribute( "STATE", "A" ); //... as should this
        property.addAttribute( "VERSIONABLE", "false" ); //... and this

        // datastreamVersionElement
        property = property.addElement( "foxml:datastreamVersion" );
        property.addAttribute( "CREATED", String.format( "%s", timeNow ) );
        property.addAttribute( "ID", itemId );
        property.addAttribute( "LABEL", label ); //note: we use the same label as for the digital object
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
     * @param itemID
     * @returns The cargocontainer constructed
     */    
    public CargoContainer getDatastream( String pid, String itemID ) throws IOException, NoSuchElementException, RemoteException{
        log.debug( String.format( "getDatastream( pid=%s, itemID=%s ) called", pid, itemID ) );
        
        CargoContainer cargo = null;
        DatastreamDef[] datastreams = null;
        MIMETypedStream ds = null;

        log.debug( String.format( "Retrieving datastream information for PID %s", pid ) );
        
        datastreams = this.apia.listDatastreams( pid, null );
        
        log.debug( String.format( "Iterating datastreams" ) );
        
        for ( DatastreamDef def : datastreams ){
            log.debug( String.format( "Got DatastreamDef with id=%s", def.getID() ) );
            
            if( def.getID().equals( itemID ) ){
                
                ds = apia.getDatastreamDissemination( pid, def.getID(), null );
 
                log.debug( String.format( "Making a bytearray of the datastream" ) );
                byte[] datastr = ds.getStream();

                log.debug( String.format( "Preparing the datastream for the CargoContainer" ) );
                InputStream inputStream = new ByteArrayInputStream( datastr );

                log.debug( String.format( "DataStream ID      =%s", def.getID() ) );
                log.debug( String.format( "DataStream Label   =%s", def.getLabel() ) );
                log.debug( String.format( "DataStream MIMEType=%s", def.getMIMEType() ) );

                // dc:format holds mimetype as well
                /** \todo: need to get language dc:language */
                String language = "";

                cargo = new CargoContainer( inputStream,
                                            def.getMIMEType(),
                                            language,
                                            itemID );
            }
        }
        log.debug( String.format( "Successfully retrieved datastream. CargoContainer has length %s", cargo.getStreamLength() ) );
        log.debug( String.format( "CargoContainer.mimetype =     %s", cargo.getMimeType() ) );
        log.debug( String.format( "CargoContainer.submitter=     %s", cargo.getSubmitter() ) );
        log.debug( String.format( "CargoContainer.streamlength = %s", cargo.getStreamLength() ) );
        
        log.info( "Successfully retrieved datastream." );
        return cargo;
    } 

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

