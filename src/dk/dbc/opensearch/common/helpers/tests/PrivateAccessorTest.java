/** \brief UnitTest for PrivateAccessor **/

package dk.dbc.opensearch.common.helpers.tests;

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