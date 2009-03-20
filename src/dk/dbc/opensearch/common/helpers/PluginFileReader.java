package dk.dbc.opensearch.common.helpers;


import dk.dbc.opensearch.common.config.FileSystemConfig;
import dk.dbc.opensearch.common.os.FileHandler;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.ParserConfigurationException;

import org.apache.log4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;


/**
 * Reads the plugin xml file into a hashmap
 */
public class PluginFileReader 
{
    static Logger log = Logger.getLogger( PluginFileReader.class );
 
    public String NAME;
    public String CLASSNAME;
    public String SUBMITTER;
    public String FORMAT;
    public String TASK;
    
    
    /**
     * Getter for a map of a .plugin file.
     */
    public void readPluginFile( String pluginFileName ) throws ParserConfigurationException, SAXException, IOException
    {
        String pluginPath = FileSystemConfig.getFileSystemPluginsPath();
    	if( pluginPath.isEmpty() )
    	{
    		throw new IOException( "<ERROR> Missing .plugin file for plugin: " + pluginFileName );
    	}
    	
    	File pluginFile = FileHandler.getFile( pluginPath );
        
        //NodeList pluginNodeList = getPluginNodeList( pluginFile, "plugin" );
    	NodeList pluginNodeList = XMLFileReader.getNodeList( pluginFile, "plugin" );
        Element pluginElement = (Element)pluginNodeList.item( 0 );
        
        NAME = pluginElement.getAttribute( "name" );        
        CLASSNAME = pluginElement.getAttribute( "classname" );
        SUBMITTER = pluginElement.getAttribute( "submitter" );
        FORMAT = pluginElement.getAttribute( "format" );
        TASK = pluginElement.getAttribute( "task" );        
    }
    
    
//    private NodeList getPluginNodeList( File pluginFile, String tagName ) throws ParserConfigurationException, SAXException, IOException
//    {
//    	DocumentBuilderFactory docBuildFact = DocumentBuilderFactory.newInstance();
//        DocumentBuilder docBuilder = docBuildFact.newDocumentBuilder();
//        Document pluginDocument = docBuilder.parse( pluginFile );
//        Element xmlRoot = pluginDocument.getDocumentElement();
//        return xmlRoot.getElementsByTagName( tagName );    	
//    }
}