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
 * \file
 * \brief
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.fedora.IObjectRepository;
import dk.dbc.opensearch.common.helpers.OpensearchNamespaceContext;
import dk.dbc.opensearch.common.metadata.DublinCore;
import dk.dbc.opensearch.common.metadata.DublinCoreElement;
import dk.dbc.opensearch.common.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;

import java.io.ByteArrayInputStream;
import java.util.Map;
import javax.xml.namespace.NamespaceContext;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.apache.log4j.Logger;
import org.xml.sax.InputSource;


public class XMLDCHarvesterEnvironment implements IPluginEnvironment
{

    private static Logger log = Logger.getLogger( XMLDCHarvester.class );

    private IObjectRepository repository;

    public XMLDCHarvesterEnvironment( IObjectRepository repository, Map<String, String> args ) throws PluginException
    {
        this.repository = repository;
    }



    public CargoContainer run( CargoContainer cargo ) throws PluginException
    {
        log.trace( "Constructing DC datastream" );

        DublinCore dcStream = createDublinCore( cargo );

        log.debug( String.format( "MH cargo dcTitle '%s'", dcStream.getDCValue( DublinCoreElement.ELEMENT_TITLE ) ) );
        cargo.addMetaData( dcStream );

        log.trace(String.format( "num of objects in cargo: %s", cargo.getCargoObjectCount() ) );

        log.trace(String.format( "CargoContainer has DublinCore element == %s", cargo.getDublinCoreMetaData().elementCount() != 0 ) );

        return cargo;
    }

    private String getDCVariable( byte[] bytes, String xPathStr ) throws PluginException
    {
        NamespaceContext nsc = new OpensearchNamespaceContext();
        XPath xpath = XPathFactory.newInstance().newXPath();
        xpath.setNamespaceContext( nsc );
        XPathExpression xPathExpression = null;

        InputSource workRelationSource = new InputSource( new ByteArrayInputStream( bytes ) );
        String dcVariable = null;

        log.debug( String.format( "xpathStr = '%s'", xPathStr ) );
        try
        {
            xPathExpression = xpath.compile( xPathStr );
        }
        catch ( XPathExpressionException xpee )
        {
            String msg = String.format( "Could not compile xpath expression '%s'", xPathStr );
            log.error( msg, xpee );
            throw new PluginException( msg, xpee );
        }

        try
        {
            //This line writes a fatal error when given an '&' as the only content in the xml
            dcVariable = xPathExpression.evaluate( workRelationSource );
        }
        catch ( XPathExpressionException xpee )
        {
            String msg = String.format( "Could not evaluate with xpath expression '%s'", xPathStr );
            log.error( msg, xpee );
            throw new PluginException( msg, xpee );
        }

        log.debug( String.format( "Found dcVariable: '%s'", dcVariable ) );

        return dcVariable;
    }


    private DublinCore createDublinCore( CargoContainer cargo ) throws PluginException
    {
        DublinCore dc = new DublinCore( );
        CargoObject co = cargo.getCargoObject( DataStreamType.OriginalData );

        byte[] b = co.getBytes();

        String titleXpathStr = "/ting:container/dkabm:record/dc:title[1]";
        log.trace( String.format( "finding dcTitle using xpath: '%s'", titleXpathStr ) );
        String dcTitle = getDCVariable( b, titleXpathStr );
        log.trace( String.format( "cargo setting dcTitle with value '%s'", dcTitle ) );
        dc.setTitle( dcTitle );

        String creatorXpathStr = "/ting:container/dkabm:record/dc:creator[1]";
        log.trace( String.format( "finding dcCreator using xpath: '%s'", creatorXpathStr ) );
        String dcCreator = getDCVariable( b, creatorXpathStr );
        log.trace( String.format( "cargo setting dcCreator with value '%s'", dcCreator ) );
        dc.setCreator( dcCreator );

        String typeXpathStr = "/ting:container/dkabm:record/dc:type[@xsi:type]";
        log.trace( String.format( "finding dcType using xpath: '%s'", typeXpathStr ) );
        String dcType = getDCVariable( b, typeXpathStr );
        log.trace( String.format( "cargo setting dcType with value '%s'", dcType ) );
        dc.setType( dcType );

        String sourceXpathStr = "/ting:container/dkabm:record/dc:source[1]";
        log.trace( String.format( "finding dcSource using xpath: '%s'", sourceXpathStr ) );
        String dcSource = getDCVariable( b, sourceXpathStr );
        log.trace( String.format( "cargo setting dcSource with value '%s'", dcSource ) );
        dc.setSource( dcSource );

        String relationXpathStr = "/*/*/*/*[@tag='014']/*[@code='a']";
        log.trace( String.format( "finding dcRelation using xpath: '%s'", relationXpathStr ) );
        String dcRelation = getDCVariable( b, relationXpathStr );
        log.trace( String.format( "cargo setting dcRelation with value '%s'", dcRelation ) );
        dc.setRelation( dcRelation );

        log.debug( String.format( "setting variables in cargo container: dcTitle '%s'; dcCreator '%s'; dcType '%s'; dcSource '%s'", dcTitle, dcCreator, dcType, dcSource ) );

        return dc;
    }

}
