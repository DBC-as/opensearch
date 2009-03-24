/**
 * \file DBConnection.java
 * \brief The DBConnection class
 * \package tools
 */
package dk.dbc.opensearch.common.db;


import dk.dbc.opensearch.common.config.DataBaseConfig;

import java.lang.ClassNotFoundException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * \ingroup tools
 * \brief Handles the connection to the database. Database parameters is
 * obtained by the xml configurator reading them from disk, and a the
 * associated driver is setup in the constructor, and after that a
 * connection can be estabished
 */
public class DBConnection
{    
	static Logger log = Logger.getLogger( DBConnection.class );
	
	
    /**
     * Variables to hold configuration parameters
     */
    private static String driver = "";
    private static String url = "";
    private static String userID = "";
    private static String passwd = "";
    
    
    /**
     * /brief Gets configuration and driver information 
     *
     * @throws ConfigurationException
     * @throws ClassNotFoundException
     */
    public DBConnection() throws ConfigurationException, ClassNotFoundException 
    {
        log.debug( "DBConnection constructor");   
        log.debug( "Obtain config paramaters");

        driver = DataBaseConfig.getDriver();
        url    = DataBaseConfig.getUrl();
        userID = DataBaseConfig.getUserID();
        passwd = DataBaseConfig.getPassWd();

        Class.forName( driver );        
        log.debug( String.format( "driver: %s, url: %s, userID: %s", driver, url, userID ) );
    }

    
    /**
     * Establishes the connction to the database
     *
     * @throws SQLException
     */
    public static Connection getConnection() throws SQLException 
    {
        log.debug( "Establishing connection." );

        Connection con = null;
        con = DriverManager.getConnection( url, userID, passwd );        
        if( con == null )
        {
            throw new NullPointerException( String.format( "Could not get connection to database using url=%s, userID=%s, passwd=%s", url, userID, passwd ) );
        }
    
        return con;
    }
}
