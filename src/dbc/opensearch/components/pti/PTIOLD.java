package dbc.opensearch.components.pti;

import dbc.opensearch.components.datadock.CargoContainer;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.net.URL;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.DocumentBuilder;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;
import org.compass.core.CompassException;
import org.apache.log4j.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.apache.commons.configuration.ConfigurationException;

/**
 * This class handles the setting up of the PTI component components;
 * such as the Compass controller object, the session pool for
 * CompassSessions, the database connections and the communication to
 * the ProcessQueue.
 *
 * Essentially, PTI have the responsibility of providing
 * applications/clients with compass sessions. At the first
 * initalization of the PTI class, a Compass object is created and PTI
 * now handles all communication with this class (primarily through
 * the requests for sessions). Furthermore, PTI handles the number of
 * open sessions that can be handled at any given time. When the last
 * reference to PTI dies, the Compass object dies as well.
 */
public class PTI {

    Logger log = Logger.getLogger("PTI");

    /**
     * The Compass object must be initialized only once. And it should
     * probably be done as a singleton. Client will never need to
     * access the Compass object directly, so there's no getter for
     * this instance
     */
    private static volatile Compass theCompass;

    private static volatile FedoraHandler fh;

    // not needed:
    //    private boolean isIndexed;

    /**
     * An org.compass.core.Compass object is constructed only once,
     * org.compass.core.CompassSessions are extracted on demand from
     * this object
     *
     * Each thread should obtain a new CompassSession
     *
     *     CompassSession session = new PTI.getSession();
     *
     *     CargoContainer data = pti.getDataFromRepository( fedoraFileHandler );
     *
     *     pti.index( data, metadata, session );
     *
     *     Thread sessionThread = new Thread( pti.index( pti.getSession, fedoraFileHandler ) );
     */

    /**
     * Constructor whose primary responsibility it is to configure the
     * Compass instance. First time this constructor is called, a
     * Compass object is initialized from the xml-configuration. All
     * subsequent calls to this constructor
     */
    public PTI( ) throws ConfigurationException {
        if( PTI.theCompass == null ) {

            CompassConfiguration conf = new CompassConfiguration();

            /** \todo: when we get more types of data (than the one we got now...) we should be able to load configurations at runtime. Or just initialize with all possible input-format? */
            URL cfg = getClass().getResource("/compass.cfg.xml");
            URL cpm = getClass().getResource("/xml.cpm.xml");
            
            log.debug( String.format( "Compass configuration=%s", cfg.getFile() ) );
            log.debug( String.format( "XSEM mappings file   =%s", cpm.getFile() ) );
            
            File cpmFile = new File( cpm.getFile() );
            
            conf.configure( cfg );
            conf.addFile( cpmFile );
            
            theCompass = conf.buildCompass();        
            
        }
        else {
            log.info( String.format( "A Compass object is already initialized, using it" ) );
        }
        if( PTI.fh == null ){
            try{
                fh = new FedoraHandler();
            }
            catch (ConfigurationException cex){
                log.fatal( "ConfigurationException: " + cex.getMessage() );
                throw new ConfigurationException( cex.getMessage() );
            }
        }else{
            log.info( String.format( "A FedoraHandler is already initialized, using it" ) );
        }
    }
    
