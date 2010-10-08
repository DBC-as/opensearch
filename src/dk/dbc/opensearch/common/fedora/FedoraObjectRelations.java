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


import dk.dbc.opensearch.common.types.Pair;

import fedora.common.Constants;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RelationshipTuple;

import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
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


    private static String p = "p"; // used to extract value from Pair. Depends on itql statement!!!
    private final IObjectRepository objectRepository;
    private final FedoraHandle fedoraHandle;


    public FedoraObjectRelations( IObjectRepository objectRepository ) throws ObjectRepositoryException
    {
        this.objectRepository = objectRepository;
        this.fedoraHandle = new FedoraHandle();
    }
    

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
     * @return Pair of
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    private String getRelationship( String predicate_1, String object_1, String predicate_2, String object_2, String relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        String query;
        String select = String.format( "select $s $%s from <#ri> ", p );
        String where = "where ";
        String relsNS = String.format( "and $s <fedora-rels-ext:%s> $%s ", relation, p );

        if( predicate_1 != null && object_1 != null )
        {
            where += String.format( "$s <dc:%s> '%s' ", predicate_1, object_1 );
        }
        else
        {
            throw new MalformedURLException( "parameters 'predicate_1' and object_1' must be set!" );
        }

        if( predicate_2 != null && object_2 != null )
        {
            where += "and " + String.format( "$s <dc:%s> '%s' ", predicate_2, object_2 );
        }

        String limit = "limit 1";

        query = select + where + relsNS + limit;
        List<Pair<String, String>> tuples = executeGetTuples( query );

        if( !tuples.isEmpty() )
        {
            return tuples.get( 0 ).getSecond();
        }
        else
        {
            return null;
        }
    }


    /**
     * returns an {@link List< Pair< String,String > >} where
     * {@link Pair.getFirst()} represents the {@code subject} tuple
     * variable and {@link Pair.getSecond()} represents the {@code object}
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
    private List<Pair<String, String>> executeGetTuples( String query ) throws ConfigurationException, ServiceException, IOException
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
        Map<String, String> qparams = new HashMap<String, String>( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        TupleIterator tuples = this.fedoraHandle.getTuples( qparams );
        ArrayList<Pair<String, String>> tupleList = new ArrayList<Pair<String, String>>();
        if( tuples != null )
        {
            try
            {
                while( tuples.hasNext() )
                {
                    Map<String, Node> row = tuples.next();
                    for( String key : row.keySet() )
                    {
                        if( key.equals( p ) )
                        {
                            String workRelation = row.get( key ).toString();
                            log.debug( "returning tupleList" );
                            tupleList.add( new Pair<String, String>( key, workRelation ) );
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
     */
    public boolean addPidToCollection( String pid, String collectionpid ) throws IOException, ServiceException, ConfigurationException
    {
        // \todo: Relationship.IS_MEMBER_OF_COLLECTION.toString() does not work, fix it.
        String predicate = Constants.RELS_EXT.IS_MEMBER_OF.toString();
        log.debug( String.format( "addPidToCollection for pid: '%s'; predicate: '%s'; collectionpid: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, collectionpid, false, null ) );

        boolean ret = this.fedoraHandle.addRelationship( pid, predicate, collectionpid, false, null );

        return ret;
    }


    /**
     * method for adding a relation to an object
     * @param pid the identifier of the digital object to add the relation to
     * @param predicate the predicate of the relation to add
     * @param targetDCIdentifier the object to relate the object to, can be a literal
     * @param literal true if the targetDCIdentifier is a literal
     * @param datatype the datatype of the literal, optional
     * @return true if the relation was added
     * @throws IOException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws ConfigurationException
     * @throws RemoteException
     * @throws IOException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws ConfigurationException
     */
    public boolean addRelation( String pid, String predicate, String targetDCIdentifier, boolean literal, String datatype ) throws ObjectRepositoryException
    {
        log.debug( String.format( "addRelation for pid: '%s'; predicate: '%s'; targetDCIdentifier: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, targetDCIdentifier, literal, datatype ) );

        /**
         * \todo: this string should be a type or the prdicate should be a type
         * and the actual predicate should be chosen through the parameter
         */
        predicate = "info:fedora/fedora-system:def/relations-external#isMemberOfCollection";//FedoraObjectRelations.Relationship.IS_MEMBER_OF_COLLECTION.toString();
        literal = false;
        log.debug( String.format( "modified addRelation for pid: '%s'; predicate: '%s'; targetDCIdentifier: '%s'; literal: '%s'; datatype: '%s'", pid, predicate, targetDCIdentifier, literal, datatype ) );

        boolean couldAddRelation = false;
        try
        {
            couldAddRelation = this.fedoraHandle.addRelationship( pid, predicate, targetDCIdentifier, literal, datatype );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Could not add relation '%s''%s' on '%s'", predicate, targetDCIdentifier, pid );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Could not add relation '%s''%s' on '%s'", predicate, targetDCIdentifier, pid );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Could not add relation '%s''%s' on '%s'", predicate, targetDCIdentifier, pid );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not add relation '%s''%s' on '%s'", predicate, targetDCIdentifier, pid );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }

        return couldAddRelation;
    }


    public boolean addIsOwnedByRelationship( String pid, String namespace ) throws ObjectRepositoryException
    {
        // \todo: namespace is merely a hard coded String and not a namespace+pid obtained from fedora by getnextpid()
        log.debug( String.format( "adding relationship for pid '%s' with namespace '%s'", pid, namespace ) );
        String predicate = "fedora:isOwnedBy";
        return addRelation( pid, predicate, namespace, true, null );
    }


    /**
     *
     */
    private String getNextRelationshipObject( String namespace ) throws ObjectRepositoryException
    {
        String[] relationshipObject;
        try
        {
            relationshipObject = fedoraHandle.getNextPID( 1,  namespace );
        }
        catch( ServiceException ex )
        {
            String error = String.format( "Could not get pid for prefix '%s'", namespace );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( ConfigurationException ex )
        {
            String error = String.format( "Could not get pid for prefix '%s'", namespace );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( MalformedURLException ex )
        {
            String error = String.format( "Could not get pid for prefix '%s'", namespace );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IOException ex )
        {
            String error = String.format( "Could not get pid for prefix '%s'", namespace );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        catch( IllegalStateException ex )
        {
            String error = String.format( "Could not get pid for prefix '%s'", namespace );
            log.error( error );
            throw new ObjectRepositoryException( error, ex );
        }
        if( null == relationshipObject && 1 != relationshipObject.length )
        {
            log.warn( String.format( "pid is empty for namespace '%s', but no exception was caught.", namespace ) );
            return null;
        }
        relationshipObject[0] = String.format( "info:fedora/%s", relationshipObject[0] );

        return relationshipObject[0];
    }


    /**
     * method for getting the relationships an object has
     * @param pid the object to get relations for
     * @param predicate the predicate to search for, null means all
     * @return RelationshipTuple[] containing the following for each relationship found:
     * String subject, the object this method was called on
     * String predicate,
     * String object, the target of the predicate
     * boolean isLiteral, tells if the object is a literal and not a pid
     * String datatype, tells what datatype to pass the object as if it is a literal
     * @throws IOException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws ConfigurationException
     */
    public RelationshipTuple[] getRelationships( String pid, String predicate ) throws ObjectRepositoryException
    {
        log.trace( String.format( "getting relationships with pid '%s' and predicate '%s'", pid, predicate ) );
        try
        {
            return this.fedoraHandle.getRelationships( pid, predicate );
        }
        catch( ConfigurationException ce )
        {
            String error = String.format( "Could not retrieve relationships on pid '%s' for predicate '%s'", pid, predicate );
            log.error( error );
            throw new ObjectRepositoryException( error, ce );
        }
        catch( MalformedURLException mue )
        {
            String error = String.format( "Could not retrieve relationships on pid '%s' for predicate '%s'", pid, predicate );
            log.error( error );
            throw new ObjectRepositoryException( error, mue );
        }
        catch( ServiceException se )
        {
            String error = String.format( "Could not retrieve relationships on pid '%s' for predicate '%s'", pid, predicate );
            log.error( error );
            throw new ObjectRepositoryException( error, se );
        }
        catch( IOException ioe )
        {
            String error = String.format( "Could not retrieve relationships on pid '%s' for predicate '%s'", pid, predicate );
            log.error( error );
            throw new ObjectRepositoryException( error, ioe );
        }
    }


    /**
     * method to see if an object has a certain relationship to another object
     * Its a filtered version of the method getRelationships
     * @param subject the pid of the object in question
     * @param predicate the relationship in queation
     * @param target the target of the predicate
     * @param isLiteral true if the target is not an object in the base
     * @return true if the relationship exists
     * @throws IOException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws ConfigurationException
     */
    public boolean hasRelationship( String subject, String predicate, String target, boolean isLiteral ) throws ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        RelationshipTuple[] rt = this.fedoraHandle.getRelationships( subject, predicate );
        int rtLength = rt.length;
        for( int i = 0; i < rtLength; i++ )
        {
            if( rt[i].getObject().equals( target ) && rt[i].isIsLiteral() == isLiteral )
            {
                return true;
            }
        }

        return false;
    }


    /**
     * Deletes the specified relationship. This method will remove the
     * specified relationship(s) from the RELS-EXT datastream. If the
     * Resource Index is enabled, this will also delete the
     * corresponding triples from the Resource Index.
     *
     * If the object has state "Active" this method will fail. It is
     * only allowed on Inactive and Deleted objects.
     *
     * @param pid fedora pid
     * @param predicate relation on the object to remove
     * @param targetDCIdentifier referenced fedora pid
     * @param isLiteral set if the referenced rdf node is a Literal ( see http://www.w3.org/TR/rdf-concepts/#section-Literals)
     * @param datatype datatype of the Literal, if any. If none should be specified, submit an empty String
     *
     * @return true iff the relationship could be removed from the object, false otherwise
     */
    private boolean removeRelation( String pid, String predicate, String targetDCIdentifier, boolean isLiteral, String datatype ) throws RemoteException, ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        return this.fedoraHandle.purgeRelationship( pid, predicate, targetDCIdentifier, isLiteral, datatype );
    }


}
