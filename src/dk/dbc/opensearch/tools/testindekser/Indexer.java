/**
 * \file Indexer.java
 * \brief The Indexer class
 * \package testindexer;
 */



import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraHandle;
import dk.dbc.opensearch.tools.testindexer.Processqueue;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import org.apache.commons.configuration.ConfigurationException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.db.IProcessqueue;
import java.lang.ClassNotFoundException;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import javax.xml.rpc.ServiceException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ExecutionException;
import java.lang.InterruptedException;

public class Indexer{
    private FedoraHandle fedorahandle;
    //private Executor pool;
    private ExecutorService pool;
    public static HashMap< Pair< String, String >, ArrayList< String > > jobMap;

    public Indexer()throws ServiceException, MalformedURLException, IOException, ConfigurationException
    {
        fedorahandle = new FedoraHandle();
        pool = Executors.newFixedThreadPool(1);
    }

    public void index( URI uri, String submitter, String format ) throws ParserConfigurationException, SAXException, IOException, ConfigurationException, ClassNotFoundException, PluginResolverException, ServiceException, InterruptedException
    {
        DatadockJob datadockJob = new DatadockJob( uri, submitter, format);
        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        jobMap = JobMapCreator.getMap( this.getClass() );
        DatadockThread ddt = new DatadockThread( datadockJob, e, p, jobMap );
        FutureTask ft = new FutureTask( ddt );
        pool.submit( ft );
        while(! ft.isDone() ){}

        Float f = -1f;

        try{
                f = (Float)ft.get();
            }
        catch( ExecutionException ee ){

                // getting exception from thread
                Throwable cause = ee.getCause();

                System.err.println( String.format( "Exception Caught: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
                StackTraceElement[] trace = cause.getStackTrace();
                for( int i = 0; i < trace.length; i++ )
                    {
                        System.err.println( trace[i].toString() );
                    }
        }
        
        

    }
}
