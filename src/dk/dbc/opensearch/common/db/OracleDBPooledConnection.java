
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

    public OracleDBPooledConnection( String cache_name, OracleDataSource ods ) 
    {
	CACHE_NAME = cache_name;
	this.ods = ods;
    }


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
    

    public synchronized void shutdown() throws SQLException
    {
	if (ods != null)
	{ 
	    ods.close();
	}
    }


}