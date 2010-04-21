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


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.metadata.DBCBIB;
import dk.dbc.opensearch.common.metadata.IPredicate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.ObjectIdentifier;
import dk.dbc.opensearch.common.types.TargetFields;

import fedora.common.PID;
import fedora.server.types.gen.FieldSearchQuery;
import fedora.server.types.gen.ComparisonOperator;
import fedora.server.types.gen.Condition;
import fedora.server.types.gen.FieldSearchResult;
import fedora.utilities.Foxml11Document.Property;
import fedora.server.types.gen.ObjectFields;
import fedora.server.types.gen.RelationshipTuple;

import fedora.utilities.Foxml11Document;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.xml.rpc.ServiceException;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamWriter;

import org.apache.axis.types.NonNegativeInteger;
import org.apache.commons.configuration.ConfigurationException;


/**
 *class for testing the functionality of the FedoraAdminstration class
 */
public class AdministrationFunc
{
    private static FedoraObjectRelations fedor;
    private static FedoraHandle fedoraHandle;
    private static IObjectRepository objectRepository;

    public static void main( String[] args ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        runTests();
    }


    static void runTests() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {

        try
        {
            objectRepository = new FedoraObjectRepository();
            fedor = new FedoraObjectRelations( objectRepository );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        /*System.out.println( "*** kalder teestStoreCC ***" );
        String pid = testStoreCC();

        System.out.println( "*** kalder get ***" );
        testGetObject( pid );

        System.out.println( "*** kalder testFindObjects ***" );
        testFindObjectPids();

        System.out.println( "*** kalder testFindObjectFields ***" );
        testFindObjectFields();

        testFindObjectRelationships();

        testGetRelationships();

        testFoxml11Document();
*/
        /*System.out.println( "*** kalder testDeleteObjects ***" );
        String[] labels = { "anmeldelser", "anmeld", "forfatterw", "matvurd", "katalog", "danmarcxchange", "ebrary", "ebsco", "artikler", "dr_forfatteratlas", "dr_atlas", "dr_bonanza", "materialevurderinger", "docbook_forfatterweb", "docbook_faktalink" };
        testDeleteObjectPids( labels, 50 );*/

        //testGetSubjectRelations();

        testGetObjectRelations();

        /*System.out.println( "*** kalder getDataStreamsOfType første gang ***" );
        testGetDataStreamsOfType( pid );

        System.out.println( "*** kalder add ***" );
        testAddDataStreamToObject( pid );

        System.out.println( "*** kalder getDataStreamsOfType anden gang ***" );
        testGetDataStreamsOfType( pid );

        System.out.println( "*** kalder modify ***" );
        testModifyDataStream( pid );

        System.out.println( "*** kalder remove ***" );
        testRemoveDataStream( pid );
        
        System.out.println( "*** kalder getDataStreamsOfType for anden gang" );
        testGetDataStreamsOfType( pid );
        testDeleteObject( pid );*/
    }


    static void testGetIdentifiers() throws ObjectRepositoryException
    {
        String predicate = "title"; // "source";
        String object = "Harry Potter og Fønixordenen";
        String predicate_2 = "creator";
        String object_2 = "J. K. Rowling s";
        String relation = "isMemberOf";

        fedor = new FedoraObjectRelations( objectRepository );
        try
        {
            String workRelation = fedor.getSubjectRelation( "source", object, relation );
            System.out.println( String.format( "first workRelation found:  %s", workRelation ) );

            workRelation = fedor.getSubjectRelation( predicate, object, predicate_2, object_2, relation );
            System.out.println( String.format( "second workRelation found: %s", workRelation ) );
        }
        catch ( ConfigurationException ce )
        {
            System.out.println( "ce caught" );
            ce.printStackTrace();
        }
        catch ( ServiceException se )
        {
            System.out.println( "se caught" );
            se.printStackTrace();
        }
        catch ( MalformedURLException mue )
        {
            System.out.println( "mue caught" );
            mue.printStackTrace();
        }
        catch ( IOException ioe )
        {
            System.out.println( "ioe caught" );
            ioe.printStackTrace();
        }
    }


    static void testGetObjectRelations() throws ObjectRepositoryException
    {
        System.out.println( "************ CALLING testGetObjectRelations **************" );
        //String subject = "info:fedora/710100:20078138";
        String subject = "710100:20078138";
        String predicate = DBCBIB.IS_MEMBER_OF_WORK.getPredicateString();
        IPredicate workPredicate = DBCBIB.HAS_MANIFESTATION;
        //System.out.println( subject + " " + predicate );
        ObjectIdentifier identifier = new dk.dbc.opensearch.common.fedora.PID( subject );
        if ( objectRepository.hasObject( identifier ) )
        {
            List< InputPair< IPredicate, String > > relations = objectRepository.getObjectRelations( subject, predicate );

            if ( relations != null )
            {
                for ( InputPair pair : relations )
                {
                    String work = pair.getSecond().toString();                    
                    List< InputPair< IPredicate, String > > workRelations = objectRepository.getObjectRelations( work, workPredicate.getPredicateString() );
                    if ( workRelations.size() > 0 )
                    {
                        ObjectIdentifier workIdentifier = new dk.dbc.opensearch.common.fedora.PID( work );
                        objectRepository.removeObjectRelation( workIdentifier, workPredicate, subject );
                    }
                }
            }
        }
    }


    static void testGetSubjectRelations() throws ObjectRepositoryException
    {
        String predicate = "title"; // "source";
        String object = "Harry Potter and the Order of the Phoenix";
        String predicate_2 = "creator";
        String object_2 = "J. K. Rowling s";
        String relation = "isMemberOf";

        fedor = new FedoraObjectRelations( objectRepository );
        try
        {
            String workRelation = fedor.getSubjectRelation( "source", object, relation );
            System.out.println( String.format( "first workRelation found:  %s", workRelation ) );
            
            workRelation = fedor.getSubjectRelation( predicate, object, predicate_2, object_2, relation );
            System.out.println( String.format( "second workRelation found: %s", workRelation ) );
        }
        catch ( ConfigurationException ce )
        {
            System.out.println( "ce caught" );
            ce.printStackTrace();
        }
        catch ( ServiceException se )
        {
            System.out.println( "se caught" );
            se.printStackTrace();
        }
        catch ( MalformedURLException mue )
        {
            System.out.println( "mue caught" );
            mue.printStackTrace();
        }
        catch ( IOException ioe )
        {
            System.out.println( "ioe caught" );
            ioe.printStackTrace();
        }
    }


    static void testFoxml11Document() throws ObjectRepositoryException
    {
        fedoraHandle = new FedoraHandle();
        try
        {
            String pid = "admin:func";
            String contentLocation = "file:" + FileSystemConfig.getTrunkPath() + "pot1.xml";
            //String contentLocation = "copy:///data1/harvest-test/kkb/danmarcxchange/pot1.xml";
            byte[] b = getFoxmlObject ( pid, contentLocation );
            System.out.println( String.format( "byte[]: %s", new String( b ) ) );

            String retPid = fedoraHandle.ingest( b, "info:fedora/fedora-system:FOXML-1.1", null );
            System.out.println( String.format( "return pid: %s", retPid ) );
        }
        catch ( RemoteException re )
        {
            System.out.println( String.format( "RemoteException caught: %s", re.getMessage()  ) );
            re.printStackTrace();
        }
        catch ( ConfigurationException ce )
        {
            System.out.println( String.format( "", "" ) );
            ce.printStackTrace();
        }
        catch ( ServiceException se )
        {
            System.out.println( String.format( "", "" ) );
            se.printStackTrace();
        }
        catch ( MalformedURLException mue )
        {
            System.out.println( String.format( "", "" ) );
            mue.printStackTrace();
        }
        catch ( IOException ioe )
        {
            System.out.println( String.format( "", "" ) );
            ioe.printStackTrace();
        }
        catch ( Exception e )
        {
            System.out.println( String.format( "", "" ) );
            e.printStackTrace();
        }
    }


    private static byte[] getFoxmlObject( String pid, String contentLocation ) throws Exception
    {
        System.out.println( "Getting foxml object" );
        Foxml11Document doc = createFoxmlObject( pid, contentLocation );
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        doc.serialize( out );

        return out.toByteArray();
    }

    
    static Foxml11Document createFoxmlObject( String spid, String contentLocation )
    {
        PID pid = PID.getInstance( spid );
        System.out.println( String.format( "pid: %s", pid ) );
        Date date = new Date( 1 );

        Foxml11Document doc = new Foxml11Document( pid.toString() );
        doc.addObjectProperty( Property.STATE, "A" );

        /*if ( contentLocation != null && contentLocation.length() > 0)
        {
            String ds = "DS";
            String dsv = "DS1.0";
            doc.addDatastream( ds, State.A, ControlGroup.M, true );
            doc.addDatastreamVersion( ds, dsv, "text/plaintext", "label", 1, date );
            doc.setContentLocation( dsv, contentLocation, "URL" );
        }*/
        
        return doc;
    }


    /*static void testFindObjectPids() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException, ObjectRepositoryException
    {
        fedoraHandle = new FedoraHandle();
        objectRepository = new FedoraObjectRepository();
        List<String> pids = new ArrayList<String>();
        pids = objectRepository.getIdentifiers( "label", 10000 );

        for( String pid: pids )
        {
            System.out.println( pid );
        }
    }*/


    static void testFindObjectRelationships() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        ObjectFields[] objs = null;
        fedoraHandle = new FedoraHandle();
        try
        {
            String[] resultFields =
            {
                "pid", "label", "state", "ownerId", "cDate", "mDate", "dcmDate", "title", "creator", "subject", "description", "publisher", "contributor", "date", "format", "identifier", "source", "language", "relation", "coverage", "rights"
            };

            Condition[] cond = { new Condition( "pid", ComparisonOperator.has, "harry:1" ) };
            FieldSearchQuery fsq = new FieldSearchQuery( cond, null );
            FieldSearchResult fsr = new FieldSearchResult();

            fsr = fedoraHandle.findObjects( resultFields, new NonNegativeInteger( "10000"), fsq);
            objs = fsr.getResultList();
        }
        catch ( RemoteException re )
        {
            re.printStackTrace();
        }

        for ( int i = 0; i < objs.length; i++ )
        {
            System.out.println( "i " + i + " :" + objs[i].getRelation()[0] );
            System.out.println( "i " + i + " :" + objs[i].getRelation()[i] );
            System.out.println( "i " + i + " :" + objs[i].getRelation( i ) );
            System.out.println( "i " + i + " :" + objs[i].getIdentifier()[i] );
            System.out.println( "i " + i + " :" + objs[i].getCreator()[i] );

        }
    }


