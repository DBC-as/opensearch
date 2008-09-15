package dbc.opensearch.components.datadock;

import dbc.opensearch.components.pti.FedoraHandler;
import dbc.opensearch.components.processqueue.*;

import java.util.concurrent.*;
import java.sql.*;
import oracle.jdbc.driver.OracleDriver;
import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;
import org.apache.commons.configuration.*;
import org.apache.commons.lang.*;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;

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
public class DataDock implements Callable<Long>{
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

    public DataDock( CargoContainer cargo ){
         // 10: construct the object with data from cargo    
        // this.pid = pid;
        log.debug("DataDock Constructor"); 
        cc = cargo; 
        // 15: read the config file 
        log.debug( "Obtain config paramaters");
          
     }
     /**
      * call estimates processtime, stores the data and queues 
      * the handle. 
      * @return the processtime estimate.
      * If the return value == 0l, no estimate is made, caller must 
      * check this, but then an exceprion should have been thrown 
      */

    public Long call(){
        long processEstimate = 0l;
        
        try{
        // 10: Estimate
            processEstimate = estimate(cc.getMimeType(), cc.getStreamLength()); 
        // 20: Store data in Fedora
        // 30: queue FedoraHandle
            queueFedoraHandle(fedoraStoreData());
        }catch(Exception e){
            e.printStackTrace();
        }
        // 40: Return estimate 
        return processEstimate;
    }

    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public long estimate( String mimeType, long length ) throws Exception{
        long average_time = 0l;
        ResultSet rs = null;
        

        //remove

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
        // 10: get the db-connection parameters for the db-connection
        log.debug("Obtain config parameters from config file"); 

         try{
             sDriver = config.getString("database.driver");
             sUrl = config.getString("database.url");
             sUserID = config.getString("database.userID");
             sPasswd = config.getString("database.passwd");
         }catch(Exception e){ 
             log.fatal( "Cannot obtain necessary configuration parameters" );
             log.debug( "sDriver: " +sDriver );
             log.debug( "sUrl:    " +sUrl );
             log.debug( "sUserID: " +sUserID );
             log.debug( "sPasswd: " +sPasswd );
             throw new Exception(e.getMessage());
             //System.exit(1);
         } 
         // 20: open database connection
         log.debug( "Establishing connection to statisticDB" );
         Connection con = establishConnection();

         // 25: create statement
         Statement stmt = null;
         String sqlQuery = String.format( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '%s'", mimeType );
         try{
             stmt = con.createStatement();
         }
         catch(Exception e) {
             log.fatal( "Exception: " + e.getMessage() );
             e.printStackTrace();
         }
         // 30: query database:
         //     SELECT average_time FROM statisticDB WHERE mimetype = mimeType;
         // We know we get only one row from the query
         /** \todo: ...but we should still check that we actually get _exactly_ one row */
         try{
             rs = stmt.executeQuery ( sqlQuery );
             log.debug( "statisticDB queried" );
                     System.out.print("Estimate queried the DB\n");
         }
         catch(Exception e){
             log.fatal("Exception: " + e.getMessage() );
             e.printStackTrace();
             
             // 35: compute this.average_time from length, processtime and dataamount 
             /* The statisticDB must garantie that mimetypes are unique, the pti must make a insert stmt 
                like update statisticDB processtime = processtime + newtime, dataamount = dataamount + newdata 
                WHERE mimitype = mimitype.
                The registration of new handlers must manage the mimetype uniqueness.*/
             /** \todo: Do we need a language in connection with the mimetype, 
                 since different languages have different handlers? yes, make later*/
         }
         if( rs != null ){
             /** \todo: what is the content of average_time, if there are more than one row? */
             while(rs.next()){
                 average_time = ( (rs.getInt("processtime") / rs.getInt("dataamount") ) * length);
             }
         }
         else{
             /** \todo: Is it a realistic scenario that we can have an unknown mimetype here? */
             throw new Exception( String.format( "Query \'%s\' returned no results. Most likely the mimetype is unknown.", sqlQuery ) );
         }
         
         // 30: return the estimate
         return average_time;
         
    }

    /** 
     * \todo: construct proper exception like an connnectionerrorexception-type thing 
     * Should this method have a parameter at all? No
     */
    public String fedoraStoreData() throws ConfigurationException, RemoteException, XMLStreamException, IOException, Exception{
        String fedoraHandle = "";
        /**
         * \todo find out where and how we get the pid
         * its format is "namespace:identifier" fx "faktalink:2"  
         * If we use the submitter as namespace we still need a way 
         * to be sure the identifier is unique      
         */
        String usePid = this.cc.getSubmitter();
        /**
         * \todo find out where and how we get the itemId
         */
        String itemId = this.cc.getMimeType();
        //String label = "";
        
       // 10: open connection to fedora base
        if( this.fh == null ){
            try{
                this.fh = new FedoraHandler();
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
            fedoraHandle = this.fh.submitDatastream( this.cc, usePid, itemId );
        }catch( RemoteException re ){
            throw new RemoteException(re.getMessage());
        }catch( XMLStreamException xmle ){
            throw new XMLStreamException(xmle.getMessage());
        }catch( IOException ioe ){
            throw new IOException(ioe.getMessage());
        }catch( Exception e ){
            throw new Exception( e.getMessage() );
        }
        // 30: deposit objectInfo as dissaminator to data
        // 40: deposit metadata as dissamminator to data
        // 50: convert handle to data from fedora to String
        // 60: return fedoraHandle
        
        return fedoraHandle;
    }
    




    /** \todo: construct proper exception like an connnectionerrorexception-type thing */
    public void queueFedoraHandle( String fedoraHandle ) throws Exception{
        /**
         * the Enqueue class queues a fedoraHandle on the processQueue 
         * to take the parameteres from the config file          
         */
        // 10: call Enqueue
        try{
        enq = new Enqueue(fedoraHandle);
        }catch(Exception e){
            throw new Exception(e.getMessage());
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