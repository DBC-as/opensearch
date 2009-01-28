package dk.dbc.opensearch.common.pluginframework;

/**
 * This interface represents the base point for all plugins. The only
 * method supported by the interface is the identification of the
 * plugin via the pluginid
 */
public interface IPluggable
{

    String getPluginID();

    String getTaskName();
}
