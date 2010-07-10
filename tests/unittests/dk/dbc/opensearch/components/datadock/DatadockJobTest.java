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
import dk.dbc.opensearch.common.types.IIdentifier;

/** \brief UnitTest for DatadockJob **/

import mockit.Mocked;
import org.junit.*;
import static org.junit.Assert.*;
import org.w3c.dom.Document;

import org.custommonkey.xmlunit.XMLUnit;
import org.custommonkey.xmlunit.Diff;

public class DatadockJobTest {

    static final String referenceDataComplete = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"123456\" format=\"someFormat\" lang=\"se\" mimetype=\"pdf\"/></referencedata>";
    static final String referenceDataNoLang = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" mimetype=\"pdf\"/></referencedata>";
    static final String referenceDataEmptyLang = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"\" mimetype=\"pdf\"/></referencedata>";
    static final String referenceDataNoMimeType = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"se\"/></referencedata>";
    static final String referenceDataEmptyMimeType = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"se\" mimetype=\"\"/></referencedata>";
    static final String referenceDataIllegalAttribute = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><referencedata><info submitter=\"775100\" format=\"ebrary\" lang=\"dk\" illegalattribute=\"illegal\"/></referencedata>";
    @Mocked IIdentifier mockIdentifier;
    private Document xmldataComplete;
    private Document xmldataNoLang;
    private Document xmldataEmptyLang;
    private Document xmldataNoMimeType;
    private Document xmldataEmptyMimeType;
    private Document xmldataIllegalAttribute;

    @Before
    public void setUp() throws Exception
    {
        xmldataComplete = XMLUtils.documentFromString( referenceDataComplete );
        xmldataNoLang = XMLUtils.documentFromString( referenceDataNoLang );
        xmldataEmptyLang = XMLUtils.documentFromString( referenceDataEmptyLang );
        xmldataNoMimeType = XMLUtils.documentFromString( referenceDataNoMimeType );
        xmldataEmptyMimeType = XMLUtils.documentFromString( referenceDataEmptyMimeType );
        xmldataIllegalAttribute = XMLUtils.documentFromString( referenceDataIllegalAttribute );

	// Closing output to screen from expected errors
	System.out.close();
    }

    @Test
    public void DatadockJobTest()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataComplete );
        assertEquals( mockIdentifier, job.getIdentifier() );
        assertEquals( "123456", job.getSubmitter() );
        assertEquals( "someFormat", job.getFormat() );
	assertEquals( "se", job.getLanguage() );
	assertEquals( "pdf", job.getMimeType() );
    }

    @Test
    public void DatadockJobTestNoLang()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataNoLang );
        assertEquals( "775100", job.getSubmitter() );
        assertEquals( "ebrary", job.getFormat() );
	assertEquals( "da", job.getLanguage() );
	assertEquals( "pdf", job.getMimeType() );
    }

    @Test
    public void DatadockJobTestEmptyLang()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataEmptyLang );
	assertEquals( "da", job.getLanguage() );
    }

    @Test
    public void DatadockJobTestNoMimeType()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataNoMimeType );
	assertEquals( "text/xml", job.getMimeType() );
    }

    @Test
    public void DatadockJobTestEmptyMimeType()
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataEmptyMimeType );
	assertEquals( "text/xml", job.getMimeType() );
    }

    @Test(expected=IllegalArgumentException.class)
    public void DatadockJobIllegalAttributeTest() throws Exception
    {
        DatadockJob job = new DatadockJob( mockIdentifier, xmldataIllegalAttribute );
    }

    @Test( expected=IllegalStateException.class )
    public void IllegalStateForEmptyReferenceDataTest() throws Exception
    {
        DatadockJob job = new DatadockJob( mockIdentifier, null );
    }

    @Test(expected=IllegalArgumentException.class)
    public void IllegalArgumentForWrongReferenceDataTest() throws Exception
    {
        Document reference = XMLUtils.documentFromString( "<?xml version=\"1.0\"?><error/>" );
        DatadockJob job = new DatadockJob( mockIdentifier, reference );
    }


    // Test the retrived referenceData XML-ducument against the Document given to the constructor.
    // We want to make sure we do not corrupt the XML inside DatadockJob.
    @Test
    public void DatadockJobGetDocumentTest() throws Exception
    {
	Document xmldataTmp = XMLUtils.documentFromString( referenceDataComplete );
    	DatadockJob job = new DatadockJob( mockIdentifier, xmldataComplete );
    	Diff diff = XMLUnit.compareXML( xmldataTmp, job.getReferenceData() );
        assertEquals( true, diff.identical() );
    }


}
