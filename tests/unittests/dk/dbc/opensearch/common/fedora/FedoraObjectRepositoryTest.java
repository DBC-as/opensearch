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
 * \file
 * \brief
 */


package dk.dbc.opensearch.common.fedora;


import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;

import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.ObjectFields;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.util.HashMap;

import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import mockit.Mock;
import mockit.MockClass;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.LogManager;
import org.custommonkey.xmlunit.SimpleNamespaceContext;
import org.custommonkey.xmlunit.XMLUnit;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;


/**
 * \Todo testen dækker ikke alle metoder. Ret op på dette.
 */

public class FedoraObjectRepositoryTest
{
    FedoraObjectRepository instance;
    
    static final String samePid = "test:1";
    static final String testPid = "test:2";
    
    static final String expectedFoxml = "<?xml version=\"1.0\" encoding=\"UTF-8\" standalone=\"no\"?><foxml:digitalObject xmlns:xsi=\"http://www.w3.org/1999/XMLSchema-instance\" PID=\"test:1\" VERSION=\"1.1\" xsi:schemaLocation=\"info:fedora/fedora-system:def/foxml# http://www.fedora.info/definitions/1/0/foxml1-1.xsd\" xmlns:foxml=\"info:fedora/fedora-system:def/foxml#\"><foxml:objectProperties><foxml:property NAME=\"info:fedora/fedora-system:def/model#state\" VALUE=\"A\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#label\" VALUE=\"testFormat\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#ownerId\" VALUE=\"testSubmitter\"/><foxml:property NAME=\"info:fedora/fedora-system:def/model#createdDate\" VALUE=\"2009-09-26T21:27:00.065\"/><foxml:property NAME=\"info:fedora/fedora-system:def/view#lastModifiedDate\" VALUE=\"2009-09-26T21:27:00.065\"/></foxml:objectProperties><foxml:datastream CONTROL_GROUP=\"M\" ID=\"originalData.0\" STATE=\"A\" VERSIONABLE=\"false\"><foxml:datastreamVersion CREATED=\"2009-09-26T21:27:00.058\" ID=\"originalData.0.0\" LABEL=\"testFormat\" MIMETYPE=\"text/xml\" SIZE=\"1\"><foxml:binaryContent>IA==</foxml:binaryContent></foxml:datastreamVersion></foxml:datastream><foxml:datastream CONTROL_GROUP=\"X\" ID=\"adminData\" STATE=\"A\" VERSIONABLE=\"true\"><foxml:datastreamVersion CREATED=\"2009-09-26T21:27:00.269\" ID=\"adminData.0\" LABEL=\"administration stream\" MIMETYPE=\"text/xml\" SIZE=\"221\"><foxml:xmlContent><admin-stream><indexingalias name=\"docbook\"/><streams><stream format=\"testFormat\" id=\"originalData.0\" index=\"0\" lang=\"da\" mimetype=\"text/xml\" streamNameType=\"originalData\" submitter=\"testSubmitter\"/></streams></admin-stream></foxml:xmlContent></foxml:datastreamVersion></foxml:datastream></foxml:digitalObject>";
    static final String administrationStream = "<admin-stream><streams><stream format=\"testFormat\" id=\"originalData.0\" index=\"0\" lang=\"da\" mimetype=\"text/xml\" streamNameType=\"originalData\" submitter=\"testSubmitter\"/><stream format=\"testFormat\" id=\"DC\" index=\"0\" lang=\"da\" mimetype=\"text/xml\" streamNameType=\"dublinCoreData\" submitter=\"testSubmitter\"/></streams></admin-stream>";
    
    static final String dublinCoreStream = "<?xml version=\"1.0\"?><oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\"><dc:title>Test title</dc:title><dc:identifier>test:1</dc:identifier></oai_dc:dc>";

    //static final String dublinCoreStream = "<?xml version=\"1.0\"?><dc xmlns=\"http://purl.org/dc/elements/1.1/\"><title>Test title</title><identifier>test:1</identifier></dc>";

    
    @MockClass( realClass = FedoraHandle.class )
    public static class MockFedoraHandle
    {
        @Mock
        public MockFedoraHandle()
        {
        }


