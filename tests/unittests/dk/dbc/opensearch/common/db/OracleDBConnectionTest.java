/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package dk.dbc.opensearch.common.db;

import java.lang.IllegalStateException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import mockit.Mock;
import mockit.MockClass;
import org.apache.commons.configuration.ConfigurationException;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author stm
 */
public class OracleDBConnectionTest {

    /**
     * 
     * @throws Exception
     */
    @MockClass( realClass=OracleDBConnection.class )
    static class MockOracleDBConnection
    {
        @Mock( minInvocations = 1 )
        void getConnection()
        {
            
        }

    }


    @BeforeClass
    public static void setUpClass() throws Exception
    {
    }


    @AfterClass
    public static void tearDownClass() throws Exception
    {
    }


    /**
     * Test of getConnection method, of class OracleDBConnection.
     */
    @Test
    public void testGetConnection() throws Exception
    {
        OracleDBConnection instance = new OracleDBConnection();
        Connection result = null;
        result = instance.getConnection();
        assertNotNull( result );
    }

    /**
     *
     * @throws ConfigurationException
     * @throws ClassNotFoundException
     * TODO before bug 9205 is solved, we cannot test missing configuration values leading to an IllegalStateException.
     * Until then, this test is ignored
     */
    @Ignore @Test( expected=IllegalStateException.class )
    public void testConnectionState() throws ConfigurationException, ClassNotFoundException, SQLException
    {
        OracleDBConnection instance = new OracleDBConnection();
        Connection result = null;
        result = instance.getConnection();



    }

}