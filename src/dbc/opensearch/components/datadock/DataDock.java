package dbc.opensearch.components.datadock;

import dbc.opensearch.components.pti.FedoraHandler;
import dbc.opensearch.components.processqueue.*;

import java.sql.Connection;

import java.sql.Statement;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;


import java.util.concurrent.Callable;

import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;
import java.util.NoSuchElementException;


/**
 * \brief The public interface for the OpenSearch DataDockService
 * DataDock, together with DataDockPool, is the primary accesspoint
 * for the delivery of material to be saved in the Fedora repository
 * and processed by lucene. The DataDock interface allows clients to
 * submit data that represents a textual material to be stored in a
 * Fedora repository and indexed by Lucene. When submitted, the data
 * is validated against a dictionary of possible handlers using the
 * supplied metadata and object-information. All methods throw
 * exceptions on errors.
 *
 * \todo a schema for errors returned should be defined
 *
 * DataDock is the central class in the datadock component. This class
 * offers the service of receiving data, metadata and objectinfo for
 * later processing with lucene. The primary responsibility of the
 * DataDock is to validate incoming data and construct data-carrying
 * objects for use within OpenSearch. Furthermore, DataDock starts the
 * process of fedora storing, data processing, indexing and
 * search-capabilities
 */

public class DataDock implements Callable<Float>{
    CargoContainer cc;
    Enqueue enq;
    // String pid;
    private XMLConfiguration config;
    private static volatile FedoraHandler fh;
    /**
     * The following 4 privates are for use for the databaseoperation
     * for the estimate of the processtime. The "s" in the variable name
     * means its for the operations on statisticDB
     */
    private static String sDriver = "";
    private static String sUrl = "";
    private static String sUserID = "";
    private static String sPasswd = "";

    /**
     * Log
     */
    private static final Logger log = Logger.getRootLogger();

    /**
     * DataDock is initialized with a CargoContainer and a configuration file for the
     * database access. The same config file is used for all the needed connections
     */
    //public DataDock( CargoContainer cargo, String pid ){

    public DataDock( CargoContainer cargo ) throws ConfigurationException{

        log.debug("DataDock Constructor");
        cc = cargo;

        // read the config file
        log.debug( "Obtain config paramaters");
        URL cfgURL = getClass().getResource("/config.xml");
        config = null;
        try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            log.fatal( "ConfigurationException: " + cex.getMessage() );
            throw new ConfigurationException( cex.getMessage() );
        }

        sDriver = config.getString("database.driver");
        sUrl = config.getString("database.url");
        sUserID = config.getString("database.userID");
        sPasswd = config.getString("database.passwd");

