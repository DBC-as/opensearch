/**
 * \file DatadockPoolTest.java
 * \brief The DatadockPoolTest class
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


import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.components.datadock.DatadockThread;
import dk.dbc.opensearch.components.datadock.DatadockPool;
import dk.dbc.opensearch.common.fedora.FedoraCommunication;
import dk.dbc.opensearch.common.fedora.PIDManager;
import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;

import junit.framework.TestCase;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.InterruptedException;
import java.lang.Throwable;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Vector;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.AbstractExecutorService;
import java.util.concurrent.Callable;


import javax.xml.rpc.ServiceException;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.*;

import org.xml.sax.SAXException;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mockit;


/**
 * This unittest does not test the getTask method, since this methods has 
 * conditionals and are creating new complex objects. The method is mocked 
 * through a mockclass for this test.
 */
public class DatadockPoolTest extends TestCase
{
    /**
     * The (mock)objects we need for the most of the tests
     */
    Estimate mockEstimate;
    Processqueue mockProcessqueue;
    CargoContainer mockCargoContainer;
    FutureTask mockFutureTask;
    ThreadPoolExecutor mockThreadPoolExecutor;
    DatadockJob mockDatadockJob;
    //URI mockUri;
    DatadockPool datadockPool;
    DatadockThread datadockThread;
    FedoraCommunication mockFedoraCommunication;
    PIDManager mockPIDManager;


    /**
     * After each test the mock are reset
     */
    static FutureTask mockFuture = createMock( FutureTask.class );

    @MockClass( realClass = DatadockPool.class )
    public static class MockDatadockPool
    {
        @Mock(invocations = 1)
        public static FutureTask getTask( DatadockJob datadockjob )
        {
            return mockFuture;
        }
    }

    @Before public void setUp()
    {
        mockThreadPoolExecutor = createMock( ThreadPoolExecutor.class );
        mockEstimate = createMock( Estimate.class);
        mockProcessqueue = createMock( Processqueue.class );
        mockPIDManager = createMock( PIDManager.class );
        mockFedoraCommunication = createMock( FedoraCommunication.class );
        mockDatadockJob = createMock( DatadockJob.class );
        //mockUri = createMock( URI.class );
        //mockFuture is static
    }


