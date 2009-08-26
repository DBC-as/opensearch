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

package dk.dbc.opensearch.tools.relations;

import fedora.client.FedoraClient;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;


public class ItqlTool
{

    private final String user;
    private final String pass;
    private final String url;

    ItqlTool( String fedora_url, String fedora_user, String fedora_pass )
    {
        this.user = fedora_user;
        this.pass = fedora_pass;
        this.url = fedora_url;

    }

    void testGetObjectRelationships( String itql ) throws TrippiException, MalformedURLException, IOException
    {

        boolean isLiteral = true;
        String datatype = null;
        String query = "";

        query = itql;//"select $s $o from <#ri> where $s <fedora-rels-ext:isMemberOfCollection> $o";
        TupleIterator tuples = queryRI( query );
        if( !tuples.hasNext() )
        {
            System.out.println( "No tuples found for query" + itql );
        }
        while( tuples.hasNext() )
        {
            Map<String, Node> row = tuples.next();
            for( String key : row.keySet() )
            {
                System.out.println( String.format( "'%s'", row.get( key ).toString() ) );
            }
        }
    }


    private TupleIterator queryRI( String query ) throws MalformedURLException, IOException
    {
        FedoraClient client = new FedoraClient( url, user, pass );
        Map<String, String> params = new HashMap<String, String>();
        params.put( "lang", "itql" );
        params.put( "flush", "true" );
        params.put( "query", query );
        TupleIterator ti = null;
        ti = client.getTuples( params );

        return ti;
    }
}
