/**

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



/*

  Please Notice:
  Currently the tests are commented out, because the whole test strategy 
  for the ESHarvester must be rethought.

 */


package dk.dbc.opensearch.components.harvest;

import dk.dbc.opensearch.components.harvest.ESHarvest;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

import java.sql.SQLException;
import java.sql.Connection;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;


public final class ESHarvestTest extends BasicJDBCTestCaseAdapter
{

    MockConnection mockConn;
    ESHarvest harvester;

    protected void setUp() throws Exception
    {
	super.setUp();
	mockConn = getJDBCMockObjectFactory().getMockConnection();
    }

    private void prepareUpdateCount()
    {
	StatementResultSetHandler statementHandler = getJDBCMockObjectFactory().getMockConnection().getStatementResultSetHandler();

	statementHandler.prepareUpdateCount("SELECT recordstatus", 1);


    }



   
    /**
     *  Test of constructor when all is allright.
     *  Notice that we do not call the method cleanupESBase 
     *  in the class, but in a mock.
     */ 
    @Test
    @Ignore
    public void testConstructor() throws HarvesterIOException
    {
	/*
	harvester = new ESHarvest(mockConn, "test");

	verifyAllResultSetsClosed();
	verifyAllStatementsClosed();
	*/
    }


    /*
    @Test( expected = HarvesterIOException.class )
    @Ignore
    public void testConstructorClosedConnection() throws SQLException
    {

	// testStr is taken from ESHarvester constructor:
	String testStr = new String("Database connection is closed at startup");
	String resStr = "";

	// close the connection:
	mockConn.close();
	try
	{
	    harvester = new ESHarvest(mockConn, "test");
	}
	catch( HarvesterIOException hioe )
	{
	    // Expected!
	    resStr = hioe.getMessage();
	}
	assertEquals( testStr, resStr );

    }

    
    @Test
    @Ignore
    public void testCleanupESBaseNoInProgress() throws IllegalAccessException, InvocationTargetException, HarvesterIOException, NoSuchMethodException, SQLException
    {
	harvester = new ESHarvest(mockConn, "test"); // previously tested.
	harvester.changeRecordstatusFromInProgressToQueued();

	//	Method method = harvester.getClass().getDeclaredMethod( "cleanupESBase" );
	//	method.setAccessible( true );
	//	method.invoke( harvester );

	verifySQLStatementExecuted("select recordstatus");
	verifySQLStatementNotExecuted("update taskpackagerecordstructure");
	verifyNotCommitted();
        verifyAllStatementsClosed();

    }

    @Test
    @Ignore
    public void testCleanupESBaseWithInProgress() throws IllegalAccessException, InvocationTargetException, HarvesterIOException, NoSuchMethodException, SQLException
    {
	prepareUpdateCount();

	harvester = new ESHarvest(mockConn, "test"); // previously tested
	harvester.changeRecordstatusFromInProgressToQueued();

	//	Method method = harvester.getClass().getDeclaredMethod( "cleanupESBase" );
	//	method.setAccessible( true );
	//	method.invoke( harvester );

	verifySQLStatementExecuted("select recordstatus");
	verifySQLStatementExecuted("update taskpackagerecordstructure");
	verifyCommitted();
        verifyAllStatementsClosed();

    }
    

    @Test
    @Ignore
    public void testCleanupESBaseWithException() throws IllegalAccessException, InvocationTargetException, HarvesterIOException, NoSuchMethodException, SQLException
    {
	prepareUpdateCount();

       	harvester = new ESHarvest(mockConn, "test"); // previously tested.
	harvester.changeRecordstatusFromInProgressToQueued();

	//	Method method = harvester.getClass().getDeclaredMethod( "cleanupESBase" );
	//	method.setAccessible( true );
	//	method.invoke( harvester );

	verifySQLStatementExecuted("select recordstatus");
	verifySQLStatementExecuted("update taskpackagerecordstructure");
	verifyCommitted();
        verifyAllStatementsClosed();

    }
    */
}
