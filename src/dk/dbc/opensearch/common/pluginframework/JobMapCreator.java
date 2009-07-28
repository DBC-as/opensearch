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

package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.helpers.XMLUtils;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.ComparablePair;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Reads the jobmap file into the hashmap
 */
public class JobMapCreator
{
    static Logger log = Logger.getLogger( JobMapCreator.class );


    protected static HashMap< InputPair< String, String >, ArrayList< String > > jobMap;
    
    
    /**
     * Retrives the map of lists of tasks for all registrated pairs of submitter, format.
     * @param classType the class of the calling object, either DatadockMain or PTIMain
     * @returns the map containing a list of tasks for each pair of submitter, format
     * @throws IllegalArgumentException if the classType is neither DatadockMain or PTIMain
     * @throws ConfigurationException 
     */
    public static void init( String path ) throws ParserConfigurationException, SAXException, IOException, IllegalStateException
    {
    	log.debug( "JobMapCreator constructor called" );
    	
    	jobMap = new HashMap<InputPair<String, String>, ArrayList<String>>();
        ArrayList<String> sortedPluginList = new ArrayList<String>();

        File jobFile = FileHandler.getFile( path );

        // Build the jobMap
        log.debug( String.format( "init calling getNodeList with jobFile %s ", jobFile ) );
        NodeList jobNodeList = XMLUtils.getNodeList( jobFile, "job" );
        int listLength = jobNodeList.getLength();

        List< ComparablePair<Integer, String>> pluginAndPriority = new ArrayList<ComparablePair<Integer, String>>();

        // For each node read the task name and position        
        Element jobElement;
        String submitter = "";
        String format = "";
        int position;

        for( int x = 0; x < listLength ; x++ )
        {
            jobElement = (Element)jobNodeList.item( x );

            submitter = jobElement.getAttribute( "submitter" );
            format = jobElement.getAttribute( "format" );

            NodeList pluginList = jobElement.getElementsByTagName( "plugin" );
            int pluginListLength = pluginList.getLength();

            pluginAndPriority.clear();

            String plugin;
            // Store the classname in a List
            for( int y = 0; y < pluginListLength; y++ )
            {
                Element pluginElement = (Element)pluginList.item( y );
                plugin = pluginElement.getAttribute( "classname" );
                position = Integer.decode( pluginElement.getAttribute( "position" ) );
                pluginAndPriority.add( new ComparablePair< Integer, String >( position, plugin ) );
            }
            
            // Sort the plugin classname based on the position (order)
            Collections.sort( pluginAndPriority );

            // Store in sorted list
            sortedPluginList.clear();
            for( int z = 0; z < pluginListLength; z++ )
            {
                plugin = ( (Pair<Integer, String>)pluginAndPriority.get( z ) ).getSecond();
                sortedPluginList.add( plugin );
            }

            // Put it into the map with  <submitter, format> as key and List as value
            jobMap.put( new InputPair< String, String >( submitter, format ), new ArrayList< String >( sortedPluginList) );
        }

        if( jobMap.isEmpty() )
        {
            throw new IllegalStateException( String.format( "no jobs found for: %s ", path ) );
        }
    }
    
    
    public static void validateXsdJobXmlFile( String path ) throws IOException, ConfigurationException, SAXException
    {
    	SchemaFactory factory = SchemaFactory.newInstance( "http://www.w3.org/2001/XMLSchema" );
        
        String xsdPath = FileSystemConfig.getJobsXsdPath();
        File schemaLocation = FileHandler.getFile( xsdPath );
        Schema schema = factory.newSchema( schemaLocation );
    
        Validator validator = schema.newValidator();
        
        Source source = new StreamSource( path );
        
        try 
        {
            validator.validate( source );
        }
        catch ( SAXException ex ) 
        {
            log.debug( path + " is not valid because ");
            log.debug( ex.getMessage() );
        }
    }


    public static void validateJobXmlFilePosition( String path ) throws ParserConfigurationException, SAXException, IOException, IllegalStateException
    {
        File jobsXmlFile = new File( path );
        NodeList jobsNodeList = XMLUtils.getNodeList( jobsXmlFile, "job" );

        int nodeListLen = jobsNodeList.getLength();
        for ( int i = 0; i < nodeListLen; i++ )
        {
            Element jobElement = (Element)jobsNodeList.item( i );
                
            NodeList pluginList = jobElement.getElementsByTagName( "plugin" );
            int pluginListLength = pluginList.getLength();

            ArrayList<Integer> testPosition = new ArrayList<Integer>();
            int position = -1;

            for( int y = 0; y < pluginListLength; y++ )
            {
                Element pluginElement = (Element)pluginList.item( y );
                position = Integer.decode( pluginElement.getAttribute( "position" ) );
                if ( ! testPosition.contains( position ) )
                {
                    testPosition.add( position );
                }
                else 
                {                    
                    String msg = "Two position values in a job node from a *_jobs.xml are equal!";
                    throw new IllegalStateException( msg );
                }
            }
        }
    }
}