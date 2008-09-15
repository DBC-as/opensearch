package dbc.opensearch.components.datadock.tests.estimateTest;

import org.apache.commons.lang.*;
import org.apache.commons.configuration.*;
import java.sql.*;
import oracle.jdbc.driver.OracleDriver;
import java.net.URL;
/**
 * Test of the estimate method in DataDock
 */
public class EstimateTest {
    XMLConfiguration config = null;
    Connection con = null;
    private static String driver = "";
    private static String url = "";
    private static String userID = "";
    private static String passwd = "";
    
    public EstimateTest(){
   
        setup();
        try{
            if(2l == estimate("testtype", 2l )){
                System.out.print("the estimate is made correctly \n");
            }
            else{
                System.out.print("the estimate is wrong \n");
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }
    /**
     * a copy of the estimate method from the DataDock class
     * It does not call the establishConnection method, since this is done 
     * already in the main, so the call i replaced with a use of this.con
     */
    public void setup (){
        /**
         * Setting up the config file
         */
        URL cfgURL = getClass().getResource("/config.xml");
         try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            System.out.println( "Exception: " + cex.getMessage() );
             System.exit(0);
        }


        driver = "oracle.jdbc.driver.OracleDriver";
        this.config.setProperty("statisticDB.driver" , driver );
        url = "dbc:oracle:thin:lvh/lvh@tora1.dbc.dk:1521";
        config.setProperty("statisticDB.url" , url );
        userID = "lvh";
        config.setProperty("statisticDB.userID" , userID );
        passwd = "lvh";
        config.setProperty("statisticDB.passwd" , passwd );
        /**
         * establishing the DB-connection
         */
        con = establishConnection();
        System.out.print("connection established in setup \n");
        Statement stmt = null;
        try{
            stmt = con.createStatement();
        }catch(Exception e){
            e.printStackTrace();
        }
        System.out.print("Statment created in setup \n");
        try{
            stmt.executeUpdate( "INSERT INTO statisticDB (dataamount, processtime, mimetype) VALUES( 2, 2, 'testtype' )" );
        }catch(Exception e){
            e.printStackTrace(); 
        }
        System.out.print( "Testdata inserted into StatisticDB \n" );
        
    }
    /**
     * a copy of the estimate method from the DataDock class
     * It does not call the establishConnection method, since this is done 
     * already in the main, so the call i replaced with a use of this.con
     */
    public long estimate( String mimeType, long length )throws Exception{
        long average_time = 0l; 
        ResultSet rs = null;
        Statement stmt = null;
        /**
         * creates a statement for use on the connection of the class
         */
        try{
            stmt = this.con.createStatement();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        
        /**
         * Selecting the testdata from the StatisticDB
         */
        System.out.print(" Selecting from the database in estimate \n");
        try{
                rs = stmt.executeQuery ( "SELECT processtime, dataamount FROM statisticDB WHERE mimetype = '" + mimeType + "'" );
        }
        catch(Exception e){
            e.printStackTrace();
        }
        System.out.print("Select completed in estimate \n"); 
        if( rs != null ){
            System.out.print("the resultset contains something \n");
            while(rs.next()){
            average_time = ( (rs.getInt("processtime") / rs.getInt("dataamount") ) * length);
            }
        }
        else{
            //throw exception
        }
        return average_time;
    }
        
    /**
     * A copy of the establishConnection method from the DataDock class
     * the config property values are extracted into Strings here 
     * instead of in the class
     */
    private static Connection establishConnection(){
        Connection con = null;
        
        try {
            Class.forName(driver);
            
        } catch(java.lang.ClassNotFoundException e) {
            e.printStackTrace();
        }
        
        try {
                con = DriverManager.getConnection(url, userID, passwd);
        } catch(SQLException ex) {
            ex.printStackTrace();
        }
        return con;
    }
}

