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
 * \file IndexerXSEM.java
 * \brief maps and indexes the cargoContainer
 */


package dk.dbc.opensearch.plugins;

import dk.dbc.opensearch.common.compass.CPMAlias;
import dk.dbc.opensearch.common.pluginframework.IIndexer;
import dk.dbc.opensearch.common.pluginframework.PluginException;
import dk.dbc.opensearch.common.pluginframework.PluginType;
import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.CargoObject;
import dk.dbc.opensearch.common.types.DataStreamType;
import dk.dbc.opensearch.common.xml.XMLUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.compass.core.CompassTransaction;
import org.compass.core.xml.AliasedXmlObject;
import org.compass.core.xml.RawAliasedXmlObject;
import org.compass.core.xml.javax.NodeAliasedXmlObject;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;


/**
 * The IndexerXSEM maps the given indexable datastreams in the
 * cargocontainer, and writes the resulting values in a document to
 * the index.
 */
public class IndexerXSEM implements IIndexer
{
    private static Logger log = Logger.getLogger( IndexerXSEM.class );


    PluginType pluginType = PluginType.INDEX;

    /**
     * The main method of the IndexerXSEM plugin
     */
    @Override
    public boolean index(CargoContainer cargo, CompassSession session, String fedoraHandle ) throws PluginException, ConfigurationException
    {
        boolean success = false;

        try
        {
            success = index( session, cargo, fedoraHandle );
        }
        catch( CompassException ce )
        {
            throw new PluginException( "Could not commit index on CompassSession", ce );
        }
        return success;
    }

