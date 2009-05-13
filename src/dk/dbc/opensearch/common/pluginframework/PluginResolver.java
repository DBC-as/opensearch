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


import dk.dbc.opensearch.common.config.FileSystemConfig;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.util.ArrayList;
import java.util.Vector;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    static Logger log = Logger.getLogger( PluginResolver.class );

    static String path;
    static ClassLoader pluginClassLoader;
    static PluginFinder PFinder;
    static PluginLoader PLoader;
    static boolean constructed = false;
    

    /**
     * @throws IOException 
     * @throws NullPointerException
     * @throws PluginResolverException if the PluginFinder has trouble while reading the .plugin files
     * @throws ParserConfigurationException from PluginFinder if it cant parse the .plugin files
     * @throws FileNotFoundException when the PluginFinder cant find the .plugin files
     * @throws ConfigurationException 
     */
    public PluginResolver() throws NullPointerException, PluginResolverException, ParserConfigurationException, FileNotFoundException, ConfigurationException
    {      
        if( ! constructed )
        {
            pluginClassLoader = new PluginClassLoader();
            PLoader = new PluginLoader( pluginClassLoader );
            
            constructed = true;
            log.info( "PluginResolver constructed" );            
        }
    }

    
    /**
     * @param submitter, the submitter of the data the plugin works on
     * @param format, the format of the data the plugin works on
     * @param name, the name of the plugin (see the .plugin files)
     * @returns a plugin matching the key made out of the params  
     * @throws InstantitionException if the PluginLoader cant load the desired plugin
     * @throws FileNotFoundException if the desired plugin file cannot be found
     * @throws IllegalAccessException if the plugin file cant be accessed by the PluginLoader
     * @throws ClassNotFoundException if the class of the plugin cannot be found
     * @throws PluginResolverException if key doesnot give a value from the PluginFinder
     * @throws ParserConfigurationException 
     */
    public IPluggable getPlugin( String className ) throws FileNotFoundException, InstantiationException, IllegalAccessException, ClassNotFoundException, PluginResolverException, ParserConfigurationException
    {  
        return PLoader.getPlugin( className );
    }

    
    /**
     * @param submitter, the submitter to solve tasks for
     * @param format, the format of the data to perform tasks on
     * @param taskList, the tasks to be validated that plugins exists to perform
     * @returns a Vector of tasks there are no plugins to perform og the specified format from the submitter. If it is empty execution can continue in the calling thread
     * @throws PluginResolverException when there are parser and reader errors from the PluginFinder
     * @throws ParserConfigurationException 
     */
    /*public Vector<String> validateArgs( String submitter, String format, ArrayList< String > classNameList ) throws PluginResolverException, ParserConfigurationException
    {
        log.debug( String.format( "Validating submitter %s, format %s, and %s", submitter, format, classNameList.toString() ) );
        Vector< String > pluginNotFoundVector = new Vector< String >();

        // Loop through list of tasks finding tasks without matching plugin.
        for( int i = 0; i < classNameList.size(); i++ )
        {
            String className = classNameList.get( i ).toString();
            //int key = className.hashCode();
            //log.debug( String.format( "hashcode: %s generated for key: %s", key, hashSubject  ) );            
            try
            {
            	//PFinder.getPluginClassName( key );
                PLoader.getPlugin( className );
            }
            catch( ClassNotFoundException cnfe )
            {
            	pluginNotFoundVector.add( classNameList.get( i ) );
            }
            catch( InstantiationException ie )
            {
                pluginNotFoundVector.add( classNameList.get( i ) );
            }
            catch( IllegalAccessException ice )
            {
                pluginNotFoundVector.add( classNameList.get( i ) );
            }
        }
       
        log.debug( "pluginNotFoundVector size " + pluginNotFoundVector.size() );
        return pluginNotFoundVector;
        }*/
}