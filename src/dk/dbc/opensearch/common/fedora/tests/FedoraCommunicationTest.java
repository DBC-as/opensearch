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


package dk.dbc.opensearch.common.fedora.tests;

/** \brief UnitTest for FedoraCommunication */
import dk.dbc.opensearch.common.fedora.FedoraCommunication;
import dk.dbc.opensearch.common.fedora.FedoraHandle;

import dk.dbc.opensearch.common.config.FedoraConfig;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.fedora.FedoraTools;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.IndexingAlias;
import dk.dbc.opensearch.common.types.InputPair;

import java.io.IOException;
import java.lang.ClassNotFoundException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.sql.SQLException;
import java.util.ArrayList;


import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.xml.sax.SAXException;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.MIMETypedStream;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;

import static org.easymock.classextension.EasyMock.*;

import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 */
public class FedoraCommunicationTest
{

    FedoraCommunication fc;

    CargoContainer mockCC;
    CargoObject mockCO;
    DatadockJob mockDatadockJob;
    Processqueue mockProcessqueue;
    Estimate mockEstimate;
    static FedoraAPIA mockFea = createMock( FedoraAPIA.class );
    static FedoraAPIM mockFem = createMock( FedoraAPIM.class );


    @MockClass( realClass = FedoraClient.class )
    public static class MockFedoraClient
    {

        @Mock public void $init( String baseURL, String user, String pass )
        {
        }

        @Mock public static FedoraAPIA getAPIA()
        {
            return mockFea;
        }

        @Mock public static FedoraAPIM getAPIM()
        {
            return mockFem;
        }
    }

    @MockClass( realClass = FedoraConfig.class )
    public static class MockFedoraConfig
    {
        @Mock public String getHost()
        {
            return "host";
        }
        @Mock public String getPort()
        {
            return "port";
        }
        @Mock public String getUser()
        {
            return "User";
        }
        @Mock public String getPassPhrase()
        {
            return "pass";
        }
    }

    @MockClass( realClass = FedoraTools.class )
    public static class MockFedoraTools
    {
        @Mock public byte[] constructFoxml( CargoContainer cargo, String nextPid, String label )
        {
            String byteString = "bytes";
            byte[] bytes = byteString.getBytes();
            return bytes;
        }
    }

    /**
     *
     */
    @Before public void SetUp() {
        mockCC = createMock( CargoContainer.class );
        mockDatadockJob = createMock( DatadockJob.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockEstimate = createMock( Estimate.class );
        mockCO = createMock( CargoObject.class );
    }

    /**
     *
     */
    @After public void TearDown()
    {
        Mockit.tearDownMocks();
        reset( mockCC );
        reset( mockCO );
        reset( mockProcessqueue );
        reset( mockEstimate );
        reset( mockFem );
        reset( mockFea );
        reset( mockDatadockJob );

    }

    /**
     * Testing the happy path of the constructor, the only path, since the
     * super class is being mocked...
     */
    @Test public void testConstructor() throws ConfigurationException, java.io.IOException, java.net.MalformedURLException, ServiceException
    {
        Mockit.setUpMocks( MockFedoraClient.class );
        Mockit.setUpMocks( MockFedoraConfig.class );
        fc = new FedoraCommunication();

    }

    /**
     * Testing the happy path of the storeContainer method
     */
    @Test public void testStoreContainer() throws ConfigurationException, java.io.IOException, java.net.MalformedURLException, ServiceException, ClassNotFoundException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException
    {
        Mockit.setUpMocks( MockFedoraClient.class );
        Mockit.setUpMocks( MockFedoraConfig.class );
        Mockit.setUpMocks( MockFedoraTools.class );
        ArrayList<CargoObject> COList = new ArrayList<CargoObject>();
        COList.add( mockCO );
        COList.add( mockCO );

        //expectations
        expect( mockCC.getCargoObjects() ).andReturn( COList );
        expect( mockCO.getDataStreamName() ).andReturn( DataStreamType.OriginalData );
        expect( mockCO.getMimeType() ).andReturn( "mimeType" );
        expect( mockCO.getContentLength() ).andReturn( 2 );
        expect( mockCO.getDataStreamName() ).andReturn( DataStreamType.AdminData );
        expect( mockCO.getContentLength() ).andReturn( 2 );
        expect( mockDatadockJob.getPID() ).andReturn( "PID" );
        expect( mockDatadockJob.getFormat() ).andReturn( "format" ).times( 2 );
        expect( mockFem.ingest( isA( byte[].class ), isA( String.class ), isA(String.class ) ) ).andReturn( "pid" );
        mockProcessqueue.push( "pid" );
        expect( mockEstimate.getEstimate( "mimeType", 4l ) ).andReturn( 3f );

        //replay
        replay( mockCO );
        replay( mockCC );
        replay( mockDatadockJob );
        replay( mockProcessqueue );
        replay( mockEstimate );
        replay( mockFem );
        replay( mockFea );

        //do stuff
        fc = new FedoraCommunication();
        InputPair<String, Float> result = fc.storeContainer( mockCC, mockDatadockJob, mockProcessqueue, mockEstimate);

        //verify
        verify( mockCO );
        verify( mockCC );
        verify( mockDatadockJob );
        verify( mockProcessqueue );
        verify( mockEstimate );
        verify( mockFem );
        verify( mockFea );

    }

    /**
     * Tests the happy path of the retrieveContainer method
     */
    @Test
    public void testRetrieveContainer() throws IOException, ParserConfigurationException, RemoteException, SAXException
    {}
}