package dbc.opensearch.components.datadock.tests;
/** \brief UnitTest for DataDockPool */

import dbc.opensearch.components.datadock.DataDockPoolAdm;
import dbc.opensearch.components.datadock.DataDockPool;
import dbc.opensearch.components.datadock.CargoContainer;

import dbc.opensearch.tools.FedoraHandler;
import dbc.opensearch.tools.Estimate;
import dbc.opensearch.tools.Processqueue;
//import dbc.opensearch.tools.PrivateAccessor;


import org.junit.*;
import static org.junit.Assert.*;
//import static org.easymock.EasyMock.*;
import static org.easymock.classextension.EasyMock.*;

import java.util.concurrent.*;
import java.lang.reflect.Method;

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

import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import javax.xml.rpc.ServiceException;
import java.io.IOException;
import org.apache.commons.configuration.ConfigurationException;
import java.lang.ClassNotFoundException; 
// import java.rmi.RemoteException;
import java.net.UnknownHostException;  
// import javax.xml.stream.XMLStreamException;
// import java.io.UnsupportedEncodingException;

public class DataDockPoolAdmTest {
    
    /**
     * The (mock)objects we need for the most of the tests
     */
    FedoraHandler mockFedoraHandler; 
    Estimate mockEstimate;
    Processqueue mockProcessqueue;
    CargoContainer mockCargoContainer;
    FutureTask mockFutureTask;
    DataDockPool mockDDP;
    
    DataDockPoolAdm DDPA;
    
        
    /**
     * Before each test we construct the needed mockobjects 
     *  
     */
    @Before public void Setup(){
        mockFedoraHandler = createMock( FedoraHandler.class );
        mockEstimate = createMock( Estimate.class );
        mockProcessqueue = createMock( Processqueue.class );
        mockCargoContainer = createMock( CargoContainer.class );
        mockFutureTask = createMock( FutureTask.class );
        mockDDP = createMock( DataDockPool.class );

    }
    
    /**
     * After each test the mock are reset
     */

    @After public void TearDown(){
        reset( mockFedoraHandler );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockCargoContainer );
        reset( mockFutureTask );
        reset( mockDDP );
                
    }
    // return = (cast)PrivateAccessor.invokePrivateMethod(object, "method", arguments )
    /**
     * Tests the construction with the correct arguments
     */    

    @Test public void constructorTest() throws ClassNotFoundException, ConfigurationException, MalformedURLException, UnknownHostException, ServiceException, IOException {
         
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
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler ); 
        
        
        /**4 check if it happened as expected 
         * Nothing to check
         */  
                
    }
    
    /***
     * Tests that construction throws an IllegalArgumentException when
     * the first argument is < 0
     */
    
    @Test public void startTest()throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException {
        
       
        /**1 Setting up the needed mocks
         * Most done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup
         */

        /**3 replay */

        /** do the stuff */ 
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler );
        final Method[] methods = DDPA.getClass().getDeclaredMethods();
        for( int i = 0; i < methods.length; i++ ){
            if( "privateStart".equals(methods[i].getName() ) ){
                try{
                    methods[i].setAccessible( true );
                    methods[i].invoke( DDPA, mockDDP, "text/xml", "dan", "dbc", "test", "test" );
                }catch( IllegalAccessException iae ){
                    Assert.fail( String.format( "IllegalAccessException accessing privateStart" ) );
                }catch( InvocationTargetException ite ){
                    Assert.fail( String.format( "InvocationTargetException (the method has thrown an error) accessing privateStart " ) );  
                }
            }
        } 
        //DataDockPoolAdm.privateStart.setAccessible(true);
        //        DDPA.privateStart.setAccesible( true );
        // DDPA.privateStart( mockDDP, "text/xml", "dan", "dbc", "test", "test" );
        /**4 check if it happened as expected */  
        
    }
   //  /**
//      * Tests that the methods cant be called until the DataDockPool 
//      * has been constructed
//      */
//     @Test public void noMethodCallsBeforeInitialisationTest(){
//         DataDockPool DDP;
//         boolean gotException = false;
//         FutureTask future;
//         try{
//             DDP.createAndJoinThread( mockCargoContainer );
//         }catch( Exception iae ){
//             assertTrue( iae.getClass() == IllegalArgumentException.class ); 
//             gotException = true;
//         }
//         assertTrue( gotException );
//     }
}