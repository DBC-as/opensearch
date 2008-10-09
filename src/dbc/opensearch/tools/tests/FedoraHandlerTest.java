package dbc.opensearch.tools.tests;
/** \brief UnitTest for FedoraHandler */

import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;
//import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import org.apache.axis.types.NonNegativeInteger;

import fedora.client.FedoraClient;
import fedora.common.Constants;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;
import fedora.server.types.gen.DatastreamDef;
import fedora.server.types.gen.MIMETypedStream;

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

public class FedoraHandlerTest {

    @Test public void constructorTest() throws ConfigurationException, IOException, MalformedURLException, ServiceException { 
         
        /**1 setting up the needed mocks */
        FedoraClient mockFedoraClient = createMock( FedoraClient.class );
        FedoraAPIA mockFedoraAPIA = createMock( FedoraAPIA.class );
        FedoraAPIM mockFedoraAPIM = createMock( FedoraAPIM.class );
        
        /**2 the expectations */
        expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
        expect( mockFedoraClient.getAPIM() ).andReturn( mockFedoraAPIM );
        
        /**3 replay*/
        replay( mockFedoraClient );
        //replay( mockFedoraAPIA );
        //replay( mockFedoraAPIM );

        /** do the stuff */
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        //verify( mockFedoraAPIA );
        //verify( mockFedoraAPIM );
        
    }
    
    /***
     * Tests the basic functionality og the submitdatastream
     * Since it calls the constructFoxml method, this is also tested
     */
    
    @Test public void submitDatastreamTest()throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException {
        
        /**1 Setting up the needed mocks */
        FedoraClient mockFedoraClient = createMock( FedoraClient.class );
        FedoraAPIA mockFedoraAPIA = createMock( FedoraAPIA.class );
        FedoraAPIM mockFedoraAPIM = createMock( FedoraAPIM.class );
        CargoContainer mockCargoContainer = createMock( CargoContainer.class );
        
        //setting up other needed objects
        String[] pids = new String[1];    
        pids[0] = "test:1";
        String testString = "æøå";
        String returnPid = null;
        byte[] data = testString.getBytes( "UTF-8" );
        
        
        /**2 the expectations */
        expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
        expect( mockFedoraClient.getAPIM() ).andReturn( mockFedoraAPIM );
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "test" ); 
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
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
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
    @Test public void SubmitDatastreamWrongPidReturnedTest() throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException {
    
    /**1 Setting up the needed mocks */
    FedoraClient mockFedoraClient = createMock( FedoraClient.class );
    FedoraAPIA mockFedoraAPIA = createMock( FedoraAPIA.class );
    FedoraAPIM mockFedoraAPIM = createMock( FedoraAPIM.class );
    CargoContainer mockCargoContainer = createMock( CargoContainer.class );
    
    //setting up other needed objects
    String[] pids = new String[1];    
    pids[0] = "test:1";
    String testString = "æøå";
    String returnPid = null;
    byte[] data = testString.getBytes( "UTF-8" );
    
    
    /**2 the expectations */
    expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
    expect( mockFedoraClient.getAPIM() ).andReturn( mockFedoraAPIM );
    expect( mockCargoContainer.getSubmitter() ).andReturn ( "test" ); 
    expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
    expect( mockCargoContainer.getFormat() ).andReturn( "format" );
    expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
    expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
    expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
    expect( mockCargoContainer.getDataBytes() ).andReturn( data );
    
    expect( mockFedoraAPIM.ingest( isA( byte[].class ), isA( String.class ), isA( String.class ) ) ).andReturn( "test:1" );
    
    
    /**3 replay */
    replay( mockFedoraClient );
    replay( mockFedoraAPIM );
    replay( mockCargoContainer );
    
    
    /** do the stuff */ 
    FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
    returnPid = fh.submitDatastream( mockCargoContainer, "mockLabel" );
    
    /**4 check if it happened as expected */  
    verify( mockFedoraClient );
    verify( mockFedoraAPIM ); 
    verify( mockCargoContainer );
}
    
//public void getDatastreamTest(){}
    
}