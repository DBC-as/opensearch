package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.pluginframework.TasksNotValidatedException;
import dk.dbc.opensearch.common.pluginframework.PluginResolverException;
import java.io.FileNotFoundException;
import java.lang.InstantiationException;

import java.lang.IllegalAccessException;

public interface IPluginResolver
{
    /**
     * Finds and loads a plugin that can solve a specific task on an object 
     * matching the format and submitter 
     * @param submitter the submitter of the object
     * @param format, the foramt of the object the plugin must work on
     * @param task, the task to solve on the object
     * @throws FileNotFoundException when the wanted plugin cannot be found or 
     * there are no plugin registration files to be found. 
     * @throws InstantiationException when the plugin cannot be instantiated
     * \Todo: Should we throw different exceptions for the 2 cases 
     * FileNotFoundException covers?  
     * @throws IllegalAccessException 
     */
    IPluggable getPlugin ( String submitter, String format, String task ) throws FileNotFoundException, InstantiationException, ClassNotFoundException, IllegalAccessException, PluginResolverException;

    
    /**
     * validates whether plugins for the tasks specified for the submitter and 
     * format exists.
     * @param submitter, the submitter to search for plugins for
     * @param format, the format to search for plugins for
     * @param taskList the tasks to find plugins for
     * @return validateArgs when the plugins are present
     * @throws TasksNotValidatedException when not all the needed plugins are found, the
     * Exception contains info about which plugins 
     */
    boolean validateArgs( String submitter, String format, String[] taskList )throws TasksNotValidatedException;
    
    
    /**
     * clears the registrations of plugins and forces an update next time 
     * plugin information is needed
     */    
    void clearPluginRegistration();
}