package dk.dbc.opensearch.common.types.tests;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import dk.dbc.opensearch.common.types.CPMAlias;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import org.xml.sax.SAXException;


public class CPMAliasTest 
{
	@Test
	public void cpmIsValidAliasTest() throws ParserConfigurationException, SAXException, IOException
	{
		boolean valid = CPMAlias.isValidAlias( "faktalink" );
		assertTrue( valid );
	}
	
	
	@Test
	public void cpmIsValidAliasFailTest() throws ParserConfigurationException, SAXException, IOException
	{
		boolean inValid = CPMAlias.isValidAlias( "fejlmester" );
		assertFalse( inValid );
	}
}
