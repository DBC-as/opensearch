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
 * \file NormalizeDocument.java
 * \brief plugin used to copy and normalize fields
 */
package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.compass.PhraseMap;
import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.pluginframework.IPluggable;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.string.StringUtils;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import dk.dbc.opensearch.common.xml.XMLUtils;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;


/**
 * Plugin used to copy and normalize fields. Implements IProcesser
 */
public class NormalizeDocument implements IPluggable
{

    static Logger log = Logger.getLogger( NormalizeDocument.class );
    private PluginType pluginType = PluginType.PROCESS;
    private IObjectRepository objectRepository;

    public NormalizeDocument( IObjectRepository repository )
    {
        this.objectRepository = repository;
    }


    /**
     * Returns Modified cargoContainer.
     * This method parses the OriginalData from the cargoContainer and if it contains
     * Characters which should be normalized, it parses the document and add
     * normalized mirrored phrasenodes
     * @param cargoContainer original CargoContainer
     * @return Modified CargoContainer
     * @throws PluginException
     */
    @Override
    public CargoContainer getCargoContainer( CargoContainer cargoContainer, Map<String, String> argsMap ) throws PluginException
    {
        log.trace( "Retrieving orignial Data from cargoContainer" );
        CargoObject co = cargoContainer.getCargoObject( DataStreamType.OriginalData );
        if( co == null )
        {
            String error = "Could not retrieve CargoObject with original data from CargoContainer";
            log.error( error );
            throw new PluginException( String.format( error ) );
        }

        byte[] b = co.getBytes();
        ByteArrayInputStream bis = new ByteArrayInputStream( b );
        Document doc = null;

        try
        {
            doc = XMLUtils.getDocument( b );
        }
        catch( ParserConfigurationException pce )
        {
            String error = String.format( "Could not contruct the objects for reading/parsing the : %s", pce.getMessage() );
            log.error( error, pce );
            throw new PluginException( error, pce );
        }
        catch( SAXException se )
        {
            String error = String.format( "Could not parse document: %s", se.getMessage() );
            log.error( error, se );
            throw new PluginException( error, se );
        }
        catch( IOException ioe )
        {
            String error = String.format( "Could not read Document: %s", ioe.getMessage() );
            log.error( error, ioe );
            throw new PluginException( error, ioe );
        }

        // build replaceMap
        HashMap<String, String> replaceMap = new HashMap<String, String>();
        replaceMap.put( "\uA732", "AA" );
        replaceMap.put( "\uA733", "aa" );

        // Check if Document contains characters that need normalization
        if( StringUtils.contains( doc.getDocumentElement().getTextContent(), replaceMap ) )
        {
            log.trace( "document contains characters that need normalization" );
            HashMap<XPathExpression, String> map = PhraseMap.getPhraseMap( co.getIndexingAlias() );
            InputSource is = new InputSource( bis );

            for( XPathExpression orgPath : map.keySet() )
            {
                bis.reset();
                String pathValue = null;
                try
                {   // Evaluate xpath
                    NodeList nodeList = (NodeList) orgPath.evaluate( is, XPathConstants.NODESET );
                    for( int i = 0; i < nodeList.getLength(); i++ )
                    {
                        Node valueNode = nodeList.item( i );
                        pathValue = valueNode.getTextContent();

                        if( pathValue != null && StringUtils.contains( pathValue, replaceMap ) )
                        {
                            log.trace( "phrase contains characters that need normalization" );
                            String newPath = map.get( orgPath );
                            log.trace( String.format( "newPath: '%s'", newPath ) );
                            String[] pathSplit = newPath.split( "/" );
                            Element elem = doc.getDocumentElement();
                            // Build node
                            for( int j = 2; j < pathSplit.length - 1; j++ )
                            {
                                NodeList children = elem.getElementsByTagName( pathSplit[j] );
                                if( children.getLength() > 0 )
                                {
                                    elem = (Element) children.item( 0 );
                                }
                                else
                                {
                                    log.trace( String.format("create Node from string='%s' - trimmed='%s'",pathSplit[j],pathSplit[j].trim() ) );
                                    String name = pathSplit[j].trim();

                                    // if the path part contains * it is substituted with phraseCopy
                                    // should also see if an attribute is present and add that

                                    String attributeName= "";//tag = "";
                                    String attributeValue= "";
                                    if( name.contains( "[@" ) && name.contains( "]" ) )
                                    {
                                        int beg = name.indexOf("[@")+2;
                                        int eq = name.indexOf("=");
                                        int end = name.indexOf("']");
                                        attributeName = name.substring(beg, eq);
                                        attributeValue = name.substring(eq+2, end);
                                        log.trace( String.format( "attribute: ['%s'='%s']", attributeName, attributeValue) );
                                    }

                                    if( name.contains( "*" ) )
                                    {
                                        name = "phraseCopy";
                                    }

                                    log.trace( String.format( "NAME: %s", name ) );
                                    Element newElem = doc.createElement( name );
                                    if(! attributeName.equals("") )
                                    {
                                        newElem.setAttribute( attributeName, attributeValue );
                                    }

                                    elem.appendChild( newElem );
                                    elem = newElem;
                                }
                            }
                            // build and append text node
                            String elementName = pathSplit[pathSplit.length - 1].trim();

                            // if the path part contains * it is substituted with phraseCopy
                            // should also see if an attribute is present and add that
                            
                            String innerAttributeName= "";
                            String innerAttributeValue= "";
                            if( elementName.contains( "[@" ) && elementName.contains( "]" ) )
                            {
                                int beg = elementName.indexOf("[@")+2;
                                int eq = elementName.indexOf("=");
                                int end = elementName.indexOf("']");
                                innerAttributeName = elementName.substring(beg, eq);
                                innerAttributeValue = elementName.substring(eq+2, end);
                                log.trace( String.format( "inner attribute: ['%s'='%s']", innerAttributeName, innerAttributeValue) );
                            }

                            if( elementName.contains( "*" ) )
                            {
                                elementName = "phraseCopy";
                            }

                            Element newElem = doc.createElement( elementName );
                            if(! innerAttributeName.equals("") )
                            {
                                newElem.setAttribute( innerAttributeName, innerAttributeValue );
                            }

                            elem.appendChild( newElem );
                            elem = newElem;
                            String valueText = StringUtils.replace( pathValue, replaceMap );
                            elem.setTextContent( valueText );
                            log.trace( String.format( "Appended element='%s' with value='%s'", elementName, valueText ) );
                        }
                    }
                }
                catch( XPathExpressionException xpe )
                {
                    String error = String.format( "Could not evaluate xpath: %s", xpe.getMessage() );
                    log.error( error, xpe );
                    throw new PluginException( error, xpe );
                }

            }
            cargoContainer.remove( co.getId() );

            //save modified cargo
            try
            {
                cargoContainer.add( DataStreamType.OriginalData,
                                    co.getFormat(),
                                    co.getSubmitter(),
                                    co.getLang(),
                                    co.getMimeType(),
                                    co.getIndexingAlias(),
                                    XMLUtils.getByteArray( doc.getDocumentElement() ) );
            }
            catch( IOException ioe )
            {
                String error = String.format( "Could not add cargoObject to cargoContainer: %s", ioe.getMessage() );
                log.error( error, ioe );
                throw new PluginException( error, ioe );
            }
            catch( TransformerException te )
            {
                String error = String.format( "Could not add cargoObject to cargoContainer: %s", te.getMessage() );
                log.error( error, te );
                throw new PluginException( error, te );
            }
        }
        return cargoContainer;
    }


    /**
     * Returns PluginType
     * @return PluginType
     */
    public PluginType getPluginType()
    {
        return pluginType;
    }
}
