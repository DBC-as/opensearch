/**
 * 
 */
package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.types.DatadockJob;
import dk.dbc.opensearch.common.pluginframework.PluginException;

/**
 * @author mro
 *
 */
public interface IRepositoryStore extends IPluggable 
{
    String storeCargoContainer( CargoContainer cargo, DatadockJob job ) throws PluginException;//MarshalException, ValidationException, IllegalStateException, ServiceException, IOException, ParseException, ParserConfigurationException, SAXException, TransformerException, TransformerConfigurationException;
}
