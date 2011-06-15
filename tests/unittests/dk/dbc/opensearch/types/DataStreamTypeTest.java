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


/** \brief UnitTest for DataStreamType */


package dk.dbc.opensearch.types;


import dk.dbc.opensearch.types.DataStreamType;

import static org.junit.Assert.*;
import org.junit.*;

/**
 * 
 */
public class DataStreamTypeTest {

    DataStreamType dst;

    /**
     * 
     */
    @Test public void testGetDescription() 
    {
        dst = DataStreamType.getDataStreamTypeFrom( "adminData" );
        assertTrue( dst.getDescription().equals( "Administration" ) );
    }

    @Test public void testValidDataStreamType()
    {
        String valid = "adminData";
        String invalid = "invalid";
        assertTrue( DataStreamType.validDataStreamType( valid ) );
        assertFalse( DataStreamType.validDataStreamType( invalid ) );
    }
}