    static void testGetRelationships() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        RelationshipTuple[] subj = null;
            //String[] resultFields = { "pid", "label", "state", "ownerId", "cDate", "mDate", "dcmDate", "title", "creator", "subject", "description", "publisher", "contributor", "date", "format", "identifier", "source", "language", "relation", "coverage", "rights" };
            subj = fedor.getRelationships( "kkb:1979", "rel:isMemberOfCollection" );
        if ( subj != null )
        {
            for ( int i = 0; i < subj.length; i++ )
            {
                System.out.println( "i " + i + " :" + subj[i].getObject() );
            }
        }
        else
        {
            System.out.println( "subj is null" );
        }
    }


    static void testFindObjectFields() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        TargetFields targetTitle = FedoraObjectFields.TITLE;
        boolean ret = fedor.addIsMbrOfCollRelationship( "harry:1", targetTitle, "Harry Potter", "work" );
        System.out.println( "Everything ok: " + ret );
    }


    static void testFindObjectFieldsDouble() throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        TargetFields targetTitle = FedoraObjectFields.TITLE;
        boolean ret = fedor.addIsMbrOfCollRelationship( "harry:1", targetTitle, "Harry Potter og Fønixordenen", FedoraObjectFields.CREATOR, "Joanne K. Rowling", "work" );
        System.out.println( "Everything ok: " + ret );
    }


    static void testDeleteObjectPids( String[] labels, int runsPerLabel ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {

        for ( String str : labels )
        {
            for ( int i = 0; i < runsPerLabel; i++ )
            {
                
                ObjectFields[] pids = null;
                try
                {
                    String[] resultFields =
                    {
                        "pid"
                    };

                    Condition[] cond =
                    {
                        new Condition( "label", ComparisonOperator.eq, str )
                    };
                    FieldSearchQuery fsq = new FieldSearchQuery( cond, null );
                    FieldSearchResult fsr = new FieldSearchResult();

                    fsr = fedoraHandle.findObjects( resultFields, new NonNegativeInteger( "10000" ), fsq );
                    pids = fsr.getResultList();

                }
                catch ( RemoteException re )
                {
                    re.printStackTrace();
                }

                int pidsSize = pids.length;
                if ( pidsSize > 0 )
                {
                    System.out.println( "testDeleteObjectPids - pids.length: " + pidsSize );
                }
                
                for ( int j = 0; j < pidsSize; j++ )
                {
                    testDeleteObject( pids[j].getPid() );
                }
            }
        }
    }


    static void testDeleteObject( String pid ) throws ConfigurationException, ServiceException, MalformedURLException, IOException, ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        try
        {
            objectRepository.deleteObject( pid, "deleting" );
        }
        catch ( ObjectRepositoryException re )
        {
            System.out.println( "printing trace:" );
            re.printStackTrace();
            return;
        }

        System.out.println( String.format( "Object with pid: %s deleted", pid ) );
    }


    static void testRemoveDataStream( String pid ) throws ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        boolean success = false;
        String sID = DataStreamType.OriginalData.getName() + ".0";
        try
        {
            objectRepository.deleteDataFromObject( sID, sID );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        System.out.println( "testRemoveDataStream succeded" );
    }


