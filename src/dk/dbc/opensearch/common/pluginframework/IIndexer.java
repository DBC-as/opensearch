package dk.dbc.opensearch.common.pluginframework;


import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;

import org.compass.core.CompassException;
import org.compass.core.CompassSession;
import org.xml.sax.SAXException;


public interface IIndexer extends IPluggable
{
    long getProcessTime( CargoContainer cargo, CompassSession session ) throws PluginException, CompassException, ParserConfigurationException, SAXException, IOException;
}