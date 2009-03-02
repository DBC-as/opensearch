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
    static DocumentBuilderFactory docBuilderFactory;
    static DocumentBuilder docBuilder;
    static Document cpmDocument;
    static String cpmFile;


    public static boolean isValidAlias( String alias ) throws ParserConfigurationException, SAXException, IOException
    {
        docBuilderFactory = DocumentBuilderFactory.newInstance();
        docBuilder = docBuilderFactory.newDocumentBuilder();
        cpmFile = FileSystemConfig.getFileSystemCpmPath();
        cpmDocument = docBuilder.parse( cpmFile );
        Element xmlRoot = cpmDocument.getDocumentElement();
        NodeList cpmNodeList = xmlRoot.getElementsByTagName( "xml-object" );

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