//    static void testModifyDataStream( String pid ) throws ObjectRepositoryException
//    {
//        objectRepository = new FedoraObjectRepository();
//
//        CargoObject cargo = null;
//        try
//        {
//            cargo = createCargoObject( 2 );
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//        String sID = DataStreamType.OriginalData.getName() + ".0";
//
//        boolean checksum = false;
//        try
//        {
//            objectRepository.replaceDataInObject( pid, sID, cargo);
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//
//        System.out.println( String.format( "object replaced" ) );
//
//    }


//    static void testAddDataStreamToObject( String pid ) throws ObjectRepositoryException
//    {
//        objectRepository = new FedoraObjectRepository();
//
//        CargoObject cargo = null;
//        try
//        {
//            cargo = createCargoObject( 1 );
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//        try
//        {
//            objectRepository.storeDataInObject( pid, cargo, false, true);
//        }
//        catch ( Exception e )
//        {
//            e.printStackTrace();
//        }
//
//        System.out.println( String.format( "object stored in object '%s'", pid ) );
//
//    }


    static void testGetDataStream( String pid ) throws ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        String streamID = (DataStreamType.OriginalData).getName() + ".0";
        CargoContainer cc = new CargoContainer();
        try
        {
            cc = objectRepository.getDataFromObject( pid, streamID);
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        int count = cc.getCargoObjectCount();
        if ( count < 1 )
        {
            System.out.println( String.format( "kunne ikke finde nogen strømme med id %s", streamID ) );
        }
        else
        {
            System.out.println( String.format( "fandt %s strøm(me) med id %s", count, streamID ) );
        }
    }


    static void testGetDataStreamsOfType( String pid ) throws ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        CargoContainer cc = new CargoContainer();
        DataStreamType dst = DataStreamType.OriginalData;
        try
        {
            cc = objectRepository.getDataFromObject( pid, dst );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        int count = cc.getCargoObjectCount();
        if ( count < 1 )
        {
            System.out.println( "kunne ikke finde nogen af korrekt type!!!!!!!" );
        }
        else
        {
            System.out.println( String.format( "fandt %s strømme med typen %s", count, dst.getName() ) );
        }
    }


    //method for testing the storeCC method
    static String testStoreCC() throws ObjectRepositoryException
    {
        objectRepository = new FedoraObjectRepository();
        System.out.println( "testStoreCC called" );

        CargoContainer cc = new CargoContainer();
        try
        {
            cc = createContainer();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }
        List<CargoObject> coList = cc.getCargoObjects();
        String submitter = coList.get( 0 ).getSubmitter();
        System.out.println( String.format( "trying to store CargoContainer with submitter: %s", submitter ) );

        String format = "testObject for testing FedoraAdministration";
        String pid = "";
        try
        {
            pid = objectRepository.storeObject( cc, format, "auto" );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }


        System.out.println( String.format( "Got the pid: %s back", pid ) );
        return pid;
    }


    //method for testing the getObject method
    static void testGetObject( String pid ) throws ObjectRepositoryException
    {
        System.out.println( String.format( "testGetObject called with pid: %s", pid ) );
        List<CargoObject> coList = new ArrayList<CargoObject>();
        CargoContainer cc = new CargoContainer();
        objectRepository = new FedoraObjectRepository();
        try
        {
            cc = objectRepository.getObject( pid );
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        coList = cc.getCargoObjects();
        String submitter = coList.get( 0 ).getSubmitter();
        System.out.println( String.format( "got CargoContainer with submitter: %s", submitter ) );
    }


    /**
     * Helper methods
     */
    //method for creating a CargoObject
    static CargoObject createCargoObject( int job ) throws IllegalArgumentException, NullPointerException, IOException
    {
        String testStr;
        if ( job == 1 )
        {
            testStr = "added stream";
        }
        else
        {
            testStr = "modified stream";
        }
        byte[] cargoBytes = testStr.getBytes( "UTF-8" );

        CargoContainer cc = new CargoContainer();
        cc.add( DataStreamType.getDataStreamTypeFrom( "originalData" ), "test", "dbc", "eng", "text/xml", "article", cargoBytes );

        return cc.getCargoObject( DataStreamType.OriginalData );
    }


    //method for creating test data, a CargoContainer
    static CargoContainer createContainer() throws IllegalArgumentException, NullPointerException, IOException
    {
        String testStr = "test";
        byte[] cargoBytes = testStr.getBytes( "UTF-8" );

        CargoContainer ret = new CargoContainer();
        ret.add( DataStreamType.getDataStreamTypeFrom( "originalData" ), "test", "dbc", "eng", "text/xml", "article", cargoBytes );

        return ret;
    }


    //method for creating an XML file
    static File createTestXMLFile()
    {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        XMLStreamWriter writer;
        //building theFile
        File theFile = new File( "theFile" );
        theFile.deleteOnExit();
        //build and xml string and make it into a File

        try
        {
            writer = factory.createXMLStreamWriter( new FileOutputStream( theFile ), "UTF-8" );
            writer.writeStartDocument( "UTF-8", "1.0" ); //(encoding, version)
            writer.writeStartElement( "Text" );
            writer.writeCharacters( "created test stream" );
            writer.writeEndElement();
            writer.writeEndDocument();
        }
        catch ( Exception e )
        {
            e.printStackTrace();
        }

        return theFile;
    }


}