    @After public void tearDown()
    {
        Mockit.tearDownMocks();
        reset( mockThreadPoolExecutor );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockPIDManager );
        reset( mockFedoraCommunication );
        reset( mockDatadockJob );
        reset( mockFuture );
    }

    @Test public void testConstructor() throws ConfigurationException
    {
        /**
         * setup
         */
        /**
         * expectations
         */
        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );

    }

    @Test public void testSubmit() throws IOException, ConfigurationException, ClassNotFoundException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException
    {

        /**
         * setup
         */

        Mockit.setUpMocks( MockDatadockPool.class );
        File tmpFile = File.createTempFile("opensearch-unittest","" );
        FileWriter fstream = new FileWriter( tmpFile );
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello Java");
        out.close();

        tmpFile.deleteOnExit();
        URI testURI = tmpFile.toURI();
        /**
         * expectations
         */
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //calling getTask with the getTask method mocked
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );

        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        replay( mockDatadockJob );
        replay( mockFuture );

        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        datadockPool.submit( mockDatadockJob );
        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );
        verify( mockDatadockJob );
        verify( mockFuture );

    }



    @Test
    public void testCheckJobs_isDoneFalse() throws IOException, ConfigurationException, ClassNotFoundException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException, InterruptedException
    {
        /**
         * setup
         */
        Mockit.setUpMocks( MockDatadockPool.class );
        File tmpFile = File.createTempFile("opensearch-unittest","" );
        FileWriter fstream = new FileWriter( tmpFile );
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello Java");
        out.close();

        tmpFile.deleteOnExit();
        URI testURI = tmpFile.toURI();
        /**
         * expectations
         */
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //getTask is called and the method is mocked to return mockFuture
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );
        //calling checkJobs
        expect( mockFuture.isDone() ).andReturn( false );

        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        replay( mockDatadockJob );
        replay( mockFuture );

        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        datadockPool.submit( mockDatadockJob );
        Vector< CompletedTask > checkedJobs = datadockPool.checkJobs();
        assertTrue( checkedJobs.size() == 0 );
        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );
        verify( mockDatadockJob );
        verify( mockFuture );

    }

    @Test
    public void testCheckJobs_isDoneTrue() throws IOException, ConfigurationException, ClassNotFoundException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException
    {

        /**
         * setup
         */
        Mockit.setUpMocks( MockDatadockPool.class );
        File tmpFile = File.createTempFile("opensearch-unittest","" );
        FileWriter fstream = new FileWriter( tmpFile );
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello Java");
        out.close();

        tmpFile.deleteOnExit();
        URI testURI = tmpFile.toURI();
        /**
         * expectations
         */
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //getTask is called and the method is mocked to return mockFuture
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );
        //calling checkJobs
        expect( mockFuture.isDone() ).andReturn( true );
        expect( mockFuture.get() ).andReturn( 10f );
        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        replay( mockDatadockJob );
        replay( mockFuture );

        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        datadockPool.submit( mockDatadockJob );
        Vector< CompletedTask > checkedJobs = datadockPool.checkJobs();
        assertTrue( checkedJobs.size() == 1 );
        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );
        verify( mockDatadockJob );
        verify( mockFuture );
    }

    @Test
    public void testCheckJobs_isDoneError() throws IOException, ConfigurationException, ClassNotFoundException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException
    { 
      
        /**
         * setup
         */
        Mockit.setUpMocks( MockDatadockPool.class );
        File tmpFile = File.createTempFile("opensearch-unittest","" );
        FileWriter fstream = new FileWriter( tmpFile );
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello Java");
        out.close();

        tmpFile.deleteOnExit();
        URI testURI = tmpFile.toURI();
        /**
         * expectations
         */
        //submit method
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //getTask is called and the method is mocked to return mockFuture
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );
        //submit method 2nd call
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //getTask is called and the method is mocked to return mockFuture
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );
        //calling checkJobs
        expect( mockFuture.isDone() ).andReturn( true );
        expect( mockFuture.get() ).andThrow( new ExecutionException( new Throwable( "test exception" ) ) );
        expect( mockFuture.isDone() ).andReturn( true );
        expect( mockFuture.get() ).andReturn( 10f );
        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        replay( mockDatadockJob );
        replay( mockFuture );

        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        datadockPool.submit( mockDatadockJob );
        datadockPool.submit( mockDatadockJob );
        Vector< CompletedTask > checkedJobs = datadockPool.checkJobs();
        assertTrue( checkedJobs.size() == 2 );
      
        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );
        verify( mockDatadockJob );
        verify( mockFuture );
    }


    @Test
    public void testShutdown() throws IOException, ConfigurationException, ClassNotFoundException, ServiceException, PluginResolverException, ParserConfigurationException, SAXException, InterruptedException, ExecutionException
    {

        /**
         * setup
         */
        Mockit.setUpMocks( MockDatadockPool.class );
        File tmpFile = File.createTempFile("opensearch-unittest","" );
        FileWriter fstream = new FileWriter( tmpFile );
        BufferedWriter out = new BufferedWriter(fstream);
        out.write("Hello Java");
        out.close();
        tmpFile.deleteOnExit();
        URI testURI = tmpFile.toURI();
        
        /**
         * expectations
         */
        expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockDatadockJob.getFormat() ).andReturn( "test" );
        expect( mockDatadockJob.getSubmitter() ).andReturn( "test" );
        expect( mockPIDManager.getNextPID( "test" ) ).andReturn( "test" );
        mockDatadockJob.setPID( "test" );
        //getTask is called and the method is mocked to return mockFuture
        expect( mockThreadPoolExecutor.submit( mockFuture ) ).andReturn( mockFuture );
        //calling shutdown
        expect( mockFuture.isDone() ).andReturn( false );
        expect( mockFuture.isDone() ).andReturn( false );
        expect( mockFuture.isDone() ).andReturn( true );

        /**
         * replay
         */
        replay( mockThreadPoolExecutor );
        replay( mockEstimate );
        replay( mockProcessqueue );
        replay( mockPIDManager );
        replay( mockFedoraCommunication );
        replay( mockDatadockJob );
        replay( mockFuture );

        /**
         * do stuff
         */
        datadockPool = new DatadockPool( mockThreadPoolExecutor, mockEstimate, mockProcessqueue, mockPIDManager, mockFedoraCommunication );
        datadockPool.submit( mockDatadockJob );
        datadockPool.shutdown();

        /**
         * verify
         */
        verify( mockThreadPoolExecutor );
        verify( mockEstimate );
        verify( mockProcessqueue );
        verify( mockPIDManager );
        verify( mockFedoraCommunication );
        verify( mockDatadockJob );
        verify( mockFuture );
    }
}
