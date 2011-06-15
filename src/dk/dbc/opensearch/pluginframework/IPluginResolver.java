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

import dk.dbc.opensearch.fedora.IObjectRepository;
                                   
import java.io.FileNotFoundException;
import java.lang.IllegalAccessException;
import java.lang.InstantiationException;
import java.lang.reflect.InvocationTargetException;

import javax.xml.parsers.ParserConfigurationException;


/**
 * The pluginframework is accessed through this interface. 
 * The framework can find plugins and load them runtime and it can 
 * validate whether there exists pluigns to solve a number of tasks. 
 * It cannot garantie that the plugins will still be there when 
 * called ( someone could delete them from there folders ) the 
 * IPluginResolver can also force an update on the knowledge of 
 * available plugins through the clearPluginRegistration method, 
 * if someone changed them while the system is running.   
 */
public interface IPluginResolver
{
    /**
     * Finds and loads a plugin that can solve a specific task on an object 
     * matching the format and submitter through the PluginID object for the
     * @param classname
     * @throws FileNotFoundException when the wanted plugin cannot be found or 
     * there are no plugin registration files to be found. 
     * @throws InstantiationException when the plugin cannot be instantiated
     * \todo: Should we throw different exceptions for the 2 cases 
     * FileNotFoundException covers?  
     * @throws IllegalAccessException 
     * @throws ParserConfigurationException 
     */
    IPluggable getPlugin ( String classname ) throws FileNotFoundException, InstantiationException, ClassNotFoundException, IllegalAccessException, ParserConfigurationException, InvocationTargetException, PluginException;

}