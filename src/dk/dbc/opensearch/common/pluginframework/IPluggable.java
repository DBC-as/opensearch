package dk.dbc.opensearch.common.pluginframework;
/**
 * This interface represents the base point for all plugins. It
 * defines two methods that are the common basis for all plugins that
 * perform tasks in the datadock or pti.
 * 
 * The pluginID must be given to the plugin at object creation
 * time. The taskname is defined at compile time but could be the
 * subject of configuration variables in the future.
 * 
 * \todo: make the taskname a configuration value instead of setting it compile time
 */
public interface IPluggable
{

    /**
     * getPluginID returns the - at object creation time given - id
     * for this plugin. pluginIDs identify plugins to the
     * pluginframework, which exposes the plugin to the system based
     * on which task and format the plugin can handle and which
     * submitters the plugin is valid for.
     * 
     * @returns a string containing the plugin id
     */
    String getPluginID();

    /**
     * getTaskName returns the - at compile time given - name of the
     * task the plugin is constructed to handle. The task name is used
     * by the users of the plugin framework to assign jobs and
     * jobqueues to the plugins involved in getting a job through the
     * system.
     * 
     * @returns a string containing the task name
     */

    String getTaskName();
}
