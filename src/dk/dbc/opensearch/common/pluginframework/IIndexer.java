package dk.dbc.opensearch.common.pluginframework;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;

import org.compass.core.CompassSession;


public interface IIndexer extends IPluggable
{
    long getProcessTime( CargoContainer cargo, CompassSession session ) throws PluginException;//, CompassException, ParserConfigurationException, SAXException, IOException;
}