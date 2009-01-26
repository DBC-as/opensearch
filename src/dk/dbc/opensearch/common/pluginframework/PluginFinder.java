/**
 * \file PluginFinder.java
 * \brief the class that handles the pluginclassmap
 * \package opensearch
 */
package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.os.PluginFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;

import com.mallardsoft.tuple.Pair;

import java.io.File;
import java.io.FilenameFilter;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
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
    static Logger log = Logger.getLogger( "PluginFinder" );

    /**
     * classNameMap contains class names of plugins
     * The key for use in classNamePath: Concatination of submitter, format and task
     */

    private Map< String, String > classNameMap;
    private DocumentBuilder docBuilder;
      
    /**
     * builds the map containing the keys and related plugin classes
     * The keys are made from the task, format and datasource, the value is the
     * class of the plugin that can solve the specified task
     * @param path: the directory to look for the xml files describing the plugins
     * @param docBuilder: the DocumentBuilder used for parsing the xml files
     * @throws IllegalArgumentException when there is no directory or no files in it
     * @throws FileNotFoundException
     * @throws ParserConfigurationException
     *
     * \todo: should we hardcode the path?
     */
    public PluginFinder( DocumentBuilder docBuilder, String path ) throws FileNotFoundException, NullPointerException 
    {
        this.docBuilder = docBuilder;
        classNameMap = new HashMap();       

        // call updatePluginClassNameMap to generate the map
        updatePluginClassNameMap( path );
    }
    
    
    /**
     * Finds the right plugin class for the specified operation
     * @param task
     * @param format
     * @param submitter
     * @throws FileNotFoundException no plugin was found for the
     * given submitter, format and task
     * @return the name of the plugin class
     * \Todo: Is it the right Exception to throw and the parameters right?
     **/


    String getPluginClassName( String key ) throws FileNotFoundException
    {
        String className = null;

        //10: search through the map
        className = (String) classNameMap.get( key );
        
        //20: if there is no hit, raise exception
        if( className == null )
        {
            throw new FileNotFoundException( "No value for key: " + key );
        }
        
        //30: return classname
        return className;
    }

    
    /**
     * creates/updates the pluginClassMap
     * @throws FileNotFoundException if their are no pluginxml files
     * in the path specified in the constructor
     * 
     */
    private void updatePluginClassNameMap( String path ) throws FileNotFoundException
    {
        String pluginName = null;;
        Document pluginDocument = null;
        File pluginFile;
        Iterator pluginNameIter = null;
        
        //10: create the map 
        classNameMap.clear();
                
        //File pluginDirPath = fileHandler.getFile( path );

        //20: get the plugin xml files in the pluginDirPath
        FilenameFilter[] filterList = { new PluginFileFilter() };
        Vector<String> xmlPluginFileNames = FileHandler.getFileList( path, filterList, true );
        Vector<Pair<Throwable, String>> failedPlugins = new Vector<Pair<Throwable, String>>();
        
        pluginNameIter = xmlPluginFileNames.iterator();
        log.debug( String.format( "size of xmlPluginFileNames: ", xmlPluginFileNames.size() ) );
        //30: for each pull task, format, datasource and class out of them and build
        // the key and put it into the map with the class as value
        while( pluginNameIter.hasNext() )
        {
            boolean couldFormat = false;
            String submitterName = null;
            String formatName = null;
            String className = null;
            String taskName = null;
            String key = null;
            //35: get the xml file as a DOM object
            try
            {
                pluginName = (String)pluginNameIter.next();
                pluginFile = FileHandler.getFile( pluginName );
                pluginDocument = docBuilder.parse( pluginFile );
                couldFormat = true;
            }
            catch( SAXException saxe )
            {
                couldFormat = false;//dont try to do further work on this file
                log.error( String.format( "could not parse pluginxml file: '%s'\nException: %s", 
                                          pluginName, saxe.getMessage() ) );
                failedPlugins.add( new Pair( saxe, pluginName ) );
            }   
            catch( IOException ioe )
            {
                couldFormat = false;//dont try to do further work on this file
                log.error( String.format( "could not parse pluginxml file: '%s'\nException: %s ", 
                                          pluginName, ioe.getMessage() ) );
                failedPlugins.add( new Pair( ioe, pluginName ) );
            }
            catch( NullPointerException npe )
            {
                couldFormat = false;
                log.error( "Invalid file name in the plugin name list generated by the FileHandler" );
                failedPlugins.add( new Pair( npe, pluginName ) );
            }
            
            if( couldFormat )
            {
                log.debug("could format");
                Element xmlRoot = pluginDocument.getDocumentElement();
                log.debug( "xmlRoot tagname: " + xmlRoot.getTagName() );
                // 37: get the plugin element
                NodeList pluginNodeList = xmlRoot.getElementsByTagName( "plugin" );

                /** \todo: NodeList.item( int index ) returns null if outofbounds**/
                Element pluginElement = (Element) pluginNodeList.item( 0 );
                //40: pull out the values

                submitterName = pluginElement.getAttribute( "submitter" );
                formatName = pluginElement.getAttribute( "format" );
                taskName = pluginElement.getAttribute( "task" );
                className = pluginElement.getAttribute( "classname" );

                //45: verify that we got string form the xml file
                if( xmlRoot.getTagName().equals( "plugins" ) && submitterName != null && formatName != null && taskName != null && className != null )
                {
                    //50: build the the key
                    key = submitterName + formatName + taskName;

                    //60: add the key and value to the map
                    classNameMap.put( key, className );
                    log.debug( String.format( "key: %s added to map", key ) );
                }
                else
                {
                    log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
                }
            }
            else
            {
                log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
                //                throw new TasksNotValidatedException( failedPlugins, "Plugins could not be loaded" );
            }
        }//end .hasNext

        if ( classNameMap.size() < 1 )
        {
            throw new FileNotFoundException( String.format( "No plugin description files at %s ", path  ) );
        }

        log.debug( String.format( "Number of registrated plugins: %s ", classNameMap.size() ) );    
    }

}
