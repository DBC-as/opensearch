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


import dk.dbc.opensearch.common.db.IProcessqueue;
import dk.dbc.opensearch.common.fedora.FedoraAdministration;
//import dk.dbc.opensearch.common.fedora.IFedoraCommunication;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.statistics.IEstimate;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.pti.PTIThread;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.lang.InterruptedException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.xml.sax.SAXException;


/**
 * The Indexer class uses a datadockthread and a ptithread, to index a job
 */
public class Indexer
{
    Logger log = Logger.getLogger( Indexer.class );
    

    private static HashMap< InputPair< String, String >, ArrayList< String > > jobMap;
    private ExecutorService pool;
    private CompassSession session;
    private IEstimate e;
    private IProcessqueue p;
    //private IFedoraCommunication c;
    private FedoraAdministration fedoraAdministration;
    private Compass compass;
    private FutureTask<Long> ptiFuture;
    private FutureTask<Float> datadockFuture;

    
    /**
     * Constructor
     */
    public Indexer( Compass compass, IEstimate e, IProcessqueue p, FedoraAdministration fedoraAdministration, ExecutorService pool ) throws ConfigurationException, IOException, MalformedURLException, ServiceException
    {
    	System.out.println( "Indexer Constructor" );    	
        log.debug( "entering constructor()" );
        this.pool = pool;
        this.compass = compass;
        this.e = e;
        this.p = p;
        this.fedoraAdministration = fedoraAdministration;
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
    public void index( DatadockJob datadockJob ) throws ParserConfigurationException, SAXException, IOException, ConfigurationException, ClassNotFoundException, PluginResolverException, ServiceException, InterruptedException, ExecutionException
    {
        log.debug( String.format( "entering index( datadockJob[ job=%s, submitter=%s, format=%s ] )",
                                  datadockJob.getUri().toString(), datadockJob.getSubmitter(), datadockJob.getFormat() ) );

        CompassSession session = compass.openSession();

        runDatadock( datadockJob, e, p, fedoraAdministration );
        log.debug( "datadock finshed, starting the pti" );

        runPTI( "mock_fedoraPID", session, e, fedoraAdministration );
        log.debug( String.format( "Indexed file: %s", datadockJob.getUri().toString() ) );
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
    private void runDatadock( DatadockJob datadockJob, IEstimate estimate, IProcessqueue processqueue, FedoraAdministration fedoraAdministration ) throws ConfigurationException, ClassNotFoundException, InterruptedException, FileNotFoundException, IOException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException, ExecutionException
    {
        log.debug( "Entering runDatadock" );
        FutureTask<Float> ft = getDatadockTask( datadockJob, estimate, processqueue, fedoraAdministration );

        pool.submit( ft );

        log.debug( "Datadock job commited to threadpool.... waiting for it to return" );
        while ( ! ft.isDone() ) {} // Wait until the thread is done

        Float f = -1f;

        f = ft.get();

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
    private void runPTI( String fedoraPid,  CompassSession session, IEstimate estimate, FedoraAdministration fedoraAdministration ) throws ConfigurationException, ClassNotFoundException, InterruptedException, IOException, ServiceException, ExecutionException
    {
        log.debug( "Entering runPTI" );

        // run the PTI thread
        ptiFuture = getPTITask( fedoraPid, session, estimate, fedoraAdministration );
        pool.submit( ptiFuture );

        log.debug( "PTI job commited to threadpool.... waiting for it to return" );
        while ( ! ptiFuture.isDone() ) {} // Wait until the thread is done

        Long l = 0l;

        l = ptiFuture.get();
        log.debug( String.format( "PTI ended... returned %s", l ) );
    }

    
    /**
     *
     */
    public FutureTask<Float> getDatadockTask( DatadockJob datadockJob, IEstimate estimate, IProcessqueue processqueue, FedoraAdministration fedoraAdministration ) throws ConfigurationException, ClassNotFoundException, InterruptedException, FileNotFoundException, IOException, PluginResolverException, ParserConfigurationException, SAXException, ServiceException
    {
        DatadockThread ddt = new DatadockThread( datadockJob, estimate, processqueue, fedoraAdministration );
        FutureTask<Float> datadockFuture = new FutureTask<Float>( ddt );
        return datadockFuture;
    }

    
    /**
     *
     */
    public FutureTask<Long> getPTITask( String fedoraPid, CompassSession session, IEstimate estimate, FedoraAdministration fedoraAdministration ) throws ConfigurationException, ClassNotFoundException, InterruptedException, IOException, ServiceException
    {
        PTIThread PTIt = new PTIThread( fedoraPid, session, estimate, fedoraAdministration );
        FutureTask<Long> ptiFuture = new FutureTask<Long>( PTIt );
        return ptiFuture;
    }
}
