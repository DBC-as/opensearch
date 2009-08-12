/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.dbc.opensearch.common.fedora;

import fedora.common.rdf.FedoraRelsExtNamespace;
import fedora.common.rdf.RDFName;
import fedora.common.rdf.RDFNamespace;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
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
    public enum Relationship
    {

        /**
         * expresses the relation isMemberOfCollection
         */
        IS_MEMBER_OF_COLLECTION( "isMemberOfCollection" );

        RDFName predicate;
        private RDFNamespace relNS;

        Relationship( String predicate )
        {
            relNS = new FedoraRelsExtNamespace();
            this.predicate = new RDFName( relNS, predicate );
        }


    }

    public FedoraObjectRelations()
    {
    }


    /**
     * returns an iterator over tuples of pids that has {@link relation} to {@link pid}
     * @param pid the fedora pid to query for relations to
     * @param relation the relation to query for
     * @return an iterator containing matching tuples
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     */
    public TupleIterator getRelationships( String pid, Relationship relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
        /**
         * FedoraClient.getTuples might throw:
         * java.io.IOException: Error getting tuple iterator: Error parsing
         * 	at fedora.client.FedoraClient.getTuples(FedoraClient.java:{linenumber})
         *
         * which (to the naive coder) could indicate that there was a problem with either
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
        String query = String.format( "select $s from <#ri> where $s <%s> <%s>", relation.predicate.qName, pid );
        log.debug( String.format( "using query %s", query ) );
        Map<String, String> qparams = new HashMap<String, String>( 3 );
        qparams.put( "lang", "itql" );
        qparams.put( "flush", "true" );
        qparams.put( "query", query );
        TupleIterator tuples = FedoraHandle.getInstance().getFC().getTuples( qparams );

        return tuples;
    }


    /**
     * Returns a map of {@link TupleIterator}s mapped to the {@code pids} that
     * were used in the query. The {@link TupleIterator} value for a given
     * {@code pid} will be null if no queries matched the given {@code pid}
     * 
     * @param pids List of pids to query for relations for to
     * @param relation the relation to query for
     * @return a map of pids and matching tupleiterators.
     * @throws ConfigurationException
     * @throws ServiceException
     * @throws MalformedURLException
     * @throws IOException
     * @throws NoSuchMethodException
     */
    public List<TupleIterator> getRelationships( List<String> pids, Relationship relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, NoSuchMethodException
    {
        throw new NoSuchMethodException( "not implemented" );
    }


    /**
     * Adds the {@code pid} to a collection designated by {@code collectionpid}.
     * The return value indicated whether the relation could be constructed
     * 
     * @param pid
     * @param collectionpid
     * @return
     */
    public boolean addPidToCollection( String pid, String collectionpid ) throws NoSuchMethodException
    {
        throw new NoSuchMethodException( "not implemented" );
    }


}
