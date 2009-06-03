/**
 * \file FileHandlerStaticCallTest.java
 * \brief The FileHandlerStaticCallTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.os;

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


/** \brief UnitTest for FileHandlerStaticCall **/
import java.io.File;
import static org.junit.Assert.*;
import org.junit.*;

import junit.framework.TestCase;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.os.FileHandlerStaticCall;
import mockit.Mockit;
import static org.easymock.classextension.EasyMock.*;

/**
 * 
 */
public class FileHandlerStaticCallTest extends TestCase 
{   
//    static File mockFile = createMock( File.class );
//
//    String teststring = "røvbanan !";
//
//
//    public static class MockFileHandler{
//
//        public static File getFile( String path ){
//            
//            return mockFile;
//        }
//    }
//    
//    protected void setUp() throws Exception
//    {
//        super.setUp();
//        Mockit.redefineMethods( FileHandler.class, MockFileHandler.class );
//    }
//    
//
//    
//
//    /**
//     * 
//     */
    @Ignore
    @Test 
    public void teststatictest() 
    {
//        expect( mockFile.getAbsolutePath() ).andReturn( teststring );
//        replay( mockFile );
//    
//        FileHandlerStaticCall fhm = new FileHandlerStaticCall();
//        String returnstr = fhm.testStatic();
//        assertEquals( returnstr, teststring);
//        verify( mockFile ); 
    }
}
