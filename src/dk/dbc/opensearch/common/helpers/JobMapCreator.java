package dk.dbc.opensearch.common.helpers;

import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.types.Pair;

import java.io.File;
import java.io.IOException;

import java.util.HashMap;
import java.util.List;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Collections;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import org.apache.commons.configuration.ConfigurationException;
import java.net.URL;

/**
 *
 */
public class JobMapCreator 
{
    static Logger log = Logger.getLogger( JobMapCreator.class );
 
    private HashMap< Pair< String, String >, ArrayList<String> > jobMap;
    
    private File jobFile;
    private Document jobDocument;

    DocumentBuilderFactory docBuildFact;
    DocumentBuilder docBuilder;

    /**
     * a little helper for comparing the int thats is second member in Pairs
     * used in making the list with the tasks sorted due to posistion
     */
    class secondComparator implements Comparator< Pair< String, Integer > >
    {
        public int compare( Pair< String, Integer > x, Pair< String, Integer > y )
        {
            if( ((Pair< String, Integer >)x).getSecond() < ((Pair< String, Integer >)y).getSecond() )
            {
                return -4;
            }
            else
            {
                if( ((Pair<String, Integer>)x).getSecond() == ((Pair<String, Integer>)y).getSecond() )
                {
                    return 0;
                }
            }
            return 4;
        }

    }

    
    secondComparator secComp = new secondComparator();

    /**
     *
     */
    public JobMapCreator( Class classType ) throws ParserConfigurationException, SAXException, IOException 
    {
    	URL datadockJobURL = getClass().getResource( "/datadock_jobs.xml" );   
        log.debug( String.format( "DatadockJob path: '%s'", datadockJobURL.getPath() ) );
        
        URL ptiJobURL = getClass().getResource( "/pti_jobs.xml" );
        log.debug( String.format( "PTIJob path: '%s'", ptiJobURL.getPath() ) );

        docBuildFact = DocumentBuilderFactory.newInstance();
        docBuilder = docBuildFact.newDocumentBuilder();

        jobMap = new HashMap< Pair< String, String >, ArrayList<String> >();

        if( classType.getName().equals( "dk.dbc.opensearch.components.datadock.DatadockMain") )
        {
            jobFile = FileHandler.getFile( datadockJobURL.getPath() );
        }
        else
        {
            jobFile = FileHandler.getFile( ptiJobURL.getPath() );
        }

        jobDocument = docBuilder.parse( jobFile );

        //build the jobMap
        // 10: get the rootelement of the doc
        Element xmlRoot = jobDocument.getDocumentElement();

        // 20: Get the NodeList
        NodeList jobNodeList = xmlRoot.getElementsByTagName( "job" );

        String submitter;
        String format;
        ArrayList<String> sortedTaskList = new ArrayList< String >();
        List< Pair< String, Integer > > taskAndPriority = new ArrayList< Pair< String, Integer > >();
        Element jobElement;
        NodeList taskList;
        int taskListLength;
        Element taskElement;
        //Pair taskPriPair;
        String task;
        int position;
        int listLength = jobNodeList.getLength();

        // 30: For each node read the task name and position
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

            jobMap.put( new Pair< String, String >( submitter, format ), new ArrayList< String >(sortedTaskList) );
        }
    }

    public HashMap< Pair< String, String >, ArrayList< String > > getMap()
    {
        return jobMap;
    }
}