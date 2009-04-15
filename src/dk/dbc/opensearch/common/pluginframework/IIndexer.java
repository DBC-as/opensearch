package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;

import org.apache.commons.configuration.ConfigurationException;
import org.compass.core.CompassSession;


public interface IIndexer extends IPluggable
{
    long getProcessTime( CargoContainer cargo, CompassSession session, String fedoraHandle ) throws PluginException, ConfigurationException;//, CompassException, ParserConfigurationException, SAXException, IOException;
}