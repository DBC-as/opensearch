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

/**
 * \file OracleConnectionTest.java
 * \brief Brief description
 */


package dk.dbc.opensearch.components.harvest;

/** \brief UnitTest for OracleConnection */

import dk.dbc.opensearch.common.db.IDBConnection;
import dk.dbc.opensearch.common.db.OracleDBConnection;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;

import static org.junit.Assert.*;

/**
 * 
 */
public class OracleConnectionTest {

    /**
     * 
     */
    @Test public void testConnection() {
        
        
        ResultSet rs = null;
        try{
            IDBConnection oracleInstance = new OracleDBConnection();
            Connection conn = oracleInstance.getConnection();
            Statement stmt = conn.createStatement();
            rs = stmt.executeQuery( "SELECT * FROM test");
            if(rs == null)
            {
                System.out.println( "no rows found in database");
            }
            else{
            while( rs.next() ){
                System.out.println( String.format( "value: %s", rs.getString( "test" ) ) );
            }
            }
        }
        catch(ConfigurationException ce){}
        catch(ClassNotFoundException cnfe){}
        catch(SQLException sqle){}
            
    }
}
