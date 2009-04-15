package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.xml.sax.SAXException;

/**
 * @author mro
 *
 */
public interface IHarvestable extends IPluggable
{
	/**
	 * @return the CargoContainer that results from the plugin activity 
	 * @throws IOException if the URI provided by the DatadockJob from the init call could not be read 
	 * @throws ParserConfigurationException 
	 * @throws SAXException 
	 */
	public CargoContainer getCargoContainer( DatadockJob job ) throws PluginException;//IOException, ParserConfigurationException, SAXException;
}