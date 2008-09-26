package dbc.opensearch.components.pti;

import dbc.opensearch.components.datadock.CargoContainer;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.FedoraHandler;

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

    Logger log = Logger.getLogger("PTI");

    private FedoraHandler fh;
    private CompassSession session;
    private CargoContainer cc;
    private Date finishTime;
    private String fedoraHandle;
    private String datastreamItemID;
    private Estimate estimate;

    /**
     * \brief Constructs the PTI instance with the given parameters
     * @param session the compass session this pti should communicate with
     * @param fedoraHandle the handle identifying the data object
     * @param itemID identifying the data object
     * @param fh the fedorahandler, which communicates with the fedora repository
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
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
     * @throws CompassException
     * @throws IOException
     * @throws DocumentException
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws ClassNotFoundException if the databasedriver is not found
     */
     */
    public Long call() throws CompassException, IOException, DocumentException, SQLException, ClassNotFoundException, InterruptedException {
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
     * /brief doProcessing constructs a cargoContainer by calling the
     * fedorahandler and indexing the document wint the indexDocument
     * menthod
     * @throws CompassException
     * @throws IOException
     * @throws DocumentException
     */
    public void doProcessing( ) throws CompassException, IOException, DocumentException, InterruptedException {
        log.debug( "Entering doProcessing" );

        log.debug( String.format( "Constructing CargoContainer from fedoraHandle '%s', datastreamItemID '%s'", fedoraHandle, datastreamItemID ) );

        if( fh == null ){
            throw new NullPointerException( "FedoraHandler was null, aborting" );
        }

        cc = fh.getDatastream( fedoraHandle, datastreamItemID );

        log.debug( String.format( "CargoContainer.mimetype %s", cc.getMimeType() ) );
        log.debug( String.format( "CargoContainer.submitter %s", cc.getSubmitter() ) );
        log.debug( String.format( "CargoContainer.streamlength %s", cc.getStreamLength() ) );

        log.debug( "Starting transaction on running CompassSession" );
        Document doc = null;
        SAXReader saxReader = new SAXReader( false );

        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        doc = saxReader.read( cc.getData() );

        // this log line is _very_ verbose, but useful in a tight situation
        // log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );

        AliasedXmlObject xmlObject =
            new Dom4jAliasedXmlObject( cc.getFormat(), doc.getRootElement() );

        log.debug( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

        // index the object and end if we succeed:
        log.debug( String.format( "Indexing document" ) );
        indexDocument( session, xmlObject );

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
     * Does the indexing and the saving of the indexes
     * @param session The compassSession to use
     * @param cargoXML the xml constructed from a CargoContainer
     * @throws CompassException
     */
    private void indexDocument( CompassSession session,
                                AliasedXmlObject cargoXML ) throws CompassException, InterruptedException {
        log.debug( "Entering indexDocument" );

        log.debug( String.format( "Getting transaction object" ) );
        CompassTransaction trans;

        log.debug( "Beginning transaction" );
        trans = session.beginTransaction();

        log.debug( "Saving aliased xml object to the index" );
        session.save( cargoXML );
        log.debug( "Committing index on transaction" );
        trans.commit();
        // while( ! trans.wasCommitted() ){
            // log.debug( String.format( "Sleeping %s", System.currentTimeMillis()/1000f ) );
            // Thread.sleep( 1000 );
        // }
        log.debug( String.format( "************ Transaction wasCommitted() == %s", trans.wasCommitted() ) );
        log.debug( "Document indexed" );
        log.debug( "Closing session" );
        session.close();



    }

}
