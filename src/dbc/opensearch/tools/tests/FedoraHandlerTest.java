package dbc.opensearch.tools.tests;
/** \brief UnitTest for FedoraHandler */

import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;
import static org.junit.Assert.*;
//import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import org.apache.axis.types.NonNegativeInteger;

import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;

import org.apache.log4j.Logger;

import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import java.rmi.RemoteException;
import java.net.UnknownHostException;  
import javax.xml.stream.XMLStreamException;
import java.io.UnsupportedEncodingException;

public class FedoraHandlerTest {
    
    Logger log = Logger.getLogger("FedoraHandlerTest");
    
    /**
     * The (mock)objects we need for the most of the tests
     */
    FedoraClient mockFedoraClient; 
    FedoraAPIA mockFedoraAPIA;
    FedoraAPIM mockFedoraAPIM; 
    CargoContainer mockCargoContainer;
    
    FedoraHandler fh;
    String[] pids;
    String testString;
    String returnPid;
    byte[] data;
    
    /**
     * Before each test we construct the needed mockobjects 
     * the FedoraClient to pas to the FedoraHandler
     * The FedoraAPIA and M to call methods on
     * The CargoContainer to get data from 
     */
    @Before public void Setup()throws ServiceException, UnsupportedEncodingException, IOException {
        mockFedoraClient = createMock( FedoraClient.class );
        mockFedoraAPIA = createMock( FedoraAPIA.class );
        mockFedoraAPIM = createMock( FedoraAPIM.class );
        mockCargoContainer = createMock( CargoContainer.class );
        
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

    @After public void TearDown(){
        reset( mockFedoraClient );
        reset( mockFedoraAPIA );
        reset( mockFedoraAPIM );
        reset( mockCargoContainer );
                
    }
    
    @Test public void constructorTest() throws ConfigurationException, IOException, MalformedURLException, ServiceException { 
         
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

    /**
     * Tests that the IOException that the getAPIA call can throw is propagated 
     * and execution stops
     */
    @Test public void constructorGetAPIAIOExceptionTest()throws ConfigurationException, IOException, MalformedURLException, ServiceException {
    
     /**1 setting up the needed mocks 
         * Is done in setup()
         */
        
        /**2 the expectations 
         * moved to setup()
         */
        reset( mockFedoraClient ); // we need it from scratch
        expect( mockFedoraClient.getAPIA() ).andThrow( new IOException( "" ) );
        
        /**3 replay*/
        replay( mockFedoraClient );
        

        /** do the stuff */
        try{
        fh = new FedoraHandler(mockFedoraClient); 
        }catch( Exception re ){
            assertTrue( re.getClass() == IOException.class ); 
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );    
    
    }

 /**
     * Tests that the IOException that the getAPIM call can throw is propagated 
     * and execution stops
     */
    @Test public void constructorGetAPIMIOExceptionTest()throws ConfigurationException, IOException, MalformedURLException, ServiceException {
    
     /**1 setting up the needed mocks 
         * Is done in setup()
         */
        
        /**2 the expectations 
         * moved to setup()
         */
        reset( mockFedoraClient ); // we need it from scratch
        expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
        expect( mockFedoraClient.getAPIM() ).andThrow( new IOException( "" ) );
        
        /**3 replay*/
        replay( mockFedoraClient );
        

        /** do the stuff */
        try{
        fh = new FedoraHandler(mockFedoraClient); 
        }catch( Exception re ){
            assertTrue( re.getClass() == IOException.class ); 
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );    
    
    }

    
    /***
     * Tests the basic functionality og the submitdatastream
     * Since it calls the constructFoxml method, that method is also tested
     */
    
    @Test public void submitDatastreamTest()throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException {
        
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
    @Test public void submitDatastreamWrongPidReturnedTest() throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException {
        
        /**1 Setting up the needed mocks
         * done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup()
         */
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "wrongPidReturnedTest" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
        expect( mockCargoContainer.getFormat() ).andReturn( "format" );
        // calls from constructFoxml
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );
        // out of constructFoxml
        expect( mockFedoraAPIM.ingest( isA( byte[].class ), isA( String.class ), isA( String.class ) ) ).andReturn( "wrongpid" );
        
        /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );
        
        /** do the stuff */ 
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        try{
            returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
        }catch( Exception ise ){
            assertTrue(ise.getClass() == IllegalStateException.class);
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIM ); 
        verify( mockCargoContainer );

    } 
    
    /**
     * Test that submitDatastream throws the correct exception when its call to
     * apim.getNextPID returns a RemoteException
     */
    @Test public void SubmitDatastreamRemoteExceptionFromGetNextPIDTest() throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException {
        
        /**1 Setting up the needed mocks
         * done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup()
         */
        
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "getNextPIDRemoteExceptionThrownTest" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andThrow( new RemoteException("") );
        
        
        /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );
        
        /** do the stuff */ 
        fh = new FedoraHandler(mockFedoraClient); 
        try{
            returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
        }catch( Exception re ){
            assertTrue(re.getClass() == RemoteException.class);
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIM );
        verify( mockCargoContainer );        
        
    }  
    
    /**
     * Test that submitDatastream and constructFoxml propagtes the IOException 
     * on when construtFoxml gets an exception from cargoContainer.getDataBytes()
     * 
     * \todo: decide whether I have to test the propagation of the 
     * nullpointerException as well?
     */
    @Test public void submitDatastreamIOException()throws IOException, ServiceException, RemoteException, ConfigurationException, UnknownHostException, XMLStreamException {

        /**1 Setting up the needed mocks
         * done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup()
         */
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "IOExceptionPropagationTest" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
        expect( mockCargoContainer.getFormat() ).andReturn( "format" );
        // calls from constructFoxml
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
        expect( mockCargoContainer.getDataBytes() ).andThrow( new IOException( "" ) );
        // out of constructFoxml
               
        /**3 replay */
        replay( mockFedoraClient );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );
        
        /** do the stuff */ 
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        try{
            returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
        }catch( Exception ise ){
            assertTrue(ise.getClass() == IOException.class);
        }
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        verify( mockFedoraAPIM ); 
        verify( mockCargoContainer );
    }  
   //public void getDatastreamTest(){}

}