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

import dk.dbc.opensearch.plugins.Store;
import dk.dbc.opensearch.common.fedora.FedoraObjectRepository;
import dk.dbc.opensearch.common.fedora.PID;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.IndexingAlias;


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

 //    Store storePlugin;
//     PluginType PT = PluignType.STORE;
//     CargoContainer cargo = new CargoContainer();
//     String testString = "testStringUsedToGenerateBytes";
//     byte[] dataBytes = testString.getBytes();
//     ObjectIdentifier objIdentifier = (ObjectIdentifier)new PID( "dbc:111" );

//     cargo.setIdentifier( objIdentifier );

//     cargo.add( DataStreamType.OriginalData,
//                "testFormat",
//                "dbc",
//                "da",
//                "text/xml",
//                IndexingAlias.Danmarcxchange,
//                dataBytes );


//     @MockClass( realClass = FedoraObjectRepository.class )
//     public static class MockFedoraObjectRepository
//     {
//         @Mock public void $init()
//         {
//         }

//         @Mock
//         public static boolean hasObject( ObjectIdentifier objectIdentifier )
//         {
//             return true;
//         }

//         @Mock
//         public static void deleteObject( String identifier, String logmessage);
//         {

//         }

//         @Mock( invocations = 1 )
//         public static String storeObject( CargoContainer cargo, String logmessage, String defaultNamespace )
//         {
//             return "stored";
//         }

//     }

//     //    @MockClass( realClass = CargoContainer.class )
//     //     public static class MockCargoContainer
//     //     {
//     //         @Mock
//     //         public static CargoObject getCargoObject( DataStreamType  )
//     //         {
//     //             return mockCargoobject;
//     //         }

//     //         @Mock
//     //         public static String getIdentifierAsString()
//     //         {
//     //             return "dbc:test";
//     //         }
//     //     }

//     @Before
//     public void setUp() throws Exception
//     {
//         objIdentifier = new PID( "dbc:111" );

//         cargo.setIdentifier( objIdentifier );

//         cargo.add( DataStreamType.OriginalData,
//                    "testFormat",
//                    "dbc",
//                    "da",
//                    "text/xml",
//                    IndexingAlias.Danmarcxchange,
//                    dataBytes );

//     }

//     @After
//     public void tearDown() throws Exception
//     {
//         tearDownMocks();
//     }

//     /**
//      *
//      */
//     @Test
//     public void getPluginTypeTest() throws Exception
//     {
//         storePlugin = new StorePlugin();
//         assertTrue( PT == storePlugin.getPluignType() );

//     }

//     @Test
//     public void storeCargoContainerHappyPathTest() throws Exception
//     {
//         setUpMocks( MockFedoraOjectRepository.class );
//         FedoraObjectRepository fedObjRep = new FedoraObjectRepository();
//         CargoContainer returnCargo;

//         storePlugin.setObjectRepository( fedObjRep );
//         returnCargo = storePlugin.getCargoContainer( cargo );
//         AssertEquals( returnCargo.getIdentifierAsString(), cargo.getIdentifierAsString() );

//     }
    @Test
    public void testTest()
    {}

}
