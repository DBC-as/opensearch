package dk.dbc.opensearch.common.helpers;


import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class XMLFileReader 
{
	Logger log = Logger.getLogger( XMLFileReader.class );
	
	
	public static NodeList getNodeList( File xmlFile, String tagName ) throws ParserConfigurationException, SAXException, IOException
	{
		DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document jobDocument = docBuilder.parse( xmlFile );
        Element xmlRoot = jobDocument.getDocumentElement();

        return xmlRoot.getElementsByTagName( tagName );
	}
}
