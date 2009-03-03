/**
 * \file AnnotateTest.java
 * \brief The AnnotateTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins.tests;


/** \brief UnitTest for Annotate **/

import static org.junit.Assert.*;
import org.junit.*;
import java.io.File;
import java.net.URI;

import dk.dbc.opensearch.plugins.DocbookAnnotate;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.os.FileHandler;

import java.io.FileInputStream;
/**
 *
 */
public class AnnotateTest {

    /**
     *
     */
    @Test public void testatest() throws Exception {

//          File f = new File("/home/shm/amnesty.xml");
//          FileInputStream fis = FileHandler.readFile( f.getPath() ); 
//          URI uri = new URI( f.getPath() );
//          byte[] fb = new byte[(int)f.length()];
//          fis.read( fb );

//          DatadockJob ddj = new DatadockJob( uri, "dbc", "faktalink", "dbc:100" );
//          CargoContainer cc = new CargoContainer();
//          cc.add( DataStreamNames.OriginalData, "faktalink", "dbc", "da", "text/xml", fb );

//          System.out.println("Annotate Test !!!");
//          DocbookAnnotate dba = new DocbookAnnotate();

//          CargoContainer cc2 = dba.getCargoContainer( cc );
    }
}
