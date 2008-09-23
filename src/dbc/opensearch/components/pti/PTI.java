package dbc.opensearch.components.pti;

import dbc.opensearch.components.datadock.CargoContainer;
import dbc.opensearch.tools.Processqueue;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.FedoraHandler;

import com.mallardsoft.tuple.Tuple;
import com.mallardsoft.tuple.Pair;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;
import org.compass.core.CompassException;

import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Logger;
import org.apache.commons.configuration.ConfigurationException;

/**
 * /brief the PTI class is responsible for getting an dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values are updated
 */
public class PTI implements Callable<Long>{

    private static final Logger log = Logger.getRootLogger();
    private FedoraHandler fh;
    private CompassSession session;
    private CargoContainer cc;
    private Processqueue queue;
    private Date finishTime;
    private String fedoraHandle;
    private String datastreamItemID;
    private int queueID;
    private Estimate estimate;

    /**
     * \brief Constructs the PTI instance with the given parameters
     * @param session the compass session this pti should communicate with
     * @param fedoraHandle the handle identifying the data object
     * @param itemID identifying the data object
     * @param fh the fedorahandler, which communicates with the fedora repository
     */
    public PTI(CompassSession session, String fedoraHandle, String itemID, FedoraHandler fh ) throws ConfigurationException, ClassNotFoundException {
        log.debug( String.format( "PTI constructor(session, fedoraHandle=%s, itemID=%s, fh", fedoraHandle, itemID ) );
        this.session = session;
        this.fedoraHandle = fedoraHandle;
        datastreamItemID = itemID;
        this.fh = fh;
        estimate = new Estimate();
    }

    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     * @return the processtime
     */
    public Long call() throws CompassException, IOException, DocumentException, SQLException, ClassNotFoundException {
        log.debug( "Entering PTI.call()" );
        
        doProcessing( );

        log.debug( "Obtain processtime, and writing to statisticDB table in database" );
        finishTime = new Date();
        long processtime = finishTime.getTime() - cc.getTimestamp();
        estimate.updateEstimate( cc.getMimeType(), cc.getStreamLength(), processtime );

        log.info( String.format("Updated estimate with mimetype = %s, streamlength = %s, processtime = %s", cc.getMimeType(), cc.getStreamLength(), processtime ) );
        
        
        return processtime;
    }
    
    /**
     * This is the main entry point for getting stuff
     * processed.
     * @returns Returns a pair where first element is the stream length of the processed item, and the second element is the timestamp from when the item arrived in the datadock
     */
    public void doProcessing( ) throws CompassException, IOException, DocumentException {//, javax.xml.parsers.ParserConfigurationException, IOException, org.xml.sax.SAXException{
        log.debug( "Entering doProcessing" );

        log.debug( String.format( "Constructing CargoContainer from fedoraHandle '%s', datastreamItemID '%s'", fedoraHandle,datastreamItemID ) );
        if( fh != null ){
            cc = fh.getDatastream( fedoraHandle, datastreamItemID );
        }else{
            throw new NullPointerException( "FedoraHandler was null, aborting" );
        }

        log.debug( String.format( "CargoContainer isNull == %s", cc == null ) );
        log.debug( String.format( "CargoContainer.mimetype %s", cc.getMimeType() ) );
        log.debug( String.format( "CargoContainer.submitter %s", cc.getSubmitter() ) );
        log.debug( String.format( "CargoContainer.streamlength %s", cc.getStreamLength() ) );

        log.debug( "Starting transaction on running CompassSession" );
        CompassTransaction transaction = session.beginTransaction();
        Document doc = null;
        SAXReader saxReader = new SAXReader( false );

        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        doc = saxReader.read( cc.getData() );

        log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );

        /** \todo: hardcoded values for alias on xmlaliasedobject */
        AliasedXmlObject xmlObject =
            new Dom4jAliasedXmlObject( "data1", doc.getRootElement() );

        log.debug( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

        // index the object and end if we succeed:
        log.debug( String.format( "Indexing document" ) );
        indexDocument( session, transaction, xmlObject );
        
        log.info( String.format( "Document indexed and stored with Compass" ) );
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
    // private Document convertCargoToXml( CargoContainer cargo ) throws DocumentException, IOException{

    //     /** \todo: encoding should be determined by config/object-fields. See discussion below */
    //     /** ... it could be retrieved from the dom4j.Document.getXMLEncoding() */
    //     /** which would probably benifit from being put into the CargoContainer in the first place... */
    //     return DocumentHelper
    //         .parseText( new String( cargo.getDataBytes(), "ISO-8859-1") );
    // }

    /**
     * Does the indexing and the saving of them
     */

    private void indexDocument( CompassSession session,
                                CompassTransaction trans,
                                AliasedXmlObject cargoXML ) throws CompassException {
        
        log.debug( "Entering indexDocument" );
 
        log.debug( "Beginning transaction" );        
        trans = session.beginTransaction();
     
        log.debug( "Saving aliased xml object to the index" );
        session.save( cargoXML );

        log.debug( "Committing index on transaction" );
        trans.commit();

        log.debug( "Closing session" );
        session.close();

        log.debug( "Document indexed" );
    }

}
