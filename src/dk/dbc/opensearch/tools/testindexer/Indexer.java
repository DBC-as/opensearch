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


package dk.dbc.opensearch.tools.testindexer;


import dk.dbc.opensearch.common.compass.CompassFactory;
import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.pluginframework.JobMapCreator;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.components.datadock.DatadockJobsMap;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.pti.PTIJobsMap;
import dk.dbc.opensearch.components.pti.PTIThread;
import dk.dbc.opensearch.tools.testindexer.Estimate;
import dk.dbc.opensearch.tools.testindexer.FedoraCommunication;
import dk.dbc.opensearch.tools.testindexer.Processqueue;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.InterruptedException;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.config.CompassConfiguration;
import org.compass.core.config.CompassEnvironment;
import org.compass.core.converter.mapping.xsem.XmlContentMappingConverter;
import org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter;
import org.xml.sax.SAXException;



/**
 * The Indexer class uses a datadockthread and a ptithread, to index a job
 */
public class Indexer
{

    Logger log = Logger.getLogger( Indexer.class );

    private ExecutorService pool;
    private static HashMap< InputPair< String, String >, ArrayList< String > > jobMap;

    /**
     * Constructor
     */
    public Indexer()throws ConfigurationException, IOException, MalformedURLException, ServiceException
    {
        log.debug( "entering constructor()" );
        pool = Executors.newFixedThreadPool( 1 );
    }

    /**
     * the index method indexes the job 
     *
     * @param job
     * @param submitter
     * @param format
     * @param mappingFile
     * @param indexDir
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws InterruptedException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws SAXException
     * @throws ServiceException
     */
    public void index( URI job, String submitter, String format, URL mappingFile, String indexDir )
    throws ParserConfigurationException, SAXException, IOException, ConfigurationException, ClassNotFoundException, PluginResolverException, ServiceException, InterruptedException
    {
        log.debug( String.format( "entering index( job=%s, submitter=%s, format=%s, mappingFile=%s, indexDir=%s)",
                                  job.toString(), submitter, format, mappingFile.toString(), indexDir ) );

        log.debug( "Configuring Compass" );

        CompassConfiguration conf = new CompassConfiguration()
        .addURL( mappingFile )
        .setSetting( CompassEnvironment.CONNECTION, indexDir )
        .setSetting( CompassEnvironment.Converter.TYPE, "org.compass.core.converter.mapping.xsem.XmlContentMappingConverter" )
        .setSetting( CompassEnvironment.Converter.XmlContent.TYPE, "org.compass.core.xml.dom4j.converter.SAXReaderXmlContentConverter" );

        Compass compass = conf.buildCompass();
        CompassSession session = compass.openSession();


        log.debug( "Create needed instances for indexing" );
        DatadockJob datadockJob = new DatadockJob( job, submitter, format, "Mock_fedoraPID" );
        // using local estimate, processqueue, fedoracommunication classes
        IEstimate e = new Estimate();
        IProcessqueue p = new Processqueue();
        IFedoraCommunication c = new FedoraCommunication();

        runDatadock( datadockJob, e, p, c );

        log.debug( "datadock finshed, starting the pti" );
        
        runPTI( "mock_fedoraPID", session, e, c);
        
        log.debug( String.format( "Indexed file: %s", job ) );
    }

    /**
     * runPTI runs the job through a pti thread
     *
     * @param fedoraPid
     * @param session
     * @param estimate
     * @param fedoraCommunication
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws FileNotFoundException
     * @throws InterruptedException
     * @throws IOException
     * @throws PluginResolverException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws ServiceException
     */
    private void runDatadock( DatadockJob datadockJob, IEstimate estimate, IProcessqueue processqueue, IFedoraCommunication fedoraCommunication )
        throws ConfigurationException, ClassNotFoundException, InterruptedException, FileNotFoundException, IOException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        log.debug( "Entering runDatadock" );
        
        DatadockThread ddt = new DatadockThread( datadockJob, estimate, processqueue, fedoraCommunication );
        FutureTask<Float> ft = new FutureTask<Float>( ddt );
        

        pool.submit( ft );

        log.debug( "Datadock job commited to threadpool.... waiting for it to return" );
        while ( ! ft.isDone() ) {} // Wait until the thread is done
       
        Float f = -1f;

        try
        {
            f = ft.get();
        }
        catch ( ExecutionException ee )
        {
            // getting exception from thread
            Throwable cause = ee.getCause();

            System.err.println( String.format( "Exception Caught in runDatadock: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
            StackTraceElement[] trace = cause.getStackTrace();
            for ( int i = 0; i < trace.length; i++ )
            {
                
                System.err.println( trace[i].toString() );
            }
        }
        log.debug( String.format( "Datadock ended... returned %s", f ) );
    }

    /**
     * runPTI runs the job through a pti thread
     *
     * @param fedoraPid
     * @param session
     * @param estimate
     * @param fedoraCommunication
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws InterruptedException
     * @throws IOException
     * @throws ServiceException
     */
    private void runPTI( String fedoraPid,  CompassSession session, IEstimate estimate, IFedoraCommunication fedoraCommunication )
    throws ConfigurationException, ClassNotFoundException, InterruptedException, IOException, ServiceException
    {
        log.debug( "Entering runPTI" );

        // run the PTI thread

        PTIThread PTIt = new PTIThread( fedoraPid, session, estimate, fedoraCommunication );

        FutureTask<Long> ptiFuture = new FutureTask<Long>( PTIt );
        pool.submit( ptiFuture );
     
        log.debug( "PTI job commited to threadpool.... waiting for it to return" );
        while ( ! ptiFuture.isDone() ) {} // Wait until the thread is done

        Long l = 0l;

        try
        {
            l = ptiFuture.get();
        }
        catch ( ExecutionException ee )
        {
            // getting exception from thread
            Throwable cause = ee.getCause();

            System.err.println( String.format( "Exception Caught runPTI: '%s'\n'%s'", cause.getClass() , cause.getMessage() ) );
            StackTraceElement[] trace = cause.getStackTrace();
            for ( int i = 0; i < trace.length; i++ )
            {
                System.err.println( trace[i].toString() );
            }
        }
        log.debug( String.format( "PTI ended... returned %s", l ) );
    }
}


