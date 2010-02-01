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

package dk.dbc.opensearch.common.pluginframework;


import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.xml.parsers.ParserConfigurationException;

import org.apache.commons.configuration.ConfigurationException;
import org.apache.log4j.Logger;


/**
 *
 */
public class PluginResolver implements IPluginResolver
{
    static Logger log = Logger.getLogger( PluginResolver.class );

    static ClassLoader pluginClassLoader = new PluginClassLoader();    
    static PluginLoader PLoader = new PluginLoader( pluginClassLoader );
    static boolean constructed = false;
    
    static private final Map< String, IPluggable> pluginInstanceCache = Collections.synchronizedMap(new HashMap< String , IPluggable> ());


    /**
     * The constructor sets up the class loader for the plugins and initiates
     * the plugin loader
     */
    public PluginResolver()
    {      
        pluginClassLoader = new PluginClassLoader();
        PLoader = new PluginLoader( pluginClassLoader );

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
    public IPluggable getPlugin( String className ) throws InstantiationException, IllegalAccessException, ClassNotFoundException
    {  
        if (!pluginInstanceCache.containsKey( className ))
        {
            IPluggable plugin = PLoader.getPlugin( className );
            pluginInstanceCache.put( className, plugin );
            log.info( String.format("Plugin %s created", className ) );
        }
        return pluginInstanceCache.get( className );
    }

    

}