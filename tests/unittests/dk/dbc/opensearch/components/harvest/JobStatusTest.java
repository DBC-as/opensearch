/**
 * \file JobStatusTest.java
 * \brief The JobStatusTest class
 * \package harvest;
 */

package dk.dbc.opensearch.components.harvest;


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

/** \brief UnitTest for JobStatus */

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class JobStatusTest 
{

    JobStatus js;

    @Before 
    public void SetUp() 
    {

    }

  
    @After 
    public void TearDown() 
    {

    }

    /**
     * Tests the getName method
     */
    @Test 
    public void testGetName() 
    {
        String name = "SUCCESS";
        js = JobStatus.getJobStatus( name );
        assertTrue( name.equals( js.getName() ) );
    }

    /**
     * Tests the getdescription method
     */
    @Test
    public void testGetDescription()
    {
        String name = "SUCCESS";
        js = JobStatus.getJobStatus( name );
        assertTrue( name.equals( js.getDescription().toUpperCase() ) );
    }
    
    /**
     * Tests the happy path of the validJobStatus method
     */
    @Test
    public void testHappyValidJobStatus()
    {
        String name = "SUCCESS";
        assertTrue( JobStatus.validJobStatus( name ) );
    }

    /**
     * Tests the validJobStatus method when given an invalid name 
     */
    @Test
    public void testInvalidValidJobStatus()
    {
        String name = "invalid";
        assertFalse( JobStatus.validJobStatus( name ));

    }

}