/**
 * \file PTI.java
 * \brief The PTI Class
 * \package pti
 */
package dk.dbc.opensearch.components.pti;

import dk.dbc.opensearch.components.datadock.CargoContainer;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.fedora.FedoraHandler;

import java.io.IOException;
import java.io.StringReader;
import java.sql.SQLException;
import java.util.Date;
import java.util.NoSuchElementException;
import java.util.concurrent.Callable;

import org.compass.core.Compass;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.Resource;
import org.compass.core.impl.DefaultCompassSession;

import org.compass.core.lucene.LuceneProperty;
import org.compass.core.marshall.MarshallingStrategy;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.DocumentHelper;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.commons.configuration.ConfigurationException;

/**
 * \ingroup pti
 * \brief the PTI class is responsible for getting an dataobject from the
 * fedora repository, and index it with compass afterwards. If this
 * was succesfull the estimate values are updated
 */
public class PTI implements Callable<Long>{

    Logger log = Logger.getLogger("PTI");

    private FedoraHandler fh;
    //    private DefaultCompassSession session;
    private CompassSession session;
    private CargoContainer cc;
    private Date finishTime;
    private String fedoraHandle;
    private String datastreamItemID;
    private Estimate estimate;
    private SAXReader saxReader;

    /**
     * \brief Constructs the PTI instance with the given parameters
     *
     * @param session the compass session this pti should communicate with
     * @param fedoraHandle the handle identifying the data object
     * @param itemID identifying the data object
     * @param fh the fedorahandler, which communicates with the fedora repository
     * @param estimate used to update the estimate table in the database
     *
     * @throws ConfigurationException error reading configuration file
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public PTI(CompassSession session, String fedoraHandle, String itemID, FedoraHandler fh, Estimate estimate ) throws ConfigurationException, ClassNotFoundException {
        log.debug( String.format( "PTI constructor(session, fedoraHandle=%s, itemID=%s, fh", fedoraHandle, itemID ) );

        if( fh == null ){
            throw new NullPointerException( "FedoraHandler was null, aborting" );
        }
        this.fh = fh;        

        //        this.session = ( DefaultCompassSession )session;
        this.session = session;
        this.fedoraHandle = fedoraHandle;
        datastreamItemID = itemID;
        this.estimate = estimate;

        saxReader = new SAXReader( false );
        finishTime = new Date();
    }
    
    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     *
     * @return the processtime
     *
     * @throws CompassException something went wrong with the compasssession
     * @throws IOException something went wrong initializing the fedora client
     * @throws DocumentException Couldnt read the xml data from the cargocontainer
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws ClassNotFoundException if the databasedriver is not found
     */
    public Long call() throws CompassException, IOException, DocumentException, SQLException, ClassNotFoundException, InterruptedException {
        return call( saxReader, finishTime, fh, fedoraHandle, datastreamItemID );
    }

    /**
     * call is the main function of the PTI class. It reads the data
     * pointed to by the fedorahandler given to the class in the
     * constructor and indexes it with compass, and finally returning
     * a float, representing the processtime for the data pointed to
     * by the fedorahandle.
     * \todo: This method is probably not neccessary and should be removeed. made for testing purposes
     *
     * @return the processtime
     *
     * @param saxReader The SaxReader instance to use
     * @param finishTime Date to hold finishedprocessing time
     * @param fh The FedoraHandler instance to use
     * @param fedoraHandle the handle identifying the data object
     * @param datastreamItemID identifying the data object
     *
     * @throws CompassException something went wrong with the compasssession
     * @throws IOException something went wrong initializing the fedora client
     * @throws DocumentException Couldnt read the xml data from the cargocontainer
     * @throws SQLException if there is something wrong the database connection or the sqlquery
     * @throws ClassNotFoundException if the databasedriver is not found
     */    
    public Long call( SAXReader saxReader, Date finishTime, FedoraHandler fh, String fedoraHandle, String datastreamItemID ) throws CompassException, IOException, DocumentException, SQLException, ClassNotFoundException, InterruptedException {
        log.debug( "Entering PTI.call()" );


        // Constructing CargoConatiner
        log.debug( String.format( "Constructing CargoContainer from fedoraHandle '%s', datastreamItemID '%s'", fedoraHandle, datastreamItemID ) );
        cc = fh.getDatastream( fedoraHandle, datastreamItemID );
        log.debug( String.format( "CargoContainer.mimetype %s", cc.getMimeType() ) );
        log.debug( String.format( "CargoContainer.submitter %s", cc.getSubmitter() ) );
        log.debug( String.format( "CargoContainer.streamlength %s", cc.getStreamLength() ) );

        // Construct doc and Start Transaction
        log.debug( "Starting transaction on running CompassSession" );
        Document doc = null;

        log.debug( String.format( "Trying to read CargoContainer data from .getData into a dom4j.Document type" ) );
        doc = saxReader.read( cc.getData() );

        // this log line is _very_ verbose, but useful in a tight situation
        // log.debug( String.format( "Constructing AliasedXmlObject from Document. RootElement:\n%s", doc.getRootElement().asXML() ) );

        AliasedXmlObject xmlObject = new Dom4jAliasedXmlObject(  cc.getFormat(), doc.getRootElement() );

        log.debug( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

        log.debug( String.format( "Indexing document" ) );

        // getting transaction object and saving index
        log.debug( String.format( "Getting transaction object" ) );
        CompassTransaction trans;

        log.debug( "Beginning transaction" );
        trans = session.beginTransaction();

        log.debug( "Saving aliased xml object to the index" );
        session.save( xmlObject );

        String alias = xmlObject.getAlias();

        log.debug( String.format( "Preparing for insertion of fedora handle '%s' into the index with alias %s", fedoraHandle, alias ) );

        Resource xmlObj2 = session.loadResource( alias, xmlObject );

        // \todo do we need to remove the xmlObject from the index?
        log.debug( String.format( "Deleting old xml object" ) );
        session.delete( xmlObject );

        log.debug( String.format( "Setting field fedoraHandle='%s'", fedoraHandle ) );

        LuceneProperty fedoraField = new LuceneProperty( new Field( "fedora_uri", new StringReader( fedoraHandle ) ) );
        xmlObj2.addProperty( fedoraField );

        session.save( xmlObj2 );

        log.debug( "Committing index on transaction" );
        trans.commit();
        
        log.debug( String.format( "Transaction wasCommitted() == %s", trans.wasCommitted() ) );
        session.close();

        log.info( String.format( "Document indexed and stored with Compass" ) );

        log.debug( "Obtain processtime, and writing to statisticDB table in database" );
        
        long processtime = finishTime.getTime() - cc.getTimestamp();
        

        estimate.updateEstimate( cc.getMimeType(), cc.getStreamLength(), processtime );

        log.info( String.format("Updated estimate with mimetype = %s, streamlength = %s, processtime = %s", cc.getMimeType(), cc.getStreamLength(), processtime ) );

        return processtime;
    }    
}
