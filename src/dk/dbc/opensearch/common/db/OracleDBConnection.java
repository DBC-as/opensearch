/*
  This file is part of opensearch.
  Copyright © 2009, Dansk Bibliotekscenter a/s,
  Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043

  opensearch is free software: you can redistribute it and/or modify
  it under the terms of the GNU General Public License as published by
  the Free Software Foundation, either version 3 of the License, or
  (at your option) any later version.

  opensearch is distributed in the hope that it will be useful,
  but WITHOUT ANY WARRANTY; without even the implied warranty of
  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
  GNU General Public License for more details.

  You should have received a copy of the GNU General Public License
  along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
*/


package dk.dbc.opensearch.common.db;


import dk.dbc.opensearch.config.DataBaseConfig;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 * Handles the connection to the Oracledatabase. Database parameters are
 * obtained through the xml configurator reading them from disk, and the
 * associated driver is setup in the constructor. After that a
 * connection can be estabished
 */
public class OracleDBConnection implements IDBConnection
{    
    private static Logger log = Logger.getLogger( OracleDBConnection.class );
	
	
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
    public OracleDBConnection() throws ConfigurationException, ClassNotFoundException 
    {
        log.debug( "OracleDBConnection constructor");   
        log.debug( "Obtain config paramaters");

        driver = DataBaseConfig.getOracleDriver();
        url    = DataBaseConfig.getOracleUrl();
        userID = DataBaseConfig.getOracleUserID();
        passwd = DataBaseConfig.getOraclePassWd();

        Class.forName( driver );        
        log.debug( String.format( "driver: %s, url: %s, userID: %s", driver, url, userID ) );
}

    
    /**
     * Establishes a connection to the database
     *
     * @throws SQLException if a database error occurs when obtaining the connection
     * @throws IllegalStateException if a connection cannot be obtained from the {@link java.sql.DriverManager}
     */
    @Override
    public Connection getConnection() throws SQLException, IllegalStateException
    {
        log.debug( "Establishing connection." );

        Connection con = null;
        con = DriverManager.getConnection( url, userID, passwd );        
        if( con == null )
        {
            throw new IllegalStateException( String.format( "Could not obtain enough configuration information to connect to database using url=%s, userID=%s, passwd=%s", url, userID, passwd ) );
        }
        return con;
    }
}
