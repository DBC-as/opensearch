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


import dk.dbc.opensearch.common.xml.XMLUtils;
import dk.dbc.opensearch.components.harvest.IIdentifier;

/** \brief UnitTest for DatadockJob **/

import mockit.Mocked;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

public class DatadockJobTest {

    static final String referenceData = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"dk\"/></referencedata>";
    @Mocked IIdentifier mockIdentifier;
    private Document xmldata;

    @Before
    public void setUp() throws Exception
    {
        xmldata = XMLUtils.documentFromString( referenceData );
    }

    @Test
    public void DatadockJobTest()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldata );
        assertEquals( mockIdentifier, job.getIdentifier() );
        assertEquals( "775100", job.getSubmitter() );
        assertEquals( "ebrary", job.getFormat() );
    }

    @Test( expected=IllegalStateException.class )
    public void illegalStateForEmptyReferenceDataTest() throws Exception
    {
        DatadockJob job = new DatadockJob( mockIdentifier, null );
    }

    @Test(expected=IllegalArgumentException.class)
    public void illegalArgumentForWrongReferenceDataTest() throws Exception
    {
        Document reference = XMLUtils.documentFromString( "<?xml version=\"1.0\"?><error/>" );
        DatadockJob job = new DatadockJob( mockIdentifier, reference );
    }
}
