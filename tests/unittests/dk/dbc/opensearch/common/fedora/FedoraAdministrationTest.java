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


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import dk.dbc.opensearch.common.helpers.XMLUtils;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import java.rmi.RemoteException;
import java.text.ParseException;
import java.sql.SQLException;
import java.net.MalformedURLException;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.rpc.ServiceException;

import org.apache.commons.configuration.ConfigurationException;
import org.exolab.castor.xml.MarshalException;
import org.exolab.castor.xml.ValidationException;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
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
 * This class tests the FedoraAdministration class
 * It starts out with testing the private methods used by the 
 * public once, so that they can be mocked and not tested everytime 
 * a public method uses them 
 */
public class FedoraAdministrationTest
{
    FedoraAdministration fa;
    CargoContainer mockCC;
    MIMETypedStream mockMTStream;
    CargoObject mockCargoObject;
    static FedoraAPIA mockFea = createMock( FedoraAPIA.class );
    static FedoraAPIM mockFem = createMock( FedoraAPIM.class );
    static Element mockElement = createMock( Element.class );
    static FedoraClient mockFedoraClient = createMock( FedoraClient.class);
    static NodeList mockNodeList = createMock( NodeList.class );
    static byte[] bytes = "bytes".getBytes();

    /**
     * MockClasses
     */
    @MockClass( realClass = PIDManager.class )
    public static class MockPIDManager
    {
        @Mock public static String getNextPID( String prefix )
        {
            return "test:1";
        }
    }
    
    @MockClass( realClass = FedoraHandle.class )
    public static class MockFedoraHandle
    {

        @Mock public void $init()
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
        @Mock public static FedoraClient getFC()
        {
            return mockFedoraClient;
        }
    }

    @MockClass( realClass = FedoraTools.class )
    public static class MockFedoraTools
    {
        @Mock public byte[] constructFoxml( CargoContainer cargo, String nextPid, String label )
        {
            //   String byteString = "bytes";
            //byte[] bytes = byteString.getBytes();
            return bytes;
        }
    }

    
    @MockClass( realClass = XMLUtils.class )
    public static class MockXMLUtils
    {
        @Mock public static Element getDocumentElement( InputSource is)
        {
            return mockElement;
        }

    }

    @MockClass( realClass = FedoraAdministration.class )
    public static class MockFedoraAdministration
    {
        @Mock public static Element getAdminStream( String pid )
        { 
            return mockElement;
        }
        
        @Mock public static String getIndexingAlias( Element adminStream )
        {
            return "article";
        }

        @Mock public static NodeList getStreamNodes( Element adminStream )
        {
            return mockNodeList;
        }

        @Mock public static String createFedoraResource( CargoObject cargo )
        {
            return "dsLocation";
        }
    }

    
    /**
     *setup
     */
    @Before public void SetUp() 
    {
        mockCC = createMock( CargoContainer.class );
        mockMTStream = createMock( MIMETypedStream.class );
        mockCargoObject = createMock( CargoObject.class );
    }

    
    /**
     *teardown
     */
    @After public void TearDown()
    {
        Mockit.tearDownMocks();
        reset( mockCC );
        reset( mockFem );
        reset( mockFea );
        reset( mockElement );
        reset( mockNodeList );
        reset( mockMTStream );
        reset( mockCargoObject );

        fa = null;
    }
    
    /**
     * Testing private helper methods so that they wont have to be tested again 
     * and again
     */
    /**
     * Testing the happy path of the getAdminStream method
     */

