package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.helpers.PairComparator_SecondInteger;
import dk.dbc.opensearch.common.helpers.XMLFileReader;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;

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
     * default constructor \Todo : Is it nessecary?
     */
    public JobMapCreator() {}

    
    /**
     * Retrives the map of lists of tasks for all registrated pairs of submitter, format.
     * @param classType the class of the calling object, either DatadockMain or PTIMain
     * @returns the map containing a list of tasks for each pair of submitter, format
     * @throws IllegalArgumentException if the classType is neither DatadockMain or PTIMain
     */

    public static HashMap< Pair< String, String >, ArrayList< String > > getMap( Class classType ) throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException, IllegalStateException 
    {
        log.debug( "getMap() called" );

        HashMap< Pair< String, String >, ArrayList<String> > jobMap = new HashMap< Pair< String, String >, ArrayList<String> >();
        ArrayList<String> sortedPluginList = new ArrayList< String >();
        List< Pair< String, Integer > > pluginAndPriority = new ArrayList< Pair< String, Integer > >();

        log.debug( String.format( "Constructor( class='%s' ) called", classType.getName() ) );
        // Set jobFile depending on classType: datadock or pti.
        File jobFile = setJobFile( classType );
        
        log.debug( String.format( "Retrieving jobmap from file='%s'", jobFile.getPath() ) );
        // Build the jobMap
        //NodeList jobNodeList = getJobMapNodeList( jobFile );
        NodeList jobNodeList = XMLFileReader.getNodeList( jobFile, "job" );
        int listLength = jobNodeList.getLength();

        // 30: For each node read the task name and position        
        Element jobElement;
        String submitter = "";
        String format = "";
        int position;
        PairComparator_SecondInteger secComp = new PairComparator_SecondInteger();
        
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

        		pluginAndPriority.add( new Pair< String, Integer >( plugin, position ) );
        	}

        	// 40: sort the tasks based on the position (order)
        	Collections.sort( pluginAndPriority, secComp );

        	// 50: put it in a List
        	sortedPluginList.clear();
        	for( int z = 0; z < pluginListLength; z++ )
        	{
        		plugin = ( (Pair< String, Integer >)pluginAndPriority.get( z ) ).getFirst();
        		sortedPluginList.add( plugin );
        	}

        	// 60: Put it into the map with  <submitter, format> as key and List as value
        	jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >( sortedPluginList) );
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
     * \Todo: this method should be package private, but it is not so until we put the 
     * testfiles in the same package as the files under test... 
     */
    public static File setJobFile( Class classType ) throws MalformedURLException
    {
    	File jobFile;
    
    	if( classType.getName().equals( "dk.dbc.opensearch.components.datadock.DatadockMain" ) )
        {
            String datadockJobPath = FileSystemConfig.getFileSystemDatadockPath();
            log.debug( String.format( "DatadockJob path: '%s'", datadockJobPath ) );
            if ( datadockJobPath != null )
            {
            	jobFile = FileHandler.getFile( datadockJobPath );
            }
            else
            {
            	throw new IllegalArgumentException( "The value og datadockJobPath was null" );
            }
        }
        else if ( classType.getName().equals( "dk.dbc.opensearch.components.pti.PTIMain" ) )
        {
        	String ptiJobPath = FileSystemConfig.getFileSystemPtiPath();
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