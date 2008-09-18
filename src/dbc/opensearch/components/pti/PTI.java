package dbc.opensearch.components.pti;

import dbc.opensearch.components.datadock.CargoContainer;
import dbc.opensearch.components.tools.Processqueue;
//import dbc.opensearch.components.tools.tuple.Tuple;
//import dbc.opensearch.components.tools.tuple.Pair;

import java.util.concurrent.Callable;
// import java.io.ByteArrayInputStream;
// import java.io.File;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.sql.SQLException;
//import java.lang.ClassNotFoundException;
// import java.net.URL;
// import javax.xml.parsers.DocumentBuilderFactory;
// import javax.xml.parsers.DocumentBuilder;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
// import org.compass.core.config.CompassConfiguration;
// import org.compass.core.config.CompassConfigurationFactory;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;
import org.compass.core.CompassException;
import org.apache.log4j.Logger;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import java.util.Date;
import org.apache.commons.configuration.ConfigurationException;

public class PTI implements Callable<Float>{
    

    /**
     * Log
     */
    private static final Logger log = Logger.getRootLogger();

    private static volatile FedoraHandler fh;
    private CompassSession ourSession;
    private CargoContainer cc;
    private Processqueue queue;
    private Date finishTime;
    // private Pair<String, Integer> handlePair;
    private String fHandle;
    private int queueID;

    /**
     * Constructor
     */
    public PTI(CompassSession session, String fHandle ) throws ConfigurationException {
        ourSession = session;
        this.fHandle = fHandle;
        this.queueID = queueID;
        try{
        queue = new Processqueue();
        }catch(ConfigurationException ce){
            throw new ConfigurationException( ce.getMessage() );
        }
    }
   
    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     * @return the processtime
     */
    public Float call() throws /*ClassNotFoundException,*/ CompassException, IOException, DocumentException/*, SQLException, NoSuchElementException*/ {
        log.debug( "PTI call function" );
        float processtime = -1f;

        // start timer <-- no need to, timestamp is set in DataDock
        // retrive data from handle : gets done in doProcessing method
        // start compasss transaction : gets done in doProcessing method 
        // index data : Gets done in doProcessing method
        // store data : Gets done in the doProcessing method
        try{
        doProcessing(fHandle);
        }catch( CompassException ce ){
            throw new CompassException( ce.getMessage() );
        }catch( IOException ioe ){
            throw new IOException( ioe.getMessage() );
        }catch( DocumentException de ){
            throw new DocumentException( de.getMessage() );
        }
        // create processtime from timestamp and current time
        // commit to processqueue that the element can be removed
        finishTime = new Date();
        processtime = finishTime.getTime() - cc.getTimestamp();
        // update statisticDB
        // return processtime
        return processtime;
    }
     
 /**
     * This is the main entry point for getting stuff
     * processed. 
     */
    public void doProcessing( String fedoraHandle ) throws CompassException, IOException, DocumentException {//, javax.xml.parsers.ParserConfigurationException, IOException, org.xml.sax.SAXException{
        // prePTI: Dequeue queue = new Dequeue();
        // prePTI: String fedoraHandle = queue.pop(  ); ... and remember to commit on the queue
        
        // 20: CargoContainer cc = PTI.getDataFromRepository( fedoraHandle );
        // CargoContainer cargo = fh.getDatastream( fedoraHandle );
        this.cc = fh.getDatastream( fedoraHandle );

        // 25: retrieve the xml from the cargo:
        //Document doc = convertCargoToXml( cargo );
        
        // 30: get a compasssession:
        //CompassSession session = getSession();
        
        // 35: start a transaction:
        CompassTransaction transaction = ourSession.beginTransaction();
        
        
        Document doc = null;
        SAXReader saxReader = new SAXReader( false );
        
        try{
            doc = saxReader.read( cc.getData() );
        }catch( DocumentException de){
            System.out.println(String.format( "DocumentException=%s",de.getMessage() ) );
        }
        
        AliasedXmlObject xmlObject = 
            new Dom4jAliasedXmlObject( "faktalink", doc.getRootElement() ); 
        
        // 40: index the object and end iff we succeed: 
        try{
            indexDocument( ourSession, transaction, xmlObject );
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
     * Extracts the datastream from the CargoContainer and converts it
     * to a org.dom4j.Document
     * @returns The contents of the CargoContainer as a Document
     *
     * ***** Dont think this method is needed *****
     * the data is retrived in doProcessing 
     *
     */
       private Document convertCargoToXml( CargoContainer cargo ) throws DocumentException, IOException{
        
        /** \todo: encoding should be determined by config/object-fields */
        return DocumentHelper
            .parseText( new String( cargo.getDataBytes(), "ISO-8859-1") );
    }

       /**
        * Does the indexing and the saving of them
        */

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
    
}