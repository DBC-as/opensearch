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

import dk.dbc.opensearch.common.config.CompassConfig;

import java.io.ByteArrayInputStream;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import mockit.Mock;
import mockit.MockClass;
import mockit.Mocked;
import static mockit.Mockit.setUpMocks;
import static mockit.Mockit.tearDownMocks;

import org.junit.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class CPMAliasTest 
{
    private static NodeList getNodeList() throws Exception
    {
        String xsemxml = "<compass-core-mapping><xml-object alias=\"article\" sub-index=\"opensearch-index\"></xml-object></compass-core-mapping>";
        DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document jobDocument = docBuilder.parse( new ByteArrayInputStream( xsemxml.getBytes() ) );
        Element xmlRoot = jobDocument.getDocumentElement();
        NodeList ret = xmlRoot.getElementsByTagName( "xml-object" );
        return ret;
    }

    
    @MockClass( realClass=CompassConfig.class)
    public static class MockCompassConfig
    {
         @Mock public static String getXSEMPath(){ return "mockPath"; }
         @Mock public static String getHttpUrl(){ return "httpurl";}
         @Mock public static String getDTDPath(){ return "dtdpath"; }
    }


    @MockClass( realClass = CPMAlias.class )
    public static class MockCPMAlias
    {
        @Mocked
        NodeList cpmNodeList;
        
        @Mock public void $init() throws Exception{}
    }

    CPMAlias cpmAlias;

    @Before
    public void setUp() throws Exception
    {
        setUpMocks( MockCompassConfig.class );
        setUpMocks(MockCPMAlias.class);
    }

    @After
    public void tearDown() throws Exception
    {
        tearDownMocks();
        cpmAlias = null;
    }

    
	@Test
	public void cpmIsValidAliasTest() throws Exception
	{
       	cpmAlias = new CPMAlias();
        //injecting our own list, as the mock constructor has not created one
        cpmAlias.cpmNodeList = CPMAliasTest.getNodeList();
        boolean valid = cpmAlias.isValidAlias( "article" );
        assertTrue( valid );
    }
	
	
	@Test
	public void cpmIsValidAliasFailTest() throws Exception
	{
       	cpmAlias = new CPMAlias();
        //injecting our own list, as the mock constructor has not created one
        cpmAlias.cpmNodeList = CPMAliasTest.getNodeList();
		boolean inValid = cpmAlias.isValidAlias( "fejlmester" );
		assertFalse( inValid );
	}
}
