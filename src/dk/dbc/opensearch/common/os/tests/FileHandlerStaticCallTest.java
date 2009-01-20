/**
 * \file FileHandlerStaticCallTest.java
 * \brief The FileHandlerStaticCallTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.os.tests;


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
public class FileHandlerStaticCallTest extends TestCase {
    
    
    
    static File mockFile = createMock( File.class );

    String teststring = "røvbanan !";


    public static class MockFileHandler{

        public static File getFile( String path ){
            
            return mockFile;
        }
    }
    
    protected void setUp() throws Exception
    {
        super.setUp();
        Mockit.redefineMethods( FileHandler.class, MockFileHandler.class );
    }
    

    

    /**
     * 
     */
    @Test public void teststatictest() {
        expect( mockFile.getAbsolutePath() ).andReturn( teststring );
        replay( mockFile );
    
        FileHandlerStaticCall fhm = new FileHandlerStaticCall();
        String returnstr = fhm.testStatic();
        assertEquals( returnstr, teststring);
        verify( mockFile ); 
    }
}
