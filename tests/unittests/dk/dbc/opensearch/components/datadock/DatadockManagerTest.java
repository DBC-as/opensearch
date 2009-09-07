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
import dk.dbc.opensearch.components.datadock.DatadockJob;
import dk.dbc.opensearch.common.types.CompletedTask;
import dk.dbc.opensearch.components.datadock.DatadockManager;
import dk.dbc.opensearch.components.datadock.DatadockPool;
import dk.dbc.opensearch.components.harvest.FileHarvest;
import dk.dbc.opensearch.components.harvest.IHarvest;
import dk.dbc.opensearch.components.harvest.IJob;
import dk.dbc.opensearch.components.harvest.IIdentifier;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.Vector;
import java.util.concurrent.RejectedExecutionException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.rpc.ServiceException;

import org.w3c.dom.DOMException;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.w3c.dom.Node;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import org.junit.*;
import org.xml.sax.SAXException;


/**
 * 
 */
public class DatadockManagerTest
{
    IHarvest mockHarvester;
    DatadockPool mockDatadockPool;
    Vector<DatadockJob> mockJobs;
    DatadockJob mockDatadockJob;
    Vector< CompletedTask > mockFinJobs;
    IJob mockJob;
    Document testDocument;
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    IIdentifier mockIdentifier;

    Document buildTestDocument( String submitter, String format ,String language ) throws ParserConfigurationException
    {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        Document theDocument = docBuilder.newDocument();
        Element root = theDocument.createElement( "referencedata" );
        Element info = theDocument.createElement( "info" );
        info.setAttribute( "submitter", submitter );
        info.setAttribute( "format", format );
        info.setAttribute( "language", language );
        root.appendChild( (Node)info );
        theDocument.appendChild( (Node)root);

        return theDocument;
    }

    @Before
    public void Setup()
    {
        mockHarvester = createMock( FileHarvest.class );
        mockDatadockPool = createMock( DatadockPool.class );
        mockDatadockJob = createMock( DatadockJob.class );
        mockFinJobs = createMock( Vector.class );
        mockJob = createMock( IJob.class );
        mockIdentifier = createMock( IIdentifier.class );
    }


    @After
    public void tearDown()
    {
        reset( mockHarvester);
        reset( mockDatadockPool );
        reset( mockDatadockJob );
        reset( mockJob );
        reset( mockFinJobs );
        reset( mockIdentifier );
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
    public void testUpdate() throws Exception//InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, ConfigurationException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        /**
         * setup
         */
        ArrayList<IJob> jobs = new ArrayList<IJob>();
        jobs.add( mockJob );
        testDocument = buildTestDocument( "submitter", "format", "language" );
       

        /**
         * expectations
         */
        mockHarvester.start();

        expect( mockHarvester.getJobs( 100 ) ).andReturn( jobs );
        //buildDadadockjob
        expect( mockJob.getReferenceData() ).andReturn( testDocument );
        expect( mockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockDatadockPool.submit( isA( DatadockJob.class ) );
        //expect( mockDatadockJob.getUri() ).andReturn( testURI );
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );


        /**
         * replay
         */
        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockJob );
        replay( mockDatadockPool );
        replay( mockIdentifier );
        //  replay( mockDatadockJob );
        /**
         * do stuff
         */
        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
        /**
         * verify
         */
        verify( mockJob );
        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockIdentifier );
        //verify( mockDatadockJob );
    }


    @Test
    public void testUpdate_reject() throws Exception//InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        ArrayList< IJob > jobs = new ArrayList< IJob >();
        jobs.add( mockJob );
 testDocument = buildTestDocument( "submitter", "format", "language" );
        //        URI testURI = new URI( "testURI" );

        mockHarvester.start();
        expect( mockHarvester.getJobs( 100 ) ).andReturn( jobs );
        expect( mockJob.getReferenceData() ).andReturn( testDocument );
        expect( mockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockDatadockPool.submit( isA( DatadockJob.class ) );
        expectLastCall().andThrow( new RejectedExecutionException() );
        
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );


        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockJob );
        replay( mockIdentifier );

        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();

        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockJob);
        verify( mockIdentifier );
    }

    @Test
    public void testUpdateWithRejectionAndContinuation() throws Exception//InterruptedException, ConfigurationException, ClassNotFoundException, FileNotFoundException, IOException, URISyntaxException, ServiceException, RejectedExecutionException, NullPointerException, PluginResolverException, ParserConfigurationException, SAXException
    {
        ArrayList< IJob > jobs = new ArrayList< IJob >();
        jobs.add( mockJob );
        jobs.add( mockJob );
testDocument = buildTestDocument( "submitter", "format", "language" );
        //URI testURI = new URI( "testURI" );

        mockHarvester.start();
        //calling update 1st time
        expect( mockHarvester.getJobs( 100 ) ).andReturn( jobs );
        expect( mockJob.getReferenceData() ).andReturn( testDocument );
        expect( mockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockDatadockPool.submit( isA( DatadockJob.class ) );
        expectLastCall().andThrow( new RejectedExecutionException() );
         expect( mockJob.getReferenceData() ).andReturn( testDocument );
        expect( mockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockDatadockPool.submit( isA( DatadockJob.class ) );
       

        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );
        //calling update 2nd time
 expect( mockJob.getReferenceData() ).andReturn( testDocument );
        expect( mockJob.getIdentifier() ).andReturn( mockIdentifier );
        mockDatadockPool.submit( isA(DatadockJob.class ) );
       
        expect( mockDatadockPool.checkJobs() ).andReturn( mockFinJobs );

        replay( mockFinJobs );
        replay( mockHarvester );
        replay( mockDatadockPool );
        replay( mockJob );
        replay( mockIdentifier );

        DatadockManager datadockManager = new DatadockManager( mockDatadockPool, mockHarvester );
        datadockManager.update();
        datadockManager.update();
        verify( mockFinJobs );
        verify( mockHarvester );
        verify( mockDatadockPool );
        verify( mockJob );
        verify( mockIdentifier );
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
