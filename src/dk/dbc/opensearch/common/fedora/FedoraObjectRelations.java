/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package dk.dbc.opensearch.common.fedora;

import fedora.common.rdf.FedoraRelsExtNamespace;
import fedora.common.rdf.RDFNamespace;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.xml.rpc.ServiceException;
import org.apache.commons.configuration.ConfigurationException;
import org.trippi.TupleIterator;


/**
 *Class that handles and manages relationships between digital objects in the
 * Fedora repository
 */
public class FedoraObjectRelations
{

    public enum RELATIONSHIP
    {

        IS_MEMBER_OF;
    }
    private RDFNamespace relNS;

    public FedoraObjectRelations()
    {
        relNS = new FedoraRelsExtNamespace();
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
    public TupleIterator getRelationships( String pid, RELATIONSHIP relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException
    {
//        String query = String.format( "select $s from <#ri> where $s <%s> <%s>", "fedora-rels-ext:isMemberOf", "work:1");
        String query = String.format( "select $s from <#ri> where $s <%s> <%s>", relation, pid );
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
    public List<TupleIterator> getRelationships( List<String> pids, RELATIONSHIP relation ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, NoSuchMethodException
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
    public boolean addPidToCollection( String pid, String collectionpid) throws NoSuchMethodException
    {
        throw new NoSuchMethodException( "not implemented" );
    }


 

}
