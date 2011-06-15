package dk.dbc.opensearch.os;

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
*//** \brief UnitTest for FileFilter */


import dk.dbc.opensearch.os.XmlFileFilter;

import static org.junit.Assert.*;
import org.junit.*;

import java.io.File;
import java.io.IOException;


/**
 *
 */
public class XmlFileFilterTest 
{
    XmlFileFilter xff;
    static File xmlFile;
    static File xmlDir;
    static File otherFile;


    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before 
    public void SetUp() 
    {
        xmlDir = new File( "xmlDir" );
        xmlDir.deleteOnExit();
        xmlDir.mkdir();
        xmlFile = new File( xmlDir, ".xml" );
        xmlFile.deleteOnExit();
        otherFile = new File( xmlDir, "notXml" );
        otherFile.deleteOnExit();
        xff = new XmlFileFilter();
    }


    /**
     * After each test the xmlDir directory is removed
     */
    @After public void TearDown() {
        try{
            xmlDir.delete();
        }catch( Exception e ){}
    }

    /**
     * Files with .xml suffix should be accepted
     */
    @Test 
    public void testXmlNameAccepted() 
    {
        assertTrue( xff.accept( xmlDir, xmlFile.getName() ) );
    }


    /**
     * Files without .xml suffix should not be accepted
     */
    @Test 
    public void testNoXmlSuffixNotAccepted()
    {
        assertFalse( xff.accept( xmlDir, otherFile.getName() ) );
    }


    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        otherFile.mkdir();
        assertFalse( xff.accept( xmlDir, otherFile.getName() ) );
        otherFile.delete();
    }


    /**
     * if dir- or filename is null, java.io.File must throw
     */
    @Test(expected = NullPointerException.class) 
    public void testNullValueForFilenameShouldFail()
    {
        String hat = null;
        xff.accept( xmlDir, hat );
    }


    @Test(expected = NullPointerException.class) 
    public void testNullValueForDirShouldFail()
    {
        xmlDir = null;
        xff.accept( xmlDir, xmlFile.getName() );    
    }
}