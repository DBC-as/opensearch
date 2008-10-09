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
        // do the stuff
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        //verify( mockFedoraAPIA );
        //verify( mockFedoraAPIM );
        
    }
    
    @Test public void submitDatastreamTest()throws ServiceException, RemoteException, ConfigurationException, UnknownHostException, IOException, XMLStreamException {
        
        /**1 Setting up the needed mocks */
        FedoraClient mockFedoraClient = createMock( FedoraClient.class );
        FedoraAPIA mockFedoraAPIA = createMock( FedoraAPIA.class );
        FedoraAPIM mockFedoraAPIM = createMock( FedoraAPIM.class );
        //String[] mockPids = createMock( java.lang.String[].class );
        CargoContainer mockCargoContainer = createMock( CargoContainer.class );
        //setting up other needed objects
        String[] pids = new String[1];    
        pids[0] = "test:1";
        String testString = "æøå";
        byte[] data = testString.getBytes( "UTF-8" );
        
        
        /**2 the expectations */
        expect( mockFedoraClient.getAPIA() ).andReturn( mockFedoraAPIA );
        expect( mockFedoraClient.getAPIM() ).andReturn( mockFedoraAPIM );
        expect( mockCargoContainer.getSubmitter() ).andReturn ( "test" ); 
        expect( mockFedoraAPIM.getNextPID( isA( NonNegativeInteger.class ), isA( String.class ) ) ).andReturn( pids );
        //expect( mockPids[0] ).andReturn( "test:1" );
        expect( mockCargoContainer.getFormat() ).andReturn( "format" );
        //the constructFoxml is being called
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getMimeType() ).andReturn( "text/xml" );
        expect( mockCargoContainer.getStreamLength() ).andReturn( 6 );
        expect( mockCargoContainer.getDataBytes() ).andReturn( data );

        expect( mockFedoraAPIM.ingest( isA( byte[].class ), isA( String.class ), isA( String.class ) ) ).andReturn( "test:1" );


        /**3 replay */
        replay( mockFedoraClient );
        //replay( mockFedoraAPIA );
        replay( mockFedoraAPIM );
        replay( mockCargoContainer );
        //replay( mockPids );//Vararg type, der giver warning

        // do the stuff
        FedoraHandler fh = new FedoraHandler(mockFedoraClient); 
        fh.submitDatastream( mockCargoContainer, "mockLabel" );
        
        /**4 check if it happened as expected */  
        verify( mockFedoraClient );
        //verify( mockFedoraAPIA );
        verify( mockFedoraAPIM );   
        //verify( mockPids ); //Vararg type, der giver warning
    }
    
    //public void getDatastreamTest(){}
    
}