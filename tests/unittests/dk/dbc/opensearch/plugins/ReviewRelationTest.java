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

/**
 * \file ReviewRelationTest.java
 * \brief unittest of the ReviewRelation class
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.FedoraObjectFields;
import dk.dbc.opensearch.common.fedora.FedoraObjectRelations;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.fedora.ObjectRepositoryException;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;


import static org.junit.Assert.*;
import org.junit.*;
import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

public class ReviewRelationTest
{

    ReviewRelation RRPlugin;

    //create a CargoContainer with valid data

    @Mocked IObjectRepository mockRepository;

    @MockClass( realClass = FedoraObjectRelations.class )
    public static class MockFedoraObjectRelations
    {
        @Mock public void $init( IObjectRepository objectRepository )
        {}
        
        @Mock public boolean addIsMbrOfCollRelationship( String pid, String namespace ) throws ObjectRepositoryException
        {
            return true;
        }
    }

    @Before public void SetUp() 
    {
    }

  
    @After public void TearDown() 
    {
    }

    /**
     * Tests the ReviewRelation getCargoContianer when the data in the 
     * CargoContainer is valid and the objectRepository is set correctly
     */

    @Test
    public void getCargoContainerTestHappy() throws Exception
    {
 
    }
    
    @Test
    public void getPluginTypeTest()
    {
	PluginType pt = null;
	try
	{
	    RRPlugin = new ReviewRelation( mockRepository);
	    pt = RRPlugin.getPluginType();
	}
	catch( PluginException pe )
        {
	    
	}
        // assertEquals( RRPlugin.getPluginType(), PluginType.RELATION );
	assertEquals( pt , PluginType.RELATION );
    }
}