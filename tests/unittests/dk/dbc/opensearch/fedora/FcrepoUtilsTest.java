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
package dk.dbc.opensearch.fedora;

import java.io.ByteArrayInputStream;
import org.fcrepo.server.types.gen.Datastream;

import org.fcrepo.server.types.gen.FieldSearchQuery;
import org.fcrepo.server.types.gen.FieldSearchResult;
import org.fcrepo.server.types.gen.ObjectFields;

import java.io.IOException;
import java.rmi.RemoteException;
import java.util.HashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.rpc.ServiceException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
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
import org.fcrepo.client.FedoraClient;
import org.fcrepo.server.access.FedoraAPIA;
import org.fcrepo.server.management.FedoraAPIM;
import org.fcrepo.server.types.gen.DatastreamDef;
import org.fcrepo.server.types.gen.MIMETypedStream;
import org.fcrepo.server.types.gen.ObjectMethodsDef;
import org.fcrepo.server.types.gen.ObjectProfile;
import org.fcrepo.server.types.gen.Property;
import org.fcrepo.server.types.gen.RelationshipTuple;
import org.fcrepo.server.types.gen.RepositoryInfo;
import org.fcrepo.server.types.gen.Validation;
import org.junit.After;
import static org.junit.Assert.*;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.xml.sax.InputSource;

/**
 * \Todo testen dækker ikke alle metoder. Ret op på dette.
 */
public class FcrepoUtilsTest
{
    FcrepoReader reader;
    FcrepoModifier modifier;
    static FedoraAPIA apiaInstance = new MockFedoraAPIA();
    static FedoraAPIM apimInstance = new MockFedoraAPIM();
    private final static String samePid = "test:1";
    private final static String testPid = "test:2";
    private final static String testdPid = "test:3";
    private final static String administrationStream = "<admin-stream><streams><stream format=\"testFormat\" id=\"originalData.0\" index=\"0\" lang=\"da\" mimetype=\"text/xml\" streamNameType=\"originalData\" submitter=\"testSubmitter\"/><stream format=\"testFormat\" id=\"DC\" index=\"0\" lang=\"da\" mimetype=\"text/xml\" streamNameType=\"dublinCoreData\" submitter=\"testSubmitter\"/></streams></admin-stream>";
    private final static String dublinCoreStream = "<?xml version=\"1.0\"?><oai_dc:dc xmlns:dc=\"http://purl.org/dc/elements/1.1/\" xmlns:oai_dc=\"http://www.openarchives.org/OAI/2.0/oai_dc/\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\" xsi:schemaLocation=\"http://www.openarchives.org/OAI/2.0/oai_dc/ http://www.openarchives.org/OAI/2.0/oai_dc.xsd\"><dc:title>Test title</dc:title><dc:identifier>test:1</dc:identifier></oai_dc:dc>";

    public static class MockFedoraAPIA implements FedoraAPIA
    {
        public RepositoryInfo describeRepository() throws RemoteException
        {
            return null;
        }


        public ObjectProfile getObjectProfile( String string, String string1 ) throws RemoteException
        {
            return null;
        }


        public ObjectMethodsDef[] listMethods( String string, String string1 ) throws RemoteException
        {
            return null;
        }


        public DatastreamDef[] listDatastreams( String string, String string1 ) throws RemoteException
        {
            return new DatastreamDef[]
                    {
                        new DatastreamDef( "Stream1", "Label1", "Mimetype1" ),
                        new DatastreamDef( "Stream2", "Label2", "Mimetype2" ),
                    };
        }


        public MIMETypedStream getDatastreamDissemination( String pid, String datastreamId, String asOfDateTime ) throws RemoteException
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
            return new MIMETypedStream( null, retarray, null );
        }


        public MIMETypedStream getDissemination( String string, String string1, String string2, Property[] prprts, String string3 ) throws RemoteException
        {
            return null;
        }


        public FieldSearchResult findObjects( String[] strings, NonNegativeInteger nni, FieldSearchQuery fsq ) throws RemoteException
        {
            String relation = "rel1|" + samePid + ",rel2|" + testdPid;
            FieldSearchResult fsr = new FieldSearchResult();
            fsr.setResultList(
                    new ObjectFields[]
                    {
                        new ObjectFields( samePid, null, "A", null, null, null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ),
                        new ObjectFields( testPid, null, "I", null, null, null, null, null, relation, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null ),
                        new ObjectFields( testdPid, null, "D", null, null, null, null, null, "", null, null, null, null, null, null, null, null, null, null, null, null, null, null, null )
                    } );
            return fsr;
        }


        public FieldSearchResult resumeFindObjects( String string ) throws RemoteException
        {
            return null;
        }


