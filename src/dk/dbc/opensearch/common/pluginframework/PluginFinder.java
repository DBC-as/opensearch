/**
 * \file PluginFinder.java
 * \brief the class that handles the pluginclassmap
 * \package pluginframework
 */
package dk.dbc.opensearch.common.pluginframework;

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
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * The PluginFinder class handles creation of a map containing info about existing
 * plugins; this info originates from .plugin files adhering to the xml standard. 
 * A .plugin file essentially contains 1) the fully qualified name of the class that 
 * handles files (posts) of a given type, and 2) the type of the plugin, e.g., 
 * 'harvest', 'annotate', etc. (see PluginType). 
 * 
 * In short, the PluginFinder is used in the PluginResolver class to find the name 
 * of the plugin class, which in turn is used by the PluginLoader class to load a 
 * given plugin.
 */
public class PluginFinder
{
    static Logger log = Logger.getLogger( PluginFinder.class );

    /**
     * classNameMap contains class names of plugins of the form
     * 'dk.dbc.opensearch.plugins.Xxx'.
     * The key in classNameMap is the hash value of the 'name' given by the .plugin 
     * file.
     */
    private Map< Integer, String > classNameMap;
    private String path;

    
    /**
     * The constructor sets the path to the .plugin files and initiates the class name map.
     * The map containing the keys and related plugin classes is build, where the keys
     * are the hash values of the 'name' obtained from the .plugin files.
     * 
     * @param path: the directory to look for the xml files describing the plugins
     * @param docBuilder: the DocumentBuilder used for parsing the xml files
     * @throws IllegalArgumentException when there is no directory or no files in it
     * @throws FileNotFoundException when the upDateClassMapMethod cant find the files
     * @throws ParserConfigurationException
     * @throws PluginResolverException
     * @throws ParserConfigurationException 
     * @throws ParserConfigurationException 
     * @throws ParserConfigurationException 
     */
    public PluginFinder( String path ) throws FileNotFoundException, NullPointerException, PluginResolverException, ParserConfigurationException
    {
        this.path = path;        
        this.classNameMap = new HashMap< Integer, String >();
        
        buildPluginClassNameMap( this.path );       
    }


    /**
     * Finds the right plugin class for the specified operation.
     * @param key, the key to find the classname by
     * @throws FileNotFoundException is thrown when no plugin was found for the given key
     * @throws PluginResolverException is thrown if parsing or reading .plugin files fails
     * @throws ParserConfigurationException 
     * @return the name of the plugin class
     **/
    String getPluginClassName( int key ) throws FileNotFoundException, PluginResolverException, ParserConfigurationException
    {
        log.debug( String.format( "getting key %s from classnamemap", key ) );
        log.debug( "classNameMap " + classNameMap.toString() );

        String className = null;
        
        if( classNameMap.size() < 1 )
        {
            buildPluginClassNameMap( path );
        }
        
        className = (String) classNameMap.get( key );
        
        // If there is no hit, raise exception
        if( className == null )
        {        	
            log.debug( String.format( "No value for key: %s ", key ) );
            throw new FileNotFoundException( String.format( "No value for key: %s ", key ) );
        }
        else
        {
            return className;
        }
    }
    
    
    /**
     * Builds class name map containing names of the plugins
     *
     * @throws FileNotFoundException if there are no pluginxml files on the given path
     * @throws PluginResolverException if one or more of the found plugins could not be loaded
     * @throws ParserConfigurationException
     */
    private void buildPluginClassNameMap( String path ) throws PluginResolverException, FileNotFoundException, ParserConfigurationException
    {
    	DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();      
        DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();

        //log.debug( String.format( "Clearing map with %s members", classNameMap.size() ) );
        //clearClassNameMap();

        log.debug( String.format( "Getting file list from %s", path ) );
        FilenameFilter[] filterList = { new PluginFileFilter() };        
        Vector<String> xmlPluginFileNames = FileHandler.getFileList( path, filterList, true );
        if ( xmlPluginFileNames.size() < 1 )
        {
            throw new FileNotFoundException( String.format( "No plugin description files at %s ", path ) );
        }
        log.debug( String.format( "Number of found plugins: %s ", xmlPluginFileNames.size() ) );

        Vector<ThrownInfo> failedPlugins = new Vector<ThrownInfo>();    	
        Iterator< String > pluginNameIter = null;
    	pluginNameIter = xmlPluginFileNames.iterator();
    	
    	// For each plugin name get name and className, build key from the name, and put it into 
    	// the map with the className as value
        while( pluginNameIter.hasNext() )
        {
            boolean couldFormat = false;
            String className = null;
            String name = null;
            int key = 0;
            String pluginName = null;;
            File pluginFile = null;
            Document pluginDocument = null;

            try
            {
                pluginName = (String)pluginNameIter.next();
                pluginFile = FileHandler.getFile( pluginName );
                log.debug( String.format( "Building DOM object from file %s", pluginName ) );
                pluginDocument = docBuilder.parse( pluginFile );
                couldFormat = true;
            }
            catch( SAXException saxe )
            {
                couldFormat = false;
                log.error( String.format( "Could not parse pluginxml file: '%s'\nException: %s", pluginName, saxe.getMessage() ) );
                failedPlugins.add( new ThrownInfo( saxe, pluginName ) );
            }
            catch( IOException ioe )
            {
                couldFormat = false;
                log.error( String.format( "Could not parse pluginxml file: '%s'\nException: %s ", pluginName, ioe.getMessage() ) );
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
                NodeList pluginNodeList = xmlRoot.getElementsByTagName( "plugin" );
                Element pluginElement = (Element) pluginNodeList.item( 0 );/** \todo: NodeList.item( int index ) returns null if outofbounds**/        		
                name = pluginElement.getAttribute( "name" );
                className = pluginElement.getAttribute( "classname" );        		
                log.debug( String.format( "Found plugins with classname = %s, used for plugin: %s", className, name ) );
        	
                if( xmlRoot.getTagName().equals( "plugins" ) && name != null && className != null )
                {
                    key = className.hashCode();
                    classNameMap.put( key, className );
                    
                    log.debug( String.format( "key: %s added to map with the value %s", key, className ) );
                }
                else
                {
                    log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
                    failedPlugins.add( new ThrownInfo( new SAXException( "pluginxml file failed to validate" ), pluginName ) );
        	}
            }
        }

        if( failedPlugins.size() > 0 )
        {
            throw new PluginResolverException( failedPlugins, "Exceptions on the plugins" );
        }
        
        if ( classNameMap.size() == 0 )
        {
            throw new PluginResolverException( String.format( "%s seems to be a valid path with plugins in it, but no plugins were loaded. I'm at my wits end and cannot give a better explanation.", path ) );
        }
        
        log.debug( String.format( "Number of registrated plugins: %s ", classNameMap.size() ) );
        log.info( "classNameMap updated" );
    }
    
    
    /**
     * method for clearing the classNameMap, to force rebuild on next invocation
     * of the getPluginClassName method
     */
//    void clearClassNameMap()
//    {
//        classNameMap.clear();
//        log.info( "classNameMap cleared" );
//    }

}
