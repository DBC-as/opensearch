/**
 * \file FedoraHandler.java
 * \brief The FedoraHandler class
 * \package tools
 */
package dk.dbc.opensearch.common.fedora;


import org.apache.log4j.Logger;

import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;


/**
 * \ingroup tools
 * \brief The FedoraHandler class handles connections and communication with
 * the fedora repository.
 */
public class FedoraHandler implements Constants 
{
    private static String user = "";

    Logger log = Logger.getLogger( FedoraHandler.class );

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
    // public FedoraHandler( FedoraClient client ) throws ConfigurationException,/* MalformedURLException,*/ UnknownHostException, ServiceException, IOException 
    // {
/*        log.debug( "Fedorahandler constructor");
        this.client = client;
       
        log.debug( "Obtain config parameters for the fedora user");
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        config = new XMLConfiguration( cfgURL );
        
        //host       = config.getString( "fedora.host" );
        //port       = config.getString( "fedora.port" );
        user       = config.getString( "fedora.user" );
        
        passphrase = config.getString( "fedora.passphrase" );
        fedoraUrl  = "http://" + host + ":" + port + "/fedora";
        
        log.debug( String.format( "Connecting to fedora server at:\n%s\n using user: %s, pass: %s ", fedoraUrl, user, passphrase ) );

        log.debug( "Constructing FedoraClient");

        FedoraClient client = new FedoraClient( fedoraUrl, user, passphrase );
        
        
        apia = client.getAPIA();
        apim = client.getAPIM();
        log.debug( "Got the ClientAPIA and APIM");
*/   // }

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
/*    public String submitDatastream( CargoContainer cargo ) throws RemoteException, XMLStreamException, IOException, IllegalStateException, MarshalException, ValidationException, NullPointerException, ParseException 
    {
        log.debug( String.format( "Entering submitDatastream" ) );
        
        DatastreamDef dDef = null;
        String pid         = null;
        String nextPid     = null;
        String itemId      = null;
        byte[] foxml       = null;

        CargoObjectInfo coi = null;
        List<Byte> cc_data = null;
        for ( Pair< CargoObjectInfo, List<Byte> > content : cargo.getDataLists() )
        {
            coi = content.getFirst();
            cc_data = (ArrayList<Byte>)content.getSecond();

        
        }

        String submitter = "not to be found from cargo directly"; // cargo.getSubmitter( );
        log.debug( String.format( "Getting next pid for namespace %s", submitter ) );
        String pids[] = apim.getNextPID( new NonNegativeInteger( "1" ), submitter );
        nextPid = pids[0];

        log.debug( String.format( "Getting itemId for datastream" ) );
        // \todo: format should not be found from cargo directly
        itemId = "text/xml"; //cargo.getFormat( coi );

        //log.debug( String.format( "Constructing foxml with pid=%s, itemId=%s and label=%s", nextPid, itemId, label ) );
        foxml = FedoraTools.constructFoxml( cargo, nextPid, itemId, coi.getFormat());
        //log.debug( "FOXML constructed, ready for ingesting" );

        pid = apim.ingest( foxml, FOXML1_1.uri, "Ingesting "+coi.getFormat() );

        if( !pid.equals( nextPid ) ){
            log.fatal( String.format( "we expected pid=%s, but got pid=%s", nextPid, pid ) );
            throw new IllegalStateException( String.format( "expected pid=%s, but got pid=%s", nextPid, pid ) );
        }

        log.info( String.format( "Submitted data, returning pid %s", pid ) );
        return pid;
    }*/
}

