/**
 * \file CompletedTaskTest.java
 * \brief The CompletedTaskTest class
 * \package tests;
 */

package dk.dbc.opensearch.common.types.tests;

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


/** \brief UnitTest for CompletedTask **/

import static org.junit.Assert.*;
import org.junit.*;

import dk.dbc.opensearch.common.types.CompletedTask;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;

import static org.easymock.classextension.EasyMock.*;

public class CompletedTaskTest {

    FutureTask mockFutureTask;


    /**
     * Testing the getters and setters of CompletedTask.
     */
    @Test public void testSettersAndGetters(){
        mockFutureTask = createMock( FutureTask.class );

        //FutureTask testFuture = new FutureTask( new FutureTest( 10f ) );
        float testResult = 10f;
        
        CompletedTask completedTask = new CompletedTask<Float>( mockFutureTask, testResult );
 
        Float result = (Float) completedTask.getResult();
        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( result , testResult, 0f );

        testResult = 30f;
        completedTask = new CompletedTask( mockFutureTask, testResult );
        
        completedTask.setFuture( mockFutureTask );
        completedTask.setResult( testResult );

        result = (Float) completedTask.getResult();
        assertEquals( completedTask.getFuture(), mockFutureTask );
        assertEquals( result, testResult, 0f );
    }
}
