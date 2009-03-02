package dk.dbc.opensearch.common.types;


import dk.dbc.opensearch.common.config.FileSystemConfig;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


public class CPMAlias
{
    DocumentBuilderFactory docBuilderFactory;
    DocumentBuilder docBuilder;
    Document cpmDocument;
    String cpmFile;
    NodeList cpmNodeList;

    
    public CPMAlias() throws ParserConfigurationException, SAXException, IOException
    {    	
    	docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        cpmFile = FileSystemConfig.getFileSystemCpmPath();
        cpmDocument = docBuilder.parse( cpmFile );
        Element xmlRoot = cpmDocument.getDocumentElement();
        cpmNodeList = xmlRoot.getElementsByTagName( "xml-object" );
    }
    

    public boolean isValidAlias( String alias ) throws ParserConfigurationException, SAXException, IOException
    {
        
        for( int i = 0; i < cpmNodeList.getLength(); i++ )
        {
            Element aliasNode = (Element)cpmNodeList.item( i );
            String aliasValue = aliasNode.getAttribute( "alias" );
            if ( aliasValue.equals( alias ) )
            {
                return true;
            }
        }

        return false;
    }
}
