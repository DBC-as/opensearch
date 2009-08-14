/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.dbc.opensearch.common.fedora;

import dk.dbc.opensearch.common.types.ComparablePair;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;
import fedora.common.Constants;
import fedora.common.rdf.FedoraRelsExtNamespace;
import fedora.common.rdf.RDFName;
import fedora.common.rdf.RDFNamespace;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
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
    private List<InputPair<String, String>> getRelationships( String subject, String relation, String object ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
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
         *Caused by: org.xmlpull.v1.XmlPullParserException: caused by: org.xmlpull.v1.XmlPullParserException: resource not found: /META-INF/services/org.xmlpull.v1.XmlPullParserFactory make sure that parser implementing XmlPull API is available
         *
         * Which the scarred hacker will recognize as a form of type 'jar hell';
         * fedora uses one version of the xmlpull library and we use another. So,
         * if we update the fedora-client.jar, we'll most probably need to update
         * the xmlpull jar and the  xpp3 jar
         */
        log.debug( String.format( "using query %s", query ) );
        Map<String, String> qparams = new HashMap<String, String>( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        TupleIterator tuples = FedoraHandle.getInstance().getFC().getTuples( qparams );
        ArrayList<InputPair<String, String>> tupleList = new ArrayList<InputPair<String, String>>();
        try
        {

            while( tuples.hasNext() )
            {
                Map<String, Node> row = tuples.next();
                for( String key : row.keySet() )
                {
                    tupleList.add( new InputPair<String, String>( key, row.get( key ).toString() ) );
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
