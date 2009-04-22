/**
 * \file AnnotateTest.java
 * \brief The AnnotateTest class
 * \package tests;
 */

package dk.dbc.opensearch.plugins.tests;

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


/** \brief UnitTest for Annotate **/


import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.plugins.DocbookAnnotate;

import java.io.File;
import java.io.FileInputStream;
import java.net.URI;

import org.junit.*;
import static org.junit.Assert.*;


/**
 *
 */
public class AnnotateTest {

    /**
     *
     */
    @Ignore
    @Test public void testatest() throws Exception {

          File f = new File("/home/shm/amnesty.xml");
          FileInputStream fis = FileHandler.readFile( f.getPath() ); 
          URI uri = new URI( f.getPath() );
          byte[] fb = new byte[(int)f.length()];
          fis.read( fb );

          DatadockJob ddj = new DatadockJob( uri, "dbc", "faktalink", "dbc:100" );
          CargoContainer cc = new CargoContainer();
          cc.add( DataStreamType.OriginalData, "faktalink", "dbc", "da", "text/xml", fb );

          System.out.println("Annotate Test !!!");
          DocbookAnnotate dba = new DocbookAnnotate();

          CargoContainer cc2 = dba.getCargoContainer( cc );
    }
}
