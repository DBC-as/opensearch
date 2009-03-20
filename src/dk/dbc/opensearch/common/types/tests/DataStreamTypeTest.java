package dk.dbc.opensearch.common.types.tests;

import dk.dbc.opensearch.common.types.DataStreamType;

/** \brief UnitTest for DataStreamType */

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class DataStreamTypeTest {

    DataStreamType dst;

    /**
     *
     */
    @Before public void SetUp() {

    }

    /**
     *
     */
    @After public void TearDown() {

    }

    /**
     * 
     */
    @Test public void testGetDescription() 
    {
        dst = DataStreamType.getDataStreamNameFrom( "adminData" );
        assertTrue( dst.getDescription().equals( "Administration" ) );
    }

    @Test public void testValidDataStreamNameType()
    {
        String valid = "adminData";
        String invalid = "invalid";
        assertTrue( DataStreamType.validDataStreamNameType( valid ));
        assertFalse( DataStreamType.validDataStreamNameType( invalid ));
    }
}