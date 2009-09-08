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

package dk.dbc.opensearch.components.datadock;


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.xml.XMLUtils;
//import dk.dbc.opensearch.components.harvest.IHarvester;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.IJob;
import dk.dbc.opensearch.components.harvest.HarvesterIOException;


import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Vector;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import javax.xml.transform.TransformerException;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.XMLConfiguration;
import org.apache.log4j.Logger;
import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;
import org.xml.sax.InputSource;
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
    ArrayList<IJob> registeredJobs = null;
    //Vector< DatadockJob > registeredJobs = null;
    static int rejectedSleepTime;


    /**
     * Constructs the the DatadockManager instance.
     */
    public DatadockManager( DatadockPool pool, IHarvest harvester ) throws ConfigurationException, ParserConfigurationException, SAXException, IOException, HarvesterIOException
    {
        log.trace( "DatadockManager( pool, harvester ) called" );

        this.pool = pool;
        this.rejectedSleepTime = DatadockConfig.getRejectedSleepTime();
        this.harvester = harvester;
	harvester.start();
        registeredJobs = new ArrayList<IJob>();
    }


    public void update() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException, TransformerException, HarvesterIOException
    {
        log.trace( "DatadockManager update called" );

        // Check if there are any registered jobs ready for docking
        // if not... new jobs are requested from the harvester
        if( registeredJobs.isEmpty() )
            //if( registeredJobs.size() == 0 )
        {
            log.trace( "no more jobs. requesting new jobs from the harvester" );
            registeredJobs = (ArrayList<IJob>)harvester.getJobs( 100 );
        }

        log.debug( "DatadockManager.update: Size of registeredJobs: " + registeredJobs.size() );

        for( int i = 0; i < registeredJobs.size(); i++ )
        {
            log.trace( String.format( "processing job %s: %s", i, registeredJobs.get( i ).toString() ) );
            IJob theJob = registeredJobs.get( 0 );
            //build the DatadockJob
            DatadockJob job = buildDatadockJob( theJob );
            //DatadockJob job = registeredJobs.get( 0 );
            log.trace( String.format( "submitting job %s as datadockJob %s", theJob.toString(), job.toString() ) );
            // execute jobs
            try
            {
                pool.submit( job );
                registeredJobs.remove( 0 );
                log.debug( String.format( "submitted job: '%s'", job ) );
            }
            catch( RejectedExecutionException re )
            {
                /** \todo: explanation on the frequency of this exception.*/
                log.warn( String.format( "job: '%s' rejected, trying again", job) );
                Thread.sleep( rejectedSleepTime );
            }
        }

        //checking jobs
        Vector<CompletedTask> finishedJobs = pool.checkJobs();
    }


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
     * method for building a Datadockjob from the information in a IJob
     */

    private DatadockJob buildDatadockJob( IJob theJob )
    {
        Document referenceData = theJob.getReferenceData();
        //get submitter and format

        // ByteArrayInputStream bis = new ByteArrayInputStream( referenceData );
        Element root = null;
        Element info = null;

        root = referenceData.getDocumentElement();

        if( root == null )
        {
            log.error( String.format( "Could not retrieve data from referencedata" ) );
            throw new IllegalArgumentException( "Could not retrieve data from referencedata" );
        }

        info = (Element)root.getElementsByTagName( "info").item( 0 );

        if( info == null )
        {
            log.error( String.format( "Could not retrieve info element from referencedata" ) );
            throw new IllegalArgumentException( "Could not retrieve info element from referencedata" );
        }

        String submitter = info.getAttribute( "submitter" );
        String format = info.getAttribute( "format" );

        DatadockJob ddjob = new DatadockJob( submitter, format, theJob.getIdentifier(), referenceData );
        return ddjob;
    }
}