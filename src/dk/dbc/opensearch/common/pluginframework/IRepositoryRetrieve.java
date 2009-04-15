package dk.dbc.opensearch.common.pluginframework;


import dk.dbc.opensearch.common.types.CargoContainer;
import dk.dbc.opensearch.common.pluginframework.PluginException;

public interface IRepositoryRetrieve extends IPluggable
{    
    CargoContainer getCargoContainer( String fedoraPID ) throws PluginException;
}