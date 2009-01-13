/**
 * \file PluginFinder.java
 * \brief the class that handles the pluginclassmap
 * \package opensearch
 */
package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.os.XmlFileFilter;
import dk.dbc.opensearch.common.os.FileHandler;

import javax.xml.parsers.DocumentBuilder;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
//import org.w3c.dom.NodeList;
import java.io.File;
import java.util.Map;
import java.util.HashMap;
import org.apache.log4j.Logger;
import java.io.FilenameFilter;
import java.util.Vector;
import java.util.Iterator;

import java.io.FileNotFoundException;
import java.lang.IllegalArgumentException;
import java.lang.ClassNotFoundException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;

/**
 * class that handles the creation of the map containing info about the existing
 * plugins. I creates the map, updates when asked to and returns info needed to
 * load specific plugins by the plugin loader
 */
public class PluginFinder {

    static Logger log = Logger.getLogger( "PluginFinder" );

    /**
     * classNameMap contains class names of plugins
     * The key for use in classNamePath: Concatination of submitter, format and task
     */

    private Map classNameMap;
    private String path;
    private FileHandler fileHandler;
    private DocumentBuilder docBuilder;

    /**
     * builds the map containing the keys and related plugin classes
     * The keys are made from the task, format and datasource, the value is the
     * class of the plugin that can solve the specified task
     * @param fileHandler: A FileHander object
     * @param path: the directory to look for the xml files describing the plugins
     * @param docBuilder: the DocumentBuilder used for parsing the xml files
     * @throws IllegalArgumentException when there is no directory or no files in it
     * @throws FileNotFoundException
     * @throws ParserConfigurationException
     *
     * \Todo: should we hardcode the path?
     */

    public PluginFinder( String path, FileHandler fileHandler, DocumentBuilder docBuilder ) throws /*IllegalArgumentException,*/  NullPointerException {

        this.fileHandler = fileHandler;
        this.docBuilder = docBuilder;
        // no need to use dependency injection for this internal HasMmap.
        classNameMap = new HashMap();
        //10: verify that the path is valid
        this.path = path;
        // File pluginDirPath = fileHandler.getFile( path );

        // call updatePluginClassMap
        updatePluginClassMap();
    }
    /**
     * Finds the right plugin class for the specified operation
     * @param task
     * @param format
     * @param submitter
     * @throws ClassNotFoundException no plugin was found for the
     * given submitter, format and task
     * @return the name of the plugin class
     * \Todo: Is it the right Exception to throw and the parameters right?
     **/

    public String getPluginClass( String task, String format, String submitter ) throws ClassNotFoundException{
        String key = submitter + format + task;
        String className = "";

        //10: search through the map
        className = (String) classNameMap.get( key );
        //20: if there is no hit, raise exception
        if( className == null ){
            throw new ClassNotFoundException( "No value for key: " + key );
        }
        //30: return classname
        return className;
    }

    /**
     * updates the pluginClassMap
     * @throws IllegalArgumentException if their are no pluginxml files
     * in the path specified in the constructor
     * \Todo: Is that the right exception to throw?
     */
    public void updatePluginClassMap() throws  NullPointerException/*, SAXException*/ {
        String key;
        String pluginName = null;;
        String submitterName;
        String formatName;
        String taskName;
        String className;
        Document pluginDocument = null;
        File pluginFile;
        Boolean couldFormat = true;
        Iterator pluginNameIter = null;

        //10: create the map 
        classNameMap.clear();
                
        //File pluginDirPath = fileHandler.getFile( path );

        //20: get the plugin xml files in the pluginDirPath
        FilenameFilter[] filter = { new XmlFileFilter() };
        Vector <String> xmlPluginFileNames = fileHandler.getFileList( path, filter, true );
        pluginNameIter = xmlPluginFileNames.iterator();
        log.debug( "size of xmlPluginFileNames: " + xmlPluginFileNames.size() );

        //30: for each pull task, format, datasource and class out of them and build
        // the key and put it into the map with the class as value
        while( pluginNameIter.hasNext() ){
            couldFormat = true; //reset
            submitterName = null;
            formatName = null;
            taskName = null;
            className = null;
            //35: get the xml file as a DOM object

            try{
                pluginName = (String)pluginNameIter.next();
                pluginFile = fileHandler.getFile( pluginName );
                pluginDocument = docBuilder.parse( pluginFile );
            }catch( SAXException saxe ){
                couldFormat = false;//dont try to do further work on this file
                log.error( String.format( "could not parse pluginxml file: '%s' ", pluginName ) );
            }catch( IOException ioe ){
                couldFormat = false;//dont try to do further work on this file
                log.error( String.format( "could not parse pluginxml file: '%s' ", pluginName ) );
            }catch( NullPointerException npe ){
                couldFormat = false;
                log.error( "Invalid file name in the plugin name list generated by the FileHandler" );
            }
            if( couldFormat ){
                log.debug("could format");
                Element xmlRoot = pluginDocument.getDocumentElement();
                log.debug( "xmlRoot tagname: " + xmlRoot.getTagName() );

                //40: pull out the values
                submitterName = xmlRoot.getAttribute( "submitter" );
                formatName = xmlRoot.getAttribute( "format" );
                taskName = xmlRoot.getAttribute( "task" );
                className = xmlRoot.getAttribute( "class" );

                //45: verify that we got string form the xml file
                if( xmlRoot.getTagName() == "plugin" && submitterName != null && formatName != null && taskName != null && className != null ){
                    //50: build the the key
                    key = submitterName + formatName + taskName;

                    //60: add the key and value to the map
                    classNameMap.put( key, className );
                    log.debug( String.format( "key: %s added to map", key ) );
                }else{
                    log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
                }
            }else{
                log.error( String.format( "Pluginxml file: '%s' is invalid", pluginName ));
            }
        }
        log.debug( String.format( "Number of registrated plugins: %s ", classNameMap.size() ) );
    }

}