    /**
     * This is the main entry point for getting stuff
     * processed. Threads should call this and we will try to keep
     * concurrent...
     */
    public void doProcessing( String fedoraHandle ) throws CompassException, IOException, DocumentException {//, javax.xml.parsers.ParserConfigurationException, IOException, org.xml.sax.SAXException{
        // prePTI: Dequeue queue = new Dequeue();
        // prePTI: String fedoraHandle = queue.pop(  ); ... and remember to commit on the queue

        // 20: CargoContainer cc = PTI.getDataFromRepository( fedoraHandle );
        CargoContainer cargo = fh.getDatastream( fedoraHandle );

        // 25: retrieve the xml from the cargo:
        //Document doc = convertCargoToXml( cargo );

        // 30: get a compasssession:
        CompassSession session = getSession();

        // 35: start a transaction:
        CompassTransaction transaction = session.beginTransaction();


        Document doc = null;
        SAXReader saxReader = new SAXReader( false );

        try{
            doc = saxReader.read( cargo.getData() );
        }catch( DocumentException de){
            System.out.println(String.format( "DocumentException=%s",de.getMessage() ) );
        }

        AliasedXmlObject xmlObject = 
            new Dom4jAliasedXmlObject( "faktalink", doc.getRootElement() ); 

        // 40: index the object and end iff we succeed: 
        try{
            indexDocument( session, transaction, xmlObject );
        }catch( CompassException ce) {
            // 50: We catch all possible exceptions here and log.fatal them
            log.fatal( 
                      String.format( "Could not index CargoContainer with fedoraHandle %s:\n%s",
                                     fedoraHandle, 
                                     ce.getMessage()
                                     )
                       );
            throw new CompassException( String.format( "Could not index CargoContainer with fedoraHandle %s", fedoraHandle ), ce);
        }
    }

    /**
     * This method is responsible for retrieving the data from the
     * fedora repository. It puts the contents of the Fedora
     * datastream into a CargoContainer object and returns it.Please
     * read the comments as there are many unresolved points in this
     * method
     * \todo: this should probably be in its own class
     */
    private CargoContainer getDataFromRepository( String fedoraHandle ) {
        CargoContainer cargo = null;

        //        FedoraHandler.getDataStream( fedoraHandle );

        // 10: get data from fedora repository
        /** \todo: code/logic to handle fedora requests/retrievals are missing here */
        /** \todo: should we contruct the xml-document from the data here, or later? */
        // 20: retrieve the data, metadata and object-info respectively, from the returned object
        String mimetype = "";
        if( mimetype.equals( "text/xml" ) || mimetype.equals( "application/xml" ) ) {
            // 30: load Compass XSEM for the type of data
            /** \todo: the XmlContentConverter needs the xmldata to be
             * a Reader. We should conform rather than manipulate
             * here.

             * \see (http://www.compass-project.org/docs/2.0.2/api/org/compass/core/converter/xsem/XmlContentConverter.html)
             */
        }
        else if( mimetype.equals( "application/pdf" ) ) {
            // 50: do pdf -> xml and load Compass XSEM
        }
        else {
            // 60: should probably throw some UnsupportedFormatException kind of exception
        }
        return cargo;
    }

    /**
     * Extracts the datastream from the CargoContainer and converts it
     * to a org.dom4j.Document
     * @returns The contents of the CargoContainer as a Document
     */
    private Document convertCargoToXml( CargoContainer cargo ) throws DocumentException, IOException{
        
        /** \todo: encoding should be determined by config/object-fields */
        return DocumentHelper
            .parseText( new String( cargo.getDataBytes(), "ISO-8859-1") );
    }

    private void indexDocument( CompassSession session, 
                                CompassTransaction trans, 
                                AliasedXmlObject cargoXML )throws CompassException {
        if( !session.isClosed() ){
            trans = session.beginTransaction();
        }else{
            log.fatal( String.format( "Compass session closed. The object was not indexed and any indexes currently in orbit was not saved." ) );
            throw new CompassException( "Session unexpectedly closed, cannot initiate transaction on the index" );
        }
        session.save( cargoXML );
        
        if( !session.isClosed() ){
            trans.commit();
            session.close();
        }else{
            log.fatal( String.format( "Compass session closed. The object was indexed but never saved" ) );
            throw new CompassException( "Session closed unexpectedly, object was indexed, but not saved" );
        }

    }

    /**
     * returns a new session from the sessionpool, throwing an
     * exception if the session cannot be obtained.
     * \todo: We need a custom exception for depletion-messages from the pool
     * @returns a CompassSession from the SessionPool, if any are available.
     */
    public CompassSession getSession(){
        if( theCompass == null) {
            log.fatal( String.format( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" ) );
            throw new RuntimeException( "Something very bad happened. getSession was called on an object that in the meantime went null. Aborting" );
        }
        CompassSession s = theCompass.openSession();
        return s;
    }

}