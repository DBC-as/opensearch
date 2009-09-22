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

package dk.dbc.opensearch.components.harvest;

import dk.dbc.opensearch.components.harvest.ESHarvest;

import com.mockrunner.jdbc.*;
import com.mockrunner.mock.jdbc.MockConnection;
import com.mockrunner.jdbc.StatementResultSetHandler;
import com.mockrunner.mock.jdbc.MockResultSet;

import java.sql.SQLException;
import java.sql.Connection;

// import mockit.Mock;
// import mockit.Mockit;
// import mockit.MockClass;

import static org.junit.Assert.*;
import org.junit.*;
import org.junit.runner.*;

// import mockit.*;
// import mockit.integration.junit4.*;


public final class ESHarvestTest extends BasicJDBCTestCaseAdapter
{

    MockConnection mockConn;
    ESHarvest harvester;

//     @MockClass( realClass = ESHarvest.class ) 
//     public static class MockESHarvest
//     {
// 	@Mock public void cleanupESBase() 
// 	{
// 	    System.out.println( "Ignoring cleanupESBase." );
// 	}
//     }

    protected void setUp() throws Exception
    {
	super.setUp();

	mockConn = getJDBCMockObjectFactory().getMockConnection();
    }


    /**
     *  Test of constructor when all is allright.
     *  Notice that we do not call the method cleanupESBase 
     *  in the class, but in a mock.
     */ 
    @Test
    public void testConstructor() throws HarvesterIOException
    {

	//	Mockit.setUpMocks( MockESHarvest.class );

	harvester = new ESHarvest(mockConn, "test");

	verifyAllResultSetsClosed();
	verifyAllStatementsClosed();
    }

}

// @RunWith( JMockit.class )
// public class ESHarvestTest 
// {

//     @Mocked public Connection mockConn = null;

//     @Test
//     public void testConstructor() 
//     {
// 	new Expectations() 
// 	{
// 	    conn.isClosed(); returns(false);
// 	};
//     }

//     new ESHarvest(mockConn, "test");
// }



// @RunWith( JMockit.class )
// public final class ESHarvestTest extends BasicJDBCTestCaseAdapter
// {

//     MockConnection mockConn;
//     ESHarvest harvester;

//     @MockClass( realClass = ESHarvest.class ) 
//     public static class MockESHarvest
//     {
// 	@Mock public void cleanupESBase() 
// 	{
// 	    //    System.out.println( "This is the real deal!" );
// 	}
//     }


//     protected void setUp() throws Exception
//     {
// 	super.setUp();

// 	mockConn = getJDBCMockObjectFactory().getMockConnection();
//     }

//     protected void tearDown() 
//     {
// 	Mockit.tearDownMocks();

//     }


//     /**
//      *  Test of constructor when all is allright.
//      *  Notice that we do not call the method cleanupESBase 
//      *  in the class, but in a mock.
//      */ 
//     @Test
//     public void testConstructor() throws HarvesterIOException
//     {

// 	Mockit.setUpMocks( MockESHarvest.class );

// 	harvester = new ESHarvest(mockConn, "test");

// 	verifyAllStatementsClosed();
// 	verifyAllResultSetsClosed();
//     }

//     @Test( expected = HarvesterIOException.class )
//     public void testConstructorClosedConnection() throws SQLException
//     {

// 	new Expectations() 
// 	{
// 	    throwsException( new HarvesterIOException() );
// 	};

// 	Mockit.setUpMocks( MockESHarvest.class );

// 	// close the connection:
// 	mockConn.close();
// 	harvester = new ESHarvest(mockConn, "test");

// 	verifyAllStatementsClosed();
// 	verifyAllResultSetsClosed();
//     }
    

// }
