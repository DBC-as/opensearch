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

package dk.dbc.opensearch.common.compass;


import dk.dbc.opensearch.common.compass.CPMAlias;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.xml.sax.SAXException;


public class CPMAliasTest 
{
    /**
      * This test is not a strict unittest because we are dependant on a file on the disc.
      */
    CPMAlias cpmAlias;
    @Before
    public void setUp() throws Exception
    {
       	cpmAlias = new CPMAlias();
    }

    
    @After
    public void tearDown() throws Exception
    {
        cpmAlias = null;
    }

    
	@Test
	public void cpmIsValidAliasTest() throws ParserConfigurationException, SAXException, IOException
	{
            boolean valid = cpmAlias.isValidAlias( "article" );
            assertTrue( valid );
    }
	
	
	@Test
	public void cpmIsValidAliasFailTest() throws ParserConfigurationException, SAXException, IOException
	{
		boolean inValid = cpmAlias.isValidAlias( "fejlmester" );
		assertFalse( inValid );
	}
}
