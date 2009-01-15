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
public class PluginLoader {

    static Logger log = Logger.getLogger( "PluginLoader" );
    String pluginPathName = "classes/dk/dbc/opensearch/plugins";
    FileHandler fileHandler;
    ClassLoader cl;


    /**
     *
     */
    public PluginLoader( ClassLoader cl, FileHandler fileHandler ) {

        this.cl = cl;
        this.fileHandler = fileHandler;
    }

    /**
     * Given a qualified name of the plugin, this method locates the
     * plugin on the classpath and loads the plugin
     * @param pluginName the name of the plugin
     */
    public IPluggable loadPlugin( String pluginName )throws FileNotFoundException, ClassNotFoundException, InstantiationException, IllegalAccessException{

        File pluginPath = fileHandler.getFile( pluginPathName );
        if( !pluginPath.exists() ){
            throw new FileNotFoundException( String.format( "plugin directory %s could not be found", pluginPath.getAbsolutePath() ) );
        }
        

        String fullPluginClassName = pluginName;
        //loading the class
        log.debug( String.format( "The plugin class name: %s" ,fullPluginClassName) );
        Class loadedClass = cl.loadClass( fullPluginClassName );

        IPluggable thePlugin = ( IPluggable ) loadedClass.newInstance();

        return thePlugin;

    }
}
