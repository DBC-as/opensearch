/** \brief UnitTest for TasksNotValidatedException */

package dk.dbc.opensearch.common.pluginframework.tests;

import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;

import java.io.IOException;
import java.util.Vector;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class TasksNotValidatedExceptionTest {

    TasksNotValidatedException tnve;
    Vector<String> expVector;
    //    Vector<String> testVector;

    @Before public void setUp()
    {
        expVector = new Vector<String>();
        expVector.add( "testTask" );
    }
    /**
     * 
     */
    @Test public void testConstructor() 
    {
        tnve = new TasksNotValidatedException( expVector, "testMessage" );
        assertTrue( tnve.getExceptionVector() == expVector );
        assertEquals( tnve.getMessage(), "testMessage" );


    }
}