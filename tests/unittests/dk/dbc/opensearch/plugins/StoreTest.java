/**
 * \file StoreTest.java
 * \brief The StoreTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins;


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

import java.util.HashMap;
import java.util.List;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.commons.javascript.SimpleRhinoWrapper;
import dk.dbc.opensearch.fedora.IObjectRepository;
import dk.dbc.opensearch.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.fedora.PID;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.IObjectIdentifier;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import org.w3c.dom.Document;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

/** \brief UnitTest for Store
 *
 */
public class StoreTest
{

    Store storePlugin;
    PluginType PT = PluginType.STORE;
    CargoContainer cargo = new CargoContainer();
    String testString = "testStringUsedToGenerateBytes";
    byte[] dataBytes = testString.getBytes();
    IObjectIdentifier objectIdentifier;
    @Mocked IObjectRepository mockedRepository;
    @Mocked Map<String, String> mockArgsMap; 

    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockFedoraObjectRepository
    {
        @Mock public void $init()
        {
        }

        @Mock
        public static boolean hasObject( IObjectIdentifier objectIdentifier )
        {
            return false;
        }

        @Mock( invocations = 1 )
        public static String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }

    } 


    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockFedoraObjectRepositoryMarkDeleted
    {
        @Mock public void $init()
        {
        }

        @Mock
        public static boolean hasObject( IObjectIdentifier objectIdentifier )
        {
            return true;
        }

        @Mock( invocations = 1 )
        public void deleteObject( String objectIdentifier, String label, String ownerId, String logMessage )
        {
        }
    }

    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockFedoraObjectRepositoryMarkDeletedException
    {
        @Mock public void $init()
        {
        }

        @Mock
        public static boolean hasObject( IObjectIdentifier objectIdentifier )
        {
            return true;
        }

        @Mock( invocations = 0 )
        public static String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }

        @Mock( invocations = 1 )
        public void deleteObject( String objectIdentifier, String label, String ownerId, String logMessage ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }
    }


    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockFedoraObjectRepositoryHasObject
    {
        @Mock public void $init()
        {
        }

        @Mock
        public static boolean hasObject( IObjectIdentifier objectIdentifier )
        {
            return true;
        }

        @Mock
        public static void purgeObject( String identifier, String logmessage)
        {
        }
        
        @Mock 
        public static void removeInboundRelations( String objectIdentifier )
        {
        }

        @Mock( invocations = 1 )
        public static String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }

    } 

    @MockClass( realClass = FedoraObjectRepository.class )
    public static class MockFedoraObjectRepositoryException
    {
        @Mock public void $init()
        {
        }

        @Mock 
        public static boolean hasObject( IObjectIdentifier objectIdentifier ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }

        @Mock
        public static String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }

    }

    /**
     * mocks the constructor called in the environment
     */
    @MockClass( realClass = SimpleRhinoWrapper.class )
    public static class MockSimpleRhinoWrapper
    {
        @Mock
        public void $init( String jsFileName, List< Pair< String, Object > > objectList )
        {}

        @Mock
        public Object run( String functionEntryPoint, Object... args )
        {
            return Boolean.TRUE;
        }
        @Mock
        public boolean validateJavascriptFunction( String functionEntryPoint )
        {
            return true;
        }
    }

    @Before
    public void setUp() throws Exception
    {
        objectIdentifier = new PID( "dbc:111" );

        cargo.add( DataStreamType.OriginalData,
                   "testFormat",
                   "dbc",
                   "da",
                   "text/xml",
                   dataBytes );
        //     cargo.setIndexingAlias( "danmarcxchange", DataStreamType.OriginalData );
    }

    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
    }

    /**
     *
     */
    @Test
    public void getPluginTypeTest() throws Exception
    {
        storePlugin = new Store( mockedRepository );
        assertTrue( PT == storePlugin.getPluginType() );
    }
 
    /**
     * testing the path through the plugin where there is no object in the
     * repository with the same identifier
     */

    @Test
    public void storeCargoContainerHappyPathTest() throws Exception
    {
        setUpMocks( MockFedoraObjectRepository.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        cargo.setIdentifier( objectIdentifier );        
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );

	IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, mockArgsMap );
	returnCargo = storePlugin.runPlugin( env, cargo );
        assertEquals( returnCargo.getIdentifierAsString(), cargo.getIdentifierAsString() );

    }

    /**
     * tests the happy path where there is an object in the repository
     * that has the same identifier
     */
    @Test
    public void storeCargoContainerHappyPathDeleteTest() throws Exception
    {
        setUpMocks( MockFedoraObjectRepositoryHasObject.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        cargo.setIdentifier( objectIdentifier );        
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );

	IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, mockArgsMap );
	returnCargo = storePlugin.runPlugin( env, cargo );
        assertEquals( returnCargo.getIdentifierAsString(), cargo.getIdentifierAsString() );
    }


    /**
     * tests that no object is being tried purged from the repository when 
     * the cargoContainer have no indentifier 
     */
    @Test 
    public void storeCargoContainerHappyPathNoIdentifierTest() throws Exception
    {
         setUpMocks( MockFedoraObjectRepository.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        //cargo.setIdentifier( null );        
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );

	IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, mockArgsMap );
	returnCargo = storePlugin.runPlugin( env, cargo );
       
        assertEquals( returnCargo.getIdentifierAsString(), "" );
        
    }

    /**
     * tests the handling of the ObjectRepositoryException
     */
    @Test( expected = ObjectRepositoryException.class )
    public void testObjectRepositoryException() throws Throwable
    {
        setUpMocks( MockFedoraObjectRepositoryException.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );

        try
        {
	    IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, mockArgsMap );
	    returnCargo = storePlugin.runPlugin( env, cargo );
        }
        catch( PluginException pe )
        {
            throw pe.getCause();
        }
    }

    /**
     * testing the path through the plugin when javascript returns true.
     */
    @Ignore //we dont store objects marked for deletion
    @Test
    public void storeCargoContainerMarkDeleted() throws Exception
    {
        setUpMocks( MockSimpleRhinoWrapper.class, MockFedoraObjectRepositoryMarkDeleted.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        cargo.setIdentifier( objectIdentifier );
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );
        Map< String, String > args = new HashMap< String, String >();
        args.put("javascript", "test");
        args.put("entryfunction", "test");

        IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, args );
        returnCargo = storePlugin.runPlugin( env, cargo );
        assertEquals( returnCargo.getIdentifierAsString(), cargo.getIdentifierAsString() );
    }

    /**
     * tests the handling of PluginException when javascript returns true.
     */
    @Ignore //we dont store objects marked for deletion
    @Test( expected = PluginException.class )
    public void storeCargoContainerMarkDeletedException() throws Exception
    {
        setUpMocks( MockSimpleRhinoWrapper.class, MockFedoraObjectRepositoryMarkDeletedException.class );
        FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
        cargo.setIdentifier( objectIdentifier );
        CargoContainer returnCargo;
        storePlugin = new Store( fedObjRep );
        Map< String, String > args = new HashMap< String, String >();
        args.put("javascript", "test");
        args.put("entryfunction", "test");

        IPluginEnvironment env = storePlugin.createEnvironment( fedObjRep, args );
        returnCargo = storePlugin.runPlugin( env, cargo );
    }
}
