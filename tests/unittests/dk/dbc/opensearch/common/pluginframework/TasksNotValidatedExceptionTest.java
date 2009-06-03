/** 
 * \brief UnitTest for TasksNotValidatedException 
 */

package dk.dbc.opensearch.common.pluginframework;


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


import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;

import java.io.IOException;
import java.util.Vector;

import static org.junit.Assert.*;
import org.junit.*;


/**
 * 
 */
public class TasksNotValidatedExceptionTest 
{
    TasksNotValidatedException tnve;
    Vector<String> expVector;
    //    Vector<String> testVector;


    @Before 
    public void setUp()
    {
        expVector = new Vector<String>();
        expVector.add( "testTask" );
    }


    /**
     * 
     */
    @Test 
    public void testConstructor() 
    {
        tnve = new TasksNotValidatedException( expVector, "testMessage" );
        assertTrue( tnve.getExceptionVector() == expVector );
        assertEquals( tnve.getMessage(), "testMessage" );
    }
}