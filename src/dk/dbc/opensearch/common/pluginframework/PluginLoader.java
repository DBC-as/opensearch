package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.os.FileHandler;

import org.apache.log4j.Logger;

import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;

/**
 * PluginLoader
 */
public class PluginLoader 
{
    static Logger log = Logger.getLogger( "PluginLoader" );
    // \TODO: is this the way to give the path?
    String pluginPathName = "classes/dk/dbc/opensearch/plugins";
    FileHandler fileHandler;
    String pluginSubPathName = "build/classes/dk/dbc/opensearch/plugins/";
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
