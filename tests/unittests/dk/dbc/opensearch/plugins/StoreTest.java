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

import org.mozilla.javascript.IdFunctionObject;
import dk.dbc.jslib.ModuleHandler;
import dk.dbc.jslib.Environment;
import dk.dbc.opensearch.fedora.FcrepoUtils;
import dk.dbc.opensearch.fedora.FcrepoModifier;
import dk.dbc.opensearch.fedora.FcrepoReader;
import java.util.HashMap;
import dk.dbc.opensearch.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.fedora.PID;
import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.PluginException;
import dk.dbc.opensearch.types.CargoContainer;
import dk.dbc.opensearch.types.IObjectIdentifier;
import dk.dbc.opensearch.types.DataStreamType;

import java.util.Map;

import org.junit.*;
import static org.junit.Assert.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

/** \brief UnitTest for Store
 *
 */
public class StoreTest
{

    Store storePlugin;
    CargoContainer cargo = new CargoContainer();
    String testString = "testStringUsedToGenerateBytes";
    byte[] dataBytes = testString.getBytes();
    IObjectIdentifier objectIdentifier;
    @Mocked Map<String, String> mockArgsMap; 

    @MockClass( realClass = FcrepoReader.class )
    public static class MockReaderHasNoObject
    {
        @Mock public void $init( String host, String port ) 
        {
        }

        @Mock
        public boolean hasObject( String objectIdentifier )
        {
            return false;
        }
    }


    @MockClass( realClass = FcrepoReader.class )
    public static class MockReaderHasObject
    {
        @Mock public void $init( String host, String port )
        {
        }

        @Mock
        public boolean hasObject( String objectIdentifier )
        {
            return true;
        }
    }


    @MockClass( realClass = FcrepoModifier.class )
    public static class MockModifier
    {
        @Mock public void $init( String host, String port, String user, String passwd )
        {
        }

        @Mock
        public String purgeObject( String identifier, String logmessage)
        {
            return "";
        }

        @Mock( invocations = 1 )
        public String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }

    }


    @MockClass( realClass = FcrepoModifier.class )
    public static class MockModifierStore
    {
        @Mock public void $init( String host, String port, String user, String passwd )
        {
        }

        @Mock( invocations = 1 )
        public String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }
    }


    @MockClass( realClass = FcrepoModifier.class )
    public static class MockModifierMarkDeleted
    {
        @Mock public void $init( String host, String port, String user, String passwd ) 
        {
        }

        @Mock( invocations = 1 )
        public void deleteObject( String objectIdentifier, String logMessage )
        {
        }
    }


    @MockClass( realClass = FcrepoModifier.class )
    public static class MockModifierMarkDeletedException
    {
        @Mock public void $init( String host, String port, String user, String passwd ) 
        {
        }

        @Mock( invocations = 0 )
        public String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
        {
            return "stored";
        }

        @Mock( invocations = 1 )
        public void deleteObject( String objectIdentifier, String logMessage ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }
    }


    @MockClass( realClass = FcrepoReader.class )
    public static class MockReaderException
    {
        @Mock public void $init( String host, String port )
        {
        }

        @Mock
        public boolean hasObject( String objectIdentifier ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }
    }


    @MockClass( realClass = FcrepoModifier.class )
    public static class MockModifierException
    {
        @Mock public void $init( String host, String port, String user, String passwd )
        {
        }

        @Mock
        public String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace ) throws ObjectRepositoryException
        {
            throw new ObjectRepositoryException( "test" );
        }
    }


    @MockClass( realClass = FcrepoUtils.class )
    public static class MockUtils
    {
        @Mock( invocations = 1 )
        public static int removeInboundRelations( FcrepoReader reader, FcrepoModifier modifier, String objectIdentifier ) throws ObjectRepositoryException
        {
            return 0;
        }

        @Mock
        public static void removeOutboundRelations( FcrepoReader reader, FcrepoModifier modifier, String objectIdentifier ) throws ObjectRepositoryException
        {
        }
    }


    /**
     * mocks the JavaScript environment
     */
    @MockClass( realClass = Environment.class )
    public static class MockEnvironment
    {
        @Mock
        public void $init()
        {}

        @Mock
        public void registerUseFunction( final ModuleHandler moduleHandler )
        {
        }

        @Mock
        public Object get( final java.lang.String name )
        {
            return new IdFunctionObject( null, null, 0, 0 );
        }

        @Mock
        public void put( final java.lang.String name, final java.lang.Object value )
        {
        }

        @Mock
        public Object evalFile( final String fileName )
        {
            return new Object();
        }

        @Mock
        public Object callMethod( String functionEntryPoint, Object[] args )
        {
            return Boolean.TRUE;
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
     * testing the path through the plugin where there is no object in the
     * repository with the same identifier
     */

    @Test
    public void storeCargoContainerHappyPathTest() throws Exception
    {
        setUpMocks( MockReaderHasNoObject.class, MockModifier.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        cargo.setIdentifier( objectIdentifier );        
        CargoContainer returnCargo;
        storePlugin = new Store();

        IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, mockArgsMap, null );
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
        setUpMocks( MockReaderHasObject.class, MockModifier.class, MockUtils.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        cargo.setIdentifier( objectIdentifier );        
        CargoContainer returnCargo;
        storePlugin = new Store();

        IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, mockArgsMap, null );
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
        setUpMocks( MockReaderHasNoObject.class, MockModifier.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        //cargo.setIdentifier( null );        
        CargoContainer returnCargo;
        storePlugin = new Store();

        IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, mockArgsMap, null );
        returnCargo = storePlugin.runPlugin( env, cargo );

        assertEquals( returnCargo.getIdentifierAsString(), "" );
    }

    /**
     * tests the handling of the ObjectRepositoryException
     */
    @Test( expected = ObjectRepositoryException.class )
    public void testObjectRepositoryException() throws Throwable
    {
        setUpMocks( MockReaderException.class, MockModifierException.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        CargoContainer returnCargo;
        storePlugin = new Store();

        try
        {
            IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, mockArgsMap, null );
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
    @Test
    public void storeCargoContainerMarkDeleted() throws Exception
    {
        setUpMocks( MockEnvironment.class, MockReaderHasObject.class, MockModifierMarkDeleted.class, MockUtils.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        cargo.setIdentifier( objectIdentifier );
        CargoContainer returnCargo;
        storePlugin = new Store();
        Map< String, String > args = new HashMap< String, String >();
        args.put("javascript", "test");
        args.put("entryfunction", "test");

        IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, args, null );
        returnCargo = storePlugin.runPlugin( env, cargo );
        assertEquals( returnCargo.getIdentifierAsString(), cargo.getIdentifierAsString() );
    }

    /**
     * tests the handling of PluginException when javascript returns true.
     */
    @Test( expected = PluginException.class )
    public void storeCargoContainerMarkDeletedException() throws Exception
    {
        setUpMocks( MockEnvironment.class, MockReaderHasNoObject.class, MockModifierMarkDeletedException.class );
        FcrepoReader reader = new FcrepoReader( "Host", "Port" );
        FcrepoModifier modifier = new FcrepoModifier( "Host", "Port", "User", "Password" );
        cargo.setIdentifier( objectIdentifier );
        CargoContainer returnCargo;
        storePlugin = new Store();
        Map< String, String > args = new HashMap< String, String >();
        args.put("javascript", "test");
        args.put("entryfunction", "test");

        IPluginEnvironment env = storePlugin.createEnvironment( reader, modifier, args, null );
        returnCargo = storePlugin.runPlugin( env, cargo );
    }
}
