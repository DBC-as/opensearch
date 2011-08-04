package dk.dbc.opensearch.pluginframework;

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

import dk.dbc.opensearch.pluginframework.IPluginEnvironment;
import dk.dbc.opensearch.pluginframework.IPluggable;

import java.util.Map;
import java.util.HashMap;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The PluginTask is a container for objects which needs to be grouped together
 * in relation to a plugin.
 *
 * \note: I am not sure that PluginTask is an appropiate name for this class.
 */
public class PluginTask
{

    private IPluggable plugin;
    private IPluginEnvironment env;

    public PluginTask( IPluggable plugin,  IPluginEnvironment env )
    {
        this.plugin  = plugin;
	this.env     = env;
    }

    public IPluggable getPlugin()
    {
        return plugin;
    }

    public IPluginEnvironment getEnvironment()
    {
	return env;
    }
}

