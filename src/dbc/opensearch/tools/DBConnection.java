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
 * \ingroup tools
 * \brief Handles the connection to the database. Database parameters is
 * obtained by the xml configurator reading them from disk, and a the
 * associated driver is setup in the constructor, and after that a
 * connection can be estabished
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
    static Logger log = Logger.getLogger("DBConnection");

    
    /**
     * /brief Gets configuration and driver information 
     * @throws ConfigurationException
     * @throws ClassNotFoundException
     */
    public DBConnection() throws ConfigurationException, ClassNotFoundException {
        log.debug( "DBConnection constructor");
   
        log.debug( "Obtain config paramaters");
        
        URL cfgURL = getClass().getResource("/config.xml");
        XMLConfiguration config = null;
        
        config = new XMLConfiguration( cfgURL );
        
        driver = config.getString( "database.driver" );
        url    = config.getString( "database.url" );
        userID = config.getString( "database.userID" );
        passwd = config.getString( "database.passwd" );

        Class.forName(driver);
        
        log.debug( String.format( "driver: %s, url: %s, userID: %s",driver, url, userID) );
    }

    /**
     * Establishes the connction to the database
     * @throws SQLException
     */
    protected static Connection establishConnection() throws SQLException {
        log.debug( "Establishing connection." );

        Connection con = null;
        con = DriverManager.getConnection(url, userID, passwd);        
        
        log.debug( "Got connection." );

        return con;
    }

}