        log.info( "sDriver: " +sDriver );
        log.info( "sUrl:    " +sUrl );
        log.info( "sUserID: " +sUserID );

    }
    /**
     * call estimates processtime, stores the data and queues
     * the handle.
     * @return the processtime estimate.
     * If the return value == 0l, no estimate is made, caller must
     * check this, but then an exceprion should have been thrown
     */

    public Float call() throws SQLException, NoSuchElementException, ConfigurationException, RemoteException, XMLStreamException, IOException, ClassNotFoundException, Exception{
        Float processEstimate = 0f;

        try{
            // 10: Estimate
            processEstimate = estimate(cc.getMimeType(), cc.getStreamLength());
            // 20: Store data in Fedora
            // 30: queue FedoraHandle
            queueFedoraHandle(fedoraStoreData());
        }

        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException(cne.getMessage());
        }
        catch(ConfigurationException ce) {
            throw new ConfigurationException( ce.getMessage() );
        }
        catch(SQLException sqe) {
            throw new SQLException( sqe.getMessage() );
        }
        catch(NoSuchElementException nee) {
            throw new NoSuchElementException( nee.getMessage() );
        }
        catch(RemoteException re) {
            throw new RemoteException( re.getMessage() );
        }
        catch(XMLStreamException xe) {
            throw new XMLStreamException( xe.getMessage() );
        }
        catch(IOException  ioe) {
            throw new IOException( ioe.getMessage() );
        }
        catch( Exception e ) {
            throw new Exception( e.getMessage() );
        }


        // 40: Return estimate
        return processEstimate;
    }

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public float estimate( String mimeType, long length ) throws SQLException, NoSuchElementException{

        log.info( String.format( "in estimate(). Length=%s",length ) );

        float average_time = 0f;
        ResultSet rs = null;

        // 20: open database connection
        log.debug( "Establishing connection to statisticDB" );
        Connection con = establishConnection();

        // 25: create statement
        Statement stmt = null;

        String sqlQuery = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimeType );

        log.info( String.format( "SQL Query == %s", sqlQuery ) );

        try{
            stmt = con.createStatement( ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        try{
            rs = stmt.executeQuery ( sqlQuery );
            log.info( String.format( "statisticDB queried with \"%s\"", sqlQuery ) );
        }
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }
        if( rs == null ){
            throw new NoSuchElementException( String.format( "We didnt get anything from the database, the mimetype \"%s\"is unknown.", mimeType ) );
        }
        rs.last();
        int rowCount = rs.getRow();
        log.info( String.format( "rows = %s", rowCount ) );

        if( rowCount != 1 ){
            throw new SQLException( String.format( "Count if rows is different from 1. RowCount==%s", rowCount) );
        }
        rs.first();

        float p = rs.getInt("processtime");
        float d = rs.getInt("dataamount");
        average_time = ( ( p / d ) * length );
        log.info( String.format( "\nprocesstime=%s\ndataamount=%s\np/d=%s\naverage time for mimetype %s = %s", p, d, p/d, mimeType, average_time ) );

        return average_time;


    }
    /**
     *
     */
    public String fedoraStoreData() throws ConfigurationException, RemoteException, XMLStreamException, IOException, Exception {
        String fedoraHandle = "";
        /**
         * \todo find out where and how we get the pid
         * its format is "namespace:identifier" fx "faktalink:2"
         * If we use the submitter as namespace we still need a way
         * to be sure the identifier is unique
         */
        String usePid = cc.getSubmitter();
        /**
         * \todo find out where and how we get the itemId
         */
        String itemId = cc.getMimeType().substring( cc.getMimeType().indexOf("/") + 1 );
        
        /** todo: give real applicable value to label. value should be given by cargo container*/
        String label = "test";
        // 10: open connection to fedora base
        if( fh == null ){
            try{
                fh = new FedoraHandler();
            }
            catch (ConfigurationException cex){
                throw new ConfigurationException( cex.getMessage() );
            }

        }else{
            log.info( String.format( "A FedoraHandler is already initialized, using it" ) );
        }

        // 20: submit data
        try{
            // The next 2 lines of code waits for the getNextPid method from fh
            // String submitter = cargo.getSubmitter();
            //try{
            // String usePid = submitter + ":" + fh.getNextPid(submitter);
            // }catch (RemoteException re){
            // }catch(XMLStreamException xmle){
            // }catch(IOException ioe){
            //}
            fedoraHandle = fh.submitDatastream( cc, usePid, itemId, label );
        }catch( RemoteException re ){
            throw new RemoteException(re.getMessage());
        }catch( XMLStreamException xmle ){
            throw new XMLStreamException(xmle.getMessage());
        }catch( IOException ioe ){
            throw new IOException(ioe.getMessage());
        }
        catch( fedora.server.errors.ServerException se ){
            throw new Exception( se.getMessage() ) ;
        }
        //        }catch( Exception e ){
        // throw new Exception( e.getMessage() );

        // 30: deposit objectInfo as dissaminator to data
        // 40: deposit metadata as dissamminator to data
        // 50: convert handle to data from fedora to String
        // 60: return fedoraHandle
        log.info( String.format( "Ingest succeded, returning pid=%s",fedoraHandle ) );
        return fedoraHandle;
    }

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public void queueFedoraHandle( String fedoraHandle ) throws ClassNotFoundException, ConfigurationException, SQLException {
        /**
         * the Enqueue class queues a fedoraHandle on the processQueue
         * to take the parameteres from the config file
         */
        // 10: call Enqueue
        try{
            enq = new Enqueue(fedoraHandle);
        }
        catch(ClassNotFoundException cne){
            throw new ClassNotFoundException(cne.getMessage());
        }
        catch(ConfigurationException ce){
            throw new ConfigurationException(ce.getMessage());
        }
        catch(SQLException sqe){
            throw new SQLException(sqe.getMessage());
        }

    }

    /**
     * Creates a connection to the statisticDB
     * code stolen from Enqueue.java
     */
    private static Connection establishConnection() {

        Connection con = null;

        try {
            Class.forName(sDriver);

        } catch(java.lang.ClassNotFoundException e) {
            log.fatal( "Exception: " + e.getMessage() );
            e.printStackTrace();
        }

        try {
            con = DriverManager.getConnection(sUrl, sUserID, sPasswd);
        } catch(SQLException ex) {
            log.fatal( "1SQLException: " + ex.getMessage() );
            ex.printStackTrace();
        }

        log.debug( "Got connection." );

        return con;

    }

}