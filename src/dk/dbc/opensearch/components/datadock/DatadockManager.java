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
 * \file DatadockManager.java
 * \brief manages the responsebilities of the datadock.
 */


package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.components.harvest.HarvesterIOException;
import dk.dbc.opensearch.components.harvest.HarvesterInvalidStatusChangeException;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.common.types.IJob;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.pluginframework.PluginTask;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Collections;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;


/**
 * \brief the DataDockManager manages the startup, running and
 * closedown of the associated harvester and threadpool
 */
public class DatadockManager
{
    static Logger log = Logger.getLogger( DatadockManager.class );

    private DatadockPool pool= null;
    private IHarvest harvester = null;
    XMLConfiguration config = null;
    List<IJob> registeredJobs = null;
    private Map<String, List< PluginTask > > flowMap;

    private final Map< Pair< String,String >, Boolean > jobExecutionCheckSet = 
        Collections.synchronizedMap( new HashMap< Pair< String,String >, Boolean >() );

    /**
     * Constructs the the DatadockManager instance.
     *
     * @param pool the threadpool used for executing datadock jobs
     * @param harvester the harvester to supply the datadock with jobs
     * @param flowMap the map used for checking which 
     * submitter format pairs are valid
     * @throws ConfigurationException
     * @throws HarvesterIOException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * 
     */
    public DatadockManager( DatadockPool pool, IHarvest harvester, Map< String, List< PluginTask > > flowMap ) throws ConfigurationException, ParserConfigurationException, SAXException, IOException, HarvesterIOException
    {
        log.trace( "DatadockManager( pool, harvester ) called" );

        this.pool = pool;
        this.harvester = harvester;
        harvester.start();
        registeredJobs = new ArrayList<IJob>();
        this.flowMap = flowMap;
    }


    /**
     * The update method asks for new jobs, put them on the execution
     * queue, cleans up the pool, and return the number of submitted
     * jobs.
     *
     * @throws ClassNotFoundException
     * @throws ConfigurationException
     * @throws FileNotFoundException
     * @throws HarvesterIOException
     * @throws HarvesterInvalidStatusChangeException
     * @throws IOException
     * @throws InterruptedException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerException
     */
    public int update() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, ServiceException, ParserConfigurationException, SAXException, TransformerException, HarvesterIOException, HarvesterInvalidStatusChangeException
    {
        log.trace( "DatadockManager update called" );

        if( null == registeredJobs )
        {
            String error = "Internal job list was null, much to my surprise. Cannot continue";
            log.error( error );
            throw new IllegalStateException( error );
        }

        // Check if there are any registered jobs ready for docking
        // if not... new jobs are requested from the harvester
        if( registeredJobs.isEmpty() )
        {
            log.trace( "no more jobs. requesting new jobs from the harvester" );
            registeredJobs = this.harvester.getJobs( 100 );
        }

        log.debug( "DatadockManager.update: Size of registeredJobs: " + registeredJobs.size() );
        int jobs_submitted = 0;

        while ( registeredJobs.size() > 0 )
        {
            log.trace( String.format( "processing job: %s", registeredJobs.get( 0 ).getIdentifier() ) );

            try
            {
            //build the DatadockJob
            IJob theJob = registeredJobs.get( 0 );
            DatadockJob job = buildDatadockJob( theJob );
            log.trace( String.format( "submitting job %s as datadockJob %s", theJob.toString(), job.toString() ) );

            if( isJobIsPossible( job ) )
            {
                pool.submit( job.getIdentifier() );
                registeredJobs.remove( 0 );
                ++jobs_submitted;

                log.debug( String.format( "submitted job: '%s'", job ) );

            }
            else
            {
                log.warn( String.format( "Jobs for submitter, format \"%s,%s\" has no joblist from plugins and will henceforth be rejected.", job.getSubmitter(), job.getFormat() ) );
                registeredJobs.remove( 0 );
            }
            }
            catch ( RuntimeException re )
            {
                String error = "Runtime exception caught " + re.getMessage();
                log.error( error, re );
            }
        }
        //checking for finished jobs in the pool
        pool.checkJobs();

        return jobs_submitted;
    }


    private synchronized Boolean isJobIsPossible( DatadockJob job )
    {
        Boolean exists = Boolean.FALSE;
        final Pair<String, String> entry = new InputPair<String,String>( job.getSubmitter(), job.getFormat() );

        if( ! this.jobExecutionCheckSet.containsKey( entry ) )
        {
            List< PluginTask > checkList = flowMap.get( job.getSubmitter() + job.getFormat() );
            //            if ( DatadockJobsMap.hasPluginList( job.getSubmitter(), job.getFormat() ) )
            if( ! ( checkList == null ) )
            {
                exists = Boolean.TRUE;
            }else
            {
                exists = Boolean.FALSE;
            }

            this.jobExecutionCheckSet.put( entry, exists );

        }else
        {
            return this.jobExecutionCheckSet.get( entry );
        }

        return exists;
    }

    /**
     * shuts down the resources of the datadock and the datadock
     * itself.
     * @throws InterruptedException
     * @throws HarvesterIOException
     */
    public void shutdown() throws InterruptedException, HarvesterIOException
    {
        log.debug( "Shutting down the pool" );
        pool.shutdown();
        log.debug( "The pool is down" );

        log.debug( "Stopping harvester" );
        harvester.shutdown();
        log.debug( "The harvester is stopped" );
    }


    /**
     * method for building a Datadockjob from the information in a
     * IJob.
     * @param theJob the Ijob to build DatadockJob from
     */
    private DatadockJob buildDatadockJob( IJob theJob )
    {
        log.trace( "building datadock job " + theJob.getIdentifier() );        
        Document referenceData = theJob.getReferenceData();

        if( null == referenceData.getDocumentElement() )
        {
            log.error( String.format( "Could not retrieve data from referencedata" ) );
            throw new IllegalArgumentException( "Could not retrieve data from referencedata" );
        }

        return new DatadockJob( theJob.getIdentifier(), referenceData );
    }
}