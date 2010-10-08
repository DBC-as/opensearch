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

package dk.dbc.opensearch.common.db;


import java.sql.SQLException;
import java.util.NoSuchElementException;

import org.apache.commons.configuration.ConfigurationException;
import java.util.Vector;
import dk.dbc.opensearch.common.types.Pair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import mockit.Mocked;
import mockit.Mockit;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;


/** \brief Unittest for Processqueue */
public class ProcessqueueTest// extends BasicJDBCTestCaseAdapter
{
    @Mocked PostgresqlDBConnection connection;
    List<Pair<String, Integer>> resultVector;
    Processqueue processqueue;
    IDBConnection dbConnection;

    @Before
    public void setUp() throws Exception
    {
        processqueue = new Processqueue( connection );
        resultVector = new ArrayList<Pair<String, Integer>>();
    }

    @Ignore
    @Test
    public void testPush() throws ClassNotFoundException, SQLException
    {
        String mockFedorahandle = "mockhandle";
        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','N' )", mockFedorahandle ) );
        
        processqueue.push( mockFedorahandle );
    }

    @Ignore
    @Test
    public void testPushExceptionThrown() throws ClassNotFoundException
    {
        String mockFedorahandle = "mockhandle";

        String sql_query = (  String.format( "INSERT INTO processqueue( queueid, fedorahandle, processing ) "+
                                             "VALUES( nextval( 'processqueue_sequence' ) ,'%s','N' )", mockFedorahandle ) );
    }

    @Ignore
    @Test
    public void testPopAll() throws SQLException
    {
        String sql_query = "SELECT * from get_all_posts()";
    }


    @Ignore
    @Test
    public void testPopAllExceptionThrown()
    {
        String sql_query = "SELECT * from get_all_posts()";
    }

}
