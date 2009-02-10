package dk.dbc.opensearch.common.types.tests;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.UnsupportedEncodingException;
import dk.dbc.opensearch.common.types.CargoMimeType;
import dk.dbc.opensearch.common.types.CargoObjectInfo;;


public class CargoObjectInfoTest{

    CargoMimeType cmt;
    CargoObjectInfo coi;
    String test_submitter = "test_submitter";
    String test_format = "test_format";

    @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring" )
    @Before public void SetUp()throws UnsupportedEncodingException{
        cmt =  CargoMimeType.TEXT_XML;
        //        coi = new CargoObjectInfo( cmt, "test_lang", test_submitter,  test_format, 666 );
    }
    @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring" )
    @Test public void testCorrectnessOfgetSubmitter() {
        assertTrue( test_submitter.equals( coi.getSubmitter() ) );       
    }
    @Ignore( "ignoring until the architecture has been stabilised after the CargoContainer refactoring" )
    @Test public void testCorrectnessOfgetFormat() {
        assertTrue( test_format.equals( coi.getFormat() ) );       
    }
}
