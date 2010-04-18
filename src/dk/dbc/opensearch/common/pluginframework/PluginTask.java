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

import dk.dbc.opensearch.common.types.InputPair;

import java.util.List;
import java.util.ArrayList;

import org.apache.log4j.Logger;

public class PluginTask
{
    private String pluginName;
    private String scriptName;
    private List<InputPair<String, String>> argList;

    public PluginTask( String pluginName, String scriptName, List<InputPair<String, String>> argList )
    {
        this.pluginName = pluginName;
        this.scriptName = scriptName;
        this.argList = argList;
    }

    public String getPluginName()
    {
        return pluginName;
    }

    public String getScriptName()
    {
        return scriptName;
    }

    public List<InputPair<String, String>> getArgList()
    {
        return argList;
    }
}