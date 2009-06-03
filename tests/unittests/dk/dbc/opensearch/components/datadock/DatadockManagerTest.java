/**
 * \file DatadockManagerTest.java
 * \brief The DatadockManagerTest class
 * \package tests;
 */

package dk.dbc.opensearch.components.datadock;


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


import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.components.datadock.DatadockManager;
import dk.dbc.opensearch.components.datadock.DatadockPool;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.IHarvester;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import org.xml.sax.SAXException;


/**
 * 
 */
public class DatadockManagerTest
{
    IHarvester mockHarvester;
    DatadockPool mockDatadockPool;
    Vector<DatadockJob> mockJobs;
    DatadockJob mockDatadockJob;
    Vector< CompletedTask > mockFinJobs;


    @Before
    public void Setup()
    {
        mockHarvester = createMock( FileHarvest.class );
        mockDatadockPool = createMock( DatadockPool.class );
        mockDatadockJob = createMock( DatadockJob.class );
        mockFinJobs = createMock( Vector.class );
    }


    @After
    public void tearDown()
    {
        reset( mockHarvester);
        reset( mockDatadockPool );
        reset( mockDatadockJob );

        reset( mockFinJobs );
    }


    @Test
    public void testConstructor() throws ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        mockHarvester.start();
        replay( mockHarvester );
        DatadockManager datadockmanager = new DatadockManager( mockDatadockPool, mockHarvester );
        verify( mockHarvester );
    }


    @Test
    public void testUpdate() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, ConfigurationException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        /**
         * setup
         */
        Vector<DatadockJob> jobs = new Vector<DatadockJob>();
        jobs.add( mockDatadockJob );

        URI testURI = new URI( "testURI" );
        /**
         * expectations
         */
        mockHarvester.start();

        expect( mockHarvester.getJobs() ).andReturn( jobs );

        mockDatadockPool.submit( mockDatadockJob );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );


        /**
         * replay
         */
        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockDatadockJob );
        /**
         * do stuff
         */
        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
        /**
         * verify
         */
        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockDatadockJob );
    }


    @Test
    public void testUpdate_reject() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        Vector< DatadockJob > jobs = new Vector< DatadockJob >();
        jobs.add( mockDatadockJob );

        URI testURI = new URI( "testURI" );

        mockHarvester.start();
        expect( mockHarvester.getJobs() ).andReturn( jobs );

        mockDatadockPool.submit( mockDatadockJob );
        expectLastCall().andThrow( new RejectedExecutionException() );
        //mockDatadockPool.submit( mockDatadockJob );

        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );


        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockDatadockJob );

        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();

        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockDatadockJob );
    }

  
    @Test
    public void testUpdateWithRejectionAndContinuation() throws InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        Vector< DatadockJob > jobs = new Vector< DatadockJob >();
        jobs.add( mockDatadockJob );
        jobs.add( mockDatadockJob );

        URI testURI = new URI( "testURI" );

        mockHarvester.start();
        //calling update 1st time
        expect( mockHarvester.getJobs() ).andReturn( jobs );

        mockDatadockPool.submit( mockDatadockJob );
        expectLastCall().andThrow( new RejectedExecutionException() );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        mockDatadockPool.submit( mockDatadockJob );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );

      expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );
        //calling update 2nd time
        mockDatadockPool.submit( mockDatadockJob );
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
      expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );

        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockDatadockJob );

        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
        datadockManager.update();
        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockDatadockJob );
    }


    @Test
    public void testShutdown() throws InterruptedException, ConfigurationException, ParserConfigurationException, SAXException, IOException
    {
        mockHarvester.start();
        mockHarvester.shutdown();
        mockDatadockPool.shutdown();

        replay( mockHarvester );
        replay( mockDatadockPool );
        DatadockManager datadockmanager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockmanager.shutdown();

        verify( mockDatadockPool );
        verify( mockHarvester );
    }
}