        @Mock
        public String purgeObject( String identifier, String logmessage, boolean force )
        {
            if( identifier.equals( samePid ) || identifier.equals( testPid ) )
            {
                return "timestamp";
            }

            return null;
        }


        @Mock
        public String ingest( byte[] data, String datatype, String logmessage ) throws Exception
        {
            String newPid = "new:1";

            NamespaceContext nsc = new FedoraNamespaceContext();
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext( nsc );
            XPathExpression xPathExpression = null;
            String xPathStr = "/foxml:digitalObject[1]/@PID";
            InputSource dataInput = new InputSource( new ByteArrayInputStream( data ) );
			xPathExpression = xpath.compile( xPathStr );
			String foundPid = xPathExpression.evaluate( dataInput );

            if( foundPid.equals( samePid ) )
            {
                return samePid;
            }
            else if( foundPid.equals( testPid ) )
            {
                return testPid;
            }
            return newPid;
        }


        @Mock
        public byte[] getDatastreamDissemination( String pid, String datastreamId, String asOfDateTime )
        {

            byte[] retarray = null;
            if( pid.equals( samePid ) )
            {
                if( datastreamId.equals( "originalData.0" ) )
                {
                    retarray = "original data".getBytes();
                }
                else if( datastreamId.equals( "DC" ) )
                {
                    retarray = dublinCoreStream.getBytes();
                }
                else if( datastreamId.equals( "adminData" ) )
                {
                    retarray = administrationStream.getBytes();
                }
            }
            else if( pid.equals( testPid ) ) // with no dc data
            {
                if( datastreamId.equals( "originalData.0" ) )
                {
                    retarray = "original data".getBytes();
                }
                else if( datastreamId.equals( "adminData" ) )
                {
                    retarray = administrationStream.getBytes();
                }
            }
            else
            {
                retarray = "".getBytes();
            }
            return retarray;
        }


        @Mock
        public String[] getNextPID( int maxPids, String prefix )
        {
            return new String[]{prefix+":1"};
        }


        @Mock
        FieldSearchResult findObjects( String[] resultFields, NonNegativeInteger maxResults, FieldSearchQuery fsq )
        {
            FieldSearchResult fsr = new FieldSearchResult();
            fsr.setResultList( 
                    new ObjectFields[]
                    {
                        new ObjectFields( samePid, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ),
                        new ObjectFields( testPid, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null )
                    } );            
            return fsr;
        }


        @Mock
        public String uploadFile( File fileToUpload )
        {
            return "testLocation";
        }


        @Mock
        public String modifyDatastreamByReference( String pid, String datastreamID, String[] alternativeDsIds, String dsLabel, String MIMEType, String formatURI, String dsLocation, String checksumType, String checksum, String logMessage, boolean force )
        {
            if( pid.startsWith( "test" ) )
            {
                return "timestamp";
            }
            return null;
        }


        @Mock
        public String addDatastream( String pid, String datastreamID, String[] alternativeDsIds, String dsLabel, boolean versionable, String MIMEType, String formatURI, String dsLocation, String controlGroup, String datastreamState, String checksumType, String checksum, String logmessage )
        {
            if( pid.startsWith( "test" ) )
            {
                return "timestamp";
            }
            return null;
        }


        @Mock
        public String[] purgeDatastream( String pid, String sID, String startDate, String endDate, String logm, boolean breakDependencies )
        {
            if( pid.startsWith( "test" ) )
            {
                return new String[]{ "timestamp" };
            }
            return new String[]{};
        }

        
        @Mock
        public boolean addRelationship( String pid, String predicate, String object, boolean isLiteral, String datatype ) 
        {
            if( ! pid.startsWith("object:")) {
                return false;
            }
            if( ! pid.startsWith("subject:")) {
                return false;
            }                       
            return true;
        }


