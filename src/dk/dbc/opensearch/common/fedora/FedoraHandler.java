/**
 * \file FedoraHandler.java
 * \brief The FedoraHandler class
 * \package tools
 */
package dk.dbc.opensearch.common.fedora;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.UnknownHostException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.NoSuchElementException;

import javax.xml.rpc.ServiceException;
import javax.xml.stream.XMLStreamException;

import org.apache.axis.encoding.Base64;
import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.lang.NotImplementedException;
import org.apache.log4j.Logger;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.Marshaller;
import org.exolab.castor.xml.ValidationException;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.xsd.ContentDigest;
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
import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;

/**
 * \ingroup tools
 * \brief The FedoraHandler class handles connections and communication with
 * the fedora repository.
 */
public class FedoraHandler implements Constants {
    /*
    private static String host = "";
    private static String port = "";
    private static String fedoraUrl = "";
    private static String passphrase = "";
    */
    private static String user = "";

    Logger log = Logger.getLogger("FedoraHandler");

    // The fedora api
    FedoraClient client;
    FedoraAPIA apia;
    FedoraAPIM apim;

    /**
     * \brief The constructor for the FedoraHandler connects to the fedora
     * base and initializes the FedoraClient. FedoraClient is used to
     * get the Fedora API objects.
     * FedoraHandler
     *
     * @throws ConfigurationException error reading configuration file
     * @throws MalformedURLException error obtaining fedora configuration
     * @throws UnknownHostException error obtaining fedora configuration
     * @throws ServiceException something went wrong initializing the fedora client
     * @throws IOException something went wrong initializing the fedora client
     */
    public FedoraHandler( FedoraClient client ) throws ConfigurationException,/* MalformedURLException,*/ UnknownHostException, ServiceException, IOException 
    {
        log.debug( "Fedorahandler constructor");
        this.client = client;
       
        log.debug( "Obtain config parameters for the fedora user");
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );
        
        //host       = config.getString( "fedora.host" );
        //port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        /*
        passphrase = config.getString( "fedora.passphrase" );
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );

        log.debug( "Constructing FedoraClient");

        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );
        */
        
        apia = client.getAPIA();
        apim = client.getAPIM();
        log.debug( "Got the ClientAPIA and APIM");
    }

    /**
     * Submits the datastream to fedora repository 
     * \todo: what are these parameters?
     *
     * @param cargo the cargocontainer with the data 
     * @param label the identifier for the data - used to construct the FOXML
     *
     * @throws RemoteException error in communiction with fedora
     * @throws XMLStreamException an error occured during xml document creation
     * @throws IOException something went wrong initializing the fedora client
     * @throws IllegalStateException pid mismatch when trying to write to fedora
     * @throws NullPointerException 
     * @throws ValidationException 
     * @throws MarshalException 
     */
    public String submitDatastream( CargoContainer cargo, String label ) throws RemoteException, XMLStreamException, IOException, IllegalStateException, MarshalException, ValidationException, NullPointerException, ParseException 
    {
        log.debug( String.format( "submitDatastream(cargo, %s) called", label ) );
        
        DatastreamDef dDef = null;
        String pid         = null;
        String nextPid     = null;
        String itemId      = null;
        byte[] foxml       = null;
        String submitter = cargo.getSubmitter();
        /** \todo: We need a pid-manager for getting lists of available pids for a given ns */
        log.debug( String.format( "Getting next pid for namespace %s", submitter ) );
        String pids[] = apim.getNextPID( new NonNegativeInteger( "1" ), submitter );
        nextPid = pids[0];

        log.debug( String.format( "Getting itemId for datastream" ) );
        itemId = cargo.getFormat();

        log.debug( String.format( "Constructing foxml with pid=%s, itemId=%s and label=%s", nextPid, itemId, label ) );
        foxml = FedoraTools.constructFoxml( cargo, nextPid, itemId, label );
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
     * \brief creates a cargocontainer by getting a dataobject from the repository, identified by the parameters.
     * \todo: what are these parameters?
     *
     * @param pid 
     * @param itemID
     *
     * @returns The cargocontainer constructed
     *
     * @throws NotImplementedException
     */    
    public CargoContainer getDatastream( java.util.regex.Pattern pid, java.util.regex.Pattern itemID ) throws NotImplementedException
    {
        throw new NotImplementedException( "RegEx matching on pids not yet implemented" );
    }
    
    
    /**
     * \brief creates a cargocontainer by getting a dataobject from the repository, identified by the parameters.
     * \todo: what are these parameters?
     *
     * @param pid 
     * @param itemId
     *
     * @returns The cargocontainer constructed
     *
     * @throws IOException something went wrong initializing the fedora client
     * @throws NoSuchElementException if there is no matching element on the queue to pop
     * @throws RemoteException error in communiction with fedora
     * @throws IllegalStateException pid mismatch when trying to write to fedora
     */    
    public CargoContainer getDatastream( String pid, String itemId ) throws IOException, NoSuchElementException, RemoteException, IllegalStateException
    {
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
        
        for ( DatastreamDef def : datastreams )
        {
            log.debug( String.format( "Got DatastreamDef with id=%s", def.getID() ) );
            
            if( def.getID().equals( itemId ) )
            {                
                log.debug( String.format( "trying to retrieve datastream with pid='%s' and itemId_version='%s'", pid, itemId ) );
                ds = apia.getDatastreamDissemination( pid, itemId, null );
                // pid and def.getID() are equal, why give them both?
 
                log.debug( String.format( "Making a bytearray of the datastream" ) );
                byte[] datastr = ds.getStream();

                log.debug( String.format( "Preparing the datastream for the CargoContainer" ) );
                InputStream inputStream = new ByteArrayInputStream( datastr );

                log.debug( String.format( "DataStream ID      =%s", itemId ) );
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
        
        if( cargo == null )
        {
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
    private void addDatastreamToObject( CargoContainer cargo, String pid, String itemId, String label, char management, char state )
    {
        /**
         * For future reference (mostly because the Fedora API is unclear on this):
         * addDatastream resides in fedora.server.management.Management.java
         * String pid is the combination namespace:identifier
         * String dsId is the itemID of the datastream. Can be null and will in this case be autogenerated
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

