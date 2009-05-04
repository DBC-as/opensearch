package dk.dbc.opensearch.common.pluginframework;

/**
 *   
 * This file is part of opensearch.
 * Copyright © 2009, Dansk Bibliotekscenter a/s, 
 * Tempovej 7-11, DK-2750 Ballerup, Denmark. CVR: 15149043
 *
 * opensearch is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * opensearch is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with opensearch.  If not, see <http://www.gnu.org/licenses/>.
 */


import dk.dbc.opensearch.common.config.DatadockConfig;
import dk.dbc.opensearch.common.config.PtiConfig;
//import dk.dbc.opensearch.common.helpers.PairComparator_SecondInteger;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.types.InputPair;
import dk.dbc.opensearch.common.types.ComparablePair;

import java.io.File;
import java.io.IOException;
import java.lang.IllegalArgumentException;
import java.lang.IllegalStateException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

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


    /**
     * default constructor \todo : Is it nessecary?
     */
    public JobMapCreator() {}

    
    /**
     * Retrives the map of lists of tasks for all registrated pairs of submitter, format.
     * @param classType the class of the calling object, either DatadockMain or PTIMain
     * @returns the map containing a list of tasks for each pair of submitter, format
     * @throws IllegalArgumentException if the classType is neither DatadockMain or PTIMain
     * @throws ConfigurationException 
     */

    public static HashMap< InputPair< String, String >, ArrayList< String > > getMap( Class classType ) throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, IllegalStateException, ConfigurationException 
    {
        //System.out.println( String.format( "calling getMap with %s", classType.getName() ) );
        log.debug( "getMap() called" );

        HashMap< InputPair< String, String >, ArrayList<String> > jobMap = new HashMap< InputPair< String, String >, ArrayList<String> >();
        ArrayList<String> sortedPluginList = new ArrayList< String >();
        List< ComparablePair< String, Integer > > pluginAndPriority = new ArrayList< ComparablePair< String, Integer > >();

        log.debug( String.format( "Constructor( class='%s' ) called", classType.getName() ) );
        // Set jobFile depending on classType: datadock or pti.
        File jobFile = setJobFile( classType );
        
        log.debug( String.format( "Retrieving jobmap from file='%s'", jobFile.getPath() ) );
        // Build the jobMap
        NodeList jobNodeList = XMLFileReader.getNodeList( jobFile, "job" );
        int listLength = jobNodeList.getLength();
       
        // 30: For each node read the task name and position        
        Element jobElement;
        String submitter = "";
        String format = "";
        int position;
        //PairComparator_SecondInteger secComp = new PairComparator_SecondInteger();
        
        for( int x = 0; x < listLength ; x++ )
        {
        	jobElement = (Element)jobNodeList.item( x );
                
        	submitter = jobElement.getAttribute( "submitter" );
        	format = jobElement.getAttribute( "format" );

        	NodeList pluginList = jobElement.getElementsByTagName( "plugin" );
        	int pluginListLength = pluginList.getLength();

        	pluginAndPriority.clear();
        	
        	String plugin;
        	// 35: get the tasks in a List
        	for( int y = 0; y < pluginListLength; y++ )
        	{
        		Element pluginElement = (Element)pluginList.item( y );
        		//get the name and position of the task element
        		plugin = (String)pluginElement.getAttribute( "name" );
        		position = Integer.decode(pluginElement.getAttribute( "position" ) );

        		pluginAndPriority.add( new ComparablePair< String, Integer >( plugin, position ) );
        	}

        	// 40: sort the tasks based on the position (order)
        	Collections.sort( pluginAndPriority ); //, secComp );

        	// 50: put it in a List
        	sortedPluginList.clear();
        	for( int z = 0; z < pluginListLength; z++ )
        	{
        		plugin = ( (Pair< String, Integer >)pluginAndPriority.get( z ) ).getFirst();
        		sortedPluginList.add( plugin );
        	}

        	// 60: Put it into the map with  <submitter, format> as key and List as value
        	jobMap.put( new InputPair< String, String >( submitter, format ), new ArrayList< String >( sortedPluginList) );
        }

        // Put job into the map with <submitter, format> as key and List as value
        //jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >( sortedPluginList) );

        if( jobMap.isEmpty() )
        {
        	throw new IllegalStateException( String.format( "no jobs found for: %s ", classType.getName() ) );
        }
        
        return jobMap;
    }

    /**
     * \todo: this method should be package private, but it is not so until we put the 
     * testfiles in the same package as the files under test... 
     * @throws ConfigurationException 
     */
    public static File setJobFile( Class classType ) throws MalformedURLException, ConfigurationException
    {
    	File jobFile;
        /** \todo: wouldn't it be better to let the classes that are allowed to call JobMapCreator inherit the same interface and check on that instead? It would make this whole setup _much_ less dependant on classpaths (which notoriously change) and would prevent this class in having to break at runtime because of unrelated changes elsewhere in the project. */
    	if( classType.getName().equals( "dk.dbc.opensearch.components.datadock.DatadockMain" ) )
        {
            String datadockJobPath = DatadockConfig.getPath();
            log.debug( String.format( "DatadockJob path: '%s'", datadockJobPath ) );
            if ( datadockJobPath != null )
            {
            	jobFile = FileHandler.getFile( datadockJobPath );
            }
            else
            {
            	throw new IllegalArgumentException( "The value of datadockJobPath was null" );
            }
        }
        else if ( classType.getName().equals( "dk.dbc.opensearch.components.pti.PTIMain" ) )
        {
        	String ptiJobPath = PtiConfig.getPath();
        	log.debug( String.format( "PTIJob path: '%s'", ptiJobPath ) );
        	if ( ptiJobPath != null)
        	{
        		jobFile = FileHandler.getFile( ptiJobPath );
        	}
        	else
        	{
        		throw new IllegalArgumentException( "the value of ptiJobPath was null" );
        	}
        }
        else
        {
            log.error( "wrong class given to JobMapCreator.getMap method" );
            throw new IllegalArgumentException( String.format( "Unknown class given to the JobMapCreator.getMap method, the class given: %s", classType.getName() ) );          
        }
    	
    	return jobFile;
    }
}