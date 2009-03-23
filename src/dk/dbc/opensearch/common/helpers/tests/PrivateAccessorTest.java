/** \brief UnitTest for PrivateAccessor **/

package dk.dbc.opensearch.common.helpers.tests;

import dk.dbc.opensearch.common.helpers.PrivateAccessor;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * Class that tests the helper PrivateAccessor.
 * It is not possible to generate the IllegalAccessException in the 
 * getPrivateField and invokePrivateMethod methods and likewise with 
 * the InvocationTargetException in the invokePrivateMethod method. 
 * So those lines will remain untested
 */
public class PrivateAccessorTest {

    /**
     * 
     */
    class TestClass
    {
        private String privateString = "private Field";

        private String privateMethod( String testArg )
        {
            // System.out.println( "hello" );
            return privateString;
        }
   } 

    TestClass tc = new TestClass();
    
    @Test public void testGetPrivateField() 
{
    String match = (String)PrivateAccessor.getPrivateField( tc, "privateString" );
    assertEquals( match, "private Field" );
}

    @Test(expected = IllegalArgumentException.class) 
public void testGetPrivateFieldNotExisting() 
    {
     String match = (String)PrivateAccessor.getPrivateField( tc, "notExistingFiledName" );
    }

    @Test public void testInvokePrivateMethod() 
    {
        String match = (String)PrivateAccessor.invokePrivateMethod( tc, "privateMethod", "testarg" );
        assertEquals( match, "private Field" );
    }
  
    @Test(expected=IllegalArgumentException.class)  
        public void testInvokePrivateMethodNotExisting() 
    {
         String match = (String)PrivateAccessor.invokePrivateMethod( tc, "nonExistingMethod", "testarg" );
    }
}