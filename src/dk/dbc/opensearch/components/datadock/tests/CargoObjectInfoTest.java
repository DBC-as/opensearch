package dk.dbc.opensearch.components.datadock.tests;

import static org.junit.Assert.*;
import org.junit.*;
import java.io.UnsupportedEncodingException;
import dk.dbc.opensearch.components.datadock.*;

public class CargoObjectInfoTest{

    CargoMimeType cmt;
    CargoObjectInfo coi;
    String test_submitter = "test_submitter";
    String test_format = "test_format";

    @Before public void SetUp()throws UnsupportedEncodingException{
        cmt =  CargoMimeType.TEXT_XML;
        coi = new CargoObjectInfo( cmt, "test_lang", test_submitter,  test_format, 666 );
    }
    @Test public void testCorrectnessOfgetSubmitter() {
        assertTrue( test_submitter.equals( coi.getSubmitter() ) );       
    }
    @Test public void testCorrectnessOfgetFormat() {
        assertTrue( test_format.equals( coi.getFormat() ) );       
    }
}
