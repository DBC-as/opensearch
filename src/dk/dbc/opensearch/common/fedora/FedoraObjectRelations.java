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
 * \file 
 * \brief 
 */

package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.types.InputPair;

import fedora.common.Constants;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.jrdf.graph.Node;
import org.trippi.TrippiException;
import org.trippi.TupleIterator;


/**
 *Class that handles and manages relationships between digital objects in the
 * Fedora repository
 */
public class FedoraObjectRelations
{
    private static Logger log = Logger.getLogger( FedoraObjectRelations.class );

    
    private static String p = "p"; // used to extract value from InputPair. Depends on itql statement!!!

    
    public String getSubjectRelation( String predicate, String object, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        object = object.replace( "'", "" );        
        return getRelationship( predicate, object, null, null, relation );
    }


    public String getSubjectRelation( String predicate_1, String object_1, String predicate_2, String object_2, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        object_1 = object_1.replace( "'", "" );
        object_2 = object_2.replace( "'", "" );
        return getRelationship( predicate_1, object_1, predicate_2, object_2, relation );
    }

   
    /**
     *
     * @param predicate_1 Must be set
     * @param object_1 Must be set
     * @param predicate_2 Null value accepted
     * @param object_2 Null value accepted
     * @param relation Name of relation Fedora object must have
     * @return InputPair of
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    private String getRelationship( String predicate_1, String object_1, String predicate_2, String object_2, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String query;
        String select = String.format(  "select $s $%s from <#ri> ", p );
        String where = "where ";
        String relsNS = String.format( "and $s <fedora-rels-ext:%s> $%s ", relation, p );

        if ( predicate_1 != null && object_1 != null )
        {
            where += String.format( "$s <dc:%s> '%s' ", predicate_1, object_1 );
        }
        else
        {
            throw new MalformedURLException( "parameters 'predicate_1' and object_1' must be set!" );
        }

        if ( predicate_2 != null && object_2 != null )
        {
            where += "and " + String.format( "$s <dc:%s> '%s' ", predicate_2, object_2 );
        }

        String limit = "limit 1";

        query = select + where + relsNS + limit;        
        List< InputPair< String, String > > tuples = executeGetTuples( query );

        if ( ! tuples.isEmpty() )
        {
            return tuples.get( 0 ).getSecond();
        }
        else
        {
            return null;
        }
    }


    /**
     * returns an {@link List< InputPair< String,String > >} where
     * {@link InputPair.getFirst()} represents the {@code subject} tuple
     * variable and {@link InputPair.getSecond()} represents the {@code object}
     * tuple variable. If the variable is not named, e.g. by using null as
     * parameter, the getters will display the sparql variable substitute.
     *
     * @param subject
     * @param relation
     * @param object
     * @return
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    private List< InputPair< String, String > > executeGetTuples( String query ) throws ConfigurationException, ServiceException, IOException
    {
        log.debug( String.format( "using query %s", query ) );

        /**
         * FedoraClient.getTuples might throw:
         * java.io.IOException: Error getting tuple iterator: Error parsing
         * 	at fedora.client.FedoraClient.getTuples(FedoraClient.java:{linenumber})
         *
         * which (to anyone but the original coder) could indicate that there
         * was a problem with either;
         * a) the query
         * b) the returned HttpInputStream that Trippis TupleIterator parses, or
         * c) the connection used by the HttpInputStream
         *
         * In reality, FedoraClient swallows the original exception and rethrows
         * (part of) it as a IOException. The real cause can be guessed at from
         * TrippiExceptions stack trace:
         * org.trippi.TrippiException: Error parsing
         *	at org.trippi.io.SparqlTupleIterator.<init>(SparqlTupleIterator.java:47)
         *	at org.trippi.TupleIterator.fromStream(TupleIterator.java:152)
         *	at fedora.client.FedoraClient.getTuples(FedoraClient.java:705)
         *	... local call stack ...
         * Caused by: org.xmlpull.v1.XmlPullParserException: caused by: org.xmlpull.v1.XmlPullParserException:
         * resource not found: /META-INF/services/org.xmlpull.v1.XmlPullParserFactory make sure that parser implementing XmlPull API is available
         *
         * Which the scarred hacker will recognize as a form of type 'jar hell';
         * fedora uses one version of the xmlpull library and we use another. So,
         * if we update the fedora-client.jar, we'll most probably need to update
         * the xmlpull jar and the  xpp3 jar
         */
        /** \todo: getTuples throws IOException "'class java.io.IOException'
         * Message: 'Request failed [500 Internal Server Error] :
         * http://localhost:8080/fedora/risearch?format=Sparql&flush=true&type=tuples&lang=itql&query=..."
         *
         * Question: Are we to catch this error, and try again? What are we to do?
         */
        Map<String, String> qparams = new HashMap< String, String >( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        TupleIterator tuples = FedoraHandle.getInstance().getFC().getTuples( qparams );
        ArrayList< InputPair< String, String > > tupleList = new ArrayList< InputPair< String, String > >();
        if ( tuples != null )
        {
            try
            {
                while( tuples.hasNext() )
                {
                    Map<String, Node> row = tuples.next();
                    for( String key : row.keySet() )
                    {
                        if ( key.equals( p ) )
                        {
                            String workRelation = row.get( key ).toString();
                            log.debug( "returning tupleList" );
                            tupleList.add( new InputPair< String, String >( key, workRelation ) );
                        }
                    }
                }
            }
            catch( TrippiException ex )//ok, nothing we can do but return an empty list
            {
                log.error( String.format( "Could not retrieve tuples from TupleIterator from Fedora: %s", ex.getMessage() ) );
            }
        }

        return tupleList;
    }


    /**
     * Adds the {@code pid} to a collection designated by {@code collectionpid}.
     * The return value indicated whether the relation could be constructed
     * 
     * @param pid
     * @param collectionpid
     * @return
     */
    public boolean addPidToCollection( String pid, String collectionpid ) throws IOException, ServiceException, ConfigurationException
    {
        // \todo: Relationship.IS_MEMBER_OF_COLLECTION.toString() does not work, fix it.
        String predicate = Constants.RELS_EXT.IS_MEMBER_OF.toString();
        log.debug( String.format( "addPidToCollection for pid: '%s'; predicate: '%s'; collectionpid: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, collectionpid, false, null ) );

        return FedoraHandle.getInstance().getAPIM().addRelationship( pid, predicate, collectionpid, false, null );
    }


    /*public boolean addOwnerToPid( String pid, String owner ) throws IOException, ServiceException, ConfigurationException
    {
        // \todo: Relationship.IS_MEMBER_OF_COLLECTION.toString() does not work, fix it.
        String predicate = "info:fedora/fedora-system:def/relations-external#isOwnedBy";
        log.debug( String.format( "addPidToCollection for pid: '%s'; predicate: '%s'; collectionpid: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, owner, false, null ) );

        return FedoraHandle.getInstance().getAPIM().addRelationship( pid, predicate, owner, false, null );
    }*/
}
