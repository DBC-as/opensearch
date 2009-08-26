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
 * \file FedoraObjectRelations.java
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

    /**
     * Helper class providing a type-safe way of referring relations in the
     * fedora repository
     */
//    public enum Relationship
//    {
//
//        /**
//         * expresses the relation isMemberOfCollection
//         */
//        IS_MEMBER_OF_COLLECTION( "isMemberOfCollection" );
//
//        RDFName predicate;
//        private RDFNamespace relNS;
//
//        Relationship( String predicate )
//        {
//            relNS = new FedoraRelsExtNamespace();
//            this.predicate = new RDFName( relNS, predicate );
//        }
//
//
//    }
//
//    public FedoraObjectRelations()
//    {
//    }


    /**
     * Returns the matching objects from the simple rdf query of selecting all
     * objects that has {@code relation} from {@code subject}
     *
     * @param subject
     * @param relation
     * @return
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     * @throws FedoraCommunicationException
     */
    public List<String> getSubjectRelationships( String subject, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        List<String> foundObjects = new ArrayList<String>();
        for( InputPair<String, String> objects : getRelationships( subject, relation, null ) )
        {
            foundObjects.add( objects.getSecond() );
        }
        return foundObjects;
    }


    public List< String > getSubjectRelations( String predicate, String object, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        object = object.replace( "'", "" );
        
        List< String > foundRelations = new ArrayList<String>();
        for( InputPair<String, String> relations : getRelationships2( predicate, object, relation ) )
        {
            foundRelations.add( relations.getSecond() );
        }
        
        return foundRelations;
    }


    public List< String > getSubjectRelations( String predicate_1, String object_1, String predicate_2, String object_2, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        object_1 = object_1.replace( "'", "" );
        object_2 = object_2.replace( "'", "" );
        
        List< String > foundRelations = new ArrayList<String>();
        for( InputPair<String, String> relations : getRelationships2( predicate_1, object_1, predicate_2, object_2, relation ) )
        {
            foundRelations.add( relations.getSecond() );
        }

        return foundRelations;
    }


    /**
     * Returns the matching objects from the simple rdf query of selecting all
     * subjects that has {@code relation} to {@code object}
     *
     * @param relation
     * @param object
     * @return
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     * @throws FedoraCommunicationException
     */
    public List<String> getObjectRelationships( String relation, String object ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        List<String> foundObjects = new ArrayList<String>();
        for( InputPair<String, String> objects : getRelationships( null, relation, object ) )
        {
            foundObjects.add( objects.getFirst() );
        }
        return foundObjects;
    }


    private List< InputPair< String, String > > getRelationships2( String predicate, String object, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String query;
        String p = "p";

        String relsNS = "fedora-rels-ext";

        String select = "select $s $p from <#ri> where $s";

        //rewrite rules, specific for itql:
        query = String.format( "%s <dc:%s> '%s' and $s <%s> $%s limit 1", select, predicate, object, relsNS + ":" + relation, p );

        return executeGetTuples( query, p );
    }


    private List< InputPair< String, String > > getRelationships2( String predicate_1, String object_1, String predicate_2, String object_2, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String query;
        String p = "p";

        String relsNS = "fedora-rels-ext";

        String select = "select $s $p from <#ri> where";
        String where_1 = String.format( "$s <dc:%s> '%s'", predicate_1, object_1 );
        String where_2 = String.format( "$s <dc:%s> '%s'", predicate_2, object_2 );

        //rewrite rules, specific for itql:
        query = String.format( " %s %s and %s and $s <%s> $%s limit 1", select, where_1, where_2, relsNS + ":" + relation, p );

        return executeGetTuples( query, p );
    }


    private List< InputPair< String, String > > executeGetTuples( String query, String p ) throws ConfigurationException, ServiceException, IOException
    {
        log.debug( String.format( "using query %s", query ) );
        Map<String, String> qparams = new HashMap< String, String >( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        /** \todo: getTuples throws IOException "'class java.io.IOException'
         * Message: 'Request failed [500 Internal Server Error] :
         * http://localhost:8080/fedora/risearch?format=Sparql&flush=true&type=tuples&lang=itql&query=..."
         *
         * Question: Are we to catch this error, and try again? What are we to do?
         */
        TupleIterator tuples = FedoraHandle.getInstance().getFC().getTuples( qparams );
        ArrayList< InputPair< String, String > > tupleList = new ArrayList< InputPair< String, String > >();
        try
        {
            System.out.println( "before while( tuples.hasNext()" );
            while( tuples.hasNext() )
            {
                Map<String, Node> row = tuples.next();
                for( String key : row.keySet() )
                {
                    if ( key.equals( p ) )
                    {
                        System.out.println( "key: " + key );
                        String workRelation = row.get( key ).toString();
                        System.out.println( "workRelation: " + workRelation );
                        //tupleList.add( new InputPair< String, String >( key, row.get( key ).toString() ) );
                        tupleList.add( new InputPair< String, String >( key, workRelation ) );
                    }
                }
            }
        }
        catch( TrippiException ex )//ok, nothing we can do but return an empty list
        {
            log.error( String.format( "Could not retrieve tuples from TupleIterator from Fedora: %s", ex.getMessage() ) );
        }

        log.debug( "returning tupleList" );
        return tupleList;
    }


    /**
     * returns an {@link List<InputPair<String,String>>} where
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
    private List< InputPair< String, String > > getRelationships( String subject, String relation, String object ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String query;

        String fedoraNS = "fedora";
        String relsNS = "fedora-rels-ext";

        //rewrite rules, specific for itql:
        if( subject == null )
        {
            query = String.format( "select $s from <#ri> where $s <%s> <%s>", relsNS + ":" + relation, fedoraNS + ":" + object );

        }
        else if( object == null )
        {
            query = String.format( "select $o from <#ri> where <%s> <%s> $o", fedoraNS + ":" + subject, relsNS + ":" + relation );
        }
        else
        {
            query = String.format( "select <%s> <%s> from <#ri> where <%s> <%s> <%s>", fedoraNS + ":" + subject, fedoraNS + ":" + object, fedoraNS + ":" + subject, relsNS + ":" + relation, fedoraNS + ":" + object );
        }
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
        log.debug( String.format( "using query %s", query ) );
        Map<String, String> qparams = new HashMap< String, String >( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        log.warn( "before calling getTuples" );
        TupleIterator tuples = FedoraHandle.getInstance().getFC().getTuples( qparams );
        log.warn( "after  calling getTuples" );
        ArrayList< InputPair< String, String > > tupleList = new ArrayList< InputPair< String, String > >();
        try
        {
            while( tuples.hasNext() )
            {
                Map<String, Node> row = tuples.next();
                for( String key : row.keySet() )
                {
                    tupleList.add( new InputPair< String, String >( key, row.get( key ).toString() ) );
                }
            }
        }
        catch( TrippiException ex )//ok, nothing we can do but return an empty list
        {
            log.error( String.format( "Could not retrieve tuples from TupleIterator from Fedora: %s", ex.getMessage() ) );
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


    public boolean addOwnerToPid( String pid, String owner ) throws IOException, ServiceException, ConfigurationException
    {
        // \todo: Relationship.IS_MEMBER_OF_COLLECTION.toString() does not work, fix it.
        String predicate = "info:fedora/fedora-system:def/relations-external#isOwnedBy";
        log.debug( String.format( "addPidToCollection for pid: '%s'; predicate: '%s'; collectionpid: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, owner, false, null ) );

        return FedoraHandle.getInstance().getAPIM().addRelationship( pid, predicate, owner, false, null );
    }

}
