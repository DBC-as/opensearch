package dk.dbc.opensearch.components.pti.tests;
/** \brief UnitTest for PTI class */

import dk.dbc.opensearch.components.pti.PTI;

import dk.dbc.opensearch.common.fedora.FedoraHandler;
import dk.dbc.opensearch.common.statistics.Estimate;
import dk.dbc.opensearch.common.db.Processqueue;
import dk.dbc.opensearch.components.datadock.CargoContainer;

import org.junit.*;
import static org.junit.Assert.*;

import static org.easymock.classextension.EasyMock.*;


import java.util.concurrent.*;
import java.util.Date;

import dk.dbc.opensearch.common.helpers.PrivateAccessor;

import org.apache.log4j.Logger;

import org.compass.core.Compass;
import org.compass.core.CompassSession;
import org.compass.core.Resource;
import org.compass.core.CompassTransaction;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.dom4j.Dom4jAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;
import org.compass.core.CompassException;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import java.io.BufferedInputStream;

public class PTITest {

    Logger log = Logger.getLogger("PTITest");
        
    /**
     * The (mock)objects we need for the most of the tests
     */
    
    CompassSession mockCompassSession;
    CompassTransaction mockCompassTransaction;
    Resource mockResource;
    FedoraHandler mockFedoraHandler; 
    Estimate mockEstimate;
    AliasedXmlObject mockAliasedXmlObject;
    SAXReader mockSAXReader;
    Date mockDate;
    CargoContainer mockCargoContainer;
    Document mockDocument;

    PTI pti;

    BufferedInputStream mockBis;

    @Before public void Setup(){
        
        mockCompassSession = createMock( CompassSession.class );
        mockResource = createMock( Resource.class );
        mockCompassTransaction = createMock( CompassTransaction.class );
        mockFedoraHandler = createMock( FedoraHandler.class );
        mockEstimate = createMock( Estimate.class );
        mockAliasedXmlObject = createMock( AliasedXmlObject.class );
        mockSAXReader = createMock( SAXReader.class );
        mockDate = createMock( Date.class ); 
        mockCargoContainer = createMock( CargoContainer.class );
        mockDocument = createMock( Document.class );
        mockBis = createMock( BufferedInputStream.class );
    }
    
    @Test public void ContructorTest(){
        try{
            pti = new PTI( mockCompassSession, "test_fedoraHandle", "test_itemID", mockFedoraHandler, mockEstimate);
        }catch(Exception e){
            fail( String.format( "Caught Exception %s", e.getMessage() ) );
        }        
    }    
    
    @Test public void callTest() throws Exception {
            
        pti = new PTI( mockCompassSession, "test_fedoraHandle", "test_itemID", mockFedoraHandler, mockEstimate);

        String test_fedoraHandle = "test_handle";
        String test_datastreamItemID = "test_ID";
        String cc_mimetype = "test_mime";
        String cc_submitter = "test_submitter";
        int cc_length = 42;
        String test_format = "test_format";
        Element elem = null;
        long p_time = 10000l;
        long p_length = 1000l;
        long p_diff = p_time - p_length;
        
        expect( mockFedoraHandler.getDatastream( test_fedoraHandle, test_datastreamItemID ) ).andReturn( mockCargoContainer ); 

        expect( mockCargoContainer.getMimeType() ).andReturn( cc_mimetype ).times( 3 );
        expect( mockCargoContainer.getSubmitter() ).andReturn( cc_submitter );
        expect( mockCargoContainer.getStreamLength() ).andReturn( cc_length ).times( 3 );
        expect( mockCargoContainer.getData() ).andReturn( mockBis );

        expect( mockSAXReader.read( mockBis ) ).andReturn( mockDocument );

        expect( mockCargoContainer.getFormat() ).andReturn( test_format );
        expect( mockDocument.getRootElement() ).andReturn( null );
        expect( mockCompassSession.beginTransaction() ).andReturn( mockCompassTransaction );

        mockCompassSession.save( isA( AliasedXmlObject.class ) );

        expect( mockAliasedXmlObject.getAlias() ).andReturn( isA( String.class ) );

        expect( mockCompassSession.loadResource( test_format, isA( Dom4jAliasedXmlObject.class ) ) ).andReturn( mockResource );

        mockCompassSession.save( isA( Resource.class ) );

        mockCompassTransaction.commit();
        mockCompassSession.close();
        
        expect( mockDate.getTime() ).andReturn( p_time );
        expect( mockCargoContainer.getTimestamp() ).andReturn( p_length );

        mockEstimate.updateEstimate( cc_mimetype, cc_length, p_time - p_length );
        
        replay( mockFedoraHandler );
        replay( mockCargoContainer );
        replay( mockSAXReader );
        replay( mockDocument );
        replay( mockCompassSession );
        replay( mockDate );
        
        long returnval = pti.call( mockSAXReader, mockDate, mockFedoraHandler, test_fedoraHandle, test_datastreamItemID );

        replay(mockEstimate);

        verify(mockCompassSession);
        verify(mockFedoraHandler);
        //        verify(mockEstimate);
        verify( mockSAXReader );
        verify( mockDate );
        verify( mockCargoContainer );
        verify( mockDocument );
        
        assertTrue( p_time - p_length == returnval );
    }
}
