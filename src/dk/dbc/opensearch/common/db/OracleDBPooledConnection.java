
package dk.dbc.opensearch.common.db;

import java.sql.*;
import java.util.Properties;
import oracle.jdbc.pool.OracleDataSource;
import org.apache.log4j.Logger;

public class OracleDBPooledConnection
{
    private static Logger log = Logger.getLogger( OracleDBPooledConnection.class );
    private String CACHE_NAME;
    private OracleDataSource ods = null;


    public OracleDBPooledConnection( String cache_name ) throws SQLException
    {
	CACHE_NAME = cache_name;

	try
	{
	
	    ods = new OracleDataSource();

	    // set db-params:
	    ods.setURL( "jdbc:oracle:thin:@tora1.dbc.dk:1521" );
	    ods.setUser( "damkjaer" );
	    ods.setPassword( "damkjaer" );
	
	    // set db-cache-params:
	    ods.setConnectionCachingEnabled( true ); // connection pool
	    ods.setConnectionCacheName( CACHE_NAME );

	    // set cache properties:
	    Properties cacheProperties = new Properties();
	    cacheProperties.setProperty( "MinLimit", "1" );
	    cacheProperties.setProperty( "MaxLimit", "3" );
	    cacheProperties.setProperty( "InitialLimit", "1" );
	    cacheProperties.setProperty( "ConnectionWaitTimeout", "5" );
	    cacheProperties.setProperty( "ValidateConnection", "true" );
	
	    ods.setConnectionCacheProperties( cacheProperties );

	}
	catch( SQLException sqle )
	{
	    String errorMsg = new String( "An SQL error occured during the setup of the OracleDataSource" );
	    log.fatal( errorMsg, sqle );
	    throw sqle;
	}

    }
    

    public synchronized Connection getConnection() throws SQLException
    {
	if ( ods == null )
	{
	    throw new SQLException("Could not get connection. The OracleDataSource is null (unintialized?)");
	}
	return ods.getConnection();
    } 
    
    public synchronized void releaseConnection() throws SQLException
    {
	if ( ods == null )
	{
	    throw new SQLException("Could not release connection. The OracleDataSource is null (unintialized?)");
	}

    }


    public synchronized void shutdown() throws SQLException
    {
	if (ods != null)
	{ 
	    ods.close();
	}
    }


}