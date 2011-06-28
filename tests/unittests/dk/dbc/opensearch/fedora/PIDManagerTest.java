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
 

package dk.dbc.opensearch.fedora;


import java.io.IOException;
import java.net.MalformedURLException;

import javax.xml.rpc.ServiceException;

import mockit.Mock;
import mockit.MockClass;
import org.apache.commons.configuration.ConfigurationException;
import static org.junit.Assert.*;
import org.junit.*;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;


public class PIDManagerTest
{

    @MockClass( realClass = FedoraHandle.class )
    public static class MockFedoraHandle
    {
        @Mock public void $init( String host, String port, String user, String passwd ) {}

        @Mock
        public String[] getNextPID( int maxPids, String prefix )
        {
            if( null == prefix || prefix.isEmpty() )
            {
                throw new IllegalStateException();
            }
            return new String[]
                    {
                        prefix + ":1"
                    };
        }
    }
    private FedoraHandle fedoraHandle;

    @Before
    public void setUp() throws Exception
    {
        setUpMocks( MockFedoraHandle.class );
    }

    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
    }

    @Test ( expected=IllegalStateException.class )
    public void testPrefixMustBeSpecified() throws ObjectRepositoryException, ServiceException, ConfigurationException, MalformedURLException, IllegalStateException, IOException
    {
        FedoraHandle fedoraHandle = new FedoraHandle( "Host", "Port", "User", "Password" );
        String[] pid = fedoraHandle.getNextPID( 1,  "" );
    }


    @Test //( expected=IllegalStateException.class )
    public void testFedoraConnectionMustBePresent() throws ObjectRepositoryException, ServiceException, ConfigurationException, MalformedURLException, IllegalStateException, IOException
    {
        FedoraHandle fedoraHandle = new FedoraHandle( "Host", "Port", "User", "Password" );
        fedoraHandle.getNextPID( 1,  "a" );
    }
}