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


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


public class XMLFileReader 
{
	Logger log = Logger.getLogger( XMLFileReader.class );
	
	
	public static Element getDocumentElement( InputSource is ) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docFact.newDocumentBuilder();
        Document admDoc = docBuilder.parse( is );
        Element root = admDoc.getDocumentElement();
        
        return root;
	}
	
	
	public static NodeList getNodeList( File xmlFile, String tagName ) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document jobDocument = docBuilder.parse( xmlFile );
        Element xmlRoot = jobDocument.getDocumentElement();

        return xmlRoot.getElementsByTagName( tagName );
	}
}
