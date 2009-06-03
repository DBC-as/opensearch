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


import dk.dbc.opensearch.common.helpers.XMLFileReader;

import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Element;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import static org.junit.Assert.*;
import org.junit.*;


/** \brief UnitTest for XMLFileReader
 * \todo make it possible to the DocumentBuilderFactory or the DocumentBuilder...
 * If that is not possible it is only possible to test the methods with real files */
public class XMLFileReaderTest 
{
    /**
     *
     */
    @Before 
    public void SetUp() { }


    /**
     *
     */
    @After 
    public void TearDown() { }


    /**
     * 
     */
    @Test 
    public void testGetDocumentElement() { }


    @Test 
    public void testGetNodeList() { }
}