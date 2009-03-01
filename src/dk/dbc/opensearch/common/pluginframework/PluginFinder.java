/**
 * \file PluginFinder.java
 * \brief the class that handles the pluginclassmap
 * \package pluginframework
 */
package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.os.PluginFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import dk.dbc.opensearch.common.types.ThrownInfo;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

/**
 * class that handles the creation of the map containing info about the existing
 * plugins. I creates the map, updates when asked to and returns info needed to
 * load specific plugins by the plugin loader
 */
public class PluginFinder
{
    static Logger log = Logger.getLogger( PluginFinder.class );

    /**
     * classNameMap contains class names of plugins
     * The key for use in classNamePath: Concatination of submitter, format and task
     */
    private Map< Integer, String > classNameMap;
    private DocumentBuilder docBuilder;
    String path;

    
    /**
     * builds the map containing the keys and related plugin classes
     * The keys are made from the task, format and datasource, the value is the
     * class of the plugin that can solve the specified task
     * @param path: the directory to look for the xml files describing the plugins
     * @param docBuilder: the DocumentBuilder used for parsing the xml files
     * @throws IllegalArgumentException when there is no directory or no files in it
     * @throws FileNotFoundException when the upDateClassMapMethod cant find the files
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     *
     * \todo: should we hardcode the path?
     */
    public PluginFinder( DocumentBuilder docBuilder, String path ) throws FileNotFoundException, NullPointerException, PluginResolverException
    {
        this.path = path;
        this.docBuilder = docBuilder;
        classNameMap = new HashMap< Integer, String >();

        // call updatePluginClassNameMap to generate the map
        updatePluginClassNameMap( path );
    }


    /**
     * Finds the right plugin class for the specified operation
     * @param key, the key to find the classname by
     * @throws FileNotFoundException no plugin was found for the
     * given key that is a hashcode made based on the concatinated 
     * String submitter+format+task
     * @throws PluginResolverException when there are expections from
     * parsing and reading plugin xml files which are not nessecarily
     * show stoppers. See the source to understand the use of it.
     * @return the name of the plugin class
     **/
    String getPluginClassName( int key ) throws FileNotFoundException, PluginResolverException
    {
    	//printClassNameMap();
        String className = null;
        
        // 5: check the map is not null
        if( classNameMap.size() < 1 )
            updatePluginClassNameMap( path );
        
        //10: search through the map
        className = (String) classNameMap.get( key );
        
        //20: if there is no hit, raise exception
        if( className == null )
        	throw new FileNotFoundException( String.format( "No value for key: %s ", key ) );
        
        return className;
    }
    
    
    void printClassNameMap()
    {
    	java.util.Collection< String > c = (java.util.Collection< String >) classNameMap.values();
    	Iterator< String > iter = c.iterator();
    	
    	Set< Integer> keySet = classNameMap.keySet();
    	for( Integer i : keySet )
    	{
    		System.out.println( "key: " + i );
    		System.out.println( "value: " + iter.next().toString() );
    	}
    }
    

