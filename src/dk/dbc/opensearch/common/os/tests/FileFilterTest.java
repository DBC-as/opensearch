/**
 * \file FileFilterTest.java
 * \brief The FileFilterTest class
 * \package tests;
 */

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


import dk.dbc.opensearch.common.os.FileFilter;

import java.io.File;

import static org.junit.Assert.*;
import org.junit.*;


/**
 *
 */
public class FileFilterTest 
{
    FileFilter ff;
    static String dir = ".shouldnotbeaccepted";
    static String testString = "test"; 
    static File dummy = null;
    static File dummyChild = null;

    
    /**
     * Before each test we construct a dummy directory path and a
     * clean FileFilter instance
     */
    @Before 
    public void SetUp() 
    {
        dummy = new File( dir );
        dummy.mkdir();
        dummyChild = new File( dummy, testString );
        dummyChild.mkdir();
        ff = new FileFilter();
    }

    
    /**
     * After each test the dummy directory is removed
     */
    @After 
    public void TearDown() 
    {
        try
        {
            dummyChild.delete();
            dummy.delete();
        }
        catch( Exception e ) 
        {
        	// do nothing!
        }
    }

    
    /**
     * Files or dirs beginning with a '.' should not be accepted
     */
    @Test 
    public void testDotFileOrDirNotAccepted() 
    {
        assertFalse( ff.accept( dummy, dir ) );
    }

    
    /**
     * Files not beginning with a '.' should be accepted
     */
    @Test 
    public void testNonDotFileOrDirAccepted()
    {
        assertTrue( ff.accept( dummy, "arbitraryname" ) );
    }

    
    /**
     * directories should not be accepted
     */
    @Test public void testDirsNotAccepted()
    {
        assertFalse( ff.accept( dummy, dummyChild.getName() ) );
    }

    /**
     * if dir- or filename is null, java.io.File must throw
     */
    @Test(expected = NullPointerException.class) 
    public void testNullValueForFilenameShouldFail()
    {
        assertFalse( ff.accept( new File( "idontexist" ), null ) );
    }
}