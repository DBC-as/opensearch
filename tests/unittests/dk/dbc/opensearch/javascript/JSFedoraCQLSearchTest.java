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


package dk.dbc.opensearch.javascript;

import dk.dbc.opensearch.fedora.FedoraObjectFields;
import dk.dbc.opensearch.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.fedora.OpenSearchCondition;
import dk.dbc.opensearch.types.ITargetField;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.junit.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;


import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;
import static org.junit.Assert.*;

public class JSFedoraCQLSearchTest
{
    //The parsing and transformation of the queries are done in the 
    //helper class, so here we test how the results are treated

    //@Mocked IObjectRepository repo;
    
    /*
     * Mock repository whos getIdentifiersByState method returns
     * a String array containing the values of each search condition
     */
    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockRep
    {
        @Mock 
        public void $init( String host, String port, String user, String pass )
        {}
        
        @Mock
        public static List< String > getIdentifiersByState( List< OpenSearchCondition > conditions, int maximumResults, Set< String > states )
        {
            ArrayList< String > results = new ArrayList< String >();
            for( OpenSearchCondition condition : conditions )
            {
                results.add( condition.getValue() );
            }
            
            return results;
        }
    }

    @Before
    public void setUp() throws Exception
    {
        setUpMocks( MockRep.class );
    }
    
    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
    }
    
    @Test
    public void constructorTest() throws Exception
    {
        FedoraObjectRepository repo = new FedoraObjectRepository( "test", "test", "test", "test" );
        
        JSFedoraCQLSearch cqlSearcher = new JSFedoraCQLSearch( repo );
    }

    /*
     * Testing that we get the result back in a simple query
     */
    @Test 
    public void testSearchResultReturnedAsString() throws Exception
    {
        String query = "title = test";
        FedoraObjectRepository repo = new FedoraObjectRepository( "test", "test", "test", "test" );
        
        JSFedoraCQLSearch cqlSearcher = new JSFedoraCQLSearch( repo );
        String[] result = cqlSearcher.search( query );
        assertEquals( result[ 0 ], "test" );
    }

    /*
     * Testing that we get the results in a query that gets split up 
     * into more, since there is an OR in it
     */ 
    @Test 
    public void queryWithOr() throws Exception
    {
        String query = "title = test OR title = test2";
        FedoraObjectRepository repo = new FedoraObjectRepository( "test", "test", "test", "test" );
        
        JSFedoraCQLSearch cqlSearcher = new JSFedoraCQLSearch( repo );
        String[] result = cqlSearcher.search( query );
        assertEquals( result[ 0 ], "test" );
        assertEquals( result[ 1 ], "test2" );
    }

    /*
     * Testing that the result doesnt contain copies 
     * This is done by giving the same value more than once
     * in the query. The Mockclass of the repository
     * returns the value of the search condition
     */  
    
    @Test 
    public void duplicateResultTest() throws Exception
    {
        String query = "title = test OR title = test2 AND title = test";
        FedoraObjectRepository repo = new FedoraObjectRepository( "test", "test", "test", "test" );
        
        JSFedoraCQLSearch cqlSearcher = new JSFedoraCQLSearch( repo );
        String[] result = cqlSearcher.search( query );
        assertTrue( result.length == 2 );
        assertEquals( result[ 0 ], "test" );
        assertEquals( result[ 1 ], "test2" );
    } 
}