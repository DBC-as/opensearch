/**
 * \file StoreTest.java
 * \brief The StoreTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins.tests;

import dk.dbc.opensearch.common.os.FileHandler;

import dk.dbc.opensearch.plugins.FaktalinkHarvester;
import dk.dbc.opensearch.plugins.RUBHarvester;
import dk.dbc.opensearch.plugins.AllStore;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.types.CargoContainer;
import java.io.IOException;
import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.io.ByteArrayOutputStream;

/** \brief UnitTest for Store **/

import static org.junit.Assert.*;
import org.junit.*;

/**
 *
 */
public class StoreTest {

    /**
     *
     */

    @Test public void test() throws Exception{
        System.out.println( "\nstore TEST" );


        // FAKTALINK

//         URI uri = new URI( "/home/shm/amnesty.xml" );
//         DatadockJob dbj = new DatadockJob( uri, "dbc", "Faktalink", "dbc:100");        
//         FaktalinkHarvester fth = new FaktalinkHarvester();
//         CargoContainer cc = fth.getCargoContainer( dbj );
//         AllStore as = new AllStore();
//         as.storeCargoContainer( cc, dbj );

        // RUB

//         URI uri = new URI( "/home/shm/139/" );
//         DatadockJob dbj = new DatadockJob( uri, "rub", "afhandlinger", "pid:1");
//         RUBHarvester rbh = new RUBHarvester();
//         CargoContainer cc = rbh.getCargoContainer( dbj );
//         AllStore as = new AllStore();
//        as.storeCargoContainer( cc, dbj );

    }
}
