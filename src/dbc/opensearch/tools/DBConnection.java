package dbc.opensearch.tools;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import oracle.jdbc.driver.OracleDriver;

import org.apache.log4j.Logger;
import org.apache.log4j.xml.DOMConfigurator;

import org.apache.commons.configuration.XMLConfiguration;
import org.apache.commons.configuration.ConfigurationException;

import java.net.URL;

import java.lang.ClassNotFoundException;

/**
 * 
 */
public class DBConnection{
    
    /**
     * Variables to hold configuration parameters
     */
    private static String driver = "";
    private static String url = "";
    private static String userID = "";
    private static String passwd = "";

    /**
     *  Log
     */

    private static final Logger log = Logger.getRootLogger();

    public DBConnection() throws ConfigurationException {
        
        log.debug( "Obtain config paramaters");
        
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        try{
            config = new XMLConfiguration( cfgURL );
        }
        catch (ConfigurationException cex){
            log.fatal( "ConfigurationException: " + cex.getMessage() );
            throw new ConfigurationException( cex.getMessage() );
        }

        driver = config.getString( "database.driver" );
        url    = config.getString( "database.url" );
        userID = config.getString( "database.userID" );
        passwd = config.getString( "database.passwd" );
        
        log.debug( "driver: "+driver );
        log.debug( "url:    "+url );
        log.debug( "userID: "+userID );
    }

    /**
     * Establishes the connction to the database
     */
    protected static Connection establishConnection() throws ClassNotFoundException, SQLException {

        Connection con = null;

        try {
            Class.forName(driver);

        } 
        catch(ClassNotFoundException ce) {
            log.fatal( "ClassNotFoundException: " + ce.getMessage() );
            throw new ClassNotFoundException( ce.getMessage() );
        }

        try {
            con = DriverManager.getConnection(url, userID, passwd);
        } 
        catch(SQLException sqe) {
            log.fatal( "SQLException: " + sqe.getMessage() );
            throw new SQLException( sqe.getMessage() );
        }

        log.debug( "Got connection." );

        return con;
    }

}
