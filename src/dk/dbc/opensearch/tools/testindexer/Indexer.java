/**
 * \file Indexer.java
 * \brief The Indexer class
 * \package testindexer;
 */

package dk.dbc.opensearch.tools.testindexer;

import org.compass.core.Compass;
import dk.dbc.opensearch.common.compass.CompassFactory;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.pti.PTIThread;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraCommunication;
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
import org.compass.core.CompassSession;

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
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import org.apache.log4j.Logger;

public class Indexer{

    Logger log = Logger.getLogger( Indexer.class );

    private ExecutorService pool;
    private static HashMap< InputPair< String, String >, ArrayList< String > > jobMap;

    public Indexer()throws ServiceException, MalformedURLException, IOException, ConfigurationException
    {

        pool = Executors.newFixedThreadPool(1);
    }

    public void index( URI uri, String submitter, String format, Compass compass ) throws ParserConfigurationException, SAXException, IOException, ConfigurationException, ClassNotFoundException, PluginResolverException, ServiceException, InterruptedException
    {

        // Firstly, the data pointed to by uri, is worked on by a datadockThread

        DatadockJob datadockJob = new DatadockJob( uri, submitter, format);
        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        IFedoraCommunication c = new FedoraCommunication();
        jobMap = JobMapCreator.getMap( this.getClass() );
        
        DatadockThread ddt = new DatadockThread( datadockJob, e, p, jobMap, c );
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
        
        // After, the data is gone through the datadock, it is indexed through the pti thread

        log.debug( "Setting up the Compass object" );
        CompassSession session = compass.openSession();

        PTIThread PTIt = new PTIThread( "mockPID", session, e, jobMap);
        FutureTask ptiFuture = new FutureTask( PTIt );
        pool.submit( ptiFuture );
        while(! ptiFuture.isDone() ){}

        Long l = 0l;

        try{
            l = (Long) ptiFuture.get();
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

        log.debug( String.format( "Indexed file: %s", uri ) );
    }
}