    @Test public void testGetAdminStreamHappy() throws IOException, ParserConfigurationException, RemoteException, ServiceException, SAXException, ConfigurationException, NoSuchMethodException, IllegalAccessException
    {
        //setup
        Mockit.setUpMocks( MockFedoraHandle.class );
        Mockit.setUpMocks( MockXMLUtils.class );
        String byteString = "admindata";
        byte[] bytearraystring = byteString.getBytes();

        String pid = "pid";
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ pid };
        Element result;
        //expectations
        expect( mockFea.getDatastreamDissemination( "pid", "adminData" , null ) ).andReturn( mockMTStream );
        expect( mockMTStream.getStream() ).andReturn( bytearraystring );
        //replay
        replay( mockElement );
        replay( mockMTStream );
        replay( mockFea );
        //do stuff
        fa = new FedoraAdministration();
        try
        {
            method = fa.getClass().getDeclaredMethod( "getAdminStream", argClasses );
            method.setAccessible( true );
            result = (Element)method.invoke( fa, args );
        }
        catch( InvocationTargetException ite )
        {
            Assert.fail();
        }
        //verify
        verify( mockElement );
        verify( mockMTStream );
        verify( mockFea );
    }

    /**
     * Testing the throwing of the IllegalStateException
     */
    @Test (expected=IllegalStateException.class)
    public void testGetAdminStreamIllegalState() throws Exception
    {
     //setup
        Mockit.setUpMocks( MockFedoraHandle.class );
        //Mockit.setUpMocks( MockFedoraConfig.class );
        Mockit.setUpMocks( MockXMLUtils.class );
        String byteString = "admindata";
        byte[] bytearraystring = byteString.getBytes();

        String pid = "pid";
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ pid };
        Element result;
        //expectations
        expect( mockFea.getDatastreamDissemination( "pid", "adminData" , null ) ).andReturn( mockMTStream );
        expect( mockMTStream.getStream() ).andReturn( null );
        //replay
        replay( mockElement );
        replay( mockMTStream );
        replay( mockFea );
        //do stuff
        fa = new FedoraAdministration();
        try
        {
            method = fa.getClass().getDeclaredMethod( "getAdminStream", argClasses );
            method.setAccessible( true );
            result = (Element)method.invoke( fa, args );
        }
        catch( InvocationTargetException ite )
        {
            //check the class of the exception...
            if( ite.getCause().getClass().equals( IllegalStateException.class ) )
            {
                //rethrow to conform with the test specification
                throw new IllegalStateException( ite.getCause() );
            }
        }
    }

    /**
     * Testing the throwing of the IOException in getAdminStreamMethod
     */ 
    @Test
    public void testGetAdminStreamIOExp() throws IOException, ParserConfigurationException, RemoteException, ServiceException, SAXException, ConfigurationException, NoSuchMethodException, IllegalAccessException
    {
     //setup
        boolean illegalCaught = false;
        Mockit.setUpMocks( MockFedoraHandle.class );
        //Mockit.setUpMocks( MockFedoraConfig.class );
        Mockit.setUpMocks( MockXMLUtils.class );
        String byteString = "admindata";
        byte[] bytearraystring = byteString.getBytes();

        String pid = "pid";
        Method method;
        Class[] argClasses = new Class[]{ String.class };
        Object[] args = new Object[]{ pid };
        Element result;
        //expectations
        expect( mockFea.getDatastreamDissemination( "pid", "adminData" , null ) ).andThrow( new RemoteException( "test" ) );
        //        expect( mockMTStream.getStream() ).andReturn( null );
        //replay
        replay( mockElement );
        replay( mockMTStream );
        replay( mockFea );
        //do stuff
        fa = new FedoraAdministration();
        try
        {
            method = fa.getClass().getDeclaredMethod( "getAdminStream", argClasses );
            method.setAccessible( true );
            result = (Element)method.invoke( fa, args );
        }
        catch( InvocationTargetException ite )
        {
            //check the class of the exception...
            if( ite.getCause().getClass().equals( IOException.class ) )
            {
                illegalCaught = true;
            }
        }
        assertTrue( illegalCaught );
        //verify
        verify( mockElement );
        verify( mockMTStream );
        verify( mockFea );
    }

    /**
     * Testing the getIndexingAlias methods happypath
     */
    @Test public void testGetIndexingAlias() throws NoSuchMethodException, IllegalAccessException
    {  
        //setup
        String testString = "test";
        String result = "not equal to test";
        Method method;
        Class[] argClasses = new Class[]{ Element.class };
        Object[] args = new Object[]{ mockElement };

        //expectations
        expect( mockElement.getElementsByTagName( "indexingalias" ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "name" ) ).andReturn( testString );
        
        //replay
        replay( mockElement );
        replay( mockNodeList );        

        //do stuff
        fa = new FedoraAdministration();
        try
        {
            method = fa.getClass().getDeclaredMethod( "getIndexingAlias", argClasses );
            method.setAccessible( true );
            result = (String)method.invoke( fa, args );
        }
        catch( InvocationTargetException ite )
        {
            //ite.getCause().printStackTrace();
            Assert.fail();
            
        }
        assertTrue( result.equals( testString ));

        //verify
        verify( mockElement );
        verify( mockNodeList );

    }


    /**
     * Testing the getIndexingAlias with indexingAliasElem == null
     */ 
    @Test public void testGetIndexingAliasNull() throws NoSuchMethodException, IllegalAccessException
    {  
        //setup
        //String testString = "test";
        boolean correctException = false;
        String result = "not equal to test";
        Method method;
        Class[] argClasses = new Class[]{ Element.class };
        Object[] args = new Object[]{ mockElement };

        //expectations
        expect( mockElement.getElementsByTagName( "indexingalias" ) ).andReturn( null );
               
        //replay
        replay( mockElement );
       
        //do stuff
        fa = new FedoraAdministration();
        try
        {
            method = fa.getClass().getDeclaredMethod( "getIndexingAlias", argClasses );
            method.setAccessible( true );
            result = (String)method.invoke( fa, args );
        }
        catch( InvocationTargetException ite )
        {
            if( ite.getCause().getClass().equals( NullPointerException.class ) )
            {
                correctException = true;
            }
            else
            {
            Assert.fail();
            }
            
        }
        assertTrue( correctException );

        //verify
        verify( mockElement );

    }

    /**
     * Testing getStreamNodes method, so that it can be mocked for 
     * other testcases using it
     */
    @Test public void testGetStreamNodes() throws NoSuchMethodException, IllegalAccessException
    {
        //setup
        NodeList result = null;
        Method method;
        Class[] argClasses = new Class[]{ Element.class };
        Object[] args = new Object[]{ mockElement };

        //expectations
        expect( mockElement.getElementsByTagName( "streams" ) ).andReturn( mockNodeList );
        expect( mockNodeList.item( 0) ).andReturn( mockElement );
        expect( mockElement.getElementsByTagName( "stream" ) ).andReturn( mockNodeList );
               
        //replay
        replay( mockElement );
        replay( mockNodeList );
        //do stuff
        fa = new FedoraAdministration();
        try
        {
                       method = fa.getClass().getDeclaredMethod( "getStreamNodes", argClasses );
                       method.setAccessible( true );
                       result = (NodeList)method.invoke( fa, args );
                   }
        catch( InvocationTargetException ite )
        {
            Assert.fail();
        }
        assertTrue( result == mockNodeList );

        //verify
        verify( mockElement );
        verify( mockNodeList );

    }
    /**
     * Testing createFedoraResource method, so that it can be mocked 
     * for testcases that calls it
     */
    @Test public void testCreateFedoraResource() throws NoSuchMethodException, IllegalAccessException, IOException
    {
      //setup
        Mockit.setUpMocks( MockFedoraHandle.class);
        String byteString = "bytes";
        String returnString = "result";
        byte[] bytes = byteString.getBytes();
        String result = null;
        Method method;
        Class[] argClasses = new Class[]{ CargoObject.class };
        Object[] args = new Object[]{ mockCargoObject };

        //expectations
        expect( mockCargoObject.getId() ).andReturn( 2l );
        //2nd method called
        expect( mockCargoObject.getBytes() ).andReturn( bytes );
        expect( mockFedoraClient.uploadFile( isA( File.class ) ) ).andReturn( returnString );
               
        //replay
        replay( mockCargoObject );
        replay( mockFedoraClient );
        //do stuff
        fa = new FedoraAdministration();
        try
        {
                       method = fa.getClass().getDeclaredMethod( "createFedoraResource", argClasses );
                       method.setAccessible( true );
                       result = (String)method.invoke( fa, args );
                   }
        catch( InvocationTargetException ite )
        {
            Assert.fail();
        }
        assertTrue( result.equals( returnString ) );

        //verify
        verify( mockCargoObject );
        verify( mockFedoraClient );
    }  
    /**
     * Testing the constructor
     */
    /**
     * Testing the happy path of the constructor, the only path.
     */
    
    @Test public void testConstructor()
    {
        fa = new FedoraAdministration();
    }

    /**
     * Testing public methods and mocking the private once.
     */
    /**
     * Testing the deleteObject method
     */
    @Test public void testDeleteObject()throws ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        //nothing to test yet
    }
 
    /**
     * Testing the markObjectAsDeleted method
     */
    @Test public void testMarkObjectAsDeleted()
    {
        //nothing to test yet
    }
 
    /**
     * Tests the happy path of the retrieveCargoContainer method
     */
    @Test
    public void testRetrieveContainer() throws IOException, ParserConfigurationException, RemoteException, SAXException, ConfigurationException, MalformedURLException, ServiceException
    {
        Mockit.setUpMocks( MockFedoraHandle.class );
        Mockit.setUpMocks( MockFedoraAdministration.class );
        Mockit.setUpMocks( MockXMLUtils.class );
        String byteString = "admindata";
        byte[] bytes = byteString.getBytes();

        //expectations
        expect( mockNodeList.getLength()).andReturn( 1 );
        //loop
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "id" ) ).andReturn( "streamID" );
        expect( mockFea.getDatastreamDissemination( "pid", "streamID", null) ).andReturn( mockMTStream );
        //construnting the CargoContainer

        expect( mockElement.getAttribute( "streamNameType" ) ).andReturn( "originalData" );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "test" ).times( 3 );
        expect( mockElement.getAttribute( isA( String.class ) ) ).andReturn( "text/xml" );
        expect( mockMTStream.getStream() ).andReturn( bytes );
        //out of loop

        //replay
        replay( mockElement );
        replay( mockNodeList );
        replay( mockMTStream );
        replay( mockFea );
        
        //do stuff
        fa = new FedoraAdministration();
        CargoContainer cc = fa.retrieveCargoContainer( "pid" );
        assertTrue( cc.getCargoObjectCount() == 1 );

        //verify
        verify( mockElement );
        verify( mockNodeList );
        verify( mockMTStream );
        verify( mockFea );
    }


    /**
     * Testing the happy path of the storeContainer method
     */
    @Test public void testStoreCargoContainer() throws ConfigurationException, java.io.IOException, java.net.MalformedURLException, ServiceException, ClassNotFoundException, MarshalException, ParseException, ParserConfigurationException, RemoteException, SAXException, SQLException, TransformerException, ValidationException, XPathExpressionException
    {
        //setup
        Mockit.setUpMocks( MockFedoraHandle.class );
        Mockit.setUpMocks( MockFedoraTools.class );
        Mockit.setUpMocks( MockPIDManager.class );
        //  String byteString = "bytes";
        String format = "test";
        String logm = String.format( "%s inserted", format);
        String fedMessage = "info:fedora/fedora-system:FOXML-1.1";
        //byte[] bytes = byteString.getBytes();
        

        //expectations
        expect( mockCC.getCargoObjectCount() ).andReturn( 2 );
        mockCC.setDCIdentifier( "test:1" );
        expect( mockFem.ingest( bytes, fedMessage, logm ) ).andReturn( "test:1" ); 
        //replay
        
        replay( mockCC );
        replay( mockFem );

        //do stuff
        fa = new FedoraAdministration();
        String result = fa.storeCargoContainer( mockCC, "test", format );
        assertTrue( result.equals( "test:1" ) );

        //verify
        verify( mockCC );
        verify( mockFem );        
    }

    /**
     * Testing the storeCa the IllegalStateException when there are no 
     * CargoObjects in the CargoContainer
     */
    @Test (expected = IllegalStateException.class)
    public void testEmptyCargoContainerShouldNotBeStored() throws ConfigurationException, IOException, ServiceException, ClassNotFoundException, MarshalException, ParseException, ParserConfigurationException, SAXException, SQLException, TransformerException, ValidationException, XPathExpressionException
    {
        //expectations
        expect( mockCC.getCargoObjectCount() ).andReturn( 0 );
        
        //replay
        replay( mockCC );

        //do stuff
        fa = new FedoraAdministration();
        String result = fa.storeCargoContainer( mockCC, "test", "test" );
        //verify
        verify( mockCC );
    }

    /**
     * Testing the getDataStreamsOfType method
     */
    @Test 
    public void testGetDataStreamsOfType() throws MalformedURLException, IOException, RemoteException, ParserConfigurationException, SAXException, ServiceException, ConfigurationException
    {
        //setup
        Mockit.setUpMocks( MockFedoraAdministration.class );
        Mockit.setUpMocks( MockFedoraHandle.class );
        String pid = "test:1";
        String streamID = "streamID";
        DataStreamType typeOfStream = DataStreamType.OriginalData;
        String typeOfStreamString = typeOfStream.getName();
        CargoContainer cc;

        //expectations
        expect( mockNodeList.getLength() ).andReturn( 2 );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "streamNameType" ) ).andReturn( typeOfStreamString );
        expect( mockElement.getAttribute( "id" ) ).andReturn( streamID );
        expect( mockFea.getDatastreamDissemination( pid, streamID, null ) ).andReturn( mockMTStream );
        expect( mockMTStream.getStream() ).andReturn( bytes );
        expect( mockElement.getAttribute( isA(String.class ) ) ).andReturn( "string" ).times( 3 );
        expect( mockElement.getAttribute( "mimetype" ) ).andReturn( "text/xml" );
        //2nd time in loop
        expect( mockNodeList.item( 1 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "streamNameType" ) ).andReturn( "hat" );

        //replay
        replay( mockElement );
        replay( mockNodeList );
        replay( mockMTStream );
        replay( mockFea );

        //do stuff
        fa = new FedoraAdministration();
        cc = fa.getDataStreamsOfType( pid, typeOfStream );
        assertTrue( cc.getCargoObjectCount() == 1 );

        //verify
        verify( mockElement );
        verify( mockNodeList );
        verify( mockMTStream );
        verify( mockFea );
    }

    /**
     * Testing the getDataStream method
     */
    @Test
    public void testGetDataStream() throws MalformedURLException, IOException, RemoteException, ServiceException, ParserConfigurationException, SAXException, ConfigurationException
    {
        //setup
        Mockit.setUpMocks( MockFedoraAdministration.class );
        Mockit.setUpMocks( MockFedoraHandle.class );
        String streamID = "streamID";
        String pid = "test:1";
        CargoContainer cc;

        //expectations
        expect( mockNodeList.getLength() ).andReturn( 2 );
        expect( mockNodeList.item( 0 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "id" ) ).andReturn( streamID );
expect( mockFea.getDatastreamDissemination( pid, streamID, null ) ).andReturn( mockMTStream );
        expect( mockMTStream.getStream() ).andReturn( bytes );
        expect( mockElement.getAttribute( "streamNameType" ) ).andReturn( "originalData" );
        expect( mockElement.getAttribute( isA(String.class ) ) ).andReturn( "string" ).times( 3 );
        expect( mockElement.getAttribute( "mimetype" ) ).andReturn( "text/xml" );        
        //2nd time in loop
        expect( mockNodeList.item( 1 ) ).andReturn( mockElement );
        expect( mockElement.getAttribute( "id" ) ).andReturn( "hat" );

        //replay
        replay( mockElement );
        replay( mockNodeList );
        replay( mockMTStream );
        replay( mockFea );

        //do stuff
        fa = new FedoraAdministration();
        cc = fa.getDataStream( pid, streamID );
        assertTrue( cc.getCargoObjectCount() == 1 );

        //verify
        verify( mockElement );
        verify( mockNodeList );
        verify( mockMTStream );
        verify( mockFea );
    }
}