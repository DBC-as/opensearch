/*
 *
 *This file is part of opensearch.
 *Copyright © 2009, Dansk Bibliotekscenter a/s,
 *Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 *opensearch is free software: you can redistribute it and/or modify
 *it under the terms of the GNU General Public License as published by
 *the Free Software Foundation, either version 3 of the License, or
 *(at your option) any later version.
 *
 *opensearch is distributed in the hope that it will be useful,
 *but WITHOUT ANY WARRANTY; without even the implied warranty of
 *MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *GNU General Public License for more details.
 *
 *You should have received a copy of the GNU General Public License
 *along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */

/**
 * \file OracleDBPooledConnection.java
 * \brief
 */

package dk.dbc.opensearch.common.db;

import java.sql.*;
import java.util.Properties;

import oracle.jdbc.pool.OracleDataSource;

import org.apache.log4j.Logger;


/**
 * Handles an Oracle Connection Pool. 
 * The connection pool maintains the Oracle Connections in a safe manner.
 */
public class OracleDBPooledConnection
{
    private static Logger log = Logger.getLogger( OracleDBPooledConnection.class );
    private String CACHE_NAME; // intended use: if a function for watching the pool is implemented
    private OracleDataSource ods = null;

    /**
     * Initializes the OracleDBPooledConnection.
     * 
     * @param cache_name the name of the cache used in the OracleDataSource.
     * @param ods an initialized OracleDataSource.
     */
    public OracleDBPooledConnection( String cache_name, OracleDataSource ods ) 
    {
	CACHE_NAME = cache_name;
	this.ods = ods;
    }

    /**
     * Retrieves a database connection from the connection pool
     *
     * @return a valid database connection.
     *
     * @throws SQLException if {@link ods} is uninitialized (i.e. is null).
     */
    public synchronized Connection getConnection() throws SQLException
    {
	log.info( "Requesting a connection" );
	if ( ods == null )
	{
	    throw new SQLException("Could not get connection. The OracleDataSource is null (unintialized?)");
	}
	Connection tmp = ods.getConnection();
	if ( tmp == null ) {
	    log.info( "THIS IS NOT RIGHT!" ); 
	}
	log.info( "Returning connection" );
	return tmp;
	//	return ods.getConnection();
    } 
    

    /**
     * Closes the connection pool
     *
     * @throws SQLException if something goes wrong in closing down {@link ods}.
     */
    public synchronized void shutdown() throws SQLException
    {
	if (ods != null)
	{ 
	    ods.close();
	}
    }


}