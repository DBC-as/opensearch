package dk.dbc.opensearch.common.helpers;


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

import dk.dbc.opensearch.common.helpers.PairComparator_FirstString;
import dk.dbc.opensearch.common.types.InputPair;

import static org.junit.Assert.*;
import org.junit.*;


/** \brief UnitTest for PairComparator_FirstString */
public class PairComparator_FirstStringTest 
{
    PairComparator_FirstString pcfs;
    InputPair<String, Integer> small;
    InputPair<String, Integer> large;
 

    /**
     *
     */
    @Before 
    public void SetUp() 
    {
        pcfs = new PairComparator_FirstString();
    }


    /**
     *
     */
    @After 
    public void TearDown() { }


    /**
     * 
     */
    @Test 
    public void testConstructor() 
    {
        assertTrue( pcfs != null );
    }


    @Test 
    public void testcompareLargerThan()
    {
        small = new InputPair<String, Integer>( "a", 1 );
        large = new InputPair<String, Integer>( "b", 1 );
        
        assertTrue( pcfs.compare( large, small ) > 0 );
    }
    

    @Test 
    public void testcompareSmallerThan()
    {
            small = new InputPair<String, Integer>( "a", 1 );
            large = new InputPair<String, Integer>( "b", 1 );
            
            assertTrue( pcfs.compare( small, large ) < 0 );
    }


    @Test 
    public void testcompareEquals()
    {
            small = new InputPair<String, Integer>( "a", 1 );

            assertTrue( pcfs.compare( small, small ) == 0 );
    }
}