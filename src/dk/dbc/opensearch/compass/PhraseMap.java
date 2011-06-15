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

/**
 * \file PhraseMap.java
 * \brief Builds PhraseMap for normalization and modifies mapping file
 */

package dk.dbc.opensearch.compass;

import dk.dbc.opensearch.common.config.CompassConfig;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.xml.XMLUtils;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.EntityResolver;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 * Singleton class that builds a phraseMap from the mapping file and modifies the
 * mapping file with extra phrase xpaths. The the phraseMap corresponding to a alias
 * in the mapping file can be retrieved with getPhraseMap
 */
public class PhraseMap 
{
    static Logger log = Logger.getLogger(PhraseMap.class);
    static HashMap<String, HashMap<XPathExpression, String>> phraseMap;
    static NamespaceContext nsc = new OpensearchNamespaceContext();
    static private PhraseMap _instance = null;
    
    /**
     * Protected Constructor to implement singleton pattern
     * @param originalCPMPath the path to the original mapping file
     * @param targetCPMFile the path target for the modified mapping file
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    protected PhraseMap( String originalCPMPath, String targetCPMFile )throws ConfigurationException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException, XPathExpressionException
    {
        phraseMap = buildPhraseMap(originalCPMPath, targetCPMFile );
    }

    /**
     * Instance method Used to initialize and obtain the PhraseMap instance
     * @param originalCPMPath the path to the original mapping file
     * @param targetCPMFile the path target for the modified mapping file
     * @return The PhraseMap instance
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    static public PhraseMap instance(String originalCPMPath, String targetCPMFile) throws ConfigurationException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException, XPathExpressionException
    {
        if (null == _instance) {
            _instance = new PhraseMap(originalCPMPath, targetCPMFile);
        }
        return _instance;
    }

    /**
     * Fetches the phraseMap corresponding to the given mapping alias.
     * The returned hashMap contains the phrase mapping where the key is the
     * XPathExpressions obtained from the original mapping file, and the value is
     * is the modified phrase xpaths
     * @param alias The alias the fetch phraseMap for
     * @return Hashmap where the key is the original phrase XPathExpression and the value is the modified path
     */
    static public HashMap<XPathExpression, String> getPhraseMap(String alias)
    {
        return phraseMap.get(alias);
    }

    /**
     * Builds the PhraseMap from originalCPMPath, modifies mapping file and writes
     * it to targetCPMFile
     * @param originalCPMPath the path to the original mapping file
     * @param targetCPMFile the path target for the modified mapping file
     * @return a HashMap where the key is a alias and the value is the corresponding PhraseMap
     * @throws FileNotFoundException
     * @throws IOException
     * @throws ParserConfigurationException
     * @throws SAXException
     * @throws TransformerConfigurationException
     * @throws TransformerException
     * @throws XPathExpressionException
     */
    static private HashMap<String, HashMap<XPathExpression, String>> buildPhraseMap(String originalCPMPath, String targetCPMFile) throws  ConfigurationException, FileNotFoundException, IOException, ParserConfigurationException, SAXException, TransformerConfigurationException, TransformerException, XPathExpressionException
    {
        log.trace( String.format( "read original file %s", originalCPMPath ) );
        InputSource is = new InputSource( new FileInputStream( originalCPMPath ) );

        // Build entityResolver
        String publicUrl = CompassConfig.getHttpUrl();
        String dtdPath = CompassConfig.getDTDPath();
        String systemUrl = "file://" + dtdPath;
        Document cpmDoc = XMLUtils.getDocument(is, new CompassEntityResolver( publicUrl, systemUrl));

        DocumentType docType = cpmDoc.getDoctype();
        String publicID = docType.getPublicId();
        String systemID = docType.getSystemId();

        HashMap<String, HashMap<XPathExpression, String>> phraseMap = new HashMap<String, HashMap<XPathExpression, String>>();

        log.trace( "parse document" );
        NodeList cpmNodeList = cpmDoc.getElementsByTagName( "xml-object" );
        for ( int i = 0; i < cpmNodeList.getLength(); i++ )
        {
            Element cpmNode = (Element) cpmNodeList.item( i );
            log.trace( String.format( "Parsing xml-object with alias='%s'", cpmNode.getAttribute( "alias" ) ) );

            String key = cpmNode.getAttribute( "alias" );
            HashMap<XPathExpression, String> innerMap = new HashMap<XPathExpression, String>();

            NodeList properties = cpmNode.getElementsByTagName( "xml-property" );
            for ( int j = 0; j < properties.getLength(); j++ )
            {
                Element property = (Element) properties.item( j );
                String nameAttribute = property.getAttribute( "name" );

                // Finding mapping name prefix
                String mappingPrefix = "";
                if (! nameAttribute.equals( "" ) && nameAttribute != null ) // mapping name = null means no name in xml.cpm.xml file
                {
                    String[] mappingSplit = nameAttribute.split( "\\." );
                    if ( mappingSplit.length > 1 ) // mapping  name with prefix
                    {
                        mappingPrefix = mappingSplit[0];
                    }
                }

                if ( mappingPrefix.equals( "phrase" ) )
                {
                    String xPathAttribute = property.getAttribute( "xpath" );
                    String[] paths = xPathAttribute.split("\\|");

                    String newXPathAttribute = "";
                    for(String path : paths)
                    {
                        String[] pathSplit = path.split("/");
                        String targetPath = String.format("/%s/phrase",pathSplit[1]);
                        String phraseMapPath = String.format("/%s",pathSplit[1]);

                        for (int k = 2; k < pathSplit.length; k++) {
                            targetPath += String.format("/%s", pathSplit[k]);
                            phraseMapPath += String.format("/%s", pathSplit[k]);
                        }
                        
                        XPath xpath = XPathFactory.newInstance().newXPath();
                        xpath.setNamespaceContext(nsc);
                        XPathExpression xPathExpression = xpath.compile(phraseMapPath);

                        innerMap.put(xPathExpression, targetPath);
                        newXPathAttribute += String.format("| %s", targetPath);
                    }
                    newXPathAttribute = xPathAttribute + newXPathAttribute;
                    log.trace( String.format( "modified xpath '%s'='%s'", xPathAttribute, newXPathAttribute ) );
                    property.setAttribute( "xpath", newXPathAttribute );
                }
            }
            phraseMap.put(key, innerMap);
        }

        log.trace(String.format( "Write new cpm file = '%s'", targetCPMFile ) );
        Source source = new DOMSource( cpmDoc );
        File file = new File( targetCPMFile );
        file.deleteOnExit();
        Result result = new StreamResult(file);
        Transformer transformer = TransformerFactory.newInstance().newTransformer();
        transformer.setOutputProperty(OutputKeys.DOCTYPE_PUBLIC, publicID );
        transformer.setOutputProperty(OutputKeys.DOCTYPE_SYSTEM, systemID );
        transformer.transform( source, result );
        return phraseMap;
    }
}