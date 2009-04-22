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


import org.apache.log4j.Logger;

import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

/**
 * PluginLoader
 */
public class PluginLoader 
{
    static Logger log = Logger.getLogger( PluginLoader.class );
    
    //String pluginPathName = FileSystemConfig.getFileSystemPluginsPath(); // "classes/dk/dbc/opensearch/plugins";
    //FileHandler fileHandler;
    //String pluginSubPathName = "build/classes/dk/dbc/opensearch/plugins/";
    ClassLoader cl;


    /**
     * 
     */
    public PluginLoader( ClassLoader cl ) 
    {
        this.cl = cl;
    }

    
    /**
     * Given a qualified class name of the plugin, this method locates the
     * plugin on the classpath and loads and returns the plugin
     * @param pluginName the class name of the wanted plugin
     * @return the loaded plugin
     * @throws InstantiationException if the classloader cant sinstantiate the desired plugin
     * @throws IllegalAccessException if the wanted plugin cant be accessed
     * @throws ClassNotFoundException if the specified class cannot found  
     */
    IPluggable getPlugin( String pluginClassName ) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {        
        Class loadedClass = null;
        //loading the class
        log.debug( String.format( "The plugin class name: %s", pluginClassName) );
       
        loadedClass = cl.loadClass( pluginClassName );
       
        IPluggable thePlugin = ( IPluggable )loadedClass.newInstance();

        return thePlugin;
    }
}
