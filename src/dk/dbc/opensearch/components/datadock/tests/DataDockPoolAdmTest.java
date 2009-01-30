package dk.dbc.opensearch.components.datadock.tests;
/** \brief UnitTest for DataDockPool */

import dk.dbc.opensearch.components.datadock.DataDockPoolAdm;
import dk.dbc.opensearch.components.datadock.DataDockPool;
import dk.dbc.opensearch.common.types.CargoContainer;

import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;

import java.io.*;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.lang.ClassNotFoundException; 
import java.lang.IllegalArgumentException;
import java.lang.NoSuchMethodException;
import java.net.MalformedURLException;
import java.net.UnknownHostException;  
import java.util.concurrent.*;

import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.junit.*;
import static org.junit.Assert.*;
import static org.easymock.classextension.EasyMock.*;



public class DataDockPoolAdmTest 
{    
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
    @Before public void Setup()
    {
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

    @After public void TearDown()
    {
        reset( mockFedoraHandler );
        reset( mockEstimate );
        reset( mockProcessqueue );
        reset( mockCargoContainer );
        reset( mockFutureTask );
        reset( mockDDP );
                
    }
    
  
    /**
     * Tests the construction with the correct arguments
     */    
    @Test public void constructorTest() throws ClassNotFoundException, ConfigurationException, MalformedURLException, UnknownHostException, ServiceException, IOException 
    {         
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

    /**
    * Tests the overall functionality of the DataDockPoolAdm by calling the 
    * privateStart method that invokes the readFiles, createFutureTaskList and 
    * 
    */
    @Ignore @Test public void privateStartTest() throws  ClassNotFoundException, ConfigurationException, MalformedURLException, UnknownHostException, ServiceException, IOException, InterruptedException, ExecutionException 
    {        
        /**1 setting up the needed mocks 
         * Is done in setup()
         */
        
        /**2 the expectations 
         */
        expect( mockDDP.createAndJoinThread( isA( CargoContainer.class ) ) ).andReturn( mockFutureTask ).times( 3 );
        expect( mockFutureTask.isDone() ).andReturn( false ).times( 2 );
        expect( mockFutureTask.isDone() ).andReturn( true );
        expect( mockFutureTask.get() ).andReturn( 2l );
        expect( mockFutureTask.isDone() ).andReturn( false ).times( 4 );
        expect( mockFutureTask.isDone() ).andReturn( true );
        expect( mockFutureTask.get() ).andReturn( 4l );
        expect( mockFutureTask.isDone() ).andReturn( false ).times( 6 );
        expect( mockFutureTask.isDone() ).andReturn( true );
        expect( mockFutureTask.get() ).andReturn( 6l );

        /**3 replay
         */       
        replay( mockDDP );
        replay( mockFutureTask );

        /** do the stuff */
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler ); 
        Method method;
        Class[] argClasses = new Class[]{ DataDockPool.class , String.class , String.class, String.class, String.class, String.class };
        Object[] args = new Object[]{ mockDDP, "text/xml", "dan", "dbc", "test", "testdir/datadockpooladmtestdir/" };
        try
        {
            method = DDPA.getClass().getDeclaredMethod("privateStart", argClasses);
            method.setAccessible( true );
            method.invoke( DDPA, args);
        }
        catch( IllegalAccessException iae )
        {
            Assert.fail( String.format( "IllegalAccessException accessing privateStart" ) );
        }
        catch( InvocationTargetException ite )
        {            
            //   assertTrue( ite.getTargetException().getClass() == IllegalArgumentException.class );
            //assertTrue( ite.getTargetException().getMessage().startsWith( "the filepath: 'notValid'" )  );
            Assert.fail( String.format( "privateStart threw an unexpected exception. the type is: '%s' ", ite.getTargetException().getClass() ) );
        }
        catch( NoSuchMethodException nsme )
        {
            Assert.fail( String.format( "No method called privateStart" ) );
        } 
        
        /**4 check if it happened as expected
         */  
        verify( mockDDP );
        verify( mockFutureTask );
                
    }
    
    /***
     * Tests that an IllegalArgumentException is thrown when the filepath given 
     * to the DataDaockPoolAdm is neither a directory, a file or ends with the 
     * string "*.xml" meaning that all .xml files in the specified dir shold 
     * be taken
     */
    @Test public void noFilesOnFilepathTest()throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException {
        
        /**1 Setting up the needed mocks
         * Most done in setup()
         */
        
        /**2 the expectations 
         * partly done in setup
         */

        /**3 replay */

        /** do the stuff */ 
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler );
        Method method;
        Class[] argClasses = new Class[]{ DataDockPool.class , String.class , String.class, String.class, String.class, String.class };
        Object[] args = new Object[]{ mockDDP, "text/xml", "dan", "dbc", "test", "notValid" };
        try
        {
            method = DDPA.getClass().getDeclaredMethod("privateStart", argClasses);
            method.setAccessible( true );
            method.invoke( DDPA, args);
        }
        catch( IllegalAccessException iae )
        {
            Assert.fail( String.format( "IllegalAccessException accessing privateStart" ) );
        }
        catch( InvocationTargetException ite )
        {            
            assertTrue( ite.getTargetException().getClass() == IllegalArgumentException.class );
            assertTrue( ite.getTargetException().getMessage().startsWith( "the filepath: 'notValid'" )  );
        }
        catch( NoSuchMethodException nsme )
        {
            Assert.fail( String.format( "No method called privateStart" ) );
        }            
        
        /**4 check if it happened as expected */  
        
    }
    /**
     * Tests that the createFutureTaskList throws an illegalArgumentException 
     * if given an empty filelist
     */
    @Test public void emptyFileListTest()throws ConfigurationException, ClassNotFoundException, MalformedURLException, UnknownHostException, ServiceException, IOException, NoSuchMethodException, IllegalAccessException {
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler );
        Method method;
        Class[] argClasses = new Class[]{ File[].class };
        Object[] args = new Object[]{ null };
        try{
            method = DDPA.getClass().getDeclaredMethod("createFutureTaskList", argClasses);
            method.setAccessible( true );
            method.invoke( DDPA, args);
        }catch( InvocationTargetException ite ){
            assertTrue( ite.getTargetException().getClass() == IllegalArgumentException.class ); 
            assertTrue( ite.getTargetException().getMessage().startsWith( "no files on specified path:" ) );
        }
    }
    /**
     * Tests that checkThreads throws an exception when the get() method
     * on a FutureTask throws an ExecutionException and that this is a 
     * RuntimeException
     */
    @Ignore @Test public void getMethodExecutionException() throws InterruptedException, ConfigurationException, ExecutionException, ClassNotFoundException, MalformedURLException, ServiceException, IOException, UnknownHostException {
 
        /**1 setting up the needed mocks 
         * Is done in setup()
         */
        boolean gotException = false;
        String testExceptionString = "testException";
        IllegalArgumentException throwniae = new IllegalArgumentException( testExceptionString );
        ExecutionException thrownee = new ExecutionException( throwniae );
        
        /**2 the expectations 
         */
        expect( mockDDP.createAndJoinThread( isA( CargoContainer.class ) ) ).andReturn( mockFutureTask ).times( 3 );
        expect( mockFutureTask.isDone() ).andReturn( true );
        expect( mockFutureTask.get() ).andThrow( thrownee );

        /**3 replay
         */       
        replay( mockDDP );
        replay( mockFutureTask );

        /** do the stuff */
        DDPA = new DataDockPoolAdm( mockEstimate, mockProcessqueue, mockFedoraHandler ); 
        Method method;
        Class[] argClasses = new Class[]{ DataDockPool.class , String.class , String.class, String.class, String.class, String.class };
        Object[] args = new Object[]{ mockDDP, "text/xml", "dan", "dbc", "test", "testdir/datadockpooladmtestdir/" };
        try{
            method = DDPA.getClass().getDeclaredMethod("privateStart", argClasses);
            method.setAccessible( true );
            method.invoke( DDPA, args); 
        }catch( IllegalAccessException iae ){
            Assert.fail( String.format( "IllegalAccessException accessing privateStart" ) );
        }catch( InvocationTargetException ite ){
            assertTrue( ite.getTargetException().getClass() == RuntimeException.class );
            assertTrue( ite.getTargetException().getCause().getClass() == IllegalArgumentException.class );
            assertTrue( ite.getTargetException().getCause().getMessage().equals( testExceptionString ) );            
            gotException = true;
        }catch( NoSuchMethodException nsme ){
            Assert.fail( String.format( "No method called privateStart" ) );
        } 
        
        /**4 check if it happened as expected
         */  
        assertTrue( gotException );
        verify( mockDDP );
        verify( mockFutureTask );
        
    }
}