package dbc.opensearch.components.pti.tests.fedora_test;

import fedora.server.storage.types.BasicDigitalObject;
import fedora.server.storage.types.Datastream;
import fedora.server.storage.types.DatastreamDef;

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
        DatastreamDef dsd = new DatastreamDef();


    }

    private static Datastream[] MakeBlankRelsExtDatastream() {
        Datastream[] ds = new Datastream[1];
        ds[0] =
            new Datastream(DatastreamControlGroup.fromValue("X"),
                           s_dsid,
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
                                            Char state,
                                            String checksumType,
                                            String logMessage){

        String newID =
            Administrator.APIM.addDatastream(m_pid,
                                             s_dsid,
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

}