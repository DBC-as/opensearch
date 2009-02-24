package dk.dbc.opensearch.common.helpers;

import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;
import dk.dbc.opensearch.common.helpers.SecondComparator;
import java.io.File;
import java.io.IOException;

import java.net.MalformedURLException;
import java.net.URL;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections;

import java.lang.IllegalArgumentException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

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


    /**
     * default constructor \Todo : Is it nessecary?
     */
    public JobMapCreator() {}

    
    /**
     * Retrives the map of lists of tasks for all registrated pairs of submitter, format.
     * @param classType the class of the calling object, either DatadockMain or PTIMain
     * @returns the map containing a list of tasks for each pair of submitter, format
     * @throws IllegalArgumentException if the classType is neither DatadockMain or PTIMain
     * \Todo: Make the method static when we are not using URL, getClass and getResource...
     */

    public static HashMap< Pair< String, String >, ArrayList< String > > getMap( Class classType ) throws IllegalArgumentException, ParserConfigurationException, SAXException, IOException 
    {
        log.debug( "GetMap() called" );

        ArrayList<String> sortedTaskList = new ArrayList< String >();
        List< Pair< String, Integer > > taskAndPriority = new ArrayList< Pair< String, Integer > >();

        log.debug( String.format( "Constructor( class='%s' ) called", classType.getName() ) );

        // Set jobFile depending on classType: datadock or pti.
        File jobFile = setJobFile( classType );
        
        log.debug( String.format( "Retrieving jobmap from file='%s'", jobFile.getPath() ) );

        DocumentBuilderFactory docBuilderFact = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder = docBuilderFact.newDocumentBuilder();
        Document jobDocument = docBuilder.parse( jobFile );

        // Build the jobMap
        Element xmlRoot = jobDocument.getDocumentElement();

        // Get the NodeList
        NodeList jobNodeList = xmlRoot.getElementsByTagName( "job" );
        int listLength = jobNodeList.getLength();

        HashMap< Pair< String, String >, ArrayList<String> > jobMap = new HashMap< Pair< String, String >, ArrayList<String> >();

        // 30: For each node read the task name and position        
        Element jobElement;
        String submitter = "";
        String format = "";
        NodeList taskList;
        int taskListLength;
        Element taskElement;
        String task;
        int position;
        SecondComparator secComp = new SecondComparator();    
        
        for( int x = 0; x < listLength ; x++ )
        {
        	jobElement = (Element)jobNodeList.item( x );

        	submitter = jobElement.getAttribute( "submitter" );
        	format = jobElement.getAttribute( "format" );

        	taskList = jobElement.getElementsByTagName( "task" );
        	taskListLength = taskList.getLength();

        	taskAndPriority.clear();

        	// 35: get the tasks in a List
        	for( int y = 0; y < taskListLength; y++ )
        	{
        		taskElement = (Element)taskList.item( y );
        		//get the name and position of the task element
        		task = (String)taskElement.getAttribute( "name" );
        		position = Integer.decode(taskElement.getAttribute( "position" ) );

        		taskAndPriority.add( new Pair< String, Integer >( task, position ) );
        	}

        	// 40: sort the tasks based on the position (order)
        	Collections.sort( taskAndPriority, secComp );

        	// 50: put it in a List
        	sortedTaskList.clear();
        	for( int z = 0; z < taskListLength; z++ )
        	{
        		task = ( (Pair< String, Integer >)taskAndPriority.get( z ) ).getFirst();
        		sortedTaskList.add( task );
        	}

        	// 60: Put it into the map with  <submitter, format> as key and List as value
        	jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >( sortedTaskList) );
        }

        // Put job into the map with <submitter, format> as key and List as value
        jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >( sortedTaskList) );

        return jobMap;
    }
    
    
    private static File setJobFile( Class classType ) throws MalformedURLException
    {
    	File jobFile;
    
    	if( classType.getName().equals( "dk.dbc.opensearch.components.datadock.DatadockMain" ) )
        {
            String datadockJobPath = PluginsConfig.getPluginsDatadock();
            log.debug( String.format( "DatadockJob path: '%s'", datadockJobPath ) );
            jobFile = FileHandler.getFile( datadockJobPath );

        }
        else if ( classType.getName().equals( "dk.dbc.opensearch.components.pti.PTIMain" ) )
        {
        	String ptiJobPath = PluginsConfig.getPluginsPti();
        	log.debug( String.format( "PTIJob path: '%s'", ptiJobPath ) );
        	jobFile = FileHandler.getFile( ptiJobPath );
        }
        else
        {
            log.error( "wrong class given to JobMapCreator.getMap method" );
            throw new IllegalArgumentException( String.format( "unknown class given to the JobMapCreator.getMap method, the class given: %s", classType.getName() ) );          
        }
    	
    	return jobFile;
    }
}