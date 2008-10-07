package dbc.opensearch.components.pti.tests.fedora_test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.DigitalObject;

import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamDef;
import fedora.server.types.gen.DatastreamControlGroup;
//import fedora.server.types.gen.Datastream;

import fedora.server.storage.translation.FOXMLDODeserializer;
import fedora.server.storage.translation.FOXMLDOSerializer;

import fedora.server.storage.translation.DOTranslationUtility;

//import fedora.server.storage.translation.FOXML1_1DOSerializer;

//for MakeBlankRelsExtDatastream:
import fedora.common.Constants;

// for addDatastreamToDO:
import fedora.client.Administrator;

import java.util.Date;
import java.io.ByteArrayOutputStream;

import java.util.HashMap;

import fedora.server.storage.translation.DOTranslatorImpl;


/**
 *
 */
public class DigitalObjectTest {

    fedora.server.access.FedoraAPIA apia;
    fedora.server.management.FedoraAPIM apim;

    public DigitalObjectTest()throws java.net.MalformedURLException, javax.xml.rpc.ServiceException, java.io.IOException{

        fedora.client.FedoraClient fc = new fedora.client.FedoraClient( "http://sempu.dbc.dk:8080/fedora", "fedoraAdmin", "fedora_1");
        apia = fc.getAPIA();
        apim = fc.getAPIM();
    }

    public BasicDigitalObject constructDO(  ){
        
        BasicDigitalObject bdo = new BasicDigitalObject();

        bdo.setPid( "dbc:1" );
        bdo.setState( "Active" );
        bdo.setLabel( "dbc faktalink" );
        bdo.setOwnerId( "fedoraAdmin" );
        bdo.setCreateDate( new Date() );
        bdo.setLastModDate( new Date() );
        bdo.setNew( true );

        bdo.addDatastreamVersion( DigitalObjectTest.MakeBlankRelsExtDatastream(), true );

        String[] altId = new String[]{""};

        bdo.addDatastreamVersion( DigitalObjectTest.constructDatastream( DatastreamControlGroup.M, "", "", altId, "", false, "", "", new Date(), 1l, "", "", "", "") , true );

        return bdo;
    }

    private static Datastream constructDatastream( DatastreamControlGroup controlGroup,
                                                   String ID,
                                                   String versionID,
                                                   String[] altIDs,
                                                   String label,
                                                   boolean versionable,
                                                   String MIMEType,
                                                   String formatURI,
                                                   Date createDate,
                                                   long size,
                                                   String state,
                                                   String location,
                                                   String checksumType,
                                                   String checksum ){

        Datastream ds = new Datastream();

        ds.DSControlGrp   = DatastreamControlGroup.M.toString();
        ds.DatastreamID     = ID;
        ds.DSVersionID      = versionID;
        ds.DatastreamAltIDs = altIDs;
        ds.DSLabel          = label;
        ds.DSVersionable    = versionable;
        ds.DSMIME           = MIMEType;
        ds.DSFormatURI      = formatURI;
        ds.DSCreateDT       = createDate;
        ds.DSSize           = size;
        ds.DSState          = state;
        ds.DSLocation       = location;
        ds.DSChecksumType   = checksumType;
        ds.DSChecksum       = checksum;

        return ds;

    }


    private static Datastream MakeBlankRelsExtDatastream() {

        Datastream ds = new Datastream();

        ds.DSControlGrp   = DatastreamControlGroup.X.toString();
        ds.DatastreamID     = "RELS-EXT";
        ds.DSVersionID      = "RELS-EXT.0";
        ds.DatastreamAltIDs = null;
        ds.DSLabel          = "RDF Statements about this object";
        ds.DSVersionable    = true;
        ds.DSMIME           = "application/rdf+xml";
        ds.DSFormatURI      = Constants.RELS_EXT1_0.uri;
        ds.DSCreateDT       = null;
        ds.DSSize           = 0;
        ds.DSState          = "A";
        ds.DSLocation       = null;
        ds.DSChecksumType   = null;
        ds.DSChecksum       = null;

        return ds;
    }

    private String addDatastreamToExistingPid( String pid,
                                               String dsid,
                                               String[] altIDs,
                                               String label,
                                               boolean versionable,
                                               String mimeType,
                                               String formatURI,
                                               String location,
                                               String state,
                                               String checksumType,
                                               String logMessage) throws java.rmi.RemoteException{

        return apim.addDatastream(pid,
                                  dsid,
                                  altIDs,
                                  label,
                                  versionable, // DEFAULT_VERSIONABLE
                                  mimeType,
                                  formatURI,
                                  location,
                                  "X",
                                  state,
                                  checksumType,
                                  null, // checksum type and checksum
                                  logMessage); // DEFAULT_LOGMESSAGE
    }

    public static void main(String[] args)throws java.net.MalformedURLException, 
                                                 javax.xml.rpc.ServiceException, 
                                                 java.io.IOException,fedora.server.errors.ObjectIntegrityException, 
                                                 fedora.server.errors.StreamIOException,
                                                 fedora.server.errors.UnsupportedTranslationException,
                                                 fedora.server.errors.ServerException{


        DigitalObjectTest dot = new DigitalObjectTest();

        BasicDigitalObject bdo = dot.constructDO();


        ByteArrayOutputStream baos  = new ByteArrayOutputStream();
        FOXMLDODeserializer deser=new FOXMLDODeserializer();
        FOXMLDOSerializer ser=new FOXMLDOSerializer();
        HashMap desermap=new HashMap();
        HashMap sermap=new HashMap();
        sermap.put("foxml1.1", ser);
        desermap.put( "foxml1.1", deser );

        DOTranslatorImpl translator = new DOTranslatorImpl( sermap, desermap );

        int transContext = DOTranslationUtility.SERIALIZE_EXPORT_MIGRATE;

        translator.serialize( bdo, baos, "foxml1.1", "UFT-8", transContext );

        System.out.println( String.format( "DigitalObject:\n%s", baos.toString() ) );

    }

}
