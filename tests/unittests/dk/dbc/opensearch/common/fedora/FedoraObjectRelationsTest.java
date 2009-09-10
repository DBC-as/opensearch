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

/**
 * \file
 * \brief
 */


package dk.dbc.opensearch.common.fedora;


import fedora.client.FedoraClient;
import fedora.server.access.FedoraAPIA;
import fedora.server.management.FedoraAPIM;

import java.io.IOException;
import java.rmi.RemoteException;
import java.net.MalformedURLException;

import javax.xml.rpc.ServiceException;

import mockit.Mock;
import mockit.Mockit;
import mockit.MockClass;

import org.apache.commons.configuration.ConfigurationException;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;
import org.junit.*;


/**
 * This class tests the FedoraAdministration class
 * It starts out with testing the private methods used by the
 * public once, so that they can be mocked and not tested everytime
 * a public method uses them
 */
public class FedoraObjectRelationsTest
{
    FedoraObjectRelations fedor;
    
    static FedoraAPIA mockFea = createMock( FedoraAPIA.class );
    static FedoraAPIM mockFem = createMock( FedoraAPIM.class );
    static FedoraClient mockFedoraClient = createMock( FedoraClient.class);
    
    
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
    
       
    @MockClass( realClass = FedoraObjectRelations.class )
    public static class MockFedoraObjectRelations
    {    
    }
    
    
    /**
     *setup
     */
    @Before public void SetUp()
    {
    }


    /**
     *teardown
     */
    @After public void TearDown()
    {
        Mockit.tearDownMocks();
        reset( mockFem );
        reset( mockFea );
        reset( mockFedoraClient );

        fedor = null;
    }


    /**
     * Testing the constructor
     */
    /**
     * Testing the happy path of the constructor, the only path.
     */
    @Test public void testConstructor()
    {
        fedor = new FedoraObjectRelations();
    }


    /**
     * Testing the addRelation method, happy path
     */
    @Test
    public void testAddRelation() throws RemoteException, ConfigurationException, MalformedURLException, ServiceException, IOException
    {
        //setup
        Mockit.setUpMocks( MockFedoraHandle.class);
        String pid = "test:1";
        String predicate = "predicate";
        String isMemberOfCollectionPredicate =  "info:fedora/fedora-system:def/relations-external#isMemberOfCollection";
        String targetDCIdentifier = "DCid";
        boolean literal = false;
        String datatype = "unknown";
        //expectations
        expect( mockFem.addRelationship( pid, isMemberOfCollectionPredicate, targetDCIdentifier, literal, datatype) ).andReturn( true );

        //replay
        replay( mockFem );

        //do stuff
        fedor = new FedoraObjectRelations();
        assertTrue( fedor.addRelation( pid, predicate, targetDCIdentifier, literal, datatype ) );
        
        //verify
        verify( mockFem );
    }
}
