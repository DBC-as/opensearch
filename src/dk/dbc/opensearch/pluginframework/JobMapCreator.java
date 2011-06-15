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
 * \file JobMapCreator
 * \brief
 */


package dk.dbc.opensearch.pluginframework;


import dk.dbc.opensearch.compass.CPMAlias;
import dk.dbc.commons.xml.XMLUtils;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Reads the jobmap file into the hashmap
 */
public class JobMapCreator
{
    static Logger log = Logger.getLogger( JobMapCreator.class );


    protected static HashMap< Pair< String, String >, ArrayList< String > > jobMap;
    protected static HashMap< String, String > aliasMap = new HashMap< String, String >();

    
    /**
     * Retrives the map of lists of tasks for all registrated pairs of submitter, format.
     * @param path
     * @throws IllegalArgumentException if the classType is neither DatadockMain or PTIMain
     * @throws ConfigurationException 
     */
    public static void init( String path ) throws ConfigurationException, ParserConfigurationException, SAXException, IOException, IllegalStateException
    {
    	log.debug( "JobMapCreator constructor called" );

        CPMAlias cpmAlias = new CPMAlias();

    	jobMap = new HashMap<Pair<String, String>, ArrayList<String>>();

        File jobFile = FileHandler.getFile( path );

        // Build the jobMap
        log.debug( String.format( "init calling getNodeList with jobFile %s ", jobFile ) );
        NodeList jobNodeList = XMLUtils.getNodeList( jobFile, "job" );
        int listLength = jobNodeList.getLength();

        if( listLength < 1 )
        {
            String error = String.format( "No job element found in file '%s', cannot construct plugin list. Please review the relevant jobs.xml file", path );
            log.warn( error );
            throw new IllegalStateException( error );
        }

        // For each node read the task name         
        Element jobElement;
        String submitter = "";
        String format = "";
        String alias = "";

        for( int x = 0; x < listLength ; x++ )
        {
            jobElement = (Element)jobNodeList.item( x );

            submitter = jobElement.getAttribute( "submitter" );
            format = jobElement.getAttribute( "format" );
            alias = jobElement.getAttribute( "alias" );

            if( submitter.isEmpty() || format.isEmpty() )
            {
                String error = String.format( "Submitter or format was empty for job element, cannot construct plugin list. Please review the relevant jobs.xml file (at '%s')", path );
                log.warn( error );
                throw new IllegalStateException( error );
            }

            if ( ! alias.equals("") && ! cpmAlias.isValidAlias( alias ) )
            {
                log.warn( String.format( "alias '%s' in job submitter='%s', format='%s' is not Valid, ie. not found in the xml.cpm.xml file. If this job is used, expect incexing failures", alias, submitter, format ) );
            }
            
            aliasMap.put( submitter + format , alias );

            NodeList pluginList = jobElement.getElementsByTagName( "plugin" );
            int pluginListLength = pluginList.getLength();

            ArrayList< String > sortedPluginList = new ArrayList< String >();

            String plugin;
            // Store the classname in a List
            for ( int y = 0; y < pluginListLength; y++ )
            {
                Element pluginElement = (Element)pluginList.item( y );
                plugin = pluginElement.getAttribute( "classname" );
                sortedPluginList.add( plugin );             
            }           

            if ( sortedPluginList.isEmpty() )
            {
                log.warn( String.format( "No jobs (plugins that handle jobs) found for submitter %s, format %s", submitter, format ) );
                continue;
            }

            // Put it into the map with  <submitter, format> as key and List as value
            jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >( sortedPluginList) );
        }
    }
   
    public static void validateXsdJobXmlFile( String XMLPath, String XSDPath ) throws IOException, ConfigurationException, SAXException
    {
    	SchemaFactory factory = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
        
        File schemaLocation = FileHandler.getFile( XSDPath );
        Schema schema = factory.newSchema( schemaLocation );
    
        Validator validator = schema.newValidator();
        
        Source source = new StreamSource( XMLPath );
        
        try 
        {
            validator.validate( source );
        }
        catch ( SAXException ex ) 
        {
            String error = String.format( "Could not validate job xml file: %s", ex.getMessage() );
            log.error( error, ex );
            throw ex;
        }
    }
}
