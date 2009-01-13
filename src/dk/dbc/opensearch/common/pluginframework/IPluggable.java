package dk.dbc.opensearch.common.pluginframework;

public interface IPluggable
{
    /**
     * Initializes the plugin
     */
    void init();

    /**
     * following methods return information about the plugin for
     * the building of the key in the map containing the plugins
     * 
     * \Todo Decide what information is needed for building the key 
     */
    String getPluginTask();

    String getPluginFormat();

    String getPluginSubmitter();

}