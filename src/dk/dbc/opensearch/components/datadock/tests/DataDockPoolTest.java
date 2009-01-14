package dk.dbc.opensearch.components.datadock.tests;
/** \brief UnitTest for DataDockPool */

import dk.dbc.opensearch.components.datadock.DataDockPool;

import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.common.types.CargoContainer;

import org.junit.*;
import static org.junit.Assert.*;
//import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import java.util.concurrent.*;

// import org.apache.axis.types.NonNegativeInteger;

// import fedora.client.FedoraClient;
// import fedora.common.Constants;
// import fedora.server.access.FedoraAPIA;
// import fedora.server.management.FedoraAPIM;
// import fedora.server.types.gen.DatastreamDef;
// import fedora.server.types.gen.MIMETypedStream;

// import java.io.InputStream;
// import java.io.ByteArrayInputStream;
// import java.io.ByteArrayOutputStream;

// import java.net.MalformedURLException;
// import javax.xml.rpc.ServiceException;
// import java.io.IOException;
// import org.apache.commons.configuration.ConfigurationException;
// import java.rmi.RemoteException;
// import java.net.UnknownHostException;  
// import javax.xml.stream.XMLStreamException;
// import java.io.UnsupportedEncodingException;

public class DataDockPoolTest {
    
    /**
     * The (mock)objects we need for the most of the tests
     */
    FedoraHandler mockFedoraHandler; 
    Estimate mockEstimate;
    Processqueue mockProcessqueue;
    CargoContainer mockCargoContainer;
    FutureTask mockFutureTask;
    
    DataDockPool DDP;
    
        
    /**
     * Before each test we construct the needed mockobjects 
     * the FedoraClient to pas to the FedoraHandler
     * The FedoraAPIA and M to call methods on
     * The CargoContainer to get data from 
     */
    @Before public void Setup(){
        mockFedoraHandler = createMock( FedoraHandler.class );
        mockEstimate = createMock( Estimate.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockCargoContainer = createMock( CargoContainer.class );
        mockFutureTask = createMock( FutureTask.class );

    }
    
    /**
     * After each test the mock are reset
     */

    @After public void TearDown(){
        reset( mockFedoraHandler );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockCargoContainer );
                
    }

    /**
     * Tests the construction with the correct arguments
     */    

    @Test public void constructorTest(){
         
        /**1 setting up the needed mocks 
         * Is done in setup()
         */
        
        /**2 the expectations 
         * none
         */
        
        /**3 replay
         * Nothing to replay
         */       

        /** do the stuff */
        DDP = new DataDockPool( 10, mockEstimate, mockProcessqueue, mockFedoraHandler); 
        
        
        /**4 check if it happened as expected 
         * Nothing to check
         */  
                
    }
    
    /***
     * Tests that construction throws an IllegalArgumentException when
     * the first argument is < 0
     */
    
    @Test public void illegalArgumentConstructionTest(){
        
        boolean gotException = false;
        /**1 Setting up the needed mocks
         * Done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup
         */

        /**3 replay */

        /** do the stuff */ 
        try{
            DDP =new DataDockPool( -1, mockEstimate, mockProcessqueue, mockFedoraHandler );        
        }catch( Exception iae ){
            assertTrue(  IllegalArgumentException.class ==  iae.getClass() );
            gotException = true;
        }
        assertTrue( gotException );
        /**4 check if it happened as expected */  
        
     }

}