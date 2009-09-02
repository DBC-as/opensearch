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


import dk.dbc.opensearch.components.harvest.IIdentifier;

/** \brief UnitTest for DatadockJob **/

import java.io.ByteArrayInputStream;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import static org.junit.Assert.*;
import org.junit.*;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import static org.easymock.classextension.EasyMock.*;

public class DatadockJobTest {

    /**
     * Testing the getters and setters of DatadockJob
     */
    IIdentifier mockID;
    static Document refData;
    static String refDataString = "<root>refdata</root>";
    @BeforeClass
    static public void SetupClass()
    {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        try
        {
            DocumentBuilder builder = factory.newDocumentBuilder();
            refData = builder.parse( new InputSource( new ByteArrayInputStream( refDataString.getBytes() ) ) );
        }
        catch( ParserConfigurationException pce )
        {
        }
        catch( SAXException se )
        {
        }
        catch( IOException ioe )
        {
        }
 
    }

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
