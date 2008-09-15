package dbc.opensearch.components.datadock.tests.datadocktest;
//import dbc.opensearch.components.processqueue.*; isnt gonna use it
import dbc.opensearch.components.datadock.*;

import java.io.*;
import org.apache.commons.lang.*;
import org.apache.commons.configuration.*;
import java.sql.*;
import oracle.jdbc.driver.OracleDriver;
import java.net.URL;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.rmi.RemoteException;
/**
 * Test for the DataDock class
 *
 *Exceptions are just caught and the stack is printed
*/

public class DataDockTest {

    /**
     * Setting up a DataDock
     */
    DataDock dd;
    CargoContainer cc;
    Connection con = null;
    public DataDockTest(){
        
        System.out.print("Constructing the DataDockTest object \n");
        
        // values for the DB connection
        String driver = "oracle.jdbc.driver.OracleDriver";
        String url = "dbc:oracle:thin:lvh/lvh@tora1.dbc.dk:1521";
        String userID = "lvh"; 
        String passwd = "lvh";

        /*
        config.setProperty("statisticDB.driver" , driver );       
        config.setProperty("statisticDB.url" , url );
        config.setProperty("statisticDB.userID" , userID );
        config.setProperty("statisticDB.passwd" , passwd );
        */
        System.out.print("Database connection will be created with values: \n");
        System.out.print("driver = " + driver + "  \n");
        System.out.print("url = " + url + " \n");
        System.out.print("userID = " + userID + " \n");
        System.out.print("passwd = " + passwd + " \n");
        // 10: create connection to the DB
        System.out.print("Creating connection to the database \n");
        
        //       Connection con = null;
        
        try {
            Class.forName(driver);
            
        } catch(ClassNotFoundException e) {
            e.printStackTrace();
        }
        try {
            con = DriverManager.getConnection(url, userID, passwd);
            
            System.out.print("Connection created for the test insertion \n");
            
            Statement stmt = null;
            
            stmt = con.createStatement();
            
            System.out.print("Statement created in setUp \n");
            // 13: setup the statisticDB with mimetype "testtype", dataamount and processtime 2l    
            stmt.executeUpdate ( "INSERT INTO statisticDB (dataamount, processtime, mimetype) VALUES( 2, 2, 'testtype' )" );
            
            System.out.print("StatisticDB updated and ready for test query \n");

        }catch(SQLException ex){
            ex.printStackTrace();
        }
        // 15: setup the other dbs with relevant test data, look in their independent tests
        // 17: create 
        // 20: construct the CargoContainer
        ByteArrayInputStream data = new ByteArrayInputStream(new byte[500]);
        String mime = "testtype";
        String lang = "test";
        String submitter = "testSubmitter";

        System.out.print("Creating the CargoContainer object \n");

        try{
            cc = new CargoContainer(data, mime, lang, submitter);
        }catch(IOException ioe){
            ioe.printStackTrace();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.print("CargoContainer created \n Creating the DataDock object \n");
        // 30: construct the DataDock
        
        dd = new DataDock(cc);
        System.out.print("DataDock created \n");
        System.out.print("DataDockTest object created \n");
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
        System.out.print("Printing the values of the config file \n");
        System.out.print(config.getString("database.driver") + "\n");
        System.out.print(config.getString("database.url") + "\n");
        System.out.print(config.getString("database.userID") + "\n");
        System.out.print(config.getString("database.passwd") + "\n");

        try{
            long testEstimate =  dd.estimate( "testtype", 2l );
            if( testEstimate != 2l){
                System.out.print("DataDock.Estimate didnt estimate correctly \n");
                System.out.print("Printing the estimate: "+ testEstimate +" \n");
            }
            else{
                System.out.print("DataDock.estimate estimated correctly \n");
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
            System.out.print("Something returned, checking what it is \n");
            if (testFH.indexOf("testSubmitter") != 0){
                System.out.print("The fedoraHandle received is misshaped.");
                System.out.print(" Should be testSubmitter, but is: " + testFH + "\n" );
            }else{
                System.out.print("The returned fedoraHandle is wellshaped \n");
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
            System.out.print("creating statement on the connection \n");
            stmt = con.createStatement(); 
            
            System.out.print("creating table processqueue \n");
            String tableStmt1 = "CREATE TABLE processqueue(\n "+
                "queueid INTEGER,\n "+
                " fedoraHandle VARCHAR(100),\n"+
                " processing CHAR(1) CHECK (processing IN ( 'Y','N')))";
            String tableStmt2 = "CREATE SEQUENCE processqueue_seq\n"+
                "MAXVALUE 1000000000\n"+
                "NOCYCLE";
                System.out.print(tableStmt1 + " \n");
                stmt.executeUpdate(tableStmt1);
                System.out.print("Table processqueue created \n");

                System.out.print(tableStmt2 + " \n");
                stmt.executeUpdate(tableStmt2);
                System.out.print("Sequence processqueue_seq created \n");
        }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        // 20: queue the fedora handle
        System.out.print("Trying to queue a fedorahandle  \n");
        try{
            dd.queueFedoraHandle(testFH);
            System.out.print("DataDock.queueFedoraHandle(testFH) called without exceptions thrown\n");
        }catch(Exception e){
            e.printStackTrace();
        }
        
        // 30: Select the content of the processqueue table to check 
        // the validity of the Enqueue operation
        System.out.print("Selecting in the processqueue table to see if it is queued correctly \n");        
        try{
            rs = stmt.executeQuery("SELECT * FROM processqueue WHERE fedorahandle = 'test'");
            
            
            if( rs != null ){
                System.out.print("Something selected from the processqueue\n");
                while(rs.next()){
                    System.out.print("Printing content of resultset\n");
                    System.out.print("fedorahandle = "+ rs.getString("fedorahandle") +", should be test \n");
                    System.out.print("queueid = "+ rs.getInt("queueid") +", should be 1 \n");
                    System.out.print("processing = "+ rs.getString("processing") +", should be N \n");
                    
                } 
            } }catch(SQLException sqle){
            sqle.printStackTrace();
        }
        // 40: drop the processqueue table
        try{
            stmt.executeUpdate("DROP TABLE processqueue");
            System.out.print("Table processqueue dropped \n");
            stmt.executeUpdate("DROP SEQUENCE processqueue_seq");
            System.out.print("sequence processqueue_seq dropped \n");
        }catch(SQLException sqle){
            sqle.printStackTrace();
            }
        System.out.print("testDataDockQueueFedoraHandle ended \n");
    }
    
}   