        @Mock
        public boolean purgeRelationship( String pid, String predicate, String object, boolean isLiteral, String datatype ) 
        {
            if( ! pid.startsWith("object:")) {
                return false;
            }
            if( ! pid.startsWith("subject:")) {
                return false;
            }                       
            return true;
        }


    }

    
    @BeforeClass
    public static void generalSetup()
    {
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel( Level.OFF );

        HashMap<String, String> m = new HashMap<String, String>();
        m.put( "x", "info:fedora/fedora-system:def/foxml#" );
        SimpleNamespaceContext ctx = new SimpleNamespaceContext(m);
        XMLUnit.setXpathNamespaceContext( ctx );
    }


    @Before
    public void setUp() throws Exception
    {
        setUpMocks( MockFedoraHandle.class );
        instance = new FedoraObjectRepository();
    }


    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
    }


    @Test
    public void testDeleteObject() throws Exception
    {
        String identifier = "test:1";
        String logmessage = "log";

        instance.deleteObject( identifier, logmessage );
    }


    @Test( expected = ObjectRepositoryException.class )
    public void testDeleteObjectFailsWithNonExistingObject() throws Exception
    {
        String identifier = "idontexist";
        String logmessage = "log";

        instance.deleteObject( identifier, logmessage );
    }


    @Test
    public void testStoreObject() throws Exception
    {
        CargoContainer cargo = new CargoContainer( );
        cargo.setIdentifier( new PID("test:1") );
        cargo.add( DataStreamType.OriginalData, "testFormat", "testSubmitter", "da", "text/xml", " ".getBytes() );
        // static final String dublinCore    = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><dc:title xmlns:dc=\"hej\">æøå</dc:title>";
        String dublinCore    = "<?xml version=\"1.0\" encoding=\"UTF-8\" ?><dc:title xmlns:dc=\"hej\">test:1</dc:title>";
        cargo.add( DataStreamType.DublinCoreData, "testFormat", "testSubmitter", "da", "text/xml", dublinCore.getBytes() );
        String logmessage = "log";
        String expResult = "test:1";
        String result = instance.storeObject( cargo, logmessage, "test");
        assertEquals( expResult, result );
    }


    @Test( expected = IllegalStateException.class )
    public void testStoreObjectWithEmptyCargoGetsNewPidFromFedora() throws Exception
    {
        CargoContainer cargo = new CargoContainer();
        String logmessage = "log";
        String storeObject = instance.storeObject( cargo, logmessage, "test");
        assertEquals( "test:1", storeObject );
    }


    @Test
    public void testStoreObjectWithEmptyIdentifierFails() throws Exception
    {
        CargoContainer cargo = new CargoContainer();        
        cargo.add( DataStreamType.OriginalData, "testFormat", "testSubmitter", "da", "text/xml", " ".getBytes() );
        // cargo.setIndexingAlias( "docbook", DataStreamType.OriginalData );
        String logmessage = "log";
        String storeObject = instance.storeObject( cargo, logmessage, "test");
        assertEquals( "test:1", storeObject ); 
    }


    @Test
    public void testGetObject() throws Exception
    {
        String identifier = "test:1";
        CargoContainer result = instance.getObject( identifier );
        assertEquals( identifier, result.getIdentifierAsString() );
    }


    @Test
    public void testGetObjectReturnsCorrectDCStream() throws Exception
    {
        String identifier = "test:1";
        CargoContainer result = instance.getObject( identifier );
        assertTrue( null != result.getCargoObject( DataStreamType.DublinCoreData ) );
    }


    @Test( expected=IllegalStateException.class )
    public void testGetObjectThatDoesntExist() throws Exception
    {
        String identifier = "null:1";
        CargoContainer result = instance.getObject( identifier );
        assertTrue( result == null );
    }
    

    @Test
    public void testaddObjectRelation( ) throws Exception {        
        instance.addObjectRelation(new PID("object:1"), DBCBIB.IS_MEMBER_OF_WORK , "Subject:1");        
    }


    @Test
    public void testpurgeObjectRelation( ) throws Exception {        
        instance.removeObjectRelation(new PID("object:1"), DBCBIB.IS_MEMBER_OF_WORK , "Subject:1");        
    }
}