    private boolean index( CompassSession session, CargoContainer cc, String fedoraHandle ) throws PluginException, CompassException, ConfigurationException
    {
        boolean success = false;
        Date finishTime = new Date();

        /* \todo: right now we index all stream in a cc with the same alias. each CargoObject should have a IndexingAlias. see bug #8719 */
        //String indexingAlias = cc.getIndexingAlias().getName();

        log.debug( String.format( "Trying to read CargoContainer (pid= %s ) data from .getData into a dom4j.Document type", fedoraHandle ) );

        CPMAlias cpmAlias = null;
        log.debug( String.format( "number of streams in cc: %s", cc.getCargoObjectCount() ) );
        List< CargoObject > list = cc.getCargoObjects();
        try
        {
            cpmAlias = new CPMAlias();
        }
        catch( ParserConfigurationException pce )
        {
            log.fatal( String.format( "Could not construct CPMAlias object for reading/parsing xml.cpm file -- values used for checking cpm aliases" + pce ) );
            throw new PluginException( String.format( "Could not construct CPMAlias object for reading/parsing xml.cpm file -- values used for checking cpm aliases" ), pce );
        }
        catch (SAXException se)
        {
            log.fatal( String.format( "Could not parse XSEM mappings file" + se ) );
            throw new PluginException( String.format( "Could not parse XSEM mappings file" ), se );
        }
        catch ( IOException ioe )
        {
            log.fatal( String.format( "Could not open or read XSEM mappings file: " + ioe ) );
            throw new PluginException( String.format( "exception Could not open or read XSEM mappings file" ), ioe );
        }

        log.info( "cpmAlias constructed" );

        for( CargoObject co : list )
        {
            if( ! ( co.getDataStreamType() == DataStreamType.OriginalData ) )
            {
                log.debug( String.format( "Not indexing data with datastreamtype '%s'",co.getDataStreamType() ) );
            }
            else {

                String indexingAlias = co.getIndexingAlias().getName();
                boolean isValidAlias = false;
                try 
                {
                    isValidAlias = cpmAlias.isValidAlias( indexingAlias );
                }
                catch ( ParserConfigurationException pce ) 
                {
                    log.fatal( String.format( "Could not contruct the objects for reading/parsing the configuration file for the XSEM mappings" ), pce );
                    throw new PluginException( String.format( "Could not contruct the objects for reading/parsing the configuration file for the XSEM mappings" ), pce );
                }
                catch ( SAXException se ) 
                {
                    log.fatal( String.format( "Could not parse XSEM mappings file" ), se );
                    throw new PluginException( String.format( "Could not parse XSEM mappings file" ), se );
                }
                catch (IOException ioe) 
                {
                    log.fatal( String.format( "Second: Could not open or read XSEM mappings file" ), ioe );
                    throw new PluginException( String.format( "Second Exception Could not open or read XSEM mappings file" ), ioe );
                }

                if( ! isValidAlias )
                {
                    log.fatal( String.format( "The format %s (from pid %s) has no alias in the XSEM mapping file", indexingAlias, cc.getIdentifier() ) );
                    throw new PluginException( String.format( "The format %s has no alias in the XSEM mapping file", indexingAlias ) );
                }
                else
                {
                    byte[] bytes = co.getBytes();
                    log.debug( String.format( "altered xml: %s", new String( bytes ) ) );
                    ByteArrayInputStream is = new ByteArrayInputStream( bytes );
                    Document doc = null;
                    DocumentBuilder docBuilder = null;
                    try
                    {
                        docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        doc = docBuilder.parse( is );
                    } 
                    catch (SAXException de) 
                    {
                        log.fatal( String.format( "Error reading xml stream: %s", de.getMessage() ), de );
                        throw new PluginException( String.format( "Could not parse InputStream as an XML Instance from alias=%s, mimetype=%s, pid=%s", indexingAlias, co.getMimeType(), cc.getIdentifier() ), de );
                    } 
                    catch( IOException ioe)
                    {
                        log.fatal( String.format( "Error reading xml stream: %s", ioe.getMessage() ), ioe );
                    } 
                    catch( ParserConfigurationException pce )
                    {
                        log.fatal( String.format( "Error reading xml stream: %s", pce.getMessage() ), pce );                    
                    }

                    /** \todo: when doing this the right way, remember to modify the initial value of the HashMap*/

                    // Add aditional values to document
                    HashMap< String, String> fieldMap = new HashMap< String, String >( 3 );
                    log.debug( String.format( "Initializing new fields for the index" ) );
                    fieldMap.put( "ting:fedoraPid", fedoraHandle );
                    fieldMap.put( "ting:original_format", co.getFormat() );
                    fieldMap.put( "ting:submitter", co.getSubmitter() );

                    Element root = doc.getDocumentElement();

                    for( String key : fieldMap.keySet() )
                    {
                        log.debug( String.format( "Setting new index field '%s' to '%s'", key, fieldMap.get( key ) ) );
                        Element newElement = doc.createElement( key );
                        newElement.setTextContent( fieldMap.get( key ) );
                        root.appendChild( newElement );
                    }

                    // this log line is _very_ verbose, but useful in a tight situation
                    // try{
                    //     log.trace( String.format( "Constructing AliasedXmlObject from Document (pid = %s) with alias = %s. RootElement:\n%s", fedoraHandle, indexingAlias, XMLUtils.xmlToString( doc ) ) );
                    // }catch(TransformerException te){ System.err.println( String.format( "Caught Exception: %s", te.getMessage() ) );}

                    AliasedXmlObject xmlObject = null;
                    try {
                        xmlObject = new RawAliasedXmlObject( indexingAlias,  XMLUtils.xmlToString( doc )  );
                    }
                    catch(TransformerException te)
                    {
                        System.err.println( String.format( "Could not transform xml data %s", te.getMessage() ) );                        
                        throw new PluginException( String.format( "Could not transform xml data %s", te.getMessage() ), te );                        
                    }

                    log.info( String.format( "Constructed AliasedXmlObject with alias %s", xmlObject.getAlias() ) );

                    
                    // getting transaction object and saving index
                    log.trace( String.format( "Getting transaction object" ) );
                    CompassTransaction trans = null;

                    try
                    {
                        log.trace( "Beginning transaction" );
                        trans = session.beginTransaction();
                    }catch( CompassException ce )
                    {
                        log.fatal( "Could not initiate transaction on the CompassSession", ce );
                        throw new PluginException( "Could not initiate transaction on the CompassSession", ce );
                    }
                    
                    try
                    {
                        log.debug( String.format( "Saving Compass Resource '%s' with new fields to index", xmlObject.getAlias() ) );
                        session.save( xmlObject );
                    }
                    catch( CompassException ce ){
                        try
                        {
                            log.fatal( String.format( "Could not save index object (alias=%s, pid=%s) to index. Cause: %s, message: %s, xml='''%s''' ", xmlObject.getAlias(), fedoraHandle, ce.getCause(), ce.getMessage(), XMLUtils.xmlToString( doc ) ), ce );
                            
                            throw new PluginException( String.format( "Could not save index object to index. Cause: %s, message: %s ", ce.getCause(), ce.getMessage() ), ce );
                        }
                        catch( TransformerException ex )
                        {
                            throw new PluginException( String.format( "Could not log exception %s: %s",ce.getMessage(), ex.getMessage() ), ce );
                        }
                    }

                    log.trace( "Committing index on transaction" );
                    trans.commit();

                    /** todo: does trans.wasCommitted have any side-effects? Such as waiting for the transaction to finish before returning?*/
                    log.debug( String.format( "Transaction wasCommitted() == %s", trans.wasCommitted() ) );
                    session.close();

                    log.info( String.format( "Document indexed and stored with Compass" ) );
                    success = true;
                    //processTime += finishTime.getTime() - co.getTimestamp();
                }
            }
        }
        return success;
    }


    /**
     *
     */
    @Override
    public PluginType getPluginType()
    {
        return pluginType;
    }
}
