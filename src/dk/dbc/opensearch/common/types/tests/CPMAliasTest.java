package dk.dbc.opensearch.common.types.tests;


import dk.dbc.opensearch.common.types.CPMAlias;

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
