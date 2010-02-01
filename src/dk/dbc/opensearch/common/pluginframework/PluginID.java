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

/**
 * The PluginId type handles information about plugins.
 *
 */

/**
 * PluginID
 */
public class PluginID 
{
    private String submitter;
    private String format;
    private String task;

    /**
     * @param submitter Information on the submitter of the material. The submitter should be known by the system through other means than registering with a plugin
     * @param format Information on the format of the submitted material. 
     * @param task Information on the task that the plugin handles. This information is matched a String in the plugins 
     */
    public PluginID( String submitter, String format, String task ) 
    {
        this.submitter = submitter;
        this.format = format;
        this.task = task;
    }

    
    /**
     * getPluginID returns a hashvalue based on the submitter, format
     * and task that the PluginID object is constructed with. Please
     * note that the hashvalue is dependant on the (in this method
     * embedded) position of the informations.
     * 
     * @return an integer defining the hash value of the plugin.
     */
    public int getPluginID()
    {
        String hashSubject = submitter + format + task;
        return hashSubject.hashCode();
    }

    
    /**
     * @return the value of the submitter associated with the
     * pluginid
     */
    public String getPluginSubmitter()
    {
        return submitter;
    }
    
    
    /**
     * @return the value of the format associated with the
     * pluginid
     */
    public String getPluginFormat()
    {
        return format;
    }
    
    
    /**
     * @return the value of the task associated with the
     * pluginid
     */
    public String getPluginTask()
    {
        return task;
    }
}
