package dk.dbc.opensearch.common.pluginframework;

import org.apache.log4j.Logger;
import java.io.File;
import java.io.FileNotFoundException;
import java.lang.Class;
import java.lang.ClassNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
import dk.dbc.opensearch.common.os.FileHandler;

/**
 * PluginLoader
 */
public class PluginLoader {

    static Logger log = Logger.getLogger( "PluginLoader" );
    String pluginPathName;
    FileHandler fileHandler;


    /**
     *
     */
    public PluginLoader( FileHandler fileHandler ) {

        pluginPathName = "classes/dk/dbc/opensearch/plugins";
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

        //creating the classloader
        ClassLoader cl = new PluginClassLoader();
        //creating the full class name

        String fullPluginClassName = pluginName;
        //loading the class
        log.debug( fullPluginClassName );
        Class loadedClass = cl.loadClass( fullPluginClassName );

        IPluggable thePlugin = ( IPluggable ) loadedClass.newInstance();

        return thePlugin;

    }
}
