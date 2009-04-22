/** \brief UnitTest for PluginFileFilter */
package dk.dbc.opensearch.common.os.tests;

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

import dk.dbc.opensearch.common.os.PdfFileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class PdfFileFilterTest {

    /**
     *
     */
    PdfFileFilter pff;
    static File pdfDir = new File( "pdfDir" );
    static File pdfFile;
    static File otherFile;
    static File dir2; 
    

    @Before public void SetUp() 
    {
        pff = new PdfFileFilter();
        pdfDir.mkdir();
        pdfFile = new File( pdfDir, "test.pdf"); 
        otherFile = new File( pdfDir, "notplugin" );
    }

    /**
     *
     */
    @After public void TearDown() 
    {
        pdfDir.delete();
    }

    /**
     * 
     */
    @Test public void testAcceptsPdfFiles()
    {
        assertTrue( pff.accept( pdfDir, pdfFile.getName() ) );
    }

@Test public void testRejectsNonPdfFiles()
    {
        assertFalse( pff.accept( pdfDir, otherFile.getName() ) );
    }

    @Test public void testRejectsDirectories()
    {
        dir2 = new File( pdfDir, "testdir" );
        dir2.mkdir();
        //System.out.println( (new File( pluginDir, dir2.getName() )).isDirectory() );
        assertFalse( pff.accept( pdfDir, dir2.getName() ) );
        dir2.delete();
    }

    @Test( expected = NullPointerException.class ) 
    public void testNPException1()
    {
        otherFile = null;
        pff.accept( otherFile, pdfFile.getName() );
    }
    @Test( expected = NullPointerException.class )  
        public void testNPException2()
    {
        String hat = null;
        pff.accept( pdfDir, hat );
    }
    
}