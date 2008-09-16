package dbc.opensearch.components.pti.tests.fedora_test;

import fedora.server.storage.types.BasicDigitalObject;
//import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamDef;
import fedora.server.types.gen.DatastreamControlGroup;
import fedora.server.types.gen.Datastream;

import fedora.server.storage.translation.FOXML1_1DOSerializer;

//for MakeBlankRelsExtDatastream:
import fedora.common.Constants;


// for addDatastreamToDO:
import fedora.client.Administrator;



/**
 *
 */
public class DigitalObjectTest {
    /**
     *
     */
    public DigitalObjectTest() {
        BasicDigitalObject bdo = new BasicDigitalObject();
        Datastream ds = new Datastream();
    }

    private static Datastream[] MakeBlankRelsExtDatastream() {
        Datastream[] ds = new Datastream[1];
        ds[0] =
            new Datastream(DatastreamControlGroup.fromValue("X"),
                           "RELS-EXT",
                           "RELS-EXT.0",
                           null,
                           "RDF Statements about this object",
                           true,
                           "application/rdf+xml",
                           Constants.RELS_EXT1_0.uri,
                           null,
                           0,
                           "A",
                           null,
                           null,
                           null);
        return ds;
    }

    private static String addDatastreamToDO(
                                            String pid,
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

        return Administrator.APIM.addDatastream(pid,
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

    public static void main(String[] args){
        DigitalObjectTest dot = new DigitalObjectTest();
    }

}