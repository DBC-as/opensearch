package dbc.opensearch.components.datadock.tests.datadocktest;
//import dbc.opensearch.components.processqueue.*; isnt gonna use it
import dbc.opensearch.components.datadock.*;
import dbc.opensearch.tools.Estimate;

import java.io.*;
import org.apache.commons.lang.*;
import org.apache.commons.configuration.*;
import java.sql.*;
import oracle.jdbc.driver.OracleDriver;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;
import org.apache.log4j.Logger;

/**
 * Test for the DataDock class
 *
 *Exceptions are just caught and the stack is printed
*/

public class DataDockTest {
    private static final Logger log = Logger.getRootLogger();

    /**
     * Setting up a DataDock
     */
    DataDock dd;
    CargoContainer cc;
    Connection con = null;
    Estimate estimate;
    public DataDockTest(){
        
        log.debug("Constructing the DataDockTest object ");
        
        // values for the DB connection
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "dbc:oracle:thin:lvh/lvh@tora1.dbc.dk:1521";
        String userID = "lvh"; 
        String passwd = "lvh";
        try{
        estimate = new Estimate();
        }
        catch(Exception e){}
        /*
        config.setProperty("statisticDB.driver" , driver );       
        config.setProperty("statisticDB.url" , url );
        config.setProperty("statisticDB.userID" , userID );
        config.setProperty("statisticDB.passwd" , passwd );
        */
        log.debug("Database connection will be created with values: ");
        log.debug("driver = " + driver + "  ");
        log.debug("url = " + url + " ");
        log.debug("userID = " + userID + " ");
        log.debug("passwd = " + passwd + " ");
        // 10: create connection to the DB
        log.debug("Creating connection to the database ");
        
        //       Connection con = null;
        
        try {
            Class.forName(driver);
            
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(url, userID, passwd);
            
            log.debug("Connection created for the test insertion ");
            
            Statement stmt = null;
            
            stmt = con.createStatement();
            
            log.debug("Statement created in setUp ");
            // 13: setup the statisticDB with mimetype "testtype", dataamount and processtime 2l    
            stmt.executeUpdate ( "INSERT INTO statisticDB (dataamount, processtime, mimetype) VALUES( 2, 2, 'testtype' )" );
            
            log.debug("StatisticDB updated and ready for test query ");

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        // 15: setup the other dbs with relevant test data, look in their independent tests
        // 17: create 
        // 20: construct the CargoContainer
        ByteArrayInputStream data = new ByteArrayInputStream(new byte[500]);
        String mime = "text/xml";
        String lang = "test";
        String submitter = "testSubmitter";

        log.debug("Creating the CargoContainer object ");

        try{
            cc = new CargoContainer(data, mime, lang, submitter);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        log.debug("CargoContainer created  Creating the DataDock object ");
        // 30: construct the DataDock

        try{
        dd = new DataDock(cc);
        }
        catch(Exception e){
            log.debug( "Caught exception, bailing out. ");
            log.debug( e.getMessage() );
            e.printStackTrace();
        }
        log.debug("DataDock created ");
        log.debug("DataDockTest object created ");
    }
    
    /**
     * Testing whether DataDock.estimate can get the values 2 and 2 divide the 
     * first with the second then multiply it 
     * with the length ( 2 ) we give it, to return the value2
     * the method writes whether the test was successful or not     
     */
    public void testDataDockEstimate(){
        /**
         * Printing to check the values of config.xml
         */
        XMLConfiguration config;
       
        URL cfgURL = getClass().getResource("/config.xml");
        config = null;
        try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            // log.fatal( "ConfigurationException: " + cex.getMessage());
            cex.printStackTrace();
        }
        log.debug("Printing the values of the config file ");
        log.debug(config.getString("database.driver") + "");
        log.debug(config.getString("database.url") + "");
        log.debug(config.getString("database.userID") + "");
        log.debug(config.getString("database.passwd") + "");

        try{
            float testEstimate =  estimate.getEstimate( "text/xml", 2l );
            if( testEstimate != 2l){
                log.debug("DataDock.Estimate didnt estimate correctly ");
                log.debug("Printing the estimate: "+ testEstimate +" ");
            }
            else{
                log.debug("DataDock.estimate estimated correctly ");
            }
        }catch(Exception e){
            e.printStackTrace();
        }   
    }
    /**
     * Testing whether DataDock.fedoraStoreData returns a valid handle or ??
     */
    
    public void testDataDockFedoraStoreData(){
        String testFH = "";
        
        try{
            testFH = dd.fedoraStoreData();
        }catch(RemoteException re){
            re.printStackTrace();
        }catch (XMLStreamException xmle){
            xmle.printStackTrace();
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch (ConfigurationException cex){
            cex.printStackTrace();
        }catch( Exception e ){
            e.printStackTrace();
        }
        if(testFH != ""){
            log.debug("Something returned, checking what it is ");
            if (testFH.indexOf("testSubmitter") != 0){
                log.debug("The fedoraHandle received is misshaped.");
                log.debug(" Should be testSubmitter, but is: " + testFH + "" );
            }else{
                log.debug("The returned fedoraHandle is wellshaped ");
            }
        }    
    }
    
    /**
     * Testing that DataDock.queueFedoraHandle does that
     */
    public void testDataDockQueueFedoraHandle(){
        String testFH = "test";
        ResultSet rs = null;
        Statement stmt = null;

        // 10: create the table processqueue with a sequence and check.
        try{
            log.debug("creating statement on the connection ");
            stmt = con.createStatement(); 
            
            log.debug("creating table processqueue ");
            String tableStmt1 = "CREATE TABLE processqueue( \n"+
                "queueid INTEGER,\n "+
                " fedoraHandle VARCHAR(100),\n"+
                " processing CHAR(1) CHECK (processing IN ( 'Y','N')))";
            String tableStmt2 = "CREATE SEQUENCE processqueue_seq\n"+
                "MAXVALUE 1000000000\n"+
                "NOCYCLE";
                log.debug(tableStmt1 + " ");
                stmt.executeUpdate(tableStmt1);
                log.debug("Table processqueue created ");

                log.debug(tableStmt2 + " ");
                stmt.executeUpdate(tableStmt2);
                log.debug("Sequence processqueue_seq created ");
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        // 20: queue the fedora handle
        log.debug("Trying to queue a fedorahandle  ");
        try{
            dd.queueFedoraHandle(testFH);
            log.debug("DataDock.queueFedoraHandle(testFH) called without exceptions thrown");
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // 30: Select the content of the processqueue table to check 
        // the validity of the Enqueue operation
        log.debug("Selecting in the processqueue table to see if it is queued correctly ");        
        try{
            rs = stmt.executeQuery("SELECT * FROM processqueue WHERE fedorahandle = 'test'");
            
            
            if( rs != null ){
                log.debug("Something selected from the processqueue");
                while(rs.next()){
                    log.debug("Printing content of resultset");
                    log.debug("fedorahandle = "+ rs.getString("fedorahandle") +", should be test ");
                    log.debug("queueid = "+ rs.getInt("queueid") +", should be 1 ");
                    log.debug("processing = "+ rs.getString("processing") +", should be N ");
                    
                } 
            } }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        // 40: drop the processqueue table
        try{
            stmt.executeUpdate("DROP TABLE processqueue");
            log.debug("Table processqueue dropped ");
            stmt.executeUpdate("DROP SEQUENCE processqueue_seq");
            log.debug("sequence processqueue_seq dropped ");
        }catch(SQLException sqle){
            sqle.printStackTrace();
            }
        log.debug("testDataDockQueueFedoraHandle ended ");
    }
    
}   
