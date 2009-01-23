package dk.dbc.opensearch.common.pluginframework;

import javax.xml.parsers.DocumentBuilder;
import org.apache.log4j.Logger;

import java.lang.IllegalArgumentException;
import java.io.FileNotFoundException;
import java.lang.InstantiationException;
import java.lang.IllegalAccessException;
/**
 * 
 */
public class PluginResolver {
    private PluginFinder PFinder;
    private PluginLoader PLoader;

  /**
   * 
   */
    public PluginResolver( DocumentBuilder docBuilder, ClassLoader pluginClassLoader )throws NullPointerException, FileNotFoundException {
        PFinder = new PluginFinder( docBuilder );
        PLoader = new PluginLoader( pluginClassLoader );
    
  }
    public IPluggable getPlugin( String submitter, String format, String task )throws IllegalArgumentException, FileNotFoundException, InstantiationException, IllegalAccessException{
        String key = submitter + format + task;
        String pluginClassName = PFinder.getPluginClassName( key );
        return PLoader.getPlugin( pluginClassName );
    }
}