    /**
     * creates or updates the classname map with the names of the plugins found
     * on and loaded from the given path
     *
     * @throws FileNotFoundException if there are no pluginxml files on the given path
     * @throws PluginResolverException if one or more of the found plugins could not be loaded
     *
     */
    private void updatePluginClassNameMap( String path ) throws PluginResolverException, FileNotFoundException
    {
    	Iterator pluginNameIter = null;

        log.debug( String.format( "Clearing map with %s members", classNameMap.size() ) );
        classNameMap.clear();

        log.debug( String.format( "Getting file list from %s", path ) );
        FilenameFilter[] filterList = { new PluginFileFilter() };
        
        Vector<String> xmlPluginFileNames = FileHandler.getFileList( path, filterList, true );     
        log.debug( String.format( "Number of found plugins: %s ", xmlPluginFileNames.size() ) );

        Vector<ThrownInfo> failedPlugins = new Vector<ThrownInfo>();
        if ( xmlPluginFileNames.size() < 1 )
        {
        	throw new FileNotFoundException( String.format( "No plugin description files at %s ", path ) );
        }

        pluginNameIter = xmlPluginFileNames.iterator();

        //30: for each pull task, format, datasource and class out of them and build
        // the key and put it into the map with the class as value
        while( pluginNameIter.hasNext() )
        {
        	boolean couldFormat = false;
        	//String submitterName = null;
        	//String formatName = null;
        	String className = null;
        	//String taskName = null;
        	String name = null;
        	//String hashSubject = null;
        	int key = 0;
        	String pluginName = null;;
        	File pluginFile = null;
        	Document pluginDocument = null;

        	try
        	{
        		pluginName = (String)pluginNameIter.next();
        		log.debug( "plugin: " + pluginName );
        		pluginFile = FileHandler.getFile( pluginName );
        		log.debug( String.format( "Building DOM object from file %s", pluginName ) );
        		pluginDocument = docBuilder.parse( pluginFile );
        		couldFormat = true;
        	}
        	catch( SAXException saxe )
        	{
        		couldFormat = false;//dont try to do further work on this file
        		log.error( String.format( "could not parse pluginxml file: '%s'\nException: %s", pluginName, saxe.getMessage() ) );
        		failedPlugins.add( new ThrownInfo( saxe, pluginName ) );
        	}
        	catch( IOException ioe )
        	{
        		couldFormat = false;//dont try to do further work on this file
        		log.error( String.format( "could not parse pluginxml file: '%s'\nException: %s ", pluginName, ioe.getMessage() ) );
                failedPlugins.add( new ThrownInfo( ioe, pluginName ) );
        	}
        	catch( NullPointerException npe )
        	{
        		couldFormat = false;
        		log.error( "Invalid file name in the plugin name list generated by the FileHandler" );
        		failedPlugins.add( new ThrownInfo( npe, pluginName ) );
        	}

        	if( couldFormat )
        	{
        		Element xmlRoot = pluginDocument.getDocumentElement();

        		// 37: get the plugin element
        		NodeList pluginNodeList = xmlRoot.getElementsByTagName( "plugin" );

        		/** \todo: NodeList.item( int index ) returns null if outofbounds**/
        		Element pluginElement = (Element) pluginNodeList.item( 0 );
        		//40: pull out the values

        		//submitterName = pluginElement.getAttribute( "submitter" );
        		//formatName = pluginElement.getAttribute( "format" );
        		//taskName = pluginElement.getAttribute( "taskname" );
        		name = pluginElement.getAttribute( "name" );
        		className = pluginElement.getAttribute( "classname" );
        		
        		//log.debug( String.format( "Found plugins with classname=%s, used for task: %s, format: %s from submitter:%s", className, taskName, formatName, submitterName ) );
        		log.debug( String.format( "Found plugins with classname=%s, used for plugin: %s", className, name ) );
        		
        		//45: verify that we got string form the xml file
        		//if( xmlRoot.getTagName().equals( "plugins" ) && submitterName != null && formatName != null && taskName != null && className != null )
        		if( xmlRoot.getTagName().equals( "plugins" ) && name != null && className != null )
        		{
        			//50: build the the key
        			//hashSubject= submitterName + formatName + taskName;
        			//key = hashSubject.hashCode();
        			//key = ( submitterName + formatName + taskName ).hashCode();
        			key = name.hashCode();
        			//System.out.println( key );
        			//System.out.println( submitterName + " " + formatName + " " + taskName );
        				
        			//60: add the key and value to the map
        			classNameMap.put( key, className );
        			log.debug( String.format( "key: %s added to map with the value %s", key, className ) );
        		}
        		else
        		{
        			log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
        			failedPlugins.add( new ThrownInfo( new SAXException( "pluginxml file failed to validate" ), pluginName ) );
        		}
        	}
        }//end .hasNext

        // The vector containing exceptions is larger than 0 throw it in a PluginResolverException 
        if( failedPlugins.size() > 0 )
            throw new PluginResolverException( failedPlugins, "Exceptions on the plugins");

        if ( classNameMap.size() == 0 )
            throw new PluginResolverException( String.format( "%s seems to be a valid path with plugins in it, but no plugins were loaded. I'm at my wits end and cannot give a better explanation.", path ) );

        log.debug( String.format( "Number of registrated plugins: %s ", classNameMap.size() ) );
    }
    
    
    /**
     * method for clearing the classNameMap, to force rebuild on next invocation
     * of the getPluginClassName method
     */
    void clearClassNameMap()
    {
        classNameMap.clear();
    }

}
