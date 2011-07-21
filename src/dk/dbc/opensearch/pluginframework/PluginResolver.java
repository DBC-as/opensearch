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

package dk.dbc.opensearch.pluginframework;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


import org.apache.log4j.Logger;


/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    private static Logger log = Logger.getLogger( PluginResolver.class );
    private final PluginLoader pLoader;

    private static final Map< String, IPluggable > pluginInstanceCache = Collections.synchronizedMap( new HashMap< String , IPluggable> () );

    /**
     * The constructor sets up the class loader for the plugins and initiates
     * the plugin loader
     */
    public PluginResolver()
    {     
        this.pLoader = new PluginLoader();

        log.trace( "PluginResolver constructed" );
    }


    /**
     * Retrives a plugin instance matching the class name given with
     * {@code className}
     *
     * @param className the name of the class of the plugin
     * @return a plugin matching the key made out of the params
     * @throws InstantitionException if the PluginLoader cant load the desired plugin
     * @throws IllegalAccessException if the plugin file cant be accessed by the PluginLoader
     * @throws ClassNotFoundException if the class of the plugin cannot be found
     */
    public synchronized IPluggable getPlugin( String className ) throws InstantiationException, IllegalAccessException, ClassNotFoundException, InvocationTargetException, PluginException
    {  
        if (!pluginInstanceCache.containsKey( className ))
        {
            IPluggable plugin = pLoader.getPlugin( className );
            pluginInstanceCache.put( className, plugin );
            log.info( String.format("Plugin: '%s' created", className ) );
        }
        return pluginInstanceCache.get( className );
    }

    /**
     * The PluginLoader class contains one method used for loading plugins.
     * Essentially it wraps the native, abtstract Java ClassLoader class responsible
     * for loading classes from a specified class name (see the PluginFinder class
     * and the .plugin files.)
     */
    private static class PluginLoader
    {
        private Logger log = Logger.getLogger( PluginLoader.class );


        /**
         * Given a qualified class name of the plugin, this method locates the
         * plugin on the classpath and loads and returns the plugin
         *
         * @param pluginClassName the class name of the wanted plugin
         * @return the loaded plugin
         * @throws InstantiationException if the classloader cant sinstantiate the desired plugin
         * @throws IllegalAccessException if the wanted plugin cant be accessed
         * @throws ClassNotFoundException if the specified class cannot found
         */
        public IPluggable getPlugin( String pluginClassName ) throws InstantiationException, IllegalAccessException, ClassNotFoundException, PluginException, InvocationTargetException
        {
            try
            {
                Class[] parameterTypes = new Class[] { };

                log.debug( String.format( "PluginLoader loading plugin class name '%s'", pluginClassName ) );
                Class loadedClass = Class.forName( pluginClassName );
                Constructor pluginConstructor = loadedClass.getConstructor( parameterTypes );
                IPluggable thePlugin = (IPluggable) pluginConstructor.newInstance( new Object[] { } );

                return thePlugin;
            }
            catch( ClassNotFoundException cnfe )
            {
                String error = String.format( "No value for className: %s ", pluginClassName );
                log.error( error, cnfe );
                throw new ClassNotFoundException( error, cnfe );
            }
            catch( NoSuchMethodException nsme )
            {
                String error = String.format( "the class: '%s' lacks a default constructor", pluginClassName );
                log.error( error, nsme );
                throw new PluginException( error, nsme );
            }
            catch( InvocationTargetException ite )
            {
                String error = String.format( "couldnt invoke class: '%s'", pluginClassName );
                log.error( error, ite );
                throw new InvocationTargetException( ite, error );
            }
        }
    }
}