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

package dk.dbc.opensearch.components.datadock;


import java.net.URI;
import dk.dbc.opensearch.components.harvest.IIdentifier;
import dk.dbc.opensearch.components.datadock.DatadockJob;
import java.net.URISyntaxException;

/** \brief UnitTest for DatadockJob **/

import static org.junit.Assert.*;
import org.junit.*;
import static org.easymock.classextension.EasyMock.*;

public class DatadockJobTest {

    /**
     * Testing the getters and setters of DatadockJob
     */
    IIdentifier mockID;

    @Before
    public void setUp()
    {
        mockID = createMock( IIdentifier.class );
    }    

    @After
    public void tearDown()
    {
        reset( mockID );
    }

    @Test 
    public void testSettersAndGetters() 
    {        
        String testSubmitter = "testSubmitter";
        String testFormat = "testFormat";
        String refDataString = "refdata";
        byte[] refData = refDataString.getBytes();
     
        DatadockJob datadockJob = new DatadockJob( testSubmitter, testFormat, mockID, refData );
        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );        

        testSubmitter = "testSubmitter2";
        testFormat = "testFormat2";        

        datadockJob.setSubmitter( testSubmitter );
        datadockJob.setFormat( testFormat );

        assertTrue( datadockJob.getSubmitter().equals( testSubmitter ) );
        assertTrue( datadockJob.getFormat().equals( testFormat ) );
       
    }
}
