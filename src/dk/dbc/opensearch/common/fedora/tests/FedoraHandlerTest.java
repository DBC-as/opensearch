package dk.dbc.opensearch.tools.tests;
/** \brief UnitTest for FedoraHandler */

import dk.dbc.opensearch.tools.FedoraHandler;
import dk.dbc.opensearch.components.datadock.CargoContainer;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.rmi.RemoteException;

import javax.xml.stream.XMLStreamException;
import javax.xml.rpc.ServiceException;

import org.junit.*;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;

import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;

import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;


public class FedoraHandlerTest {
    
    Logger log = Logger.getLogger("FedoraHandlerTest");
    
    /**
     * The (mock)objects we need for the most of the tests
     */
    FedoraClient mockFedoraClient; 
    FedoraAPIA mockFedoraAPIA;
    FedoraAPIM mockFedoraAPIM; 
    CargoContainer mockCargoContainer;
    DatastreamDef mockDatastreamDef;
    MIMETypedStream mockMIMETypedStream;

    FedoraHandler fh;
    String[] pids;
    String testString;
    String returnPid;
    byte[] data;
    DatastreamDef[] datastreams;
    
    /**
     * Before each test we construct the needed mock objects 
     * the FedoraClient to pas to the FedoraHandler
     * The FedoraAPIA and M to call methods on
     * The CargoContainer to get data from 
     */
    @Before public void Setup() throws ServiceException, UnsupportedEncodingException, IOException 
    {
        mockFedoraClient = createMock( FedoraClient.class );
        mockFedoraAPIA = createMock( FedoraAPIA.class );
        mockFedoraAPIM = createMock( FedoraAPIM.class );
        mockCargoContainer = createMock( CargoContainer.class );
        mockDatastreamDef = createMock( DatastreamDef.class );
        mockMIMETypedStream = createMock( MIMETypedStream.class );

        //constructing up other needed objects
        pids = new String[]{"test:1"};    
        testString = "æøå";
        data = testString.getBytes( "UTF-8" );
        returnPid = null;
        expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
        expect( mockFedoraClient.getAPIM() ).andReturn( mockFedoraAPIM );
    }
    
    
    /**
     * After each test the mock are reset
     */
    @After public void TearDown()
    {
        reset( mockFedoraClient );
        reset( mockFedoraAPIA );
        reset( mockFedoraAPIM );
        reset( mockCargoContainer );
        reset( mockDatastreamDef );                
    }
    
    
    @Test public void constructorTest() throws ConfigurationException, IOException, MalformedURLException, ServiceException 
    {         
        /**1 setting up the needed mocks 
         * Is done in setup()
         */
        
        /**2 the expectations 
         * moved to setup()
         */
        
        /**3 replay*/
        replay( mockFedoraClient );
        

        /** do the stuff */
        fh = new FedoraHandler(mockFedoraClient); 
        
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );        
    }
    
    
    /***
     * Tests the basic functionality and the submitdatastream
     * Since it calls the constructFoxml method, that method is also tested
     */    
    @Test public void submitDatastreamTest() throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException, MarshalException, ValidationException, ParseException
    {        
        /**1 Setting up the needed mocks
         * Done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup
         */
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "SubmitDatastreamTest" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
        expect( mockCargoContainer.getFormat() ).andReturn( "format" );
        //Calls from constructFoxml
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );
        // out of constructFoxml
        expect( mockFedoraAPIM.ingest( isA( byte[].class ), isA( String.class ), isA( String.class ) ) ).andReturn( "test:1" );

        /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );

        /** do the stuff */ 
        fh = new FedoraHandler(mockFedoraClient); 
        returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIM );
        verify( mockCargoContainer );        
    }
    
    
    /**
     * Test that submitDatastream throws the correct exception when its call to
     * apim.ingest returns a wrong pid
     */
    @Test public void submitDatastreamIngestReturnsWrongPidTest() throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException 
    {        
        /**1 Setting up the needed mocks
         * done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup()
         */
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "IngestReturnsWrongPidTest" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
        expect( mockCargoContainer.getFormat() ).andReturn( "format" );
        // calls from constructFoxml
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );
        
        // out of constructFoxml
        expect( mockFedoraAPIM.ingest( isA( byte[].class ), isA( String.class ), isA( String.class ) ) ).andReturn( "wrongpid" );
        
        /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );
        
        /** do the stuff */ 
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        try
        {
            returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
        }
        catch( Exception ise )
        {
            assertTrue( ise.getClass() == IllegalStateException.class );
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIM ); 
        verify( mockCargoContainer );
    }
    
    
    /**
     * Tests the basic functionality of the getDataStream method 
     */
    @Test public void getDatastreamTest() throws RemoteException, ConfigurationException, UnknownHostException, ServiceException, IOException 
    {
       /**1 Setting up the needed mocks
        * partly done in setup()
        */
       String getDatastreamTestString = "Testing basic info flow"; 
       String testPid = "test:Pid";;
       String testItemId = "testItemId";
       datastreams = new DatastreamDef[]{mockDatastreamDef};
       CargoContainer cargo = null;
       
       /**2 the expectations
        * partly done in setup()
        */
       expect( mockFedoraAPIA.listDatastreams( testPid, null) ).andReturn( datastreams );
       expect( mockDatastreamDef.getID() ).andReturn( testItemId ).times( 2 );
       expect( mockFedoraAPIA.getDatastreamDissemination( testPid, testItemId, null ) ).andReturn( mockMIMETypedStream );
       expect( mockMIMETypedStream.getStream() ).andReturn( data );
       expect( mockDatastreamDef.getLabel() ).andReturn( "testLabel" );
       expect( mockDatastreamDef.getMIMEType() ).andReturn( "text/xml" ).times( 2 ); 
       
       /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIA );
        replay( mockDatastreamDef );
        replay( mockMIMETypedStream );               
        
        /** do the stuff */ 
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        cargo = fh.getDatastream( testPid, testItemId );
        assertTrue( cargo.getSubmitter().equals( "test" ) );
        assertTrue( cargo.getStreamLength() == 6 );
               
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIA ); 
        verify( mockDatastreamDef );
        verify( mockMIMETypedStream );
    }
    

    /**
     * Tests that the getDatastream method throws an IllegalStateException
     * when there is nothing that matches the given itemId
     */
    @Test public void getDatastreamNoMatchingStream() throws IOException, RemoteException, ConfigurationException, ServiceException 
    {
       /** 1 Setting up the needed mocks
        *  partly done in setup()
        */
       String getDatastreamTestString = "Testing basic info flow"; 
       String testPid = "test:Pid";;
       String testItemId = "testItemId";
       datastreams = new DatastreamDef[]{ mockDatastreamDef };
       CargoContainer cargo = null;
       boolean exceptionThrown = false;
       
       /**2 the expectations
        * partly done in setup()
        */
       expect( mockFedoraAPIA.listDatastreams( testPid, null) ).andReturn( datastreams );
       expect( mockDatastreamDef.getID() ).andReturn( "hat" ).times( 2 );
       
       /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIA );
        replay( mockDatastreamDef );
               
        /** do the stuff */ 
        FedoraHandler fh = new FedoraHandler( mockFedoraClient ); 
        try
        {
        	cargo = fh.getDatastream( testPid, testItemId );
        }
        catch( IllegalStateException ise )
        {
            assertTrue( ise.getMessage().equals( String.format( "no cargocontainer with data matching the itemId '%s' in pid '%s' ", testItemId, testPid ) ) );
            exceptionThrown = true;
        }
        assertTrue(exceptionThrown); //To make sure we did get the exception
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIA ); 
        verify( mockDatastreamDef );
    }
}