        public String[] getObjectHistory( String string ) throws RemoteException
        {
            return null;
        }
    }

    public static class MockFedoraAPIM implements FedoraAPIM
    {
        public String ingest( byte[] objectXML, String format, String logMessage ) throws RemoteException
        {
            String newPid = "new:1";

            NamespaceContext nsc = new FedoraNamespaceContext();
            XPath xpath = XPathFactory.newInstance().newXPath();
            xpath.setNamespaceContext( nsc );
            XPathExpression xPathExpression = null;
            String xPathStr = "/foxml:digitalObject[1]/@PID";
            InputSource dataInput = new InputSource( new ByteArrayInputStream( objectXML ) );
            try
            {
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
            catch( XPathExpressionException e )
            {
                throw new RemoteException( "Exception parsing XML", e );
            }
        }


        public String modifyObject( String pid, String state, String label, String ownerId, String logMessage ) throws RemoteException
        {
            if( samePid.equals( pid ) )
            {
                return "pid";
            }
            else
            {
                throw new RemoteException();
            }
        }


        public byte[] getObjectXML( String pid ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public byte[] export( String pid, String format, String context ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String purgeObject( String pid, String logMessage, boolean force ) throws RemoteException
        {
            if( pid.equals( samePid ) || pid.equals( testPid ) )
            {
                return "timestamp";
            }

            return null;
        }


        public String addDatastream( String pid, String dsID, String[] altIDs, String dsLabel, boolean versionable, String MIMEType, String formatURI, String dsLocation, String controlGroup, String dsState, String checksumType, String checksum, String logMessage ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String modifyDatastreamByReference( String pid, String dsID, String[] altIDs, String dsLabel, String MIMEType, String formatURI, String dsLocation, String checksumType, String checksum, String logMessage, boolean force ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String modifyDatastreamByValue( String pid, String dsID, String[] altIDs, String dsLabel, String MIMEType, String formatURI, byte[] dsContent, String checksumType, String checksum, String logMessage, boolean force ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String setDatastreamState( String pid, String dsID, String dsState, String logMessage ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String setDatastreamVersionable( String pid, String dsID, boolean versionable, String logMessage ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String compareDatastreamChecksum( String pid, String dsID, String versionDate ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public Datastream getDatastream( String pid, String dsID, String asOfDateTime ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public Datastream[] getDatastreams( String pid, String asOfDateTime, String dsState ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public Datastream[] getDatastreamHistory( String pid, String dsID ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public String[] purgeDatastream( String pid, String dsID, String startDT, String endDT, String logMessage, boolean force ) throws RemoteException
        {
            if( pid.startsWith( "test" ) )
            {
                return new String[]
                        {
                            "timestamp"
                        };
            }
            return new String[]
                    {
                    };
        }


        public String[] getNextPID( NonNegativeInteger numPIDs, String pidNamespace ) throws RemoteException
        {
            return new String[]
                    {
                        pidNamespace + ":1"
                    };
        }


        public RelationshipTuple[] getRelationships( String pid, String relationship ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }


        public boolean addRelationship( String pid, String relationship, String object, boolean isLiteral, String datatype ) throws RemoteException
        {
            return true;
        }


        public boolean purgeRelationship( String pid, String relationship, String object, boolean isLiteral, String datatype ) throws RemoteException
        {
            if( !pid.startsWith( "object:" ) )
            {
                return false;
            }
            if( !pid.startsWith( "subject:" ) )
            {
                return false;
            }
            return true;
        }


        public Validation validate( String pid, String asOfDateTime ) throws RemoteException
        {
            throw new UnsupportedOperationException( "Not supported yet." );
        }
    }

    @MockClass(realClass = FedoraClient.class)
    public static class MockFedoraClient
    {
        @Mock
        public void $init( String url, String user, String passwd )
        {
        }


        @Mock
        public FedoraAPIM getAPIM() throws ServiceException, IOException
        {
            return apimInstance;
        }


        @Mock
        public FedoraAPIA getAPIA() throws ServiceException, IOException
        {
            return apiaInstance;
        }
    }


    @BeforeClass
    public static void generalSetup()
    {
        BasicConfigurator.configure();
        LogManager.getRootLogger().setLevel( Level.OFF );

        HashMap<String, String> m = new HashMap<String, String>();
        m.put( "x", "info:fedora/fedora-system:def/foxml#" );
        SimpleNamespaceContext ctx = new SimpleNamespaceContext( m );
        XMLUnit.setXpathNamespaceContext( ctx );
    }


    @Before
    public void setUp() throws Exception
    {
        setUpMocks( MockFedoraClient.class );
        reader = new FcrepoReader( "Host", "Port" );
        modifier = new FcrepoModifier( "Host", "Port", "User", "Pass" );
    }


    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
    }


    @Test
    public void testRemoveOutboundRelations() throws Exception
    {
        String identifier = "test:1";

        FcrepoUtils.removeOutboundRelations( reader, modifier, identifier );
    }


    @Test(expected = ObjectRepositoryException.class)
    public void testRemoveOutboundRelationsFailsWithNonExistingObject() throws Exception
    {
        String identifier = "idontexist";

        FcrepoUtils.removeOutboundRelations( reader, modifier, identifier );
    }


    @Test
    public void testRemoveInboundRelations() throws Exception
    {
        String identifier = "test:1";

        int res = FcrepoUtils.removeInboundRelations( reader, modifier, identifier );
        // Expected one relation to be removed
        assertEquals( 1, res );
    }


    @Test
    public void testRemoveInboundRelationsNoneFound() throws Exception
    {
        String identifier = "test:2";

        int res = FcrepoUtils.removeInboundRelations( reader, modifier, identifier );
        // Expected none relation to be removed
        assertEquals( 0, res );
    